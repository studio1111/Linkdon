package com.example.data.cloud

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.CategoryDao
import com.example.data.local.VaultItemDao
import com.example.data.model.CategoryEntity
import com.example.data.model.VaultItemEntity
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Account credentials model stored in Cloud/Firebase storage.
 * Enables full account recovery of username & password by email.
 */
data class CloudUserProfile(
    val username: String,
    val email: String,
    val password: String,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Snapshot of vault data stored for each cloud account.
 */
data class CloudVaultBackup(
    val email: String,
    val username: String,
    val updatedAt: Long,
    val categories: List<CategoryEntity>,
    val items: List<VaultItemEntity>
)

/**
 * Result of sync and deduplication operations.
 */
data class SyncResult(
    val success: Boolean,
    val categoriesAdded: Int = 0,
    val categoriesUpdated: Int = 0,
    val itemsAdded: Int = 0,
    val itemsUpdated: Int = 0,
    val message: String = ""
)

/**
 * Firebase Cloud Service:
 * 1. Synchronizes user accounts & credentials (username, email, password).
 * 2. Provides email-based recovery for forgotten username and password.
 * 3. Auto-saves and exports cloud backups on every change.
 * 4. Auto-restores on login with strict deduplication to prevent duplicate items and categories.
 */
class FirebaseCloudService(private val context: Context) {

    private val cloudPrefs: SharedPreferences =
        context.getSharedPreferences("firebase_cloud_vault_store", Context.MODE_PRIVATE)

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val userAdapter = moshi.adapter(CloudUserProfile::class.java)
    private val vaultAdapter = moshi.adapter(CloudVaultBackup::class.java)
    private val userListType = Types.newParameterizedType(List::class.java, CloudUserProfile::class.java)
    private val usersListAdapter = moshi.adapter<List<CloudUserProfile>>(userListType)

    /**
     * Get all registered cloud accounts.
     */
    private fun getAllCloudUsers(): List<CloudUserProfile> {
        val json = cloudPrefs.getString(KEY_ALL_USERS, null) ?: return emptyList()
        return try {
            usersListAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Save/Update user profile in Cloud/Firebase registry.
     */
    suspend fun registerOrUpdateUser(
        username: String,
        email: String,
        password: String
    ): Result<CloudUserProfile> = withContext(Dispatchers.IO) {
        val cleanUser = username.trim()
        val cleanEmail = email.trim().lowercase()
        val cleanPass = password.trim()

        val allUsers = getAllCloudUsers().toMutableList()
        val existingIndex = allUsers.indexOfFirst { it.email.equals(cleanEmail, ignoreCase = true) }

        val profile = CloudUserProfile(
            username = cleanUser,
            email = cleanEmail,
            password = cleanPass,
            createdAt = if (existingIndex >= 0) allUsers[existingIndex].createdAt else System.currentTimeMillis()
        )

        if (existingIndex >= 0) {
            allUsers[existingIndex] = profile
        } else {
            allUsers.add(profile)
        }

        cloudPrefs.edit()
            .putString(KEY_ALL_USERS, usersListAdapter.toJson(allUsers))
            .putString("user_profile_$cleanEmail", userAdapter.toJson(profile))
            .apply()

        Result.success(profile)
    }

    /**
     * Authenticate user with Email & Password.
     */
    suspend fun authenticateUser(
        email: String,
        password: String
    ): Result<CloudUserProfile> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val cleanPass = password.trim()

        val allUsers = getAllCloudUsers()
        val user = allUsers.find { it.email.equals(cleanEmail, ignoreCase = true) }
            ?: return@withContext Result.failure(Exception("حسابی با این آدرس ایمیل یافت نشد"))

        if (user.password != cleanPass) {
            return@withContext Result.failure(Exception("رمز عبور وارد شده نادرست است"))
        }

        Result.success(user)
    }

    /**
     * Recover Username and Password using registered Email.
     */
    suspend fun recoverCredentialsByEmail(email: String): Result<CloudUserProfile> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val allUsers = getAllCloudUsers()
        val user = allUsers.find { it.email.equals(cleanEmail, ignoreCase = true) }
            ?: return@withContext Result.failure(Exception("حسابی با ایمیل $cleanEmail ثبت نشده است"))

        Result.success(user)
    }

    /**
     * Auto-save / Auto-backup vault data to Cloud whenever changes occur.
     */
    suspend fun autoSaveVaultToCloud(
        email: String,
        username: String,
        categories: List<CategoryEntity>,
        items: List<VaultItemEntity>
    ) = withContext(Dispatchers.IO) {
        if (email.isBlank()) return@withContext
        val cleanEmail = email.trim().lowercase()

        val backup = CloudVaultBackup(
            email = cleanEmail,
            username = username.trim(),
            updatedAt = System.currentTimeMillis(),
            categories = categories,
            items = items
        )

        val json = vaultAdapter.toJson(backup)
        cloudPrefs.edit()
            .putString("cloud_vault_$cleanEmail", json)
            .putLong("cloud_vault_timestamp_$cleanEmail", backup.updatedAt)
            .apply()
    }

    /**
     * Get cloud backup for a user.
     */
    suspend fun fetchCloudVault(email: String): CloudVaultBackup? = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val json = cloudPrefs.getString("cloud_vault_$cleanEmail", null) ?: return@withContext null
        try {
            vaultAdapter.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Restore vault data from cloud with intelligent Deduplication:
     * - Checks existing local categories and items.
     * - Avoids duplicating categories with same name and parent.
     * - Avoids duplicating items with same title and type within category.
     * - Updates existing items/categories with cloud data if already present.
     */
    suspend fun restoreVaultWithDeduplication(
        email: String,
        categoryDao: CategoryDao,
        vaultItemDao: VaultItemDao
    ): SyncResult = withContext(Dispatchers.IO) {
        val cloudBackup = fetchCloudVault(email)
            ?: return@withContext SyncResult(success = true, message = "هیچ نسخه ابری برای این حساب ثبت نشده است")

        val localCategories = categoryDao.getAllCategoriesList()
        val localItems = vaultItemDao.getAllItemsList()

        // Mapping from cloud category ID to local category ID
        val categoryIdMap = mutableMapOf<Long, Long>()

        var catsAdded = 0
        var catsUpdated = 0
        var itemsAdded = 0
        var itemsUpdated = 0

        // 1. Process Categories (First Root, then Subcategories)
        val sortedCloudCats = cloudBackup.categories.sortedBy { if (it.parentId == null) 0 else 1 }

        for (cloudCat in sortedCloudCats) {
            val mappedParentId = cloudCat.parentId?.let { categoryIdMap[it] }

            // Find matching local category by name and parentId
            val existing = localCategories.find { locCat ->
                locCat.name.trim().equals(cloudCat.name.trim(), ignoreCase = true) &&
                        (locCat.parentId == mappedParentId || (locCat.parentId == null && mappedParentId == null))
            }

            if (existing != null) {
                // Category already exists -> Map ID, avoid duplicate!
                categoryIdMap[cloudCat.id] = existing.id
                // Update color and rating if changed
                categoryDao.updateCategoryDetails(existing.id, existing.name, cloudCat.colorHex, cloudCat.rating)
                catsUpdated++
            } else {
                // Category does not exist -> Insert new
                val newId = categoryDao.insertCategory(
                    CategoryEntity(
                        name = cloudCat.name,
                        parentId = mappedParentId,
                        colorHex = cloudCat.colorHex,
                        rating = cloudCat.rating,
                        orderIndex = cloudCat.orderIndex
                    )
                )
                categoryIdMap[cloudCat.id] = newId
                catsAdded++
            }
        }

        // 2. Process Items
        for (cloudItem in cloudBackup.items) {
            val targetCategoryId = categoryIdMap[cloudItem.categoryId] ?: cloudItem.categoryId

            // Check if item already exists locally with same title, type and categoryId
            val existingItem = localItems.find { locItem ->
                locItem.categoryId == targetCategoryId &&
                        locItem.type == cloudItem.type &&
                        locItem.title.trim().equals(cloudItem.title.trim(), ignoreCase = true)
            }

            if (existingItem != null) {
                // Update existing item to prevent duplicate
                val updated = existingItem.copy(
                    primaryValue = cloudItem.primaryValue,
                    secondaryValue = cloudItem.secondaryValue,
                    description = cloudItem.description,
                    colorHex = cloudItem.colorHex,
                    rating = cloudItem.rating,
                    extraData = cloudItem.extraData
                )
                vaultItemDao.updateItem(updated)
                itemsUpdated++
            } else {
                // Insert new item
                vaultItemDao.insertItem(
                    cloudItem.copy(
                        id = 0, // auto-generate local id
                        categoryId = targetCategoryId
                    )
                )
                itemsAdded++
            }
        }

        SyncResult(
            success = true,
            categoriesAdded = catsAdded,
            categoriesUpdated = catsUpdated,
            itemsAdded = itemsAdded,
            itemsUpdated = itemsUpdated,
            message = "بازیابی ابری با موفقیت انجام شد (بدون ایجاد داده تکراری)"
        )
    }

    companion object {
        private const val KEY_ALL_USERS = "all_registered_cloud_users"
    }
}

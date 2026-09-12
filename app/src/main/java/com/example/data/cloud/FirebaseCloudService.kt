package com.example.data.cloud

import com.example.data.local.CategoryDao
import com.example.data.local.VaultItemDao
import com.example.data.model.CategoryEntity
import com.example.data.model.VaultItemEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Account credentials model stored in Cloud/Firebase storage.
 * Enables full account recovery of username & password by email.
 *
 * NOTE: Storing raw passwords in Firestore is not secure. Prefer using
 * Firebase Authentication (email/password) instead of storing passwords
 * yourself. This class keeps the same shape as before to avoid breaking
 * the rest of the app, but consider migrating to FirebaseAuth later.
 */
data class CloudUserProfile(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Snapshot of vault data stored for each cloud account.
 */
data class CloudVaultBackup(
    val email: String = "",
    val username: String = "",
    val updatedAt: Long = 0L,
    val categories: List<CategoryEntity> = emptyList(),
    val items: List<VaultItemEntity> = emptyList()
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
 * Firebase Cloud Service (real Firestore-backed version):
 * 1. Synchronizes user accounts & credentials (username, email, password).
 * 2. Provides email-based recovery for forgotten username and password.
 * 3. Auto-saves and exports cloud backups on every change.
 * 4. Auto-restores on login with strict deduplication to prevent duplicate items and categories.
 */
class FirebaseCloudService {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    private val vaultsCollection = db.collection("vaults")

    /**
     * Save/Update user profile in Firestore ("users" collection).
     * Document ID = normalized email, so lookups are O(1) and unambiguous.
     */
    suspend fun registerOrUpdateUser(
        username: String,
        email: String,
        password: String
    ): Result<CloudUserProfile> = withContext(Dispatchers.IO) {
        try {
            val cleanEmail = email.trim().lowercase()
            val cleanUser = username.trim()
            val cleanPass = password.trim()

            val docRef = usersCollection.document(cleanEmail)
            val existingSnap = docRef.get().await()
            val createdAt = if (existingSnap.exists()) {
                existingSnap.getLong("createdAt") ?: System.currentTimeMillis()
            } else {
                System.currentTimeMillis()
            }

            val profile = CloudUserProfile(
                username = cleanUser,
                email = cleanEmail,
                password = cleanPass,
                createdAt = createdAt
            )

            docRef.set(profile, SetOptions.merge()).await()
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Authenticate user with Email & Password.
     */
    suspend fun authenticateUser(
        email: String,
        password: String
    ): Result<CloudUserProfile> = withContext(Dispatchers.IO) {
        try {
            val cleanEmail = email.trim().lowercase()
            val cleanPass = password.trim()

            val snap = usersCollection.document(cleanEmail).get().await()
            if (!snap.exists()) {
                return@withContext Result.failure(Exception("حسابی با این آدرس ایمیل یافت نشد"))
            }

            val user = snap.toObject(CloudUserProfile::class.java)
                ?: return@withContext Result.failure(Exception("خطا در خواندن اطلاعات حساب"))

            if (user.password != cleanPass) {
                return@withContext Result.failure(Exception("رمز عبور وارد شده نادرست است"))
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Recover Username and Password using registered Email.
     */
    suspend fun recoverCredentialsByEmail(email: String): Result<CloudUserProfile> = withContext(Dispatchers.IO) {
        try {
            val cleanEmail = email.trim().lowercase()
            val snap = usersCollection.document(cleanEmail).get().await()

            if (!snap.exists()) {
                return@withContext Result.failure(Exception("حسابی با ایمیل $cleanEmail ثبت نشده است"))
            }

            val user = snap.toObject(CloudUserProfile::class.java)
                ?: return@withContext Result.failure(Exception("خطا در خواندن اطلاعات حساب"))

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Auto-save / Auto-backup vault data to Cloud whenever changes occur.
     * Stored in the "vaults" collection, document ID = normalized email.
     */
    suspend fun autoSaveVaultToCloud(
        email: String,
        username: String,
        categories: List<CategoryEntity>,
        items: List<VaultItemEntity>
    ) = withContext(Dispatchers.IO) {
        if (email.isBlank()) return@withContext
        try {
            val cleanEmail = email.trim().lowercase()
            val backup = CloudVaultBackup(
                email = cleanEmail,
                username = username.trim(),
                updatedAt = System.currentTimeMillis(),
                categories = categories,
                items = items
            )
            vaultsCollection.document(cleanEmail).set(backup).await()
        } catch (e: Exception) {
            // Swallow to keep auto-save best-effort like the original,
            // but you may want to log this via Crashlytics/Log.e in production.
        }
    }

    /**
     * Get cloud backup for a user.
     */
    suspend fun fetchCloudVault(email: String): CloudVaultBackup? = withContext(Dispatchers.IO) {
        try {
            val cleanEmail = email.trim().lowercase()
            val snap = vaultsCollection.document(cleanEmail).get().await()
            if (!snap.exists()) return@withContext null
            snap.toObject(CloudVaultBackup::class.java)
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
}

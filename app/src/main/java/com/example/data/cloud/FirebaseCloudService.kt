package com.example.data.cloud

import com.example.data.local.CategoryDao
import com.example.data.local.VaultItemDao
import com.example.data.model.CategoryEntity
import com.example.data.model.VaultItemEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Account profile stored in Firestore ("users" collection).
 * The password itself is never stored here — it lives only inside
 * Firebase Authentication, hashed, and is never readable by anyone,
 * including this app's own code.
 */
data class CloudUserProfile(
    val username: String = "",
    val email: String = "",
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

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    private val vaultsCollection = db.collection("vaults")

    /**
     * Register a brand-new account with Firebase Authentication (email/password),
     * then save the public profile (username, email — never the password) in
     * Firestore under the new user's UID.
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

            val authResult = auth.createUserWithEmailAndPassword(cleanEmail, cleanPass).await()
            val uid = authResult.user?.uid
                ?: return@withContext Result.failure(Exception("خطا در ایجاد حساب کاربری"))

            val profile = CloudUserProfile(
                username = cleanUser,
                email = cleanEmail,
                createdAt = System.currentTimeMillis()
            )

            usersCollection.document(uid).set(profile, SetOptions.merge()).await()
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Authenticate an existing user with Email & Password via Firebase Authentication.
     */
    suspend fun authenticateUser(
        email: String,
        password: String
    ): Result<CloudUserProfile> = withContext(Dispatchers.IO) {
        try {
            val cleanEmail = email.trim().lowercase()
            val cleanPass = password.trim()

            val authResult = auth.signInWithEmailAndPassword(cleanEmail, cleanPass).await()
            val uid = authResult.user?.uid
                ?: return@withContext Result.failure(Exception("خطا در ورود به حساب"))

            val snap = usersCollection.document(uid).get().await()
            val profile = if (snap.exists()) {
                snap.toObject(CloudUserProfile::class.java)
                    ?: CloudUserProfile(email = cleanEmail)
            } else {
                // Profile document missing for some reason — recreate a minimal one.
                CloudUserProfile(email = cleanEmail)
            }

            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Send a password-reset email via Firebase Authentication. The user gets
     * a link from Firebase to set a brand-new password — the old password is
     * never retrievable, by design.
     */
    suspend fun recoverCredentialsByEmail(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val cleanEmail = email.trim().lowercase()
            auth.sendPasswordResetEmail(cleanEmail).await()
            Result.success(Unit)
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

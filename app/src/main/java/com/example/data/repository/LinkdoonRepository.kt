package com.example.data.repository

import com.example.data.local.CategoryDao
import com.example.data.local.VaultItemDao
import com.example.data.model.BackupData
import com.example.data.model.CategoryEntity
import com.example.data.model.VaultItemEntity
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LinkdoonRepository(
    private val categoryDao: CategoryDao,
    private val vaultItemDao: VaultItemDao
) {
    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val backupAdapter = moshi.adapter(BackupData::class.java)

    // Category queries
    fun getRootCategories(): Flow<List<CategoryEntity>> = categoryDao.getRootCategories()

    fun getSubcategories(parentId: Long): Flow<List<CategoryEntity>> = categoryDao.getSubcategories(parentId)

    fun observeAllCategories(): Flow<List<CategoryEntity>> = categoryDao.observeAllCategories()

    suspend fun getCategoryById(id: Long): CategoryEntity? = categoryDao.getCategoryById(id)

    fun observeCategoryById(id: Long): Flow<CategoryEntity?> = categoryDao.observeCategoryById(id)

    fun getCategoryCount(): Flow<Int> = categoryDao.getCategoryCount()

    fun getSubcategoryCount(): Flow<Int> = categoryDao.getSubcategoryCount()

    suspend fun insertCategory(name: String, parentId: Long?, colorHex: String, rating: Int = 0): Long {
        val entity = CategoryEntity(
            name = name,
            parentId = parentId,
            colorHex = colorHex,
            rating = rating
        )
        return categoryDao.insertCategory(entity)
    }

    suspend fun updateCategory(category: CategoryEntity) = categoryDao.updateCategory(category)

    suspend fun updateCategoryDetails(id: Long, name: String, colorHex: String, rating: Int) =
        categoryDao.updateCategoryDetails(id, name, colorHex, rating)

    suspend fun deleteCategoryById(id: Long) = categoryDao.deleteCategoryById(id)

    // Vault Item queries
    fun getItemsByCategoryId(categoryId: Long): Flow<List<VaultItemEntity>> =
        vaultItemDao.getItemsByCategoryId(categoryId)

    fun observeAllItems(): Flow<List<VaultItemEntity>> = vaultItemDao.observeAllItems()

    fun getItemCount(): Flow<Int> = vaultItemDao.getItemCount()

    suspend fun insertItem(item: VaultItemEntity): Long = vaultItemDao.insertItem(item)

    suspend fun updateItem(item: VaultItemEntity) = vaultItemDao.updateItem(item)

    suspend fun updateItemColor(id: Long, colorHex: String) = vaultItemDao.updateItemColor(id, colorHex)

    suspend fun deleteItemById(id: Long) = vaultItemDao.deleteItemById(id)

    // Backup & Restore
    suspend fun exportToJson(): String = withContext(Dispatchers.IO) {
        val categories = categoryDao.getAllCategoriesList()
        val items = vaultItemDao.getAllItemsList()
        val backup = BackupData(
            categories = categories,
            items = items
        )
        backupAdapter.indent("  ").toJson(backup)
    }

    suspend fun importFromJson(jsonString: String): Result<Pair<Int, Int>> = withContext(Dispatchers.IO) {
        try {
            val backup = backupAdapter.fromJson(jsonString) ?: return@withContext Result.failure(
                IllegalArgumentException("قالب فایل نامعتبر است")
            )
            
            // Insert categories and items
            if (backup.categories.isNotEmpty()) {
                categoryDao.insertCategories(backup.categories)
            }
            if (backup.items.isNotEmpty()) {
                vaultItemDao.insertItems(backup.items)
            }
            Result.success(Pair(backup.categories.size, backup.items.size))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

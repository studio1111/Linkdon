package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.VaultItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultItemDao {

    @Query("SELECT * FROM vault_items WHERE categoryId = :categoryId ORDER BY createdAt DESC")
    fun getItemsByCategoryId(categoryId: Long): Flow<List<VaultItemEntity>>

    @Query("SELECT * FROM vault_items ORDER BY createdAt DESC")
    fun observeAllItems(): Flow<List<VaultItemEntity>>

    @Query("SELECT * FROM vault_items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: Long): VaultItemEntity?

    @Query("SELECT * FROM vault_items ORDER BY id ASC")
    suspend fun getAllItemsList(): List<VaultItemEntity>

    @Query("SELECT COUNT(*) FROM vault_items")
    fun getItemCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: VaultItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<VaultItemEntity>)

    @Update
    suspend fun updateItem(item: VaultItemEntity)

    @Query("UPDATE vault_items SET colorHex = :colorHex WHERE id = :id")
    suspend fun updateItemColor(id: Long, colorHex: String)

    @Query("UPDATE vault_items SET categoryId = :newCategoryId WHERE id = :id")
    suspend fun updateItemCategory(id: Long, newCategoryId: Long)

    @Delete
    suspend fun deleteItem(item: VaultItemEntity)

    @Query("DELETE FROM vault_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("DELETE FROM vault_items")
    suspend fun clearAllItems()
}

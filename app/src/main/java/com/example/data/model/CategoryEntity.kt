package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(
    tableName = "categories",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["parentId"])]
)
@JsonClass(generateAdapter = true)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val parentId: Long? = null,
    val name: String,
    val colorHex: String = "#3B82F6",
    val orderIndex: Int = 0,
    val rating: Float = 0f, // 0.0 to 10.0 stars (supports half stars like 8.5)
    val createdAt: Long = System.currentTimeMillis()
)

package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(
    tableName = "vault_items",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["categoryId"])]
)
@JsonClass(generateAdapter = true)
data class VaultItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryId: Long,
    val type: String = ItemType.OTHER_TEXT.id,
    val title: String,
    val description: String = "",
    val primaryValue: String = "", // URL, Card Number, Phone, Email, Security Code, AI Prompt, Note, Code, Text
    val secondaryValue: String = "", // Password, Bank Name, Social Platform, Secondary Phone, Language
    val extraData: String = "", // JSON or encoded data: CVV2, Expiry, Sheba, Owner, Channel link, etc.
    val colorHex: String = "#3B82F6",
    val rating: Float = 0f, // 0.0 to 10.0 stars (supports half stars like 8.5)
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BackupData(
    val version: Int = 1,
    val appName: String = "Linkdoon",
    val exportedAt: Long = System.currentTimeMillis(),
    val categories: List<CategoryEntity> = emptyList(),
    val items: List<VaultItemEntity> = emptyList()
)

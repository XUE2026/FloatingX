package com.xue2026.floatingx.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "config")
data class ConfigEntity(
    @PrimaryKey
    val key: String,
    val value: String
)

@Entity(tableName = "model_cache")
data class ModelCacheEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val modelName: String,
    val modelPath: String,
    val modelType: String, // PMX, FBX, SPINE
    val thumbnailPath: String? = null,
    val lastUsedTime: Long = System.currentTimeMillis(),
    val metadata: String? = null // JSON metadata
)
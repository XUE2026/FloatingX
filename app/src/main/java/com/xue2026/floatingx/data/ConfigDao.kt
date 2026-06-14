package com.xue2026.floatingx.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ConfigDao {

    @Query("SELECT value FROM config WHERE `key` = :key")
    suspend fun getValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setValue(config: ConfigEntity)

    @Query("DELETE FROM config WHERE `key` = :key")
    suspend fun deleteValue(key: String)

    @Query("SELECT * FROM config")
    suspend fun getAll(): List<ConfigEntity>
}

@Dao
interface ModelCacheDao {

    @Query("SELECT * FROM model_cache ORDER BY lastUsedTime DESC")
    suspend fun getAllModels(): List<ModelCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModel(model: ModelCacheEntity): Long

    @Query("DELETE FROM model_cache WHERE id = :id")
    suspend fun deleteModel(id: Long)

    @Query("UPDATE model_cache SET lastUsedTime = :time WHERE id = :id")
    suspend fun updateLastUsedTime(id: Long, time: Long = System.currentTimeMillis())
}
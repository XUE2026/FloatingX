package com.xue2026.floatingx.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.xue2026.floatingx.FloatingXApp

@Database(entities = [ConfigEntity::class, ModelCacheEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun configDao(): ConfigDao
    abstract fun modelCacheDao(): ModelCacheDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getInstance(): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(FloatingXApp.instance, AppDatabase::class.java, "floatingx.db")
                .fallbackToDestructiveMigration().build().also { INSTANCE = it }
        }
    }
}
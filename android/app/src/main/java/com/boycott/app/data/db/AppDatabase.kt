package com.boycott.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SearchHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
} 
package com.boycott.app.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 10")
    fun getSearchHistory(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(search: SearchHistoryEntity)

    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()

    @Delete
    suspend fun deleteSearch(search: SearchHistoryEntity)
} 
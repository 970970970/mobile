package com.boycott.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.boycott.app.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentSearches(limit: Int = 20): Flow<List<SearchHistoryEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(search: SearchHistoryEntity)
    
    @Query("DELETE FROM search_history WHERE query = :query")
    suspend fun deleteSearch(query: String)
    
    @Query("DELETE FROM search_history")
    suspend fun clearHistory()
    
    @Query("SELECT COUNT(*) FROM search_history")
    suspend fun getCount(): Int
    
    @Query("DELETE FROM search_history WHERE id NOT IN (SELECT id FROM search_history ORDER BY createdAt DESC LIMIT :limit)")
    suspend fun keepRecentSearches(limit: Int = 20)
} 
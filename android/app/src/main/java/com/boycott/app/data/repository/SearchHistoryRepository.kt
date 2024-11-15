package com.boycott.app.data.repository

import com.boycott.app.data.local.dao.SearchHistoryDao
import com.boycott.app.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepository @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao
) {
    fun getRecentSearches(limit: Int = 20): Flow<List<String>> {
        return searchHistoryDao.getRecentSearches(limit).map { items ->
            items.map { it.query }
        }
    }
    
    suspend fun addSearch(query: String) {
        searchHistoryDao.insertSearch(
            SearchHistoryEntity(
                query = query,
                createdAt = Date()
            )
        )
        searchHistoryDao.keepRecentSearches()
    }
    
    suspend fun clearHistory() {
        searchHistoryDao.clearHistory()
    }
} 
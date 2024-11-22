package com.boycott.app.data.repository

import com.boycott.app.data.api.ApiService
import com.boycott.app.data.db.SearchHistoryDao
import com.boycott.app.data.db.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val apiService: ApiService,
    private val searchHistoryDao: SearchHistoryDao
) {
    fun getSearchHistory(): Flow<List<SearchHistoryEntity>> {
        return searchHistoryDao.getSearchHistory()
    }

    suspend fun addToSearchHistory(query: String) {
        searchHistoryDao.insertSearch(SearchHistoryEntity(query))
    }

    suspend fun clearSearchHistory() {
        searchHistoryDao.clearSearchHistory()
    }

    suspend fun deleteSearch(query: String) {
        searchHistoryDao.deleteSearch(SearchHistoryEntity(query))
    }

    suspend fun getHotSearches(): List<String> {
        return apiService.getHotSearches().data
    }

    suspend fun searchBrands(
        keywords: String,
        limit: Int = 20,
        offset: Int = 0
    ) = apiService.searchBrands(keywords, limit, offset).data
} 
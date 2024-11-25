package com.boycott.app.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boycott.app.data.model.Brand
import com.boycott.app.data.repository.BrandRepository
import com.boycott.app.data.repository.SearchHistoryRepository
import com.boycott.app.data.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchHistoryViewModel @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository,
    private val searchRepository: SearchRepository
) : ViewModel() {
    
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory

    private val _searchResults = MutableStateFlow<List<Brand>>(emptyList())
    val searchResults: StateFlow<List<Brand>> = _searchResults

    init {
        Log.d("SearchDebug", "SearchHistoryViewModel initialized")
        loadSearchHistory()
    }

    private fun loadSearchHistory() {
        val history = searchHistoryRepository.getSearchHistory()
        Log.d("SearchDebug", "Loading search history: $history")
        _searchHistory.value = history
    }

    fun searchBrands(keyword: String) {
        Log.d("SearchDebug", "Searching brands with keyword: $keyword")
        viewModelScope.launch {
            try {
                val response = searchRepository.searchBrands(
                    keywords = keyword,
                    limit = 20,
                    offset = 0
                )
                _searchResults.value = response.items
                Log.d("SearchDebug", "Search results count: ${response.items.size}")
            } catch (e: Exception) {
                Log.e("SearchDebug", "Error searching brands", e)
            }
        }
    }

    fun addToHistory(query: String) {
        Log.d("SearchDebug", "Adding to history: $query")
        searchHistoryRepository.addSearchHistory(query)
        loadSearchHistory()
    }

    fun removeFromHistory(query: String) {
        Log.d("SearchDebug", "Removing from history: $query")
        searchHistoryRepository.removeFromHistory(query)
        loadSearchHistory()
    }

    fun clearHistory() {
        Log.d("SearchDebug", "Clearing all history")
        searchHistoryRepository.clearSearchHistory()
        loadSearchHistory()
    }
} 
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
    private val brandRepository: BrandRepository
) : ViewModel() {
    
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory

    private val _searchResults = MutableStateFlow<List<Brand>>(emptyList())
    val searchResults: StateFlow<List<Brand>> = _searchResults

    init {
        loadSearchHistory()
    }

    private fun loadSearchHistory() {
        _searchHistory.value = searchHistoryRepository.getSearchHistory()
    }

    fun searchBrands(keyword: String) {
        viewModelScope.launch {
            try {
                val response = brandRepository.getBrands(
                    keywords = keyword,
                    limit = 20,
                    offset = 0
                )
                _searchResults.value = response.items
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addToHistory(query: String) {
        searchHistoryRepository.addSearchHistory(query)
        loadSearchHistory()
    }

    fun removeFromHistory(query: String) {
        searchHistoryRepository.removeFromHistory(query)
        loadSearchHistory()
    }

    fun clearHistory() {
        searchHistoryRepository.clearSearchHistory()
        loadSearchHistory()
    }
} 
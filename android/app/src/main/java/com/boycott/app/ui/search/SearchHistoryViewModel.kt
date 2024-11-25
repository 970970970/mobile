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

    private val _noResults = MutableStateFlow(false)
    val noResults: StateFlow<Boolean> = _noResults

    private var onSingleBrandFound: ((String) -> Unit)? = null

    init {
        loadSearchHistory()
    }

    fun setOnSingleBrandFoundCallback(callback: (String) -> Unit) {
        Log.d("SearchDebug", "Setting single brand callback")
        onSingleBrandFound = callback
    }

    private fun loadSearchHistory() {
        _searchHistory.value = searchHistoryRepository.getSearchHistory()
    }

    fun resetNoResults() {
        _noResults.value = false
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
                Log.e("SearchDebug", "Error searching brands", e)
            }
        }
    }

    fun searchAndNavigate(keyword: String, onBrandClick: (String) -> Unit, onSearch: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = brandRepository.getBrands(
                    keywords = keyword,
                    limit = 20,
                    offset = 0
                )
                
                when {
                    response.items.isEmpty() -> {
                        _searchResults.value = emptyList()
                        onSearch()
                    }
                    response.items.size == 1 -> {
                        onBrandClick(response.items[0].id.toString())
                    }
                    else -> {
                        _searchResults.value = response.items
                        onSearch()
                    }
                }
            } catch (e: Exception) {
                Log.e("SearchDebug", "Error searching brands", e)
                onSearch()
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
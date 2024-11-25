package com.boycott.app.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boycott.app.data.api.ApiService
import com.boycott.app.data.model.Brand
import com.boycott.app.data.repository.BrandRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultsViewModel @Inject constructor(
    private val brandRepository: BrandRepository
) : ViewModel() {
    private val _searchResults = MutableStateFlow<List<Brand>>(emptyList())
    val searchResults: StateFlow<List<Brand>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _hasMoreData = MutableStateFlow(true)
    val hasMoreData: StateFlow<Boolean> = _hasMoreData

    private var currentPage = 0
    private val pageSize = 20
    private var currentKeyword = ""
    private var totalItems = 0

    fun searchBrands(keyword: String, isNewSearch: Boolean = false) {
        if (_isLoading.value) return
        if (isNewSearch) {
            currentPage = 0
            _searchResults.value = emptyList()
            _hasMoreData.value = true
            currentKeyword = keyword
        }

        if (!_hasMoreData.value) return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = brandRepository.getBrands(
                    keywords = currentKeyword,
                    limit = pageSize,
                    offset = currentPage * pageSize
                )
                
                totalItems = response.total
                val newBrands = response.items
                
                _searchResults.value = if (currentPage == 0) {
                    newBrands
                } else {
                    _searchResults.value + newBrands
                }

                _hasMoreData.value = _searchResults.value.size < totalItems
                if (_hasMoreData.value) currentPage++
                
            } catch (e: Exception) {
                Log.e("SearchDebug", "Error loading more brands", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadNextPage() {
        if (currentKeyword.isNotEmpty()) {
            searchBrands(currentKeyword, false)
        }
    }
} 
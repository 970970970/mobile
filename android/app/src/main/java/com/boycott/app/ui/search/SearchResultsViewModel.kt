package com.boycott.app.ui.search

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

    fun searchBrands(query: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = brandRepository.getBrands(
                    limit = 20,
                    offset = 0,
                    keywords = query
                )
                _searchResults.value = response.items
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
} 
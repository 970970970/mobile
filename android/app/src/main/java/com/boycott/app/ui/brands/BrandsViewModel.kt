package com.boycott.app.ui.brands

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boycott.app.data.model.Brand
import com.boycott.app.data.repository.BrandRepository
import com.boycott.app.data.repository.SearchRepository
import com.boycott.app.data.repository.SearchTextRepository
import com.boycott.app.ui.home.HomeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrandsViewModel @Inject constructor(
    private val brandRepository: BrandRepository,
    private val searchRepository: SearchRepository,
    private val searchTextRepository: SearchTextRepository
) : ViewModel() {
    
    val searchText = searchTextRepository.searchText

    private val _brands = MutableStateFlow<List<Brand>>(emptyList())
    val brands: StateFlow<List<Brand>> = _brands

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _hasMoreData = MutableStateFlow(true)
    val hasMoreData: StateFlow<Boolean> = _hasMoreData

    private var currentPage = 0
    private val pageSize = 20

    init {
        loadNextPage()
        loadHotSearches()
    }

    fun loadNextPage() {
        if (_isLoading.value || !_hasMoreData.value) return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = brandRepository.getBrands(pageSize, currentPage * pageSize)
                
                val newBrands = response.items
                if (currentPage == 0) {
                    _brands.value = newBrands
                } else {
                    _brands.value = _brands.value + newBrands
                }

                _hasMoreData.value = newBrands.size == pageSize
                if (_hasMoreData.value) currentPage++
                
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadHotSearches() {
        viewModelScope.launch {
            try {
                val hotSearches = searchRepository.getHotSearches()
                if (hotSearches.isNotEmpty()) {
                    searchTextRepository.updateSearchText(hotSearches[0])
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateSearchText(text: String) {
        searchTextRepository.updateSearchText(text)
    }
} 
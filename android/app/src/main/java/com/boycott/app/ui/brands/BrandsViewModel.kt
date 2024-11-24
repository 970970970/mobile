package com.boycott.app.ui.brands

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boycott.app.data.model.Brand
import com.boycott.app.data.repository.BrandRepository
import com.boycott.app.data.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrandsViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val brandRepository: BrandRepository
) : ViewModel() {
    
    private val _currentHotSearch = MutableStateFlow("")
    val currentHotSearch: StateFlow<String> = _currentHotSearch

    private val _brands = MutableStateFlow<List<Brand>>(emptyList())
    val brands = _brands.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _hasMoreData = MutableStateFlow(true)
    val hasMoreData: StateFlow<Boolean> = _hasMoreData

    private var currentPage = 0
    private val pageSize = 20

    init {
        loadHotSearches()
        loadNextPage()
    }

    private fun loadHotSearches() {
        viewModelScope.launch {
            try {
                val hotSearches = searchRepository.getHotSearches()
                if (hotSearches.isNotEmpty()) {
                    _currentHotSearch.value = hotSearches[0]
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadNextPage() {
        if (_isLoading.value || !_hasMoreData.value) return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = brandRepository.getBrands(
                    limit = pageSize,
                    offset = currentPage * pageSize
                )
                
                // 更新品牌列表
                val newBrands = response.items
                if (currentPage == 0) {
                    _brands.value = newBrands
                } else {
                    _brands.value = _brands.value + newBrands
                }

                // 更新分页状态
                _hasMoreData.value = newBrands.size == pageSize
                if (_hasMoreData.value) currentPage++
                
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
} 
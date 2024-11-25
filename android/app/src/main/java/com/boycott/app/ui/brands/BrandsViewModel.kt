package com.boycott.app.ui.brands

import android.util.Log
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
    private val searchRepository: SearchRepository
) : ViewModel() {
    
    private val _brands = MutableStateFlow<List<Brand>>(emptyList())
    val brands = _brands.asStateFlow()

    private val _currentHotSearch = MutableStateFlow("")
    val currentHotSearch: StateFlow<String> = _currentHotSearch

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

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
                val response = brandRepository.getBrands(
                    limit = pageSize,
                    offset = currentPage * pageSize
                )
                
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
                    _currentHotSearch.value = hotSearches[0]
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun searchAndNavigate(
        keyword: String,
        onBrandClick: (String) -> Unit,
        onSearch: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = brandRepository.getBrands(
                    keywords = keyword,
                    limit = 20,
                    offset = 0
                )
                
                Log.d("SearchDebug", "BrandsViewModel: Search results size: ${response.items.size}")
                when {
                    response.items.isEmpty() -> {
                        onSearch()  // 无结果时导航到搜索结果页
                    }
                    response.items.size == 1 -> {
                        onBrandClick(response.items[0].id.toString())  // 单个结果直接导航到详情页
                    }
                    else -> {
                        onSearch()  // 多个结果导航到搜索结果页
                    }
                }
            } catch (e: Exception) {
                Log.e("SearchDebug", "BrandsViewModel: Error searching brands", e)
                onSearch()  // 出错时导航到搜索结果页
            }
        }
    }

    fun updateSearchText(text: String) {
        Log.d("SearchDebug", "BrandsViewModel: Updating search text to: $text")
        _searchText.value = text
    }
} 
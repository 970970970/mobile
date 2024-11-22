package com.boycott.app.ui.brands

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boycott.app.data.model.Brand
import com.boycott.app.data.repository.BrandRepository
import com.boycott.app.data.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    val brands: StateFlow<List<Brand>> = _brands

    init {
        loadHotSearches()
        loadBrands()
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

    private fun loadBrands() {
        viewModelScope.launch {
            try {
                val response = brandRepository.getBrands(20, 0)
                _brands.value = response.items
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
} 
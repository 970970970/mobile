package com.boycott.app.ui.brand

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boycott.app.data.model.Brand
import com.boycott.app.data.repository.BrandRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrandDetailViewModel @Inject constructor(
    private val brandRepository: BrandRepository
) : ViewModel() {
    private val _brand = MutableStateFlow<Brand?>(null)
    val brand: StateFlow<Brand?> = _brand

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadBrand(id: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = brandRepository.getBrandDetail(id)
                _brand.value = response
            } catch (e: Exception) {
                // 处理错误
            } finally {
                _isLoading.value = false
            }
        }
    }
} 
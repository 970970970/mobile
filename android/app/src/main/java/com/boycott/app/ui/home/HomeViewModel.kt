package com.boycott.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boycott.app.data.api.ApiService
import com.boycott.app.data.model.Article
import com.boycott.app.data.model.Brand
import com.boycott.app.data.repository.ArticleRepository
import com.boycott.app.data.repository.BrandRepository
import com.boycott.app.utils.LanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    private val brandRepository: BrandRepository,
    private val languageManager: LanguageManager,
    private val apiService: ApiService
) : ViewModel() {
    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles = _articles.asStateFlow()

    private val _brands = MutableStateFlow<List<Brand>>(emptyList())
    val brands = _brands.asStateFlow()

    private val _hotSearches = MutableStateFlow<List<String>>(emptyList())
    val hotSearches: StateFlow<List<String>> = _hotSearches.asStateFlow()

    private val _currentHotSearch = MutableStateFlow<String>("")
    val currentHotSearch: StateFlow<String> = _currentHotSearch.asStateFlow()

    init {
        loadArticles()
        loadBrands()
        loadHotSearches()
        startHotSearchRotation()
    }

    private fun loadArticles() {
        viewModelScope.launch {
            try {
                val currentLanguage = languageManager.getCurrentLanguageCode()
                val articles = articleRepository.getArticles(currentLanguage, 1)
                _articles.value = articles
            } catch (e: Exception) {
                // 处理错误
            }
        }
    }

    private fun loadBrands() {
        viewModelScope.launch {
            try {
                val result = brandRepository.getBrands(20, 0)
                _brands.value = result.items
            } catch (e: Exception) {
                // 处理错误
            }
        }
    }

    private fun loadHotSearches() {
        viewModelScope.launch {
            try {
                val response = apiService.getHotSearches()
                _hotSearches.value = response.data
                if (response.data.isNotEmpty()) {
                    _currentHotSearch.value = response.data[0]
                }
            } catch (e: Exception) {
                // 处理错误
            }
        }
    }

    private fun startHotSearchRotation() {
        viewModelScope.launch {
            while (true) {
                delay(5000) // 5秒延迟
                val currentList = _hotSearches.value
                if (currentList != null && currentList.isNotEmpty()) {
                    val currentIndex = currentList.indexOf(_currentHotSearch.value)
                    val nextIndex = (currentIndex + 1) % currentList.size
                    _currentHotSearch.value = currentList[nextIndex]
                }
            }
        }
    }
} 
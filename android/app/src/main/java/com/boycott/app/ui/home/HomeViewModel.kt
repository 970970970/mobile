package com.boycott.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boycott.app.data.api.ApiService
import com.boycott.app.data.model.Article
import com.boycott.app.data.model.Brand
import com.boycott.app.data.repository.ArticleRepository
import com.boycott.app.data.repository.BrandRepository
import com.boycott.app.data.repository.SearchTextRepository
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
    private val apiService: ApiService,
    private val searchTextRepository: SearchTextRepository
) : ViewModel() {
    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles = _articles.asStateFlow()

    private val _brands = MutableStateFlow<List<Brand>>(emptyList())
    val brands = _brands.asStateFlow()

    private val _hotSearches = MutableStateFlow<List<String>>(emptyList())
    val hotSearches: StateFlow<List<String>> = _hotSearches.asStateFlow()

    private val _currentHotSearch = MutableStateFlow<String>("")
    val currentHotSearch: StateFlow<String> = _currentHotSearch.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _hasMoreData = MutableStateFlow(true)
    val hasMoreData: StateFlow<Boolean> = _hasMoreData

    private var currentPage = 0
    private val pageSize = 20

    val searchText = searchTextRepository.searchText

    init {
        loadArticles()
        loadNextPage()
        loadHotSearches()
        startHotSearchRotation()
    }

    private fun getLanguageName(code: String): String {
        return when (code) {
            "zh-CN" -> "Chinese"
            "en-US" -> "English"
            else -> "English"
        }
    }

    private fun loadArticles() {
        viewModelScope.launch {
            try {
                val currentLanguageCode = languageManager.getCurrentLanguageCode()
                val languageName = getLanguageName(currentLanguageCode)
                val response = articleRepository.getArticles(languageName, 1)
                _articles.value = response.data.list
            } catch (e: Exception) {
                // 处理错误
                _articles.value = emptyList()
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
                if (currentList.isNotEmpty()) {
                    val currentIndex = currentList.indexOf(_currentHotSearch.value)
                    val nextIndex = (currentIndex + 1) % currentList.size
                    _currentHotSearch.value = currentList[nextIndex]
                }
            }
        }
    }

    fun updateSearchText(text: String) {
        searchTextRepository.updateSearchText(text)
    }
} 
package com.boycott.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boycott.app.data.model.Article
import com.boycott.app.data.model.Brand
import com.boycott.app.data.repository.ArticleRepository
import com.boycott.app.data.repository.BrandRepository
import com.boycott.app.utils.LanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    private val brandRepository: BrandRepository,
    private val languageManager: LanguageManager
) : ViewModel() {
    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles = _articles.asStateFlow()

    private val _brands = MutableStateFlow<List<Brand>>(emptyList())
    val brands = _brands.asStateFlow()

    init {
        loadArticles()
        loadBrands()
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
                val currentLanguage = languageManager.getCurrentLanguageCode()
                val result = brandRepository.getBrands(20, 0, currentLanguage)
                _brands.value = result
            } catch (e: Exception) {
                // 处理错误
            }
        }
    }
} 
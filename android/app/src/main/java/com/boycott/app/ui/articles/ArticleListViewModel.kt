package com.boycott.app.ui.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boycott.app.data.model.Article
import com.boycott.app.data.repository.ArticleRepository
import com.boycott.app.utils.LanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleListViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    private val languageManager: LanguageManager
) : ViewModel() {
    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles = _articles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var currentPage = 1
    private var hasMorePages = true

    init {
        loadArticles(refresh = true)
    }

    fun loadArticles(refresh: Boolean = false) {
        if (_isLoading.value || (!refresh && !hasMorePages)) return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                if (refresh) {
                    currentPage = 1
                    _articles.value = emptyList()
                    hasMorePages = true
                }

                val language = languageManager.getCurrentLanguageCode()
                val languageName = when (language) {
                    "zh-CN" -> "Chinese"
                    "en-US" -> "English"
                    "hi-IN" -> "Hindi"
                    "es-ES" -> "Spanish"
                    else -> "English"
                }

                val newArticles = articleRepository.getArticles(languageName, currentPage)
                val total = articleRepository.getArticleTotal(languageName)

                _articles.value = if (refresh) {
                    newArticles
                } else {
                    _articles.value + newArticles
                }

                hasMorePages = _articles.value.size < total
                if (hasMorePages) currentPage++
            } catch (e: Exception) {
                // 处理错误
            } finally {
                _isLoading.value = false
            }
        }
    }
} 
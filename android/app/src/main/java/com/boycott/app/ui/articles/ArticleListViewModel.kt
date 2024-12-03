package com.boycott.app.ui.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boycott.app.data.model.Article
import com.boycott.app.data.repository.ArticleRepository
import com.boycott.app.utils.LanguageManager
import com.boycott.app.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context

@HiltViewModel
class ArticleListViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    private val languageManager: LanguageManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasMoreData = MutableStateFlow(true)
    val hasMoreData: StateFlow<Boolean> = _hasMoreData.asStateFlow()

    private var currentPage = 1

    private fun getLanguageName(languageCode: String): String {
        return when (languageCode) {
            "zh" -> context.getString(R.string.language_chinese)
            "ja" -> context.getString(R.string.language_japanese)
            "ko" -> context.getString(R.string.language_korean)
            "ru" -> context.getString(R.string.language_russian)
            // 可以添加更多语言映射
            else -> context.getString(R.string.language_english)  // 默认使用英语
        }
    }

    fun loadNextPage() {
        if (_isLoading.value || !_hasMoreData.value) return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val currentLanguageCode = languageManager.getCurrentLanguageCode()
                val languageName = getLanguageName(currentLanguageCode)  // 转换语言代码为语言名称
                val response = articleRepository.getArticles(languageName, currentPage)
                
                // 更新文章列表
                val newArticles = response.data.list
                if (currentPage == 1) {
                    _articles.value = newArticles
                } else {
                    _articles.value = _articles.value + newArticles
                }

                // 更新分页状态
                _hasMoreData.value = newArticles.isNotEmpty() && _articles.value.size < response.data.total
                if (_hasMoreData.value) currentPage++
                
            } catch (e: Exception) {
                // 处理错误
            } finally {
                _isLoading.value = false
            }
        }
    }
} 
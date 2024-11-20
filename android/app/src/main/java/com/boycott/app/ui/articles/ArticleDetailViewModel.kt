package com.boycott.app.ui.articles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boycott.app.data.api.ApiService
import com.boycott.app.data.model.Article
import com.boycott.app.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    private val apiService: ApiService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val articleId: Int = checkNotNull(savedStateHandle["articleId"])
    
    private val _article = MutableStateFlow<Result<Article>>(Result.Loading)
    val article = _article.asStateFlow()

    init {
        loadArticle()
    }

    private fun loadArticle() {
        viewModelScope.launch {
            try {
                val response = apiService.getArticle(articleId)
                _article.value = Result.Success(response.data)
            } catch (e: Exception) {
                _article.value = Result.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun submitFeedback(
        type: String,
        content: String,
        contact: String?,
        images: List<String>?
    ) {
        viewModelScope.launch {
            try {
                // TODO: 实现提交反馈
            } catch (e: Exception) {
                // 处理错误
            }
        }
    }
} 
package com.boycott.app.ui.article

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boycott.app.data.model.Article
import com.boycott.app.data.repository.ArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    private val articleRepository: ArticleRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ArticleDetailUiState())
    val uiState: StateFlow<ArticleDetailUiState> = _uiState
    
    fun loadArticleContent(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val article = articleRepository.getArticleDetail(id)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        content = article.content,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }
    
    fun shareArticle(context: Context, article: Article) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, article.title)
            putExtra(Intent.EXTRA_TEXT, "${article.title}\n\n${article.summary ?: ""}\n\n阅读全文：${article.url}")
        }
        context.startActivity(Intent.createChooser(shareIntent, null))
    }
}

data class ArticleDetailUiState(
    val isLoading: Boolean = false,
    val content: String? = null,
    val error: String? = null
) 
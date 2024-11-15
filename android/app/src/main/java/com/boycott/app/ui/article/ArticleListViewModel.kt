package com.boycott.app.ui.article

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
class ArticleListViewModel @Inject constructor(
    private val articleRepository: ArticleRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ArticleListUiState())
    val uiState: StateFlow<ArticleListUiState> = _uiState
    
    private var currentPage = 1
    private val pageSize = 20
    
    init {
        loadArticles()
    }
    
    fun loadArticles() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val articles = articleRepository.getArticles("index", currentPage)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        articles = if (currentPage == 1) articles else it.articles + articles,
                        error = null
                    )
                }
                currentPage++
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
    
    fun loadMore() {
        if (!uiState.value.isLoading && !uiState.value.isLoadingMore) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoadingMore = true) }
                
                try {
                    val articles = articleRepository.getArticles("index", currentPage)
                    _uiState.update {
                        it.copy(
                            isLoadingMore = false,
                            articles = it.articles + articles,
                            error = null
                        )
                    }
                    currentPage++
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            isLoadingMore = false,
                            error = e.message
                        )
                    }
                }
            }
        }
    }
    
    fun refresh() {
        currentPage = 1
        loadArticles()
    }
}

data class ArticleListUiState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null
) 
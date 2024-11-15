package com.boycott.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boycott.app.data.model.Article
import com.boycott.app.data.model.Brand
import com.boycott.app.data.repository.ArticleRepository
import com.boycott.app.data.repository.BrandRepository
import com.boycott.app.data.repository.SearchHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val brandRepository: BrandRepository,
    private val articleRepository: ArticleRepository,
    private val searchRepository: SearchHistoryRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private var currentPage = 1
    
    init {
        loadInitialData()
    }
    
    private fun loadInitialData() {
        viewModelScope.launch {
            // 加载文章
            articleRepository.getArticles("index", 1)
                .collect { resource ->
                    _uiState.update { state ->
                        state.copy(articles = resource.getOrDefault(emptyList()))
                    }
                }
            
            // 加载品牌
            loadBrands()
        }
    }
    
    fun loadMoreBrands() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            
            brandRepository.getBrands(currentPage)
                .collect { resource ->
                    _uiState.update { state ->
                        val brands = resource.getOrNull()?.items ?: emptyList()
                        state.copy(
                            brands = state.brands + brands,
                            isLoadingMore = false,
                            hasReachedEnd = brands.isEmpty(),
                        )
                    }
                    if (resource.isSuccess) {
                        currentPage++
                    }
                }
        }
    }
}

data class HomeUiState(
    val articles: List<Article> = emptyList(),
    val brands: List<Brand> = emptyList(),
    val isLoadingMore: Boolean = false,
    val hasReachedEnd: Boolean = false,
    val currentSuggestion: String = ""
) 
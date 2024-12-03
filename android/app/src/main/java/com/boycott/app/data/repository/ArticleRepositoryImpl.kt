package com.boycott.app.data.repository

import com.boycott.app.data.api.ApiService
import com.boycott.app.data.model.ApiResponse
import com.boycott.app.data.model.ArticleListResponse
import javax.inject.Inject

class ArticleRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ArticleRepository {
    
    override suspend fun getArticles(
        module: String,
        language: String, 
        page: Int
    ): ApiResponse<ArticleListResponse> {
        return apiService.getArticles(module, language, page)
    }

    override suspend fun getArticleTotal(language: String): Int {
        return apiService.getArticles("article_list", language, 1).data.total
    }
} 
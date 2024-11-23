package com.boycott.app.data.repository

import com.boycott.app.data.api.ApiService
import com.boycott.app.data.model.ArticleListResponse
import com.boycott.app.data.model.ApiResponse
import javax.inject.Inject

class ArticleRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ArticleRepository {
    override suspend fun getArticles(language: String, page: Int): ApiResponse<ArticleListResponse> {
        return apiService.getArticles(language, page)
    }

    override suspend fun getArticleTotal(language: String): Int {
        return apiService.getArticles(language, 1).data.total
    }
} 
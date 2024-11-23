package com.boycott.app.data.repository

import com.boycott.app.data.model.ArticleListResponse
import com.boycott.app.data.model.ApiResponse

interface ArticleRepository {
    suspend fun getArticles(language: String, page: Int): ApiResponse<ArticleListResponse>
    suspend fun getArticleTotal(language: String): Int
} 
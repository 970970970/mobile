package com.boycott.app.data.repository

import com.boycott.app.data.model.Article

interface ArticleRepository {
    suspend fun getArticles(language: String, page: Int): List<Article>
    suspend fun getArticleTotal(language: String): Int
} 
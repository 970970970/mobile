package com.boycott.app.data.model

data class Article(
    val id: Int,
    val title: String,
    val summary: String?,
    val image: String?,
    val publishedAt: String?,
    val content: String? = null,
    val language: String? = null
)

data class ArticleListResponse(
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val list: List<Article>
) 
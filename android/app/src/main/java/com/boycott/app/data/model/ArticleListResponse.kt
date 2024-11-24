package com.boycott.app.data.model

data class ArticleListResponse(
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val list: List<Article>
) 
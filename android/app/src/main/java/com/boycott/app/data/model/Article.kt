package com.boycott.app.data.model

data class Article(
    val id: Int,
    val title: String,
    val content: String?,
    val summary: String?,
    val image: String?,
    val publishedAt: String?,
    val status: Int,
    val language: String
) 
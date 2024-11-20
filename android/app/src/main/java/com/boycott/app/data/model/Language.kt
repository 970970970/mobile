package com.boycott.app.data.model

data class Language(
    val id: Int,
    val code: String,
    val name: String,
    val flag: String,
    val status: Int
)

data class LanguageResponse(
    val items: List<Language>,
    val total: Int
) 
package com.boycott.app.data.model

data class Brand(
    val id: String,
    val name: String,
    val description: String?,
    val status: String?,
    val logoMediaId: Int?,
    val logoPath: String?,
    val reasons: List<String>?,
    val countries: List<String>?,
    val categories: List<String>?,
    val alternatives: List<String>?
) 
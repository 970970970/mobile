package com.boycott.app.data.model

data class HotSearch(
    val id: Int,
    val name: String,
    val count: Int
)

data class HotSearchResponse(
    val data: List<String>
) 
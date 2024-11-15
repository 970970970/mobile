package com.boycott.app.data.model

import com.boycott.app.utils.DateUtils
import com.google.gson.annotations.SerializedName

data class Article(
    val id: Int,
    val title: String,
    val content: String?,
    val summary: String?,
    val author: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    val url: String?,
    val module: String?,
    val language: String?
) {
    val formattedDate: String
        get() = DateUtils.formatToSimpleDate(createdAt)
    
    val relativeTime: String
        get() = DateUtils.formatRelativeTime(createdAt)
    
    val fullDate: String
        get() = DateUtils.formatToFullDate(createdAt)
}

data class ArticleListResponse(
    val total: Int,
    val items: List<Article>
) 
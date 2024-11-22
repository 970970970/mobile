package com.boycott.app.data.model

data class Brand(
    val id: Int,
    val name: String,
    val description: String?,
    val status: String?,
    val logo_media_id: Int?,
    val logo_path: String?
) 
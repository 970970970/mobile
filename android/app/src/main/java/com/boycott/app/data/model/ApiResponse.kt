package com.boycott.app.data.model

data class ApiResponse<T>(
    val status: Int,
    val msg: String,
    val data: T
) 
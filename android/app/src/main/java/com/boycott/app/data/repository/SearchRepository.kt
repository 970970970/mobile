package com.boycott.app.data.repository

import com.boycott.app.data.model.BrandsListResponse
import com.boycott.app.data.model.ApiResponse

interface SearchRepository {
    suspend fun searchBrands(keywords: String, limit: Int = 20, offset: Int = 0): ApiResponse<BrandsListResponse>
    suspend fun getHotSearches(): List<String>
} 
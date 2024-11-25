package com.boycott.app.data.repository

import com.boycott.app.data.model.Brand
import com.boycott.app.data.model.BrandsListResponse
import com.boycott.app.data.model.ApiResponse

interface BrandRepository {
    suspend fun getBrands(
        limit: Int,
        offset: Int,
        keywords: String = ""
    ): BrandsListResponse
    suspend fun getBrandDetail(id: String): Brand
    suspend fun getHotSearches(): List<String>
} 
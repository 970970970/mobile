package com.boycott.app.data.repository

import com.boycott.app.data.model.BrandsListResponse

interface BrandRepository {
    suspend fun getBrands(limit: Int, offset: Int): BrandsListResponse
    suspend fun getHotSearches(): List<String>
} 
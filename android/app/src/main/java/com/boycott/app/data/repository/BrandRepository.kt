package com.boycott.app.data.repository

import com.boycott.app.data.model.Brand
import com.boycott.app.data.model.BrandsListResponse

interface BrandRepository {
    suspend fun getBrands(limit: Int, offset: Int): BrandsListResponse
    suspend fun getBrandDetail(id: String): Brand
    suspend fun getHotSearches(): List<String>
} 
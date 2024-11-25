package com.boycott.app.data.repository

import com.boycott.app.data.api.ApiService
import com.boycott.app.data.model.Brand
import com.boycott.app.data.model.BrandsListResponse
import javax.inject.Inject

class BrandRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : BrandRepository {
    
    override suspend fun getBrands(limit: Int, offset: Int): BrandsListResponse {
        val response = apiService.getBrands(limit, offset)
        return response.data
    }

    override suspend fun getBrandDetail(id: String): Brand {
        val response = apiService.getBrandDetail(id)
        return response.data
    }

    override suspend fun getHotSearches(): List<String> {
        val response = apiService.getHotSearches()
        return response.data
    }
} 
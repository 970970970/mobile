package com.boycott.app.data.repository

import com.boycott.app.data.api.ApiService
import com.boycott.app.data.model.BrandsListResponse
import com.boycott.app.data.model.ApiResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : SearchRepository {
    
    override suspend fun searchBrands(keywords: String, limit: Int, offset: Int): ApiResponse<BrandsListResponse> {
        return apiService.searchBrands(keywords, limit, offset)
    }

    override suspend fun getHotSearches(): List<String> {
        val response = apiService.getHotSearches()
        return response.data
    }
} 
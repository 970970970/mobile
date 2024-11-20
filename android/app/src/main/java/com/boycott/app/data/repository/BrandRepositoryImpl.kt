package com.boycott.app.data.repository

import com.boycott.app.data.api.ApiService
import com.boycott.app.data.model.Brand
import javax.inject.Inject

class BrandRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : BrandRepository {
    override suspend fun getBrands(limit: Int, offset: Int, language: String): List<Brand> {
        return apiService.getBrands(limit, offset, language)
    }
} 
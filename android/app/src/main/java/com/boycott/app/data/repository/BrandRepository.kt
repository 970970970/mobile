package com.boycott.app.data.repository

import com.boycott.app.data.model.Brand

interface BrandRepository {
    suspend fun getBrands(limit: Int, offset: Int, language: String): List<Brand>
} 
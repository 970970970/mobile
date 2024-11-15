package com.boycott.app.data.repository

import com.boycott.app.data.model.Brand
import com.boycott.app.data.model.BrandListResponse
import com.boycott.app.data.network.ApiService
import com.boycott.app.data.network.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BrandRepository @Inject constructor(
    private val apiService: ApiService
) {
    fun getBrands(
        page: Int,
        pageSize: Int = 20,
        keyword: String = ""
    ): Flow<Resource<BrandListResponse>> = flow {
        emit(Resource.Loading())
        try {
            val offset = (page - 1) * pageSize
            val response = apiService.getBrands(keyword, pageSize, offset)
            if (response.status == 0) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.msg))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }
    
    suspend fun getBrandDetail(id: Int): Resource<Brand> {
        return try {
            val response = apiService.getBrandDetail(id)
            if (response.status == 0) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.msg)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
    
    suspend fun getHotSearches(): Resource<List<String>> {
        return try {
            val response = apiService.getHotSearches()
            if (response.status == 0) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.msg)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
} 
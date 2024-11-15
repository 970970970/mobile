package com.boycott.app.data.network

import com.boycott.app.data.model.Article
import com.boycott.app.data.model.ArticleListResponse
import com.boycott.app.data.model.Brand
import com.boycott.app.data.model.BrandListResponse
import retrofit2.http.*

interface ApiService {
    @GET("brands/list")
    suspend fun getBrands(
        @Query("keywords") keywords: String = "",
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): ApiResponse<BrandListResponse>
    
    @GET("brands/hot-searches")
    suspend fun getHotSearches(): ApiResponse<List<String>>
    
    @GET("brands/{id}")
    suspend fun getBrandDetail(
        @Path("id") id: Int
    ): ApiResponse<Brand>
    
    @GET("articles/list/{module}/{language}")
    suspend fun getArticles(
        @Path("module") module: String,
        @Path("language") language: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<ArticleListResponse>
    
    @GET("articles/{id}")
    suspend fun getArticleDetail(
        @Path("id") id: Int
    ): ApiResponse<Article>
    
    @GET("articles/mod/{type}/{language}")
    suspend fun getModuleArticle(
        @Path("type") type: String,
        @Path("language") language: String
    ): ApiResponse<Article>
}

data class ApiResponse<T>(
    val status: Int,
    val msg: String,
    val data: T
) 
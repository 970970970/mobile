package com.boycott.app.data.api

import com.boycott.app.data.model.Article
import com.boycott.app.data.model.ArticleListResponse
import com.boycott.app.data.model.Brand
import com.boycott.app.data.model.BrandsListResponse
import com.boycott.app.data.model.Language
import com.boycott.app.data.model.LanguageResponse
import com.boycott.app.data.model.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path

interface ApiService {
    @GET("articles/list/index/{language}")
    suspend fun getArticles(
        @Path("language") language: String,
        @Query("page") page: Int
    ): ApiResponse<ArticleListResponse>

    @GET("brands/list")
    suspend fun getBrands(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): ApiResponse<BrandsListResponse>

    @GET("languages")
    suspend fun getLanguages(@Query("limit") limit: Int = 100): ApiResponse<LanguageResponse>

    @GET("articles/mod/privacy_policy/{language}")
    suspend fun getPrivacyPolicy(@Path("language") language: String): ApiResponse<Article>

    @GET("articles/mod/user_agreement/{language}")
    suspend fun getUserAgreement(@Path("language") language: String): ApiResponse<Article>

    @GET("articles/{id}")
    suspend fun getArticle(@Path("id") id: Int): ApiResponse<Article>

    @GET("brands/hot-searches")
    suspend fun getHotSearches(): ApiResponse<List<String>>

    @GET("brands/list")
    suspend fun searchBrands(
        @Query("keywords") keywords: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): ApiResponse<BrandsListResponse>
} 
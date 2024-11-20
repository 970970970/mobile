package com.boycott.app.data.api

import com.boycott.app.data.model.Article
import com.boycott.app.data.model.ArticleListResponse
import com.boycott.app.data.model.Brand
import com.boycott.app.data.model.Language
import com.boycott.app.data.model.LanguageResponse
import com.boycott.app.data.model.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path

interface ApiService {
    @GET("v1/articles/list/index/{language}")
    suspend fun getArticles(
        @Path("language") language: String,
        @Query("page") page: Int
    ): ApiResponse<ArticleListResponse>

    @GET("v1/brands/list")
    suspend fun getBrands(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("language") language: String
    ): List<Brand>

    @GET("v1/languages")
    suspend fun getLanguages(@Query("limit") limit: Int = 100): ApiResponse<LanguageResponse>

    @GET("v1/articles/mod/privacy_policy/{language}")
    suspend fun getPrivacyPolicy(@Path("language") language: String): ApiResponse<Article>

    @GET("v1/articles/mod/user_agreement/{language}")
    suspend fun getUserAgreement(@Path("language") language: String): ApiResponse<Article>

    @GET("v1/articles/{id}")
    suspend fun getArticle(@Path("id") id: Int): ApiResponse<Article>
} 
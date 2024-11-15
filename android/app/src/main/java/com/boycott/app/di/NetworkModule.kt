package com.boycott.app.di

import com.boycott.app.data.network.ApiService
import com.boycott.app.data.repository.BrandRepository
import com.boycott.app.data.repository.SearchHistoryRepository
import com.boycott.app.data.local.dao.SearchHistoryDao
import com.boycott.app.data.repository.ArticleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "http://10.1.0.241:8787/v1/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideBrandRepository(apiService: ApiService): BrandRepository {
        return BrandRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideSearchHistoryRepository(searchHistoryDao: SearchHistoryDao): SearchHistoryRepository {
        return SearchHistoryRepository(searchHistoryDao)
    }

    @Provides
    @Singleton
    fun provideArticleRepository(apiService: ApiService): ArticleRepository {
        return ArticleRepository(apiService)
    }
} 
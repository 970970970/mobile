package com.boycott.app.di

import com.boycott.app.data.repository.ArticleRepository
import com.boycott.app.data.repository.ArticleRepositoryImpl
import com.boycott.app.data.repository.BrandRepository
import com.boycott.app.data.repository.BrandRepositoryImpl
import com.boycott.app.data.repository.SearchRepository
import com.boycott.app.data.repository.SearchRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindArticleRepository(
        articleRepositoryImpl: ArticleRepositoryImpl
    ): com.boycott.app.data.repository.ArticleRepository

    @Binds
    @Singleton
    abstract fun bindBrandRepository(
        brandRepositoryImpl: BrandRepositoryImpl
    ): BrandRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        searchRepositoryImpl: SearchRepositoryImpl
    ): SearchRepository
} 
package com.boycott.app.data.repository

import com.boycott.app.data.model.Article
import com.boycott.app.data.network.ApiService
import com.boycott.app.data.network.Resource
import com.boycott.app.utils.LocaleUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticleRepository @Inject constructor(
    private val apiService: ApiService
) {
    fun getArticles(
        module: String,
        page: Int,
        language: String = LocaleUtils.getStoredLanguage(null)
    ): Flow<Resource<List<Article>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getArticles(module, language, page)
            if (response.status == 0) {
                emit(Resource.Success(response.data.items))
            } else {
                emit(Resource.Error(response.msg))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }
    
    suspend fun getArticleDetail(id: Int): Resource<Article> {
        return try {
            val response = apiService.getArticleDetail(id)
            if (response.status == 0) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.msg)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
    
    suspend fun getModuleArticle(
        type: String,
        language: String = LocaleUtils.getStoredLanguage(null)
    ): Resource<Article> {
        return try {
            val response = apiService.getModuleArticle(type, language)
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
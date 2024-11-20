package com.boycott.app.utils

object AppConfig {
    private const val DEV_HOST = "http://10.1.0.241:8787"
    private const val PROD_HOST = "https://api.example.com" // 正式环境域名

    private val IS_DEBUG = true // 可以根据 BuildConfig.DEBUG 来判断

    val API_HOST = if (IS_DEBUG) "$DEV_HOST/" else "$PROD_HOST/"
    val MEDIA_HOST = if (IS_DEBUG) "$DEV_HOST/media" else "$PROD_HOST/media"
} 
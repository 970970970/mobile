package com.boycott.app

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale

@HiltAndroidApp
class BoycottApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initLanguage()
    }

    private fun initLanguage() {
        val savedLanguageCode = getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getString("language_code", null)
        
        if (savedLanguageCode != null) {
            val locale = when (savedLanguageCode) {
                "zh-CN" -> Locale.CHINESE
                "en-US" -> Locale.US
                "hi-IN" -> Locale("hi", "IN")
                "es-ES" -> Locale("es", "ES")
                else -> Locale.getDefault()
            }
            
            val config = resources.configuration
            config.setLocale(locale)
            createConfigurationContext(config)
        }
    }
} 
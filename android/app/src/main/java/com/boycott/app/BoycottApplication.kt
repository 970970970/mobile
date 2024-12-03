package com.boycott.app

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import com.boycott.app.utils.LanguageManager
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale
import javax.inject.Inject

@HiltAndroidApp
class BoycottApplication : Application() {
    
    @Inject
    lateinit var languageManager: LanguageManager

    override fun attachBaseContext(base: Context) {
        // 在创建 Application 之前初始化语言设置
        val languageCode = base.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getString("language_code", null)
        
        Log.d("BoycottApplication", "Initializing with language code: $languageCode")
        
        // 如果有已保存的语言设置，使用该语言创建 Context
        if (languageCode != null) {
            val newContext = updateBaseContextLocale(base, languageCode)
            super.attachBaseContext(newContext)
        } else {
            super.attachBaseContext(base)
        }
    }

    override fun onCreate() {
        super.onCreate()
        // Application 创建后初始化语言管理器
        languageManager.initialize()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d("BoycottApplication", "Configuration changed. New locale: ${newConfig.locales[0]}")
    }

    private fun updateBaseContextLocale(context: Context, languageCode: String): Context {
        val locale = when {
            languageCode.contains("-") -> {
                val (language, country) = languageCode.split("-")
                Locale(language, country)
            }
            else -> Locale(languageCode)
        }
        
        Log.d("BoycottApplication", "Updating base context with locale: $locale")
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }
}
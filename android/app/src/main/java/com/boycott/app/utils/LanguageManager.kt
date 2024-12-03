package com.boycott.app.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Singleton
class LanguageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val KEY_LANGUAGE_CODE = "language_code"
    private val KEY_LANGUAGE_NAME = "language_name"
    private val scope = MainScope()

    // 当前语言
    private var currentLanguage: Language? = null

    // 初始化语言管理器
    fun initialize() {
        val code = prefs.getString(KEY_LANGUAGE_CODE, null)
        val name = prefs.getString(KEY_LANGUAGE_NAME, null)
        if (code != null && name != null) {
            currentLanguage = Language(code, name, getLanguageFlag(code))
            updateConfiguration(code)
        }
    }

    // 获取当前语言
    fun getCurrentLanguage(): Language {
        if (currentLanguage == null) {
            // 如果没有设置语言，使用系统语言
            val systemLocale = context.resources.configuration.locales[0]
            val code = "${systemLocale.language}-${systemLocale.country}"
            val name = SUPPORTED_LANGUAGES[code] ?: "English"
            currentLanguage = Language(code, name, getLanguageFlag(code))
        }
        return currentLanguage!!
    }

    // 设置新的语言
    fun setLanguage(language: Language) {
        // 保存语言设置
        prefs.edit()
            .putString(KEY_LANGUAGE_CODE, language.code)
            .putString(KEY_LANGUAGE_NAME, language.name)
            .apply()

        // 更新当前语言
        currentLanguage = language

        // 更新应用的语言配置
        updateConfiguration(language.code)
    }

    // 更新应用的语言配置
    private fun updateConfiguration(languageCode: String) {
        val locale = when {
            languageCode.contains("-") -> {
                val (language, country) = languageCode.split("-")
                Log.d("LanguageManager", "Setting locale with language: $language, country: $country")
                Locale(language, country)
            }
            else -> {
                Log.d("LanguageManager", "Setting locale with language only: $languageCode")
                Locale(languageCode)
            }
        }
        
        // 记录当前系统 locale
        Log.d("LanguageManager", "Current system locale: ${Locale.getDefault()}")
        
        // 设置默认语言环境
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        
        // 记录配置信息
        Log.d("LanguageManager", "New configuration locale: ${config.locales[0]}")
        
        // 获取简化的语言代码（不包含地区）
        val simpleLanguageCode = locale.language
        Log.d("LanguageManager", "Simple language code for resources: $simpleLanguageCode")
        
        // 更新 Application 的配置
        context.applicationContext.resources.updateConfiguration(
            config,
            context.applicationContext.resources.displayMetrics
        )
        
        // 保存语言设置
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .edit()
            .putString("language_code", languageCode)
            .apply()

        // 通知语言变化，使用简化的语言代码
        scope.launch {
            LocaleEvent.notifyLocaleChanged(simpleLanguageCode)
        }
    }

    // 获取所有支持的语言列表
    fun getSupportedLanguages(): List<Language> {
        return SUPPORTED_LANGUAGES.map { (code, name) ->
            Language(code, name, getLanguageFlag(code))
        }
    }

    // 获取语言对应的国旗emoji
    private fun getLanguageFlag(code: String): String {
        return when (code) {
            "zh-CN" -> "🇨🇳"
            "en-US" -> "🇺🇸"
            "hi-IN" -> "🇮🇳"
            "es-ES" -> "🇪🇸"
            "fr-FR" -> "🇫🇷"
            "ar-SA" -> "🇸🇦"
            "ru-RU" -> "🇷🇺"
            "pt-BR" -> "🇧🇷"
            "de-DE" -> "🇩🇪"
            "ja-JP" -> "🇯🇵"
            "ko-KR" -> "🇰🇷"
            "it-IT" -> "🇮🇹"
            "tr-TR" -> "🇹🇷"
            "vi-VN" -> "🇻🇳"
            "th-TH" -> "🇹🇭"
            "bn-BD" -> "🇧🇩"
            "fa-IR" -> "🇮🇷"
            "ms-MY" -> "🇲🇾"
            "nl-NL" -> "🇳🇱"
            "ur-PK" -> "🇵🇰"
            else -> "🏳️"
        }
    }

    // 获取用于 API 的语言名称
    fun getApiLanguageName(): String {
        return when (getCurrentLanguage().code) {
            "zh-CN" -> "Chinese"
            "en-US" -> "English"
            "hi-IN" -> "Hindi"
            "es-ES" -> "Spanish"
            "fr-FR" -> "French"
            "ar-SA" -> "Arabic"
            "bn-BD" -> "Bengali"
            "ru-RU" -> "Russian"
            "pt-BR" -> "Portuguese"
            "id-ID" -> "Indonesian"
            "ur-PK" -> "Urdu"
            "de-DE" -> "German"
            "ja-JP" -> "Japanese"
            "tr-TR" -> "Turkish"
            "ko-KR" -> "Korean"
            "vi-VN" -> "Vietnamese"
            "it-IT" -> "Italian"
            "th-TH" -> "Thai"
            "fa-IR" -> "Persian"
            "nl-NL" -> "Dutch"
            "ms-MY" -> "Malaysian"
            else -> "English"  // 默认使用英语
        }
    }

    companion object {
        // 支持的语言列表
        private val SUPPORTED_LANGUAGES = mapOf(
            "zh-CN" to "中文",
            "en-US" to "English",
            "hi-IN" to "हिन्दी",
            "es-ES" to "Español",
            "fr-FR" to "Français",
            "ar-SA" to "العربية",
            "ru-RU" to "Русский",
            "pt-BR" to "Português",
            "de-DE" to "Deutsch",
            "ja-JP" to "日本語",
            "ko-KR" to "한국어",
            "it-IT" to "Italiano",
            "tr-TR" to "Türkçe",
            "vi-VN" to "Tiếng Việt",
            "th-TH" to "ไทย",
            "bn-BD" to "বাংলা",
            "fa-IR" to "فارسی",
            "ms-MY" to "Bahasa Melayu",
            "nl-NL" to "Nederlands",
            "ur-PK" to "اردو"
        )
    }
}

// 语言数据类
data class Language(
    val code: String,
    val name: String,
    val flag: String
)
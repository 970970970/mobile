package com.boycott.app.utils

import android.content.Context
import android.os.Build
import java.util.Locale
import com.boycott.app.model.LocaleInfo

object LanguageUtils {
    fun getSystemLanguage(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Locale.getDefault().language
        } else {
            Locale.getDefault().language
        }
    }

    fun setAppLanguage(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = context.resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }
        context.createConfigurationContext(config)
    }

    fun getSupportedLocales(): List<LocaleInfo> = listOf(
        LocaleInfo("en", "English", "English"),
        LocaleInfo("zh", "Chinese", "中文"),
        LocaleInfo("ja", "Japanese", "日本語"),
        LocaleInfo("ko", "Korean", "한국어"),
        LocaleInfo("ru", "Russian", "Русский")
    )

    fun getLocaleByCode(code: String): LocaleInfo {
        return getSupportedLocales().find { it.code == code }
            ?: LocaleInfo("en", "English", "English")
    }
} 
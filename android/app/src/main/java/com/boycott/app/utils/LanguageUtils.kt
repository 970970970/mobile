package com.boycott.app.utils

import android.content.Context
import android.os.Build
import java.util.Locale
import com.boycott.app.model.LocaleInfo
import com.boycott.app.R

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

    fun getSupportedLocales(context: Context): List<LocaleInfo> = listOf(
        LocaleInfo("en", "English", context.getString(R.string.language_english)),
        LocaleInfo("zh", "Chinese", context.getString(R.string.language_chinese)),
        LocaleInfo("ja", "Japanese", context.getString(R.string.language_japanese)),
        LocaleInfo("ko", "Korean", context.getString(R.string.language_korean)),
        LocaleInfo("ru", "Russian", context.getString(R.string.language_russian))
    )

    fun getLocaleByCode(code: String, context: Context): LocaleInfo {
        return getSupportedLocales(context).find { it.code == code }
            ?: LocaleInfo("en", "English", context.getString(R.string.language_english))
    }
} 
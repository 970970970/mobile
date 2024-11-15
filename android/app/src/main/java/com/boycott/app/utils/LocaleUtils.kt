package com.boycott.app.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LocaleUtils {
    private const val LANGUAGE_PREF = "language_pref"
    
    private val supportedLocales = listOf(
        Locale("en"),    // English
        Locale("zh"),    // Chinese
        Locale("hi"),    // Hindi
        Locale("es"),    // Spanish
        Locale("fr"),    // French
        Locale("ar"),    // Arabic
        Locale("bn"),    // Bengali
        Locale("ru"),    // Russian
        Locale("pt"),    // Portuguese
        Locale("id"),    // Indonesian
        Locale("ur"),    // Urdu
        Locale("de"),    // German
        Locale("ja"),    // Japanese
        Locale("tr"),    // Turkish
        Locale("ko"),    // Korean
        Locale("vi"),    // Vietnamese
        Locale("it"),    // Italian
        Locale("th"),    // Thai
        Locale("fa"),    // Persian
        Locale("nl"),    // Dutch
        Locale("ms")     // Malay
    )
    
    fun isRTL(locale: Locale): Boolean {
        return when (locale.language) {
            "ar", "fa", "ur" -> true
            else -> false
        }
    }
    
    fun getLocaleList(): List<LocaleInfo> {
        return supportedLocales.map { locale ->
            LocaleInfo(
                code = locale.language,
                displayName = locale.getDisplayName(locale).capitalize(locale),
                isRTL = isRTL(locale)
            )
        }
    }
    
    fun applyLanguage(context: Context, languageCode: String) {
        val locale = supportedLocales.find { it.language == languageCode } ?: Locale.getDefault()
        updateResources(context, locale)
        saveLanguagePreference(context, languageCode)
    }
    
    private fun updateResources(context: Context, locale: Locale) {
        Locale.setDefault(locale)
        
        val configuration = Configuration(context.resources.configuration).apply {
            setLocale(locale)
        }
        
        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(
            configuration,
            context.resources.displayMetrics
        )
    }
    
    private fun saveLanguagePreference(context: Context, languageCode: String) {
        context.getSharedPreferences(LANGUAGE_PREF, Context.MODE_PRIVATE)
            .edit()
            .putString("selected_language", languageCode)
            .apply()
    }
    
    fun getStoredLanguage(context: Context): String {
        return context.getSharedPreferences(LANGUAGE_PREF, Context.MODE_PRIVATE)
            .getString("selected_language", Locale.getDefault().language) ?: "en"
    }
}

data class LocaleInfo(
    val code: String,
    val displayName: String,
    val isRTL: Boolean
) 
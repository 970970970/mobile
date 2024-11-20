package com.boycott.app.utils

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import java.util.Locale

object LocaleUtils {
    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val config = context.resources.configuration
        config.setLocale(locale)
        context.createConfigurationContext(config)
    }

    fun wrap(context: Context, language: String): ContextWrapper {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = context.resources.configuration
        config.setLocale(locale)

        return ContextWrapper(context.createConfigurationContext(config))
    }
} 
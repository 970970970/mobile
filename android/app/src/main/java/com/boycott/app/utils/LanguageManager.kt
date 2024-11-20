package com.boycott.app.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getCurrentLanguageCode(): String {
        return context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getString("language_code", "zh-CN") ?: "zh-CN"
    }

    fun setLanguageCode(code: String) {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .edit()
            .putString("language_code", code)
            .apply()
    }
} 
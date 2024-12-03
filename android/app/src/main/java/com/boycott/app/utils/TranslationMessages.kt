package com.boycott.app.utils

import android.content.Context
import com.boycott.app.R

object TranslationMessages {
    fun getTranslationWarning(context: Context, language: String): String {
        return when (language.lowercase()) {
            "chinese" -> context.getString(R.string.translation_warning_zh)
            "hindi" -> context.getString(R.string.translation_warning_hi)
            "spanish" -> context.getString(R.string.translation_warning_es)
            else -> context.getString(R.string.translation_warning_en)
        }
    }
}
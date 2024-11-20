package com.boycott.app.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object LocaleEvent {
    private val _localeChanged = MutableSharedFlow<String>()
    val localeChanged = _localeChanged.asSharedFlow()

    suspend fun notifyLocaleChanged(languageCode: String) {
        _localeChanged.emit(languageCode)
    }
} 
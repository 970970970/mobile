package com.boycott.app.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object LocaleEvent {
    private val _localeFlow = MutableSharedFlow<String>()
    val localeFlow = _localeFlow.asSharedFlow()

    suspend fun notifyLocaleChanged(languageCode: String) {
        _localeFlow.emit(languageCode)
    }
}
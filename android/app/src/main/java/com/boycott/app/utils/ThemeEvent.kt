package com.boycott.app.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object ThemeEvent {
    private val _themeChanged = MutableSharedFlow<Boolean>()
    val themeChanged = _themeChanged.asSharedFlow()

    suspend fun notifyThemeChanged(isDarkMode: Boolean) {
        _themeChanged.emit(isDarkMode)
    }
} 
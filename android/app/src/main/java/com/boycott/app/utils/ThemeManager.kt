package com.boycott.app.utils

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(
    context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val KEY_DARK_MODE = "dark_mode"

    private val _themeFlow = MutableSharedFlow<Boolean>(replay = 1)
    val themeFlow = _themeFlow.asSharedFlow()

    init {
        // 初始化主题状态
        val isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false)
        _themeFlow.tryEmit(isDarkMode)
    }

    fun isDarkMode(): Boolean {
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }

    suspend fun setDarkMode(enabled: Boolean) {
        prefs.edit()
            .putBoolean(KEY_DARK_MODE, enabled)
            .apply()
        _themeFlow.emit(enabled)
    }
}

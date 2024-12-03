package com.boycott.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.boycott.app.utils.ThemeEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    suspend fun isDarkMode(): Boolean = withContext(Dispatchers.IO) {
        prefs.getBoolean("dark_mode", false)
    }

    suspend fun setDarkMode(enabled: Boolean) = withContext(Dispatchers.IO) {
        prefs.edit().putBoolean("dark_mode", enabled).apply()
        // 立即通知主题变化
        ThemeEvent.notifyThemeChanged(enabled)
    }
}

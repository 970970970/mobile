package com.boycott.app.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boycott.app.data.repository.SettingsRepository
import com.boycott.app.utils.CacheManager
import com.boycott.app.utils.Language
import com.boycott.app.utils.LanguageManager
import com.boycott.app.utils.LocaleEvent
import com.boycott.app.utils.ThemeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val languageManager: LanguageManager,
    private val cacheManager: CacheManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    private val _cacheSize = MutableStateFlow("0 MB")
    val cacheSize: StateFlow<String> = _cacheSize

    init {
        viewModelScope.launch {
            _isDarkMode.value = settingsRepository.isDarkMode()
            updateCacheSize()
        }
    }

    // 获取当前语言
    fun getCurrentLanguage(): Language {
        return languageManager.getCurrentLanguage()
    }

    // 获取支持的语言列表
    fun getSupportedLanguages(): List<Language> {
        return languageManager.getSupportedLanguages()
    }

    // 更新语言设置
    fun updateLanguage(language: Language) {
        viewModelScope.launch {
            languageManager.setLanguage(language)
            LocaleEvent.notifyLocaleChanged(language.code)
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            try {
                settingsRepository.setDarkMode(enabled)
                _isDarkMode.value = enabled
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun updateCacheSize() {
        viewModelScope.launch {
            _cacheSize.value = cacheManager.getCacheSize()
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            cacheManager.clearCache()
            updateCacheSize()
        }
    }

    fun getAppVersion(): String {
        return cacheManager.getAppVersion()
    }

    fun openCommunity() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://community.boycott.app"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun openDonateArticle() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://boycott.app/donate"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
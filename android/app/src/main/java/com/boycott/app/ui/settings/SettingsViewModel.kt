package com.boycott.app.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boycott.app.utils.LocaleUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            // TODO: 从 DataStore 加载设置
        }
    }
    
    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDarkMode = enabled) }
            // TODO: 保存到 DataStore
        }
    }
    
    fun clearCache(context: Context) {
        viewModelScope.launch {
            context.cacheDir.deleteRecursively()
            calculateCacheSize(context)
        }
    }
    
    private fun calculateCacheSize(context: Context) {
        viewModelScope.launch {
            val size = getFolderSize(context.cacheDir)
            _uiState.update { it.copy(cacheSize = formatSize(size)) }
        }
    }
    
    fun openTelegramGroup() {
        // TODO: 打开 Telegram 群组
    }
    
    fun showDonationOptions() {
        // TODO: 显示捐赠选项
    }
    
    fun showPrivacyPolicy() {
        // TODO: 显示隐私政策
    }
    
    fun showTermsOfService() {
        // TODO: 显示用户协议
    }
    
    private fun getFolderSize(folder: File): Long {
        var size: Long = 0
        folder.listFiles()?.forEach { file ->
            size += if (file.isDirectory) {
                getFolderSize(file)
            } else {
                file.length()
            }
        }
        return size
    }
    
    private fun formatSize(size: Long): String {
        val kb = size / 1024.0
        val mb = kb / 1024.0
        return when {
            mb >= 1 -> String.format("%.1f MB", mb)
            kb >= 1 -> String.format("%.1f KB", kb)
            else -> String.format("%d B", size)
        }
    }
}

data class SettingsUiState(
    val currentLanguage: String = LocaleUtils.getStoredLanguage(null),
    val isDarkMode: Boolean = false,
    val cacheSize: String = "计算中...",
    val appVersion: String = "1.0.0"
) 
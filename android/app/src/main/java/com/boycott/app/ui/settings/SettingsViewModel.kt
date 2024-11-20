package com.boycott.app.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boycott.app.data.api.ApiService
import com.boycott.app.data.model.Language
import com.boycott.app.utils.LocaleEvent
import com.boycott.app.utils.ThemeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _languages = MutableStateFlow<List<Language>>(emptyList())
    val languages = _languages.asStateFlow()

    private val _currentLanguage = MutableStateFlow("")
    val currentLanguage = _currentLanguage.asStateFlow()

    private val _isDarkMode = MutableStateFlow(
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getBoolean("dark_mode", false)
    )
    val isDarkMode = _isDarkMode.asStateFlow()

    private val _cacheSize = MutableStateFlow(0L)
    val cacheSize = _cacheSize.asStateFlow()

    init {
        loadLanguages()
        // 从 SharedPreferences 获取保存的语言设置
        val savedLanguageCode = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getString("language_code", null)
        
        viewModelScope.launch {
            try {
                val response = apiService.getLanguages()
                _languages.value = response.data.items
                
                // 根据保存的语言代码或系统默认语言设置当前语言显示
                val currentCode = savedLanguageCode ?: when (Locale.getDefault().language) {
                    "zh" -> "zh-CN"
                    "en" -> "en-US"
                    // ... 添加其他语言映射
                    else -> "en-US" // 默认使用英语
                }
                
                val language = response.data.items.find { it.code == currentCode }
                _currentLanguage.value = language?.name ?: "English"
            } catch (e: Exception) {
                // 处理错误
            }
        }
    }

    private fun loadLanguages() {
        viewModelScope.launch {
            try {
                val response = apiService.getLanguages()
                _languages.value = response.data.items
            } catch (e: Exception) {
                // 处理错误
            }
        }
    }

    fun setLanguage(code: String) {
        viewModelScope.launch {
            try {
                // 保存语言设置
                context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                    .edit()
                    .putString("language_code", code)
                    .apply()
                
                // 更新当前语言显示
                val language = _languages.value.find { it.code == code }
                _currentLanguage.value = language?.name ?: "English"
                
                // 通知语言变更事件
                LocaleEvent.notifyLocaleChanged(code)
            } catch (e: Exception) {
                // 处理错误
            }
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            try {
                // 保存深色模式设置
                context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("dark_mode", enabled)
                    .apply()
                
                _isDarkMode.value = enabled
                
                // 通知主题变更
                ThemeEvent.notifyThemeChanged(enabled)
            } catch (e: Exception) {
                // 处理错误
            }
        }
    }

    fun clearCache(context: Context) {
        viewModelScope.launch {
            try {
                // TODO: 实现清除缓存
            } catch (e: Exception) {
                // 处理错误
            }
        }
    }

    fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "未知版本"
        }
    }

    fun openCommunity() {
        // TODO: 实现打开社区讨论
    }

    fun openDonateArticle() {
        // 实现打开支持我们的页面逻辑
        // 例如，使用 Intent 打开一个网页
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://support-us.example.com"))
        context.startActivity(intent)
    }

    fun openPrivacyPolicy(onNavigate: () -> Unit) {
        onNavigate()
    }

    fun openUserAgreement() {
        // TODO: 实现打开用户协议
    }

    fun submitFeedback(
        type: String,
        content: String,
        contact: String?,
        images: List<String>?
    ) {
        viewModelScope.launch {
            try {
                // TODO: 实现提交反馈
            } catch (e: Exception) {
                // 处理错误
            }
        }
    }
} 
package com.boycott.app

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@HiltAndroidApp
class BoycottApplication : Application() {
    lateinit var imageLoader: ImageLoader
        private set
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化语言设置
        val storedLanguage = LocaleUtils.getStoredLanguage(this)
        LocaleUtils.applyLanguage(this, storedLanguage)
        
        // 初始化图片加载器
        imageLoader = ImageUtils.createImageLoader(this)
        
        // 创建通知通道
        NotificationUtils.createNotificationChannel(this)
        
        // 注册网络状态监听
        lifecycleScope.launch {
            NetworkUtils.observeNetworkState(this@BoycottApplication).collect { isConnected ->
                // TODO: 处理网络状态变化
            }
        }
    }
    
    override fun attachBaseContext(base: Context) {
        val storedLanguage = LocaleUtils.getStoredLanguage(base)
        val locale = Locale(storedLanguage)
        val context = ContextWrapper.wrap(base, locale)
        super.attachBaseContext(context)
    }
} 
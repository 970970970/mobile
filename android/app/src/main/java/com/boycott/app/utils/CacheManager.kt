package com.boycott.app.utils

import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CacheManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun clearCache() {
        withContext(Dispatchers.IO) {
            try {
                context.cacheDir.deleteRecursively()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getCacheSize(): String {
        return withContext(Dispatchers.IO) {
            try {
                val size = getDirSize(context.cacheDir)
                formatSize(size)
            } catch (e: Exception) {
                "0 MB"
            }
        }
    }

    private fun getDirSize(dir: File): Long {
        var size: Long = 0
        dir.listFiles()?.forEach { file ->
            size += if (file.isDirectory) {
                getDirSize(file)
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

    fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }
}

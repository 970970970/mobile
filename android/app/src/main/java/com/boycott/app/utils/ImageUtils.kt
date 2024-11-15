package com.boycott.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import java.io.File

object ImageUtils {
    fun createImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(File(context.cacheDir, "image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .crossfade(true)
            .build()
    }
    
    @Composable
    fun rememberImageRequest(
        url: String,
        cachePolicy: CachePolicy = CachePolicy.ENABLED
    ): ImageRequest {
        val context = LocalContext.current
        return remember(url) {
            ImageRequest.Builder(context)
                .data(url)
                .memoryCachePolicy(cachePolicy)
                .diskCachePolicy(cachePolicy)
                .crossfade(true)
                .build()
        }
    }
    
    fun getMediaUrl(path: String?): String? {
        return path?.let { "${AppConfig.MEDIA_HOST}/$it" }
    }
    
    fun clearImageCache(context: Context) {
        val imageLoader = createImageLoader(context)
        imageLoader.memoryCache?.clear()
        imageLoader.diskCache?.clear()
    }
} 
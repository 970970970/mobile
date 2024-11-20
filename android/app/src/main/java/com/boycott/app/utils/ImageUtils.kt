package com.boycott.app.utils

import android.content.Context
import coil.ImageLoader
import coil.request.CachePolicy

object ImageUtils {
    fun createImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
    }
} 
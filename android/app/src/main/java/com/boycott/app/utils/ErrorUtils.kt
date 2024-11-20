package com.boycott.app.utils

import android.content.Context
import com.boycott.app.R
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorUtils {
    fun handleError(throwable: Throwable): String {
        return when (throwable) {
            is IOException -> "Network error occurred"
            is HttpException -> {
                when (throwable.code()) {
                    404 -> "Resource not found"
                    500 -> "Server error"
                    else -> "Unknown error occurred"
                }
            }
            else -> throwable.message ?: "Unknown error occurred"
        }
    }

    fun getErrorDrawable(throwable: Throwable): Int {
        return when (throwable) {
            is HttpException -> R.drawable.ic_error_server
            is UnknownHostException -> R.drawable.ic_error_network
            is SocketTimeoutException -> R.drawable.ic_error_timeout
            else -> R.drawable.ic_error_unknown
        }
    }
} 
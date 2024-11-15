package com.boycott.app.utils

import android.content.Context
import com.boycott.app.R
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorUtils {
    fun getErrorMessage(context: Context, throwable: Throwable): String {
        return when (throwable) {
            is HttpException -> {
                when (throwable.code()) {
                    404 -> context.getString(R.string.error_not_found)
                    500 -> context.getString(R.string.error_server)
                    else -> context.getString(R.string.error_unknown)
                }
            }
            is UnknownHostException -> context.getString(R.string.error_network)
            is SocketTimeoutException -> context.getString(R.string.error_timeout)
            is IOException -> context.getString(R.string.error_connection)
            else -> throwable.message ?: context.getString(R.string.error_unknown)
        }
    }
    
    fun getErrorIcon(throwable: Throwable): Int {
        return when (throwable) {
            is HttpException -> R.drawable.ic_error_server
            is UnknownHostException -> R.drawable.ic_error_network
            is SocketTimeoutException -> R.drawable.ic_error_timeout
            is IOException -> R.drawable.ic_error_connection
            else -> R.drawable.ic_error_unknown
        }
    }
} 
package com.boycott.app.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    private val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val fullOutputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    
    fun formatDate(dateString: String?, pattern: String = "yyyy-MM-dd"): String {
        return try {
            dateString?.let { date ->
                val outputFormatter = SimpleDateFormat(pattern, Locale.getDefault())
                outputFormatter.format(inputFormat.parse(date)!!)
            } ?: ""
        } catch (e: Exception) {
            dateString ?: ""
        }
    }
    
    fun formatToSimpleDate(dateString: String?): String {
        return try {
            dateString?.let { date ->
                outputFormat.format(inputFormat.parse(date)!!)
            } ?: ""
        } catch (e: Exception) {
            dateString ?: ""
        }
    }
    
    fun formatToFullDate(dateString: String?): String {
        return try {
            dateString?.let { date ->
                fullOutputFormat.format(inputFormat.parse(date)!!)
            } ?: ""
        } catch (e: Exception) {
            dateString ?: ""
        }
    }
    
    fun formatRelativeTime(dateString: String?): String {
        return try {
            dateString?.let { date ->
                val time = inputFormat.parse(date)!!.time
                val now = System.currentTimeMillis()
                val diff = now - time
                
                when {
                    diff < 60 * 1000 -> "刚刚"
                    diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}分钟前"
                    diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}小时前"
                    diff < 30 * 24 * 60 * 60 * 1000L -> "${diff / (24 * 60 * 60 * 1000)}天前"
                    else -> formatToSimpleDate(date)
                }
            } ?: ""
        } catch (e: Exception) {
            dateString ?: ""
        }
    }
} 
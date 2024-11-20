package com.boycott.app.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    fun formatDate(date: String): String {
        return try {
            val parsedDate = dateFormat.parse(date)
            dateFormat.format(parsedDate ?: Date())
        } catch (e: Exception) {
            date
        }
    }
} 
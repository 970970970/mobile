package com.boycott.app.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
    
    fun getSearchHistory(): List<String> {
        return prefs.getStringSet("history", setOf())?.toList() ?: emptyList()
    }
    
    fun addSearchHistory(query: String) {
        val history = getSearchHistory().toMutableSet()
        history.add(query)
        prefs.edit().putStringSet("history", history).apply()
    }
    
    fun clearSearchHistory() {
        prefs.edit().remove("history").apply()
    }
} 
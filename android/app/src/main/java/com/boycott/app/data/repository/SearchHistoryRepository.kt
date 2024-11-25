package com.boycott.app.data.repository

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
    private val maxHistorySize = 20
    private val historyKey = "search_history"

    fun addSearchHistory(query: String) {
        if (query.isBlank()) return
        
        val history = getSearchHistory().toMutableList()
        Log.d("SearchDebug", "Current history before adding: $history")
        
        history.remove(query)
        history.add(0, query)
        while (history.size > maxHistorySize) {
            history.removeLast()
        }
        
        Log.d("SearchDebug", "Saving new history: $history")
        prefs.edit()
            .putStringSet(historyKey, history.toSet())
            .apply()
    }

    fun getSearchHistory(): List<String> {
        val history = prefs.getStringSet(historyKey, emptySet())
            ?.toList()
            ?.sortedByDescending { it }
            ?: emptyList()
        Log.d("SearchDebug", "Getting search history: $history")
        return history
    }

    fun removeFromHistory(query: String) {
        Log.d("SearchDebug", "Removing query from history: $query")
        val history = getSearchHistory().toMutableList()
        history.remove(query)
        prefs.edit()
            .putStringSet(historyKey, history.toSet())
            .apply()
    }

    fun clearSearchHistory() {
        Log.d("SearchDebug", "Clearing all search history")
        prefs.edit()
            .remove(historyKey)
            .apply()
    }
} 
package com.boycott.app.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchTextRepository @Inject constructor() {
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    fun updateSearchText(text: String) {
        _searchText.value = text
    }
} 
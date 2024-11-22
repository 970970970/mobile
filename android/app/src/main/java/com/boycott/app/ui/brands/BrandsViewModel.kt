package com.boycott.app.ui.brands

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boycott.app.data.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrandsViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {
    
    private val _currentHotSearch = MutableStateFlow("")
    val currentHotSearch: StateFlow<String> = _currentHotSearch

    init {
        loadHotSearches()
    }

    private fun loadHotSearches() {
        viewModelScope.launch {
            try {
                val hotSearches = searchRepository.getHotSearches()
                if (hotSearches.isNotEmpty()) {
                    _currentHotSearch.value = hotSearches[0]
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
} 
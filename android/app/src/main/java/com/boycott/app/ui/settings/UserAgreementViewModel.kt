package com.boycott.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boycott.app.data.api.ApiService
import com.boycott.app.utils.LanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserAgreementViewModel @Inject constructor(
    private val apiService: ApiService,
    private val languageManager: LanguageManager
) : ViewModel() {
    private val _content = MutableStateFlow<Result<String>>(Result.Loading)
    val content = _content.asStateFlow()

    init {
        loadUserAgreement()
    }

    private fun loadUserAgreement() {
        viewModelScope.launch {
            try {
                val content = apiService.getUserAgreement(languageManager.getCurrentLanguage().code)
                _content.value = Result.Success(content.data.content ?: "")
            } catch (e: Exception) {
                _content.value = Result.Error(e.message ?: "Unknown error")
            }
        }
    }
} 
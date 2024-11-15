package com.boycott.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(FeedbackUiState())
    val uiState: StateFlow<FeedbackUiState> = _uiState
    
    fun submitFeedback(content: String, contact: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            
            try {
                // TODO: 调用API提交反馈
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        submitResult = FeedbackResult.Success
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        submitResult = FeedbackResult.Error(e.message ?: "Unknown error")
                    )
                }
            }
        }
    }
}

data class FeedbackUiState(
    val isSubmitting: Boolean = false,
    val submitResult: FeedbackResult? = null
)

sealed class FeedbackResult {
    object Success : FeedbackResult()
    data class Error(val message: String) : FeedbackResult()
} 
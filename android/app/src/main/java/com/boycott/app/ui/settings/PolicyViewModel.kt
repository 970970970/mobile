package com.boycott.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boycott.app.data.repository.ArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PolicyViewModel @Inject constructor(
    private val articleRepository: ArticleRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PolicyUiState())
    val uiState: StateFlow<PolicyUiState> = _uiState
    
    fun loadPolicy(type: PolicyType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val article = when (type) {
                    PolicyType.PRIVACY -> articleRepository.getPrivacyPolicy()
                    PolicyType.TERMS -> articleRepository.getTermsOfService()
                }
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        content = article.content,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }
}

data class PolicyUiState(
    val isLoading: Boolean = false,
    val content: String? = null,
    val error: String? = null
)

enum class PolicyType {
    PRIVACY,
    TERMS
} 
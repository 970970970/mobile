package com.boycott.app.ui.brand

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.boycott.app.data.api.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BrandDetailViewModel @Inject constructor(
    private val apiService: ApiService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val brandId: String = checkNotNull(savedStateHandle["brandId"])
} 
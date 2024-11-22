package com.boycott.app.ui.brands

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.boycott.app.ui.components.SearchBar

@Composable
fun BrandsView(
    onNavigateToSearchHistory: () -> Unit,
    viewModel: BrandsViewModel = hiltViewModel()
) {
    val currentHotSearch by viewModel.currentHotSearch.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            hotSearchText = currentHotSearch,
            onSearchClick = onNavigateToSearchHistory
        )
    }
} 
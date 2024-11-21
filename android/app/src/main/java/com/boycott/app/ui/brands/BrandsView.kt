package com.boycott.app.ui.brands

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandsView(
    viewModel: BrandsViewModel = hiltViewModel()
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("品牌") }
        )
        // TODO: 实现品牌列表
    }
} 
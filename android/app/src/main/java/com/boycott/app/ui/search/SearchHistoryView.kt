package com.boycott.app.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.boycott.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchHistoryView(
    searchHistory: List<String>,
    onSearch: (String) -> Unit,
    onBack: () -> Unit,
    onClearHistory: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("搜索历史") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            },
            actions = {
                if (searchHistory.isNotEmpty()) {
                    IconButton(onClick = onClearHistory) {
                        Icon(Icons.Default.Delete, contentDescription = "清除历史")
                    }
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(searchHistory) { searchTerm ->
                ListItem(
                    headlineContent = { Text(searchTerm) },
                    leadingContent = {
                        Icon(Icons.Default.History, contentDescription = null)
                    },
                    modifier = Modifier.clickable { onSearch(searchTerm) }
                )
            }
        }
    }
} 
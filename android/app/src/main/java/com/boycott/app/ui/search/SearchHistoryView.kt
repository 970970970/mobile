package com.boycott.app.ui.search

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.boycott.app.data.model.Brand
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape

@Composable
private fun SearchHistoryChip(
    text: String,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.wrapContentWidth()
    ) {
        SuggestionChip(
            onClick = onClick,
            label = { 
                Text(
                    text = text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 8.dp, start = 8.dp)
                )
            }
        )

        // 删除按钮
        Surface(
            color = MaterialTheme.colorScheme.error,
            shape = CircleShape,
            modifier = Modifier
                .size(16.dp)
                .offset(x = 4.dp, y = (-4).dp)
                .align(Alignment.TopEnd)
                .clickable(onClick = onDelete)
        ) {
            Icon(
                Icons.Default.Clear,
                contentDescription = "删除",
                modifier = Modifier
                    .padding(2.dp)
                    .size(12.dp),
                tint = Color.White
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchHistoryView(
    initialQuery: String,
    onSearch: (String) -> Unit,
    onBack: () -> Unit,
    onBrandClick: (String) -> Unit,
    viewModel: SearchHistoryViewModel = hiltViewModel()
) {
    var searchText by remember { mutableStateOf(initialQuery) }
    val searchHistory by viewModel.searchHistory.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("搜索") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 搜索框
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { 
                        searchText = it
                        if (it.isNotEmpty()) {
                            viewModel.searchBrands(it)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("搜索品牌") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = { searchText = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "清除")
                            }
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (searchText.isNotEmpty()) {
                            viewModel.addToHistory(searchText)
                            onSearch(searchText)
                        }
                    }
                ) {
                    Text("搜索")
                }
            }

            // 搜索结果（如果有）
            if (searchText.isNotEmpty() && searchResults.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(searchResults) { brand ->
                        ListItem(
                            headlineContent = { Text(brand.name) },
                            supportingContent = { 
                                Text(
                                    text = when(brand.status) {
                                        "avoid" -> "抵制"
                                        "support" -> "支持"
                                        "neutral" -> "中立"
                                        else -> brand.status ?: ""
                                    },
                                    color = when(brand.status) {
                                        "avoid" -> MaterialTheme.colorScheme.error
                                        "support" -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onBrandClick(brand.id.toString()) }
                        )
                    }
                }
            }

            // 搜索历史显示
            if (searchHistory.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "搜索历史",
                        style = MaterialTheme.typography.titleMedium
                    )
                    TextButton(onClick = { viewModel.clearHistory() }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "清空历史",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("清空")
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 100.dp),  // 增加最小宽度
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(searchHistory) { historyItem ->
                        SearchHistoryChip(
                            text = historyItem,
                            onDelete = { viewModel.removeFromHistory(historyItem) },
                            onClick = {
                                searchText = historyItem
                                viewModel.addToHistory(historyItem)
                                onSearch(historyItem)
                            }
                        )
                    }
                }
            }
        }
    }
} 
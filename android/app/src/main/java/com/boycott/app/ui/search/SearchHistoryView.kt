package com.boycott.app.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.foundation.layout.Arrangement
import androidx.hilt.navigation.compose.hiltViewModel
import com.boycott.app.R
import android.util.Log
import com.boycott.app.data.model.Brand
import kotlin.math.max

@Composable
private fun SearchHistoryChip(
    text: String,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val deleteText = stringResource(R.string.action_delete)
    
    Box(
        modifier = Modifier.wrapContentWidth()
    ) {
        SuggestionChip(
            onClick = onClick,
            label = { 
                androidx.compose.material3.Text(
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
                contentDescription = deleteText,
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
    onBrandClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: SearchHistoryViewModel = hiltViewModel()
) {
    // 预先获取所有字符串资源
    val searchTitle = stringResource(R.string.search_title)
    val backText = stringResource(R.string.action_back)
    val searchHint = stringResource(R.string.search_hint)
    val clearText = stringResource(R.string.action_clear)
    val searchButtonText = stringResource(R.string.search_button)
    val historyTitle = stringResource(R.string.search_history)
    val clearHistoryText = stringResource(R.string.clear_history)
    val avoidText = stringResource(R.string.status_avoid)
    val supportText = stringResource(R.string.status_support)
    val neutralText = stringResource(R.string.status_neutral)

    var searchText by remember { mutableStateOf(initialQuery) }
    val searchResults by viewModel.searchResults.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = searchTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = backText)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 搜索栏
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { 
                        searchText = it
                        viewModel.searchBrands(it)
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(text = searchHint) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(
                                onClick = { 
                                    searchText = ""
                                    viewModel.searchBrands("")
                                }
                            ) {
                                Icon(Icons.Default.Clear, contentDescription = clearText)
                            }
                        }
                    }
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = { 
                        onSearch(searchText)
                        viewModel.addToHistory(searchText)
                    }
                ) {
                    Text(text = searchButtonText)
                }
            }

            // 搜索结果或历史记录
            if (searchText.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(searchResults) { brand ->
                        ListItem(
                            headlineContent = { Text(text = brand.name) },
                            supportingContent = { 
                                Text(
                                    text = when(brand.status) {
                                        "avoid" -> avoidText
                                        "support" -> supportText
                                        else -> neutralText
                                    }
                                )
                            },
                            modifier = Modifier.clickable { onBrandClick(brand.name) }
                        )
                    }
                }
            } else {
                // 搜索历史
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = historyTitle,
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        TextButton(onClick = { viewModel.clearHistory() }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = clearHistoryText,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = clearHistoryText)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 搜索历史标签流式布局
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        spacing = 8.dp
                    ) {
                        searchHistory.forEach { historyItem ->
                            SearchHistoryChip(
                                text = historyItem,
                                onDelete = { viewModel.removeFromHistory(historyItem) },
                                onClick = { 
                                    searchText = historyItem
                                    viewModel.searchBrands(historyItem)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    spacing: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val spacingPx = spacing.roundToPx()
        val rows = mutableListOf<List<Placeable>>()
        var currentRow = mutableListOf<Placeable>()
        var currentRowWidth = 0

        measurables.forEach { measurable ->
            val placeable = measurable.measure(
                constraints.copy(
                    minWidth = 0,
                    minHeight = 0
                )
            )

            if (currentRowWidth + placeable.width > constraints.maxWidth && currentRow.isNotEmpty()) {
                rows.add(currentRow.toList())
                currentRow = mutableListOf()
                currentRowWidth = 0
            }

            currentRow.add(placeable)
            currentRowWidth += placeable.width + spacingPx
        }

        if (currentRow.isNotEmpty()) {
            rows.add(currentRow.toList())
        }

        val rowHeights = rows.map { row ->
            row.maxOf { it.height }
        }

        val totalHeight = rowHeights.sumOf { it } + (rows.size - 1) * spacingPx

        layout(constraints.maxWidth, totalHeight) {
            var y = 0

            rows.forEachIndexed { rowIndex, row ->
                var x = 0
                row.forEach { placeable ->
                    placeable.placeRelative(x, y)
                    x += placeable.width + spacingPx
                }
                y += rowHeights[rowIndex] + spacingPx
            }
        }
    }
}
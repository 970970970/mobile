package com.boycott.app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.boycott.app.ui.components.ArticleCarousel
import com.boycott.app.ui.components.BrandGridItem
import androidx.compose.material.icons.filled.FitScreen
import com.boycott.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    viewModel: HomeViewModel = hiltViewModel(),
    onBrandClick: (String) -> Unit,
    onNavigateToSearchHistory: () -> Unit
) {
    val articles by viewModel.articles.collectAsState()
    val brands by viewModel.brands.collectAsState()
    val currentHotSearch by viewModel.currentHotSearch.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // 搜索框
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier
                    .clickable { onNavigateToSearchHistory() }
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 扫码图标
                IconButton(onClick = { /* TODO */ }) {
                    Icon(Icons.Filled.QrCode2, contentDescription = stringResource(R.string.search_scan))
                }
                
                // 分隔线
                Divider(
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
                
                // 搜索提示文本
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = currentHotSearch,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                // 相机图标
                IconButton(onClick = { /* TODO */ }) {
                    Icon(Icons.Filled.PhotoCamera, contentDescription = stringResource(R.string.search_camera))
                }
                
                // 分隔线
                Divider(
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
                
                // 搜索按钮
                TextButton(onClick = { /* TODO */ }) {
                    Text(stringResource(R.string.search_button))
                }
            }
        }

        // 文章轮播
        if (articles.isNotEmpty()) {
            ArticleCarousel(
                articles = articles,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 16.dp)
            )
        }

        // 品牌列表
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(brands) { brand ->
                BrandGridItem(
                    brand = brand,
                    onClick = { onBrandClick(brand.id) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeViewPreview() {
    MaterialTheme {
        HomeView(
            onBrandClick = {},
            onNavigateToSearchHistory = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.FitScreen, contentDescription = "扫条码")
                }
                
                Divider(
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
                
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    placeholder = {
                        Text(
                            "搜索品牌",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
                
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.PhotoCamera, contentDescription = "拍照")
                }
                
                Divider(
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
                
                TextButton(onClick = {}) {
                    Text("搜索")
                }
            }
        }
    }
} 
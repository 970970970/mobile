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
import com.boycott.app.ui.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    onBrandClick: (String) -> Unit,
    onNavigateToSearchHistory: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val brands by viewModel.brands.collectAsState()
    val articles by viewModel.articles.collectAsState()
    val currentHotSearch by viewModel.currentHotSearch.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // 使用搜索框组件
        SearchBar(
            hotSearchText = currentHotSearch,
            onSearchClick = onNavigateToSearchHistory
        )

        // 文章轮播
        articles?.let { articleList ->  // 使用安全调用
            if (articleList.isNotEmpty()) {  // 检查非空列表
                ArticleCarousel(
                    articles = articleList,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 16.dp)
                )
            }
        }

        // 品牌列表
        brands?.let { brandList ->  // 使用安全调用
            if (brandList.isNotEmpty()) {  // 检查非空列表
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(brandList) { brand ->
                        BrandGridItem(
                            brand = brand,
                            onClick = { onBrandClick(brand.id.toString()) }
                        )
                    }
                }
            }
        } ?: run {
            // 显示加载状态
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
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
package com.boycott.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.boycott.app.R
import com.boycott.app.ui.components.SearchBar
import com.boycott.app.ui.components.ArticleCarousel
import com.boycott.app.ui.components.BrandGrid

@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToBrandList: () -> Unit,
    onShowBrandDetail: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(modifier = modifier.fillMaxSize()) {
        // 搜索栏固定在顶部
        SearchBar(
            onSearchClick = onNavigateToSearch,
            onScanClick = onNavigateToScan,
            currentSuggestion = uiState.currentSuggestion,
            modifier = Modifier.padding(16.dp)
        )
        
        // 可滚动内容
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 文章轮播
                if (uiState.articles.isNotEmpty()) {
                    ArticleCarousel(
                        articles = uiState.articles,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(bottom = 16.dp)
                    )
                }
                
                // 品牌列表标题
                if (uiState.brands.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.popular_brands),
                            style = MaterialTheme.typography.titleMedium
                        )
                        TextButton(onClick = onNavigateToBrandList) {
                            Text(stringResource(R.string.view_more))
                        }
                    }
                    
                    // 品牌网格
                    BrandGrid(
                        brands = uiState.brands,
                        onBrandClick = onShowBrandDetail,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                
                // 加载状态
                when {
                    uiState.isLoadingMore -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    uiState.hasReachedEnd -> {
                        Text(
                            text = stringResource(R.string.end_of_list),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
    
    // 监听滚动到底部
    LaunchedEffect(uiState.brands) {
        if (!uiState.isLoadingMore && !uiState.hasReachedEnd) {
            viewModel.loadMoreBrands()
        }
    }
} 
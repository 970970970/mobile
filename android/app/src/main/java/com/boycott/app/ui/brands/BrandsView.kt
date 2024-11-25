package com.boycott.app.ui.brands

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.boycott.app.utils.AppConfig
import com.boycott.app.ui.components.SearchBar
import com.boycott.app.data.model.Brand
import kotlinx.coroutines.flow.distinctUntilChanged
import com.boycott.app.ui.home.HomeViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun BrandsView(
    onNavigateToSearchHistory: () -> Unit,
    onBrandClick: (String) -> Unit,
    onNavigateToSearchResults: (String) -> Unit,
    viewModel: BrandsViewModel = hiltViewModel()
) {
    val currentHotSearch by viewModel.currentHotSearch.collectAsState()
    val brands by viewModel.brands.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val hasMoreData by viewModel.hasMoreData.collectAsState()
    val listState = rememberLazyListState()

    // 检测是否需要加载更多
    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            
            lastVisibleItemIndex > (totalItemsNumber - 4)
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad) {
                viewModel.loadNextPage()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            hotSearchText = currentHotSearch,
            onSearchClick = {
                Log.d("SearchDebug", "BrandsView: Search bar clicked")
                viewModel.updateSearchText(currentHotSearch)
                onNavigateToSearchHistory()
            },
            onSearchButtonClick = {
                Log.d("SearchDebug", "BrandsView: Search button clicked with text: $currentHotSearch")
                if (currentHotSearch.isNotEmpty()) {
                    viewModel.searchAndNavigate(
                        keyword = currentHotSearch,
                        onBrandClick = onBrandClick,
                        onSearch = { onNavigateToSearchResults(currentHotSearch) }
                    )
                }
            }
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(brands) { brand ->
                BrandItem(
                    brand = brand,
                    onClick = { onBrandClick(brand.id.toString()) }
                )
            }

            // 加载状态或没有更多数据的提示
            item {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (!hasMoreData) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "没有更多内容",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BrandItem(
    brand: Brand,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (brand.logo_path != null) {
                    AsyncImage(
                        model = "${AppConfig.MEDIA_HOST}${brand.logo_path}",
                        contentDescription = brand.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        text = brand.name.firstOrNull()?.toString() ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = brand.name,
                    style = MaterialTheme.typography.titleMedium
                )
                
                brand.status?.let { status ->
                    Text(
                        text = when(status) {
                            "avoid" -> "抵制"
                            "support" -> "支持"
                            "neutral" -> "中立"
                            else -> status
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = when(status) {
                            "avoid" -> MaterialTheme.colorScheme.error
                            "support" -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
} 
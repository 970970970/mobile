package com.boycott.app.ui.home

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.boycott.app.ui.components.BrandGridItem
import com.boycott.app.ui.components.SearchBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.foundation.lazy.grid.GridItemSpan
import com.boycott.app.ui.camera.CameraMode
import androidx.compose.ui.res.stringResource
import com.boycott.app.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeView(
    onBrandClick: (String) -> Unit,
    onArticleClick: (Int) -> Unit,
    onNavigateToSearchHistory: () -> Unit,
    onNavigateToSearchResults: (String) -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToCamera: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val brands by viewModel.brands.collectAsState()
    val articles by viewModel.articles.collectAsState()
    val currentHotSearch by viewModel.currentHotSearch.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val hasMoreData by viewModel.hasMoreData.collectAsState()
    val gridState = rememberLazyGridState()

    Log.d("SearchDebug", "HomeView: Current hot search text: $currentHotSearch")

    // 检测是否需要加载更多
    LaunchedEffect(gridState) {
        snapshotFlow {
            val layoutInfo = gridState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            
            lastVisibleItemIndex > (totalItemsNumber - 4)  // 当剩余不到4个项时加载更多
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
                Log.d("SearchDebug", "HomeView: Search bar clicked")
                viewModel.updateSearchText(currentHotSearch)
                onNavigateToSearchHistory()
            },
            onSearchButtonClick = {
                Log.d("SearchDebug", "HomeView: Search button clicked with text: $currentHotSearch")
                if (currentHotSearch.isNotEmpty()) {
                    viewModel.searchAndNavigate(
                        keyword = currentHotSearch,
                        onBrandClick = onBrandClick,
                        onSearch = { onNavigateToSearchResults(currentHotSearch) }
                    )
                }
            },
            onScanClick = { onNavigateToScan() },
            onCameraClick = { onNavigateToCamera() }
        )

        // 文章轮播
        if (articles.isNotEmpty()) {
            val pagerState = rememberPagerState(pageCount = { articles.size })
            
            // 自动轮播
            LaunchedEffect(articles) {
                while (true) {
                    delay(5000) // 5秒切换一次
                    val nextPage = (pagerState.currentPage + 1) % articles.size
                    pagerState.animateScrollToPage(nextPage)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val article = articles[page]
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onArticleClick(article.id) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // 文章图片
                            article.image?.let { imageUrl ->
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = article.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            
                            // 文章标题（半透明背景）
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth(),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                            ) {
                                Text(
                                    text = article.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }

                // 页面指示器
                Row(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(articles.size) { iteration ->
                        val color = if (pagerState.currentPage == iteration) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(color)
                        )
                    }
                }
            }
        }

        // 品牌网格列表
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(brands) { brand ->
                BrandGridItem(
                    brand = brand,
                    onClick = { onBrandClick(brand.id.toString()) }
                )
            }

            // 加载状态或没有更多数据的提示
            item(span = { GridItemSpan(maxLineSpan) }) {
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
                            text = stringResource(R.string.no_more_results),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
} 
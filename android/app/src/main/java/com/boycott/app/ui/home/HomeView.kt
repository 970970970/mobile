package com.boycott.app.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeView(
    onBrandClick: (String) -> Unit,
    onArticleClick: (Int) -> Unit,  // 添加文章点击回调
    onNavigateToSearchHistory: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val brands by viewModel.brands.collectAsState()
    val articles by viewModel.articles.collectAsState()
    val currentHotSearch by viewModel.currentHotSearch.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            hotSearchText = currentHotSearch,
            onSearchClick = onNavigateToSearchHistory
        )

        // 文章轮播
        if (!articles.isEmpty()) {
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
        }
    }
} 
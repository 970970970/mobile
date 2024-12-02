package com.boycott.app.ui.brand

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.boycott.app.data.model.Brand
import dev.jeziellago.compose.markdowntext.MarkdownText
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.boycott.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandDetailView(
    brandId: String,
    onBack: () -> Unit,
    viewModel: BrandDetailViewModel = hiltViewModel()
) {
    val brand by viewModel.brand.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(brandId) {
        viewModel.loadBrand(brandId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(brand?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            brand?.let { brandData ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Logo 区域
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box {
                            AsyncImage(
                                model = "${AppConfig.MEDIA_HOST}${brandData.logo_path}",
                                contentDescription = brandData.name,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Fit
                            )
                            
                            // 抵制图标
                            if (brandData.status == "avoid") {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_boycott),
                                    contentDescription = "抵制",
                                    modifier = Modifier
                                        .size(72.dp)
                                        .align(Alignment.TopEnd)
                                        .offset(x = (-12).dp, y = 12.dp)
                                )
                            }
                        }
                    }

                    // 品牌状态
                    brandData.status?.let { status ->
                        Text(
                            text = when(status) {
                                "avoid" -> "抵制"
                                "support" -> "支持"
                                "neutral" -> "中立"
                                else -> status
                            },
                            style = MaterialTheme.typography.titleLarge,
                            color = when(status) {
                                "avoid" -> MaterialTheme.colorScheme.error
                                "support" -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    // 品牌描述
                    brandData.description?.let { description ->
                        MarkdownText(
                            markdown = description,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
} 
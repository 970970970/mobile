package com.boycott.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import coil.compose.AsyncImage
import com.boycott.app.utils.AppConfig
import com.boycott.app.data.model.Brand
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.boycott.app.R

@Composable
fun BrandGridItem(
    brand: Brand,
    onClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val textWidth = remember { mutableStateOf(0) }
    val containerWidth = remember { mutableStateOf(0) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Logo 容器
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (brand.logo_path != null) {
                    Box {  // 添加一个 Box 来包装 Logo 和抵制图标
                        AsyncImage(
                            model = "${AppConfig.MEDIA_HOST}${brand.logo_path}",
                            contentDescription = brand.name,
                            modifier = Modifier
                                .fillMaxSize(0.8f)
                                .clip(RoundedCornerShape(4.dp)),
                            contentScale = ContentScale.Fit
                        )
                        
                        // 抵制图标
                        if (brand.status == "avoid") {
                            Image(
                                painter = painterResource(id = R.drawable.ic_boycott),
                                contentDescription = "抵制",
                                modifier = Modifier
                                    .size(48.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-8).dp, y = 8.dp)
                            )
                        }
                    }
                } else {
                    Text(
                        text = brand.name.firstOrNull()?.toString() ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 品牌名称
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .padding(bottom = 8.dp)
                    .onGloballyPositioned { coordinates ->
                        containerWidth.value = coordinates.size.width
                    }
            ) {
                Text(
                    text = brand.name,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier
                        .horizontalScroll(scrollState)
                        .onGloballyPositioned { coordinates ->
                            textWidth.value = coordinates.size.width
                        }
                )
            }
        }
    }

    // 修改滚动效果
    LaunchedEffect(textWidth.value, containerWidth.value) {
        if (textWidth.value > containerWidth.value) {
            val difference = textWidth.value - containerWidth.value
            while (true) {
                delay(2000) // 开始前等待2秒
                coroutineScope.launch {
                    scrollState.animateScrollTo(
                        value = difference,
                        animationSpec = tween(
                            durationMillis = 3000,
                            easing = LinearEasing
                        )
                    )
                }
                delay(2000) // 在末尾停留2秒
                coroutineScope.launch {
                    scrollState.animateScrollTo(
                        value = 0,
                        animationSpec = tween(
                            durationMillis = 3000,
                            easing = LinearEasing
                        )
                    )
                }
                delay(2000) // 在开始位置停留2秒再开始下一次滚动
            }
        }
    }
} 
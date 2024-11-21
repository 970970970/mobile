package com.boycott.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.boycott.app.R

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hotSearchBrand: String,
    onScanClick: () -> Unit,
    onCameraClick: () -> Unit,
    onSearchClick: (String) -> Unit,
    onSearchBoxClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 扫描条码图标 - 移除外层的 clickable
            IconButton(
                onClick = onScanClick,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    Icons.Default.QrCode2,
                    contentDescription = stringResource(R.string.search_scan),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // 分隔线
            Divider(
                modifier = Modifier
                    .height(24.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
            
            // 随机品牌名称 - 添加文本截断
            Text(
                text = hotSearchBrand,
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onSearchBoxClick)
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            // 相机图标
            IconButton(
                onClick = onCameraClick,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    Icons.Default.PhotoCamera,
                    contentDescription = stringResource(R.string.search_camera),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // 分隔线
            Divider(
                modifier = Modifier
                    .height(24.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
            
            // 搜索按钮
            TextButton(
                onClick = { onSearchClick(hotSearchBrand) },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(stringResource(R.string.search_button))
            }
        }
    }
} 
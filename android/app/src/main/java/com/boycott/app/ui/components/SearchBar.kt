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
import androidx.compose.ui.unit.dp
import com.boycott.app.R

@Composable
fun SearchBar(
    hotSearchText: String,
    onSearchClick: () -> Unit,
    onScanClick: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onSearchButtonClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onSearchClick)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 扫码图标
            IconButton(onClick = onScanClick) {
                Icon(Icons.Default.QrCode2, contentDescription = stringResource(R.string.search_scan))
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
                    text = hotSearchText,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            IconButton(onClick = onCameraClick) {
                Icon(Icons.Default.PhotoCamera, contentDescription = stringResource(R.string.search_camera))
            }
            
            // 分隔线
            Divider(
                modifier = Modifier
                    .height(24.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
            
            TextButton(onClick = onSearchButtonClick) {
                Text(stringResource(R.string.search_button))
            }
        }
    }
} 
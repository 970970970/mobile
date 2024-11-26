package com.boycott.app.ui.camera

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraView(
    viewModel: CameraViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraMode by viewModel.cameraMode.collectAsState()
    val flashEnabled by viewModel.flashEnabled.collectAsState()
    val isFrontCamera by viewModel.isFrontCamera.collectAsState()
    
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // 相机预览 - 填满整个屏幕
        if (cameraPermissionState.status.isGranted) {
            CameraPreview(
                isFrontCamera = isFrontCamera,
                flashEnabled = flashEnabled,
                onBarcodeDetected = { /* TODO */ },
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // 顶部控制栏
        TopControls(
            flashEnabled = flashEnabled,
            onFlashToggle = viewModel::toggleFlash,
            onCameraToggle = viewModel::toggleCamera,
            onBackClick = onNavigateBack,
            isFrontCamera = isFrontCamera,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )
        
        // 底部控制栏
        BottomControls(
            cameraMode = cameraMode,
            onModeToggle = viewModel::toggleCameraMode,
            onCapture = { /* TODO */ },
            onGalleryClick = { /* TODO */ },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )
    }
}

@Composable
private fun TopControls(
    flashEnabled: Boolean,
    onFlashToggle: () -> Unit,
    onCameraToggle: () -> Unit,
    onBackClick: () -> Unit,
    isFrontCamera: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Black.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    tint = Color.White
                )
            }
            
            Row {
                if (!isFrontCamera) {
                    IconButton(onClick = onFlashToggle) {
                        Icon(
                            if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "闪光灯",
                            tint = Color.White
                        )
                    }
                }
                
                IconButton(onClick = onCameraToggle) {
                    Icon(
                        Icons.Default.Cameraswitch,
                        contentDescription = "切换相机",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomControls(
    cameraMode: CameraMode,
    onModeToggle: () -> Unit,
    onCapture: () -> Unit,
    onGalleryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Black.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onGalleryClick) {
                Icon(
                    Icons.Default.PhotoLibrary,
                    contentDescription = "相册",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            IconButton(
                onClick = onCapture,
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    if (cameraMode == CameraMode.PHOTO) Icons.Default.Camera else Icons.Default.QrCode,
                    contentDescription = "拍照",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
            
            IconButton(onClick = onModeToggle) {
                Icon(
                    if (cameraMode == CameraMode.PHOTO) Icons.Default.QrCode else Icons.Default.Camera,
                    contentDescription = "切换模式",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
} 
package com.boycott.app.ui.camera

import android.content.Context
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraPreview(
    isFrontCamera: Boolean,
    flashEnabled: Boolean,
    onBarcodeDetected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val previewView = remember { PreviewView(context) }
    val preview = remember { Preview.Builder().build() }
    val cameraState = remember { mutableStateOf<Camera?>(null) }
    
    val cameraSelector = remember(isFrontCamera) {
        CameraSelector.Builder()
            .requireLensFacing(if (isFrontCamera) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK)
            .build()
    }
    
    LaunchedEffect(isFrontCamera) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        
        cameraState.value = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview
        )
        
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }
    
    // 处理闪光灯
    LaunchedEffect(flashEnabled) {
        cameraState.value?.cameraControl?.enableTorch(flashEnabled)
    }
    
    AndroidView(
        modifier = modifier,
        factory = { previewView }
    )
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener(
            {
                continuation.resume(future.get())
            },
            ContextCompat.getMainExecutor(this)
        )
    }
} 
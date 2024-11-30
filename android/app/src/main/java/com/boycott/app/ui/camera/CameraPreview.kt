package com.boycott.app.ui.camera

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size as ComposeSize
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.boycott.app.ml.YoloDetector
import com.boycott.app.ml.YoloDetector.DetectionResult
import com.boycott.app.utils.ImageUtils.toBitmap
import android.util.Size as AndroidSize
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraPreview(
    isFrontCamera: Boolean,
    flashEnabled: Boolean,
    onBarcodeDetected: (String) -> Unit,
    enableDetection: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val previewView = remember { PreviewView(context) }
    val preview = remember { Preview.Builder().build() }
    val cameraState = remember { mutableStateOf<Camera?>(null) }
    val detectionResults = remember { mutableStateOf<List<DetectionResult>>(emptyList()) }
    
    val cameraSelector = remember(isFrontCamera) {
        CameraSelector.Builder()
            .requireLensFacing(if (isFrontCamera) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK)
            .build()
    }
    
    val detector = remember { YoloDetector() }
    
    val imageAnalyzer = remember {
        ImageAnalysis.Builder()
            .setTargetResolution(AndroidSize(640, 640))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply {
                setAnalyzer(
                    ContextCompat.getMainExecutor(context)
                ) { imageProxy ->
                    try {
                        // 1. 将 ImageProxy 转换为 Bitmap
                        val bitmap = imageProxy.toBitmap()
                        
                        // 2. 执行检测
                        if (enableDetection) {
                            val results = detector.detect(bitmap)
                            
                            // 3. 更新检测结果
                            detectionResults.value = results.toList()
                        }
                        
                    } catch (e: Exception) {
                        Log.e("CameraPreview", "Detection failed", e)
                    } finally {
                        // 4. 关闭 ImageProxy
                        imageProxy.close()
                    }
                }
            }
    }
    
    LaunchedEffect(isFrontCamera) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        
        cameraState.value = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageAnalyzer
        )
        
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }
    
    // 处理闪光灯
    LaunchedEffect(flashEnabled) {
        cameraState.value?.cameraControl?.enableTorch(flashEnabled)
    }
    
    // 创建文本画笔
    val textPaint = remember {
        Paint().apply {
            color = android.graphics.Color.GREEN
            textSize = 40f
            isAntiAlias = true
        }
    }
    
    Box(modifier = modifier) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val scaleX = size.width / 640f
            val scaleY = size.height / 640f
            
            detectionResults.value.forEach { result ->
                Log.d("CameraPreview", "Drawing box: (${result.x}, ${result.y}, ${result.width}, ${result.height})")
                
                // 缩放坐标到实际预览大小
                val scaledX = result.x * scaleX
                val scaledY = result.y * scaleY
                val scaledWidth = result.width * scaleX
                val scaledHeight = result.height * scaleY
                
                drawRect(
                    color = Color.Green,
                    topLeft = Offset(scaledX, scaledY),
                    size = ComposeSize(scaledWidth, scaledHeight),
                    style = Stroke(width = 2f)
                )
                
                drawContext.canvas.nativeCanvas.drawText(
                    "${result.label} (${(result.score * 100).toInt()}%)",
                    scaledX,
                    scaledY - 10f,
                    textPaint
                )
            }
        }
    }
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine<ProcessCameraProvider> { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener(
            {
                continuation.resume(future.get())
            },
            ContextCompat.getMainExecutor(this)
        )
    }
} 
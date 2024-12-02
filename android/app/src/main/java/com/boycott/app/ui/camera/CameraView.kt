package com.boycott.app.ui.camera

import android.util.Log
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
import com.boycott.app.ml.YoloDetector
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.Close
import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.DisposableEffect
import androidx.core.content.ContextCompat
import android.graphics.BitmapFactory
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import java.util.concurrent.Executor
import android.graphics.Matrix
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraView(
    viewModel: CameraViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraMode by viewModel.cameraMode.collectAsState()
    val flashEnabled by viewModel.flashEnabled.collectAsState()
    val isFrontCamera by viewModel.isFrontCamera.collectAsState()
    
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    // 添加状态
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    // 添加一个状态来存储检测结果图片
    var detectionResultBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    // 添加一个状态来存储拍照函数
    var onTakePhoto by remember { mutableStateOf<(() -> Unit)?>(null) }
    
    // 添加条码扫描器
    val barcodeScanner = remember {
        BarcodeScanning.getClient()
    }
    
    // 添加扫描结果状态
    var barcodeResult by remember { mutableStateOf<String?>(null) }
    
    // 处理扫描结果的对话框
    if (barcodeResult != null) {
        AlertDialog(
            onDismissRequest = { barcodeResult = null },
            title = { Text("扫描结果") },
            text = { Text(barcodeResult!!) },
            confirmButton = {
                TextButton(onClick = { barcodeResult = null }) {
                    Text("确定")
                }
            }
        )
    }
    
    // 添加一个公共函数来处理图片检测
    suspend fun processImageDetection(
        context: Context,
        bitmap: Bitmap,
        scope: CoroutineScope,
        onResult: (Bitmap) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            try {
                // 确保输入图片是可变的并且使用正确的格式
                val processedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                
                Log.d("CameraView", "Processing bitmap: ${processedBitmap.width}x${processedBitmap.height}")
                
                val detector = YoloDetector()
                detector.init()
                detector.loadModel(context.assets, "model.ncnn.param", "model.ncnn.bin")
                val results = detector.detect(processedBitmap)  // 使用处理过的图片
                
                Log.d("CameraView", "Detection results: ${results.size} objects found")
                results.forEach { result ->
                    Log.d("CameraView", "Object: ${result.label}, confidence: ${result.score}")
                }
                
                // 创建结果图片 - 使用原始图片尺寸
                val resultBitmap = processedBitmap.copy(Bitmap.Config.ARGB_8888, true)
                val canvas = Canvas(resultBitmap)
                val paint = Paint().apply {
                    color = AndroidColor.RED
                    style = Paint.Style.STROKE
                    strokeWidth = 4f
                }
                
                // 绘制检测框
                results.forEach { result ->
                    // 绘制半透明背景
                    paint.apply {
                        style = Paint.Style.FILL
                        color = AndroidColor.parseColor("#33FF0000")  // 红色带透明度
                    }
                    canvas.drawRect(
                        result.x,
                        result.y,
                        result.x + result.width,
                        result.y + result.height,
                        paint
                    )
                    
                    // 绘制边框
                    paint.apply {
                        style = Paint.Style.STROKE
                        color = AndroidColor.RED
                        strokeWidth = 4f
                    }
                    canvas.drawRect(
                        result.x,
                        result.y,
                        result.x + result.width,
                        result.y + result.height,
                        paint
                    )
                    
                    // 绘制置信度文本
                    paint.apply {
                        style = Paint.Style.FILL
                        color = AndroidColor.WHITE  // 白色文字
                        textSize = 40f
                    }
                    val text = String.format("%.2f%%", result.score * 100)
                    canvas.drawText(
                        text,
                        result.x,
                        result.y - 10,
                        paint
                    )
                }
                
                onResult(resultBitmap)
                
            } catch (e: Exception) {
                Log.e("CameraView", "Failed to process image", e)
                onError(e.message ?: "Failed to process image")
            }
        }
    }
    
    // 修改相册处理代码
    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { selectedUri ->
            scope.launch {
                try {
                    Log.d("CameraView", "Selected image URI: $selectedUri")
                    
                    // 创建选项来处理大图片
                    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(context.contentResolver, selectedUri)
                        Log.d("CameraView", "Created ImageDecoder source")
                        
                        ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                            decoder.isMutableRequired = true
                        }
                    } else {
                        MediaStore.Images.Media.getBitmap(context.contentResolver, selectedUri)
                    }
                    
                    processImageDetection(
                        context = context,
                        bitmap = bitmap,
                        scope = scope,
                        onResult = { resultBitmap ->
                            detectionResultBitmap = resultBitmap
                        },
                        onError = { error ->
                            errorMessage = error
                            showError = true
                        }
                    )
                } catch (e: Exception) {
                    Log.e("CameraView", "Failed to load image", e)
                    errorMessage = e.message ?: "Failed to load image"
                    showError = true
                }
            }
        }
    }
    
    // 添加错误提示
    if (showError) {
        AlertDialog(
            onDismissRequest = { showError = false },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showError = false }) {
                    Text("OK")
                }
            }
        )
    }
    
    LaunchedEffect(Unit) {
        try {
            val detector = YoloDetector()
            // 测试初始化
            val initResult = detector.init()
            Log.d("CameraView", "Init result: $initResult")
            
            // 测试版本获取
            val version = detector.getVersion()
            Log.d("CameraView", "NCNN version: $version")
            
            // 测试计算功能
            val computeResult = detector.testCompute(5)
            Log.d("CameraView", "5 * 5 = $computeResult")
            
            // 测试模型加载
            val modelResult = detector.loadModel(
                context.assets,
                "model.ncnn.param",
                "model.ncnn.bin"
            )
            Log.d("CameraView", "Model load result: $modelResult")
            
        } catch (e: Exception) {
            Log.e("CameraView", "Failed to initialize YoloDetector", e)
        }
        
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        if (cameraPermissionState.status.isGranted) {
            if (detectionResultBitmap != null) {
                DetectionResultView(
                    bitmap = detectionResultBitmap!!,
                    onClose = {
                        detectionResultBitmap = null
                    },
                    onRetake = {
                        detectionResultBitmap = null
                    }
                )
            } else {
                CameraPreview(
                    isFrontCamera = isFrontCamera,
                    flashEnabled = flashEnabled,
                    onBarcodeDetected = { result -> 
                        barcodeResult = result
                    },
                    onImageCaptured = { bitmap ->
                        // 如果是扫码模式，则进行条码识别
                        if (cameraMode == CameraMode.SCAN) {
                            val image = InputImage.fromBitmap(bitmap, 0)
                            barcodeScanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    if (barcodes.isNotEmpty()) {
                                        val barcode = barcodes[0]
                                        barcodeResult = when (barcode.valueType) {
                                            Barcode.TYPE_URL -> "URL: ${barcode.url?.url}"
                                            Barcode.TYPE_PRODUCT -> "商品: ${barcode.rawValue}"
                                            Barcode.TYPE_TEXT -> "文本: ${barcode.rawValue}"
                                            else -> "条码: ${barcode.rawValue}"
                                        }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("CameraView", "Barcode scanning failed", e)
                                    errorMessage = "条码扫描失败"
                                    showError = true
                                }
                        } else {
                            // 如果是拍照模式，则进行图片检测
                            scope.launch {
                                processImageDetection(
                                    context = context,
                                    bitmap = bitmap,
                                    scope = scope,
                                    onResult = { resultBitmap ->
                                        detectionResultBitmap = resultBitmap
                                    },
                                    onError = { error ->
                                        errorMessage = error
                                        showError = true
                                    }
                                )
                            }
                        }
                    },
                    onCaptureReady = { takePhoto ->  // 添加这个回调
                        onTakePhoto = takePhoto
                    },
                    modifier = Modifier.fillMaxSize()
                )
                
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
                
                BottomControls(
                    cameraMode = cameraMode,
                    onModeToggle = viewModel::toggleCameraMode,
                    onCapture = { onTakePhoto?.invoke() },
                    onGalleryClick = { pickImage.launch("image/*") },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                )
            }
        }
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
                modifier = Modifier
                    .size(72.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    Icons.Default.Camera,  // 始终使用相机图标
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

// 修改 DetectionResultView 组件
@Composable
fun DetectionResultView(
    bitmap: Bitmap,
    onClose: () -> Unit,
    onRetake: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 显示结果图片
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Detection Result",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        
        // 添加顶部关闭按钮
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White
            )
        }
        
        // 调整再拍一张按钮的位置
        Button(
            onClick = onRetake,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp),  // 增加底部间距
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.8f),
                contentColor = Color.Black
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Camera,
                    contentDescription = null
                )
                Text("再拍一张")
            }
        }
    }
}

// 添加图片旋转工具函数
private fun Bitmap.rotateImage(degrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

@Composable
fun CameraPreview(
    isFrontCamera: Boolean,
    flashEnabled: Boolean,
    onBarcodeDetected: (String) -> Unit,
    onImageCaptured: (Bitmap) -> Unit,
    onCaptureReady: (() -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }
    
    // 记住相机相关的状态
    val preview = remember { Preview.Builder().build() }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }
    val cameraSelector = remember(isFrontCamera) {
        if (isFrontCamera) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
    }

    // 修改拍照功能的实现 - 移到前面
    val capturePhoto = remember {
        {
            imageCapture.takePicture(
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        try {
                            val buffer = image.planes[0].buffer
                            val bytes = ByteArray(buffer.remaining())
                            buffer.get(bytes)
                            
                            var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                ?.copy(Bitmap.Config.ARGB_8888, true)
                            
                            // 根据图片方向旋转
                            if (bitmap != null) {
                                bitmap = bitmap.rotateImage(image.imageInfo.rotationDegrees.toFloat())
                                onImageCaptured(bitmap)
                            }
                        } catch (e: Exception) {
                            Log.e("CameraPreview", "Failed to process captured image", e)
                        } finally {
                            image.close()
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("CameraPreview", "Photo capture failed", exception)
                    }
                }
            )
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )

    // 使用 LaunchedEffect 来管理相机生命周期
    LaunchedEffect(isFrontCamera, flashEnabled) {
        try {
            val cameraProvider = cameraProviderFuture.get()
            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
            
            preview.setSurfaceProvider(previewView.surfaceProvider)
            
            // 确保相机已经绑定
            if (camera != null) {
                onCaptureReady(capturePhoto)
            }
        } catch (e: Exception) {
            Log.e("CameraPreview", "Use case binding failed", e)
        }
    }
    
    // 清理相机资源
    DisposableEffect(Unit) {
        onDispose {
            try {
                val cameraProvider = cameraProviderFuture.get()
                cameraProvider.unbindAll()
            } catch (e: Exception) {
                Log.e("CameraPreview", "Failed to unbind camera", e)
            }
        }
    }

    // 通知外部拍照函数已准备好
    LaunchedEffect(capturePhoto) {
        onCaptureReady(capturePhoto)
    }
} 
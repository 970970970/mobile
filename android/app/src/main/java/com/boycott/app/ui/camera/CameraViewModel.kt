package com.boycott.app.ui.camera

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {
    // 相机模式
    private val _cameraMode = MutableStateFlow(CameraMode.PHOTO)
    val cameraMode = _cameraMode.asStateFlow()
    
    // 闪光灯状态
    private val _flashEnabled = MutableStateFlow(false)
    val flashEnabled = _flashEnabled.asStateFlow()
    
    // 前后摄像头
    private val _isFrontCamera = MutableStateFlow(false)
    val isFrontCamera = _isFrontCamera.asStateFlow()
    
    fun toggleCameraMode() {
        _cameraMode.value = when (_cameraMode.value) {
            CameraMode.PHOTO -> CameraMode.SCAN
            CameraMode.SCAN -> CameraMode.PHOTO
        }
    }
    
    fun toggleFlash() {
        _flashEnabled.value = !_flashEnabled.value
    }
    
    fun toggleCamera() {
        _isFrontCamera.value = !_isFrontCamera.value
    }
}

enum class CameraMode {
    PHOTO, SCAN
} 
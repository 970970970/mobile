package com.boycott.app.ml

import android.content.res.AssetManager
import android.graphics.Bitmap

class YoloDetector {
    // 基础初始化方法
    external fun init(): Int

    // 获取 NCNN 版本信息
    external fun getVersion(): String

    // 简单的测试方法，传入一个数字并返回其平方
    external fun testCompute(number: Int): Int

    // 加载模型文件
    external fun loadModel(assetManager: AssetManager, paramPath: String, binPath: String): Int

    // 释放模型资源
    external fun releaseModel()

    // 检测结果的数据类
    data class DetectionResult(
        val label: String,    // 检测到的类别
        val score: Float,     // 置信度分数
        val x: Float,         // 边界框左上角 x 坐标
        val y: Float,         // 边界框左上角 y 坐标
        val width: Float,     // 边界框宽度
        val height: Float     // 边界框高度
    )

    // 目标检测方法
    external fun detect(bitmap: Bitmap): Array<DetectionResult>

    companion object {
        init {
            System.loadLibrary("yolo_ncnn")
        }
    }
}
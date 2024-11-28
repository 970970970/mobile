package com.boycott.app.ml

import android.content.res.AssetManager

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

    companion object {
        init {
            System.loadLibrary("yolo_ncnn")
        }
    }
}
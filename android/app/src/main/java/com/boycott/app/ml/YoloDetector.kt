package com.boycott.app.ml

class YoloDetector {
    // 基础初始化方法
    external fun init(): Int

    // 获取 NCNN 版本信息
    external fun getVersion(): String

    // 简单的测试方法，传入一个数字并返回其平方
    external fun testCompute(number: Int): Int

    companion object {
        init {
            System.loadLibrary("yolo_ncnn")
        }
    }
}
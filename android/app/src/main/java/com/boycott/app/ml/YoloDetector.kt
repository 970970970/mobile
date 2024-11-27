package com.boycott.app.ml

class YoloDetector {
    external fun init(): Int

    companion object {
        init {
            System.loadLibrary("yolo_ncnn")
        }
    }
}

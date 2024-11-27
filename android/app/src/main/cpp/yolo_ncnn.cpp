#include <jni.h>
#include <android/log.h>
#include <string>

#define TAG "YoloNcnn"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

extern "C" {
    JNIEXPORT jint JNICALL
    Java_com_boycott_app_ml_YoloDetector_init(JNIEnv *env, jobject thiz) {
        LOGD("YoloDetector init");
        return 0;
    }
    
    JNIEXPORT jstring JNICALL
    Java_com_boycott_app_ml_YoloDetector_getVersion(JNIEnv *env, jobject thiz) {
        std::string version = "NCNN Test Version 1.0";
        LOGD("Get version: %s", version.c_str());
        return env->NewStringUTF(version.c_str());
    }
    
    JNIEXPORT jint JNICALL
    Java_com_boycott_app_ml_YoloDetector_testCompute(JNIEnv *env, jobject thiz, jint number) {
        int result = number * number;
        LOGD("Test compute: %d * %d = %d", number, number, result);
        return result;
    }
}

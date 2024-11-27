#include <jni.h>
#include <android/log.h>
#include <string>
#include <net.h>  // NCNN 头文件

#define TAG "YoloNcnn"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

// 全局变量
static ncnn::Net* net = nullptr;

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
    
    JNIEXPORT jint JNICALL
    Java_com_boycott_app_ml_YoloDetector_loadModel(JNIEnv* env, jobject thiz, jstring paramPath, jstring binPath) {
        const char* param = env->GetStringUTFChars(paramPath, nullptr);
        const char* bin = env->GetStringUTFChars(binPath, nullptr);
        
        if (net == nullptr) {
            net = new ncnn::Net();
        }
        
        // 加载模型
        int ret = net->load_param(param);
        if (ret != 0) {
            LOGD("Failed to load param file");
            return -1;
        }
        
        ret = net->load_model(bin);
        if (ret != 0) {
            LOGD("Failed to load model file");
            return -2;
        }
        
        env->ReleaseStringUTFChars(paramPath, param);
        env->ReleaseStringUTFChars(binPath, bin);
        
        LOGD("Model loaded successfully");
        return 0;
    }
    
    JNIEXPORT void JNICALL
    Java_com_boycott_app_ml_YoloDetector_releaseModel(JNIEnv* env, jobject thiz) {
        if (net != nullptr) {
            delete net;
            net = nullptr;
            LOGD("Model released");
        }
    }
}

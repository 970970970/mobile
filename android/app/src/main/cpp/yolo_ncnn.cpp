#include <jni.h>
#include <android/log.h>
#include <string>
#include <net.h>  // NCNN 头文件
#include <android/asset_manager_jni.h>
#include <android/asset_manager.h>

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
    Java_com_boycott_app_ml_YoloDetector_loadModel(JNIEnv* env, jobject thiz, jobject assetManager,
                                                  jstring paramPath, jstring binPath) {
        LOGD("Start loading model...");
        
        // 1. 检查 AssetManager
        AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
        if (!mgr) {
            LOGD("Failed to get asset manager");
            return -3;
        }
        LOGD("AssetManager obtained successfully");

        // 2. 获取文件路径
        const char* paramPathStr = env->GetStringUTFChars(paramPath, nullptr);
        const char* binPathStr = env->GetStringUTFChars(binPath, nullptr);
        std::string fullParamPath = std::string("models/") + paramPathStr;
        std::string fullBinPath = std::string("models/") + binPathStr;
        LOGD("Trying to load param file: %s", fullParamPath.c_str());
        LOGD("Trying to load model file: %s", fullBinPath.c_str());

        // 3. 检查文件是否存在
        AAsset* paramAsset = AAssetManager_open(mgr, fullParamPath.c_str(), AASSET_MODE_BUFFER);
        if (!paramAsset) {
            LOGD("Failed to open param file: %s", fullParamPath.c_str());
            env->ReleaseStringUTFChars(paramPath, paramPathStr);
            env->ReleaseStringUTFChars(binPath, binPathStr);
            return -4;
        }
        AAsset_close(paramAsset);
        LOGD("Param file exists");

        AAsset* binAsset = AAssetManager_open(mgr, fullBinPath.c_str(), AASSET_MODE_BUFFER);
        if (!binAsset) {
            LOGD("Failed to open model file: %s", fullBinPath.c_str());
            env->ReleaseStringUTFChars(paramPath, paramPathStr);
            env->ReleaseStringUTFChars(binPath, binPathStr);
            return -5;
        }
        AAsset_close(binAsset);
        LOGD("Model file exists");

        // 4. 创建或重置 NCNN 网络
        if (net == nullptr) {
            net = new ncnn::Net();
            LOGD("Created new NCNN network");
        } else {
            LOGD("Using existing NCNN network");
        }

        // 5. 加载参数文件
        int ret = net->load_param(mgr, fullParamPath.c_str());
        if (ret != 0) {
            LOGD("Failed to load param file, error code: %d", ret);
            env->ReleaseStringUTFChars(paramPath, paramPathStr);
            env->ReleaseStringUTFChars(binPath, binPathStr);
            return -1;
        }
        LOGD("Param file loaded successfully");

        // 6. 加载模型文件
        ret = net->load_model(mgr, fullBinPath.c_str());
        if (ret != 0) {
            LOGD("Failed to load model file, error code: %d", ret);
            env->ReleaseStringUTFChars(paramPath, paramPathStr);
            env->ReleaseStringUTFChars(binPath, binPathStr);
            return -2;
        }
        LOGD("Model file loaded successfully");

        // 7. 清理资源
        env->ReleaseStringUTFChars(paramPath, paramPathStr);
        env->ReleaseStringUTFChars(binPath, binPathStr);

        LOGD("Model loading completed successfully");
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

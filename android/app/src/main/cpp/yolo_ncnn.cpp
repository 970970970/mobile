#include <jni.h>
#include <android/log.h>

#define TAG "YoloNcnn"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

extern "C" {
    JNIEXPORT jint JNICALL
    Java_com_boycott_app_ml_YoloDetector_init(JNIEnv *env, jobject thiz) {
        LOGD("YoloDetector init");
        return 0;
    }
}

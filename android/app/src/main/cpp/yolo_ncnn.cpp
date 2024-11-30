#include <jni.h>
#include <android/log.h>
#include <string>
#include <net.h> // NCNN 头文件
#include <android/asset_manager_jni.h>
#include <android/asset_manager.h>
#include <android/bitmap.h>
#include <opencv2/opencv.hpp>

#define TAG "YoloNcnn"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define MAX_STRIDE 32

// 全局变量
static ncnn::Net *net = nullptr;

// 在全局变量定义之前添加
struct Rect
{
    float x;      // 左上角 x 坐标
    float y;      // 左上角 y 坐标
    float width;  // 宽度
    float height; // 高度
};

struct Object
{
    std::string label;
    float prob;
    Rect rect;
};

// 添加辅助函数到文件开头
static inline float clampf(float v, float min, float max)
{
    v = v < min ? min : v;
    v = v > max ? max : v;
    return v;
}

// 声明解码函数
static void decode_outputs(const ncnn::Mat& out, std::vector<Object>& objects,
                         float conf_threshold, float nms_threshold,
                         int input_width, int input_height,
                         float scale,
                         int wpad, int hpad);

// 添加 NMS 函数声明
static float intersection_area(const Object &a, const Object &b)
{
    float x1 = std::max(a.rect.x, b.rect.x);
    float y1 = std::max(a.rect.y, b.rect.y);
    float x2 = std::min(a.rect.x + a.rect.width, b.rect.x + b.rect.width);
    float y2 = std::min(a.rect.y + a.rect.height, b.rect.y + b.rect.height);

    float area = (x2 - x1) * (y2 - y1);
    return area > 0 ? area : 0;
}

static void nms_sorted_bboxes(std::vector<Object> &objects, std::vector<int> &picked, float nms_threshold)
{
    picked.clear();

    const int n = objects.size();
    std::vector<float> areas(n);

    for (int i = 0; i < n; i++)
    {
        areas[i] = objects[i].rect.width * objects[i].rect.height;
    }

    for (int i = 0; i < n; i++)
    {
        const Object &a = objects[i];

        int keep = 1;
        for (int j = 0; j < (int)picked.size(); j++)
        {
            const Object &b = objects[picked[j]];

            float inter_area = intersection_area(a, b);
            float union_area = areas[i] + areas[picked[j]] - inter_area;
            if (inter_area / union_area > nms_threshold)
                keep = 0;
        }

        if (keep)
            picked.push_back(i);
    }
}

static float sigmoid(float x)
{
    return 1.0f / (1.0f + exp(-x));
}

static void decode_outputs(const ncnn::Mat& out, std::vector<Object>& objects,
                         float conf_threshold, float nms_threshold,
                         int input_width, int input_height,
                         float scale,
                         int wpad, int hpad)
{
    LOGD("Output tensor shape: %dx%dx%d", out.w, out.h, out.c);
    
    std::vector<Object> proposals;
    
    // 转换输出格式为更易处理的形式
    cv::Mat output = cv::Mat((int)out.h, (int)out.w, CV_32F, (float*)out.data).t();
    const int num_anchors = output.rows;  // 8400
    const int num_channels = output.cols;  // 5 = 4(bbox) + 1(class)
    
    LOGD("Num anchors: %d, Num channels: %d", num_anchors, num_channels);
    
    for (int i = 0; i < num_anchors; i++)
    {
        const float* row_ptr = output.row(i).ptr<float>();
        const float* bboxes_ptr = row_ptr;
        const float* scores_ptr = row_ptr + 4;
        float score = scores_ptr[0];  // 只有一个类别
        
        if (score > conf_threshold)
        {
            float x = bboxes_ptr[0];
            float y = bboxes_ptr[1];
            float w = bboxes_ptr[2];
            float h = bboxes_ptr[3];
            
            // 计算边界框坐标
            float x0 = clampf((x - 0.5f * w), 0.f, input_width);
            float y0 = clampf((y - 0.5f * h), 0.f, input_height);
            float x1 = clampf((x + 0.5f * w), 0.f, input_width);
            float y1 = clampf((y + 0.5f * h), 0.f, input_height);
            
            Object obj;
            obj.label = "LOGO";
            obj.prob = score;
            obj.rect.x = x0;
            obj.rect.y = y0;
            obj.rect.width = x1 - x0;
            obj.rect.height = y1 - y0;
            
            proposals.push_back(obj);
        }
    }
    
    // 按置信度排序
    std::sort(proposals.begin(), proposals.end(),
              [](const Object& a, const Object& b) { return a.prob > b.prob; });
    
    // 执行 NMS
    std::vector<int> picked;
    nms_sorted_bboxes(proposals, picked, nms_threshold);
    
    // 收集最终结果
    objects.clear();
    for (int i = 0; i < picked.size(); i++)
    {
        objects.push_back(proposals[picked[i]]);
        
        // 调整坐标到原始图片大小
        float x0 = (objects[i].rect.x - wpad/2) / scale;
        float y0 = (objects[i].rect.y - hpad/2) / scale;
        float x1 = (objects[i].rect.x + objects[i].rect.width - wpad/2) / scale;
        float y1 = (objects[i].rect.y + objects[i].rect.height - hpad/2) / scale;
        
        objects[i].rect.x = x0;
        objects[i].rect.y = y0;
        objects[i].rect.width = x1 - x0;
        objects[i].rect.height = y1 - y0;
    }
    
    LOGD("Found %zu objects before NMS, %zu after NMS", 
         proposals.size(), objects.size());
}

extern "C"
{
    JNIEXPORT jint JNICALL
    Java_com_boycott_app_ml_YoloDetector_init(JNIEnv *env, jobject thiz)
    {
        LOGD("YoloDetector init");
        return 0;
    }

    JNIEXPORT jstring JNICALL
    Java_com_boycott_app_ml_YoloDetector_getVersion(JNIEnv *env, jobject thiz)
    {
        std::string version = "NCNN Test Version 1.0";
        LOGD("Get version: %s", version.c_str());
        return env->NewStringUTF(version.c_str());
    }

    JNIEXPORT jint JNICALL
    Java_com_boycott_app_ml_YoloDetector_testCompute(JNIEnv *env, jobject thiz, jint number)
    {
        int result = number * number;
        LOGD("Test compute: %d * %d = %d", number, number, result);
        return result;
    }

    JNIEXPORT jint JNICALL
    Java_com_boycott_app_ml_YoloDetector_loadModel(JNIEnv *env, jobject thiz, jobject assetManager,
                                                   jstring paramPath, jstring binPath)
    {
        LOGD("Start loading model...");

        // 1. 检查 AssetManager
        AAssetManager *mgr = AAssetManager_fromJava(env, assetManager);
        if (!mgr)
        {
            LOGD("Failed to get asset manager");
            return -3;
        }
        LOGD("AssetManager obtained successfully");

        // 2. 获取文件路径
        const char *paramPathStr = env->GetStringUTFChars(paramPath, nullptr);
        const char *binPathStr = env->GetStringUTFChars(binPath, nullptr);
        std::string fullParamPath = std::string("models/") + paramPathStr;
        std::string fullBinPath = std::string("models/") + binPathStr;
        LOGD("Trying to load param file: %s", fullParamPath.c_str());
        LOGD("Trying to load model file: %s", fullBinPath.c_str());

        // 3. 检查文件是否存在
        AAsset *paramAsset = AAssetManager_open(mgr, fullParamPath.c_str(), AASSET_MODE_BUFFER);
        if (!paramAsset)
        {
            LOGD("Failed to open param file: %s", fullParamPath.c_str());
            env->ReleaseStringUTFChars(paramPath, paramPathStr);
            env->ReleaseStringUTFChars(binPath, binPathStr);
            return -4;
        }
        AAsset_close(paramAsset);
        LOGD("Param file exists");

        AAsset *binAsset = AAssetManager_open(mgr, fullBinPath.c_str(), AASSET_MODE_BUFFER);
        if (!binAsset)
        {
            LOGD("Failed to open model file: %s", fullBinPath.c_str());
            env->ReleaseStringUTFChars(paramPath, paramPathStr);
            env->ReleaseStringUTFChars(binPath, binPathStr);
            return -5;
        }
        AAsset_close(binAsset);
        LOGD("Model file exists");

        // 4. 创建或重置 NCNN 网络
        if (net == nullptr)
        {
            net = new ncnn::Net();
            LOGD("Created new NCNN network");
        }
        else
        {
            LOGD("Using existing NCNN network");
        }

        // 5. 加载参数文件
        int ret = net->load_param(mgr, fullParamPath.c_str());
        if (ret != 0)
        {
            LOGD("Failed to load param file, error code: %d", ret);
            env->ReleaseStringUTFChars(paramPath, paramPathStr);
            env->ReleaseStringUTFChars(binPath, binPathStr);
            return -1;
        }
        LOGD("Param file loaded successfully");

        // 在加载 param 文件后添加
        LOGD("Param file loaded successfully, checking network info...");
        LOGD("Network input count: %zu", net->input_names().size());
        LOGD("Network output count: %zu", net->output_names().size());
        for (const auto &name : net->input_names())
        {
            LOGD("Input blob name: %s", name);
        }
        for (const auto &name : net->output_names())
        {
            LOGD("Output blob name: %s", name);
        }

        // 6. 加载模型文件
        ret = net->load_model(mgr, fullBinPath.c_str());
        if (ret != 0)
        {
            LOGD("Failed to load model file, error code: %d", ret);
            env->ReleaseStringUTFChars(paramPath, paramPathStr);
            env->ReleaseStringUTFChars(binPath, binPathStr);
            return -2;
        }
        LOGD("Model file loaded successfully");

        // 在成功加载模型后添加
        LOGD("Model loaded successfully, checking layer names...");

        // 打印所有 blob 名称
        const std::vector<const char *> &blobs = net->input_names();
        LOGD("Input blob names:");
        for (const char *name : blobs)
        {
            LOGD("  %s", name);
        }

        const std::vector<const char *> &out_blobs = net->output_names();
        LOGD("Output blob names:");
        for (const char *name : out_blobs)
        {
            LOGD("  %s", name);
        }

        // 7. 清理资源
        env->ReleaseStringUTFChars(paramPath, paramPathStr);
        env->ReleaseStringUTFChars(binPath, binPathStr);

        LOGD("Model loading completed successfully");
        return 0;
    }

    JNIEXPORT void JNICALL
    Java_com_boycott_app_ml_YoloDetector_releaseModel(JNIEnv *env, jobject thiz)
    {
        if (net != nullptr)
        {
            delete net;
            net = nullptr;
            LOGD("Model released");
        }
    }

    JNIEXPORT jobjectArray JNICALL
    Java_com_boycott_app_ml_YoloDetector_detect(JNIEnv* env, jobject thiz, jobject bitmap)
    {
        LOGD("Start detection...");
        
        const int target_size = 640;
        const float prob_threshold = 0.25f;
        const float nms_threshold = 0.45f;
        
        // 获取图像信息
        AndroidBitmapInfo info;
        AndroidBitmap_getInfo(env, bitmap, &info);
        
        // 计算缩放比例 - 修改为与 Python 一致的方式
        int img_w = info.width;
        int img_h = info.height;
        float scale = std::min((float)target_size / img_w, (float)target_size / img_h);
        int scaled_w = int(img_w * scale);
        int scaled_h = int(img_h * scale);
        
        LOGD("Original size: %dx%d, scaled size: %dx%d, scale: %f",
             img_w, img_h, scaled_w, scaled_h, scale);
        
        // 锁定像素
        void* pixels;
        AndroidBitmap_lockPixels(env, bitmap, &pixels);
        
        // resize - 使用计算好的 scaled 尺寸
        ncnn::Mat in = ncnn::Mat::from_pixels_resize(
            (const unsigned char*)pixels,
            ncnn::Mat::PIXEL_RGBA2RGB,  // 这里可能需要根据 bitmap 格式调整
            img_w, img_h,
            scaled_w, scaled_h
        );
        
        // padding - 修改为与 Python 一致的方式
        int wpad = (target_size + 31) / 32 * 32 - scaled_w;
        int hpad = (target_size + 31) / 32 * 32 - scaled_h;
        
        LOGD("Padding: wpad=%d, hpad=%d", wpad, hpad);
        
        ncnn::Mat in_pad;
        ncnn::copy_make_border(
            in, in_pad,
            hpad/2, hpad - hpad/2,
            wpad/2, wpad - wpad/2,
            ncnn::BorderType::BORDER_CONSTANT,
            114.f
        );
        
        // 归一化 - 与 Python 保持一致
        const float mean_vals[3] = {0.f, 0.f, 0.f};
        const float norm_vals[3] = {1/255.f, 1/255.f, 1/255.f};
        in_pad.substract_mean_normalize(mean_vals, norm_vals);
        
        // 检查网络是否已初始化
        if (net == nullptr) {
            LOGD("Network not initialized");
            AndroidBitmap_unlockPixels(env, bitmap);
            return nullptr;
        }
        
        // 执行推理
        ncnn::Extractor ex = net->create_extractor();
        ex.input("in0", in_pad);
        ncnn::Mat out;
        ex.extract("out0", out);
        
        LOGD("Network output shape: %dx%dx%d", out.w, out.h, out.c);
        
        // 解析检测结果
        std::vector<Object> objects;
        decode_outputs(out, objects, prob_threshold, nms_threshold,
                      img_w, img_h, scale, wpad, hpad);
        
        // 释放 bitmap 像素
        AndroidBitmap_unlockPixels(env, bitmap);
        
        // 转换结果为 Java 对象
        jclass resultClass = env->FindClass("com/boycott/app/ml/YoloDetector$DetectionResult");
        jobjectArray results = env->NewObjectArray(objects.size(), resultClass, nullptr);
        
        for (size_t i = 0; i < objects.size(); i++)
        {
            const Object &obj = objects[i];
            
            jmethodID constructor = env->GetMethodID(resultClass, "<init>",
                                                     "(Ljava/lang/String;FFFFF)V");
            
            jstring label = env->NewStringUTF(obj.label.c_str());
            jobject result = env->NewObject(resultClass, constructor,
                                            label, obj.prob,
                                            obj.rect.x, obj.rect.y,
                                            obj.rect.width, obj.rect.height);
            
            env->SetObjectArrayElement(results, i, result);
        }
        
        LOGD("Detection completed, found %zu objects", objects.size());
        
        // 在 detect 函数中添加
        for (const Object &obj : objects)
        {
            LOGD("Detected object: label=%s, prob=%.2f, box=(%.1f, %.1f, %.1f, %.1f)",
                 obj.label.c_str(), obj.prob,
                 obj.rect.x, obj.rect.y,
                 obj.rect.width, obj.rect.height);
        }
        
        // 在 detect 函数中，转换坐标到屏幕空间
        for (Object &obj : objects)
        {
            // 将归一化坐标转换到实际像素坐标
            obj.rect.x *= info.width;
            obj.rect.y *= info.height;
            obj.rect.width *= info.width;
            obj.rect.height *= info.height;
        }
        
        return results;
    }
}

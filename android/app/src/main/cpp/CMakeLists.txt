cmake_minimum_required(VERSION 3.4.1)
project(yolo_ncnn)

# 设置 NDK 路径变量
set(NDK_PATH ${ANDROID_NDK})

# 设置 OpenCV 路径
set(OpenCV_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../opencv-mobile/sdk/native/jni)
find_package(OpenCV REQUIRED)

# 设置 C++ 标准
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# 添加头文件路径
include_directories(
    ${CMAKE_CURRENT_SOURCE_DIR}/include
    ${CMAKE_CURRENT_SOURCE_DIR}/include/ncnn
    ${OpenCV_INCLUDE_DIRS}
    ${NDK_PATH}/sysroot/usr/include
    ${NDK_PATH}/sysroot/usr/include/${ANDROID_TOOLCHAIN_PREFIX}
    ${NDK_PATH}/sources/cxx-stl/llvm-libc++/include
    ${NDK_PATH}/sources/android/native_app_glue
    ${NDK_PATH}/toolchains/llvm/prebuilt/${ANDROID_HOST_TAG}/sysroot/usr/include/android
    ${NDK_PATH}/toolchains/llvm/prebuilt/${ANDROID_HOST_TAG}/sysroot/usr/include/jni
)

# 添加 NDK 系统头文件路径
include_directories(SYSTEM
    ${NDK_PATH}/toolchains/llvm/prebuilt/${ANDROID_HOST_TAG}/sysroot/usr/include
    ${NDK_PATH}/toolchains/llvm/prebuilt/${ANDROID_HOST_TAG}/sysroot/usr/include/android
    ${NDK_PATH}/toolchains/llvm/prebuilt/${ANDROID_HOST_TAG}/sysroot/usr/include/${ANDROID_TOOLCHAIN_PREFIX}
    ${NDK_PATH}/toolchains/llvm/prebuilt/${ANDROID_HOST_TAG}/include/c++/v1
)

# 添加编译选项
add_compile_options(-fno-rtti -fno-exceptions)

# 添加 native 库
add_library(yolo_ncnn SHARED
            yolo_ncnn.cpp)

# 链接系统库
target_link_libraries(yolo_ncnn
                     ${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libncnn.a
                     ${OpenCV_LIBS}
                     android
                     jnigraphics
                     log)

# 设置 IDE 的 includePath
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

# 添加 NDK 路径到 IDE 的 includePath
target_include_directories(yolo_ncnn PRIVATE
    ${NDK_PATH}/sysroot/usr/include
    ${NDK_PATH}/sysroot/usr/include/${ANDROID_TOOLCHAIN_PREFIX}
    ${NDK_PATH}/toolchains/llvm/prebuilt/${ANDROID_HOST_TAG}/sysroot/usr/include
    ${NDK_PATH}/toolchains/llvm/prebuilt/${ANDROID_HOST_TAG}/sysroot/usr/include/android
    ${NDK_PATH}/toolchains/llvm/prebuilt/${ANDROID_HOST_TAG}/include/c++/v1
    ${OpenCV_INCLUDE_DIRS}
    ${CMAKE_CURRENT_SOURCE_DIR}/../opencv-mobile/sdk/native/jni/include
    ${NDK_PATH}/sources/cxx-stl/llvm-libc++/include
    ${NDK_PATH}/sources/android/native_app_glue
)

# 添加 NDK 系统库路径
link_directories(
    ${NDK_PATH}/toolchains/llvm/prebuilt/${ANDROID_HOST_TAG}/sysroot/usr/lib/${ANDROID_TOOLCHAIN_PREFIX}
)

# 添加 JNI 头文件路径
target_include_directories(yolo_ncnn PRIVATE
    ${NDK_PATH}/toolchains/llvm/prebuilt/${ANDROID_HOST_TAG}/sysroot/usr/include/jni
    ${NDK_PATH}/platforms/android-${ANDROID_NATIVE_API_LEVEL}/arch-${ANDROID_ARCH}/usr/include
)

# 添加 Android NDK 特定的头文件路径
target_include_directories(yolo_ncnn PRIVATE
    ${NDK_PATH}/platforms/android-${ANDROID_NATIVE_API_LEVEL}/arch-${ANDROID_ARCH}/usr/include
    ${NDK_PATH}/toolchains/llvm/prebuilt/${ANDROID_HOST_TAG}/sysroot/usr/include/android
    ${NDK_PATH}/sources/android/ndk/sources/android/native_app_glue
)

# 添加 OpenCV 和 NCNN 的头文件路径
target_include_directories(yolo_ncnn PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/include/ncnn
    ${CMAKE_CURRENT_SOURCE_DIR}/../opencv-mobile/sdk/native/jni/include
)

# 添加 Android NDK 的系统头文件路径
target_include_directories(yolo_ncnn PRIVATE
    ${NDK_PATH}/toolchains/llvm/prebuilt/${ANDROID_HOST_TAG}/sysroot/usr/include/android
    ${NDK_PATH}/toolchains/llvm/prebuilt/${ANDROID_HOST_TAG}/sysroot/usr/include/jni
    ${NDK_PATH}/toolchains/llvm/prebuilt/${ANDROID_HOST_TAG}/sysroot/usr/include/android/bitmap.h
)

# 添加 NDK 平台特定的头文件路径
target_include_directories(yolo_ncnn PRIVATE
    ${NDK_PATH}/platforms/android-${ANDROID_NATIVE_API_LEVEL}/arch-${ANDROID_ARCH}/usr/include
    ${NDK_PATH}/platforms/android-${ANDROID_NATIVE_API_LEVEL}/arch-${ANDROID_ARCH}/usr/include/android
)

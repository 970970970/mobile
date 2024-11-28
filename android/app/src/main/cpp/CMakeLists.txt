cmake_minimum_required(VERSION 3.4.1)
project(yolo_ncnn)

# 设置 C++ 标准
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# 添加头文件路径
include_directories(
    ${CMAKE_CURRENT_SOURCE_DIR}/include
    ${CMAKE_CURRENT_SOURCE_DIR}/include/ncnn
    ${ANDROID_NDK}/sources/cxx-stl/llvm-libc++/include
    ${ANDROID_NDK}/sources/android/native_app_glue
    ${ANDROID_NDK}/platforms/android-24/arch-arm/usr/include
)

# 添加 native 库
add_library(yolo_ncnn SHARED
            yolo_ncnn.cpp)

# 链接系统库
target_link_libraries(yolo_ncnn
                     ${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libncnn.a
                     android
                     log)

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
    ${NDK_PATH}/toolchains/llvm/prebuilt/${ANDROID_HOST_TAG}/sysroot/usr/include
    ${NDK_PATH}/toolchains/llvm/prebuilt/${ANDROID_HOST_TAG}/sysroot/usr/include/android
    ${NDK_PATH}/sources/cxx-stl/llvm-libc++/include
    ${NDK_PATH}/sources/android/native_app_glue
    ${NDK_PATH}/platforms/android-${ANDROID_NATIVE_API_LEVEL}/arch-${ANDROID_ARCH}/usr/include
    ${NDK_PATH}/platforms/android-${ANDROID_NATIVE_API_LEVEL}/arch-${ANDROID_ARCH}/usr/include/android
    ${NDK_PATH}/toolchains/llvm/prebuilt/${ANDROID_HOST_TAG}/lib64/clang/14.0.7/include
    ${NDK_PATH}/toolchains/llvm/prebuilt/${ANDROID_HOST_TAG}/sysroot/usr/local/include
    ${NDK_PATH}/toolchains/llvm/prebuilt/${ANDROID_HOST_TAG}/sysroot/usr/include/${ANDROID_TOOLCHAIN_PREFIX}
    ${OpenCV_INCLUDE_DIRS}
)

# 添加 NDK 头文件路径
include_directories(SYSTEM
    ${NDK_PATH}/sysroot/usr/include
    ${NDK_PATH}/sysroot/usr/include/${ANDROID_TOOLCHAIN_PREFIX}
    ${NDK_PATH}/toolchains/llvm/prebuilt/${ANDROID_HOST_TAG}/sysroot/usr/include
    ${NDK_PATH}/toolchains/llvm/prebuilt/${ANDROID_HOST_TAG}/sysroot/usr/include/${ANDROID_TOOLCHAIN_PREFIX}
    ${NDK_PATH}/toolchains/llvm/prebuilt/${ANDROID_HOST_TAG}/sysroot/usr/include/android
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
set_target_properties(yolo_ncnn PROPERTIES
    INTERFACE_INCLUDE_DIRECTORIES "${include_directories}"
)

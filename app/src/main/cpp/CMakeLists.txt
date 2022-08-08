cmake_minimum_required(VERSION 3.18.1)

#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")
#判断编译器类型,如果是gcc编译器,则在编译选项中加入c++11支持
if (CMAKE_COMPILER_IS_GNUCXX)
    set(CMAKE_CXX_FLAGS "-std=c++11 ${CMAKE_CXX_FLAGS}")
    message(STATUS "optional:-std=c++11")
endif (CMAKE_COMPILER_IS_GNUCXX)

project("opencvface")


#把当前目录作为源文件目录赋值给 SRC_LIST
AUX_SOURCE_DIRECTORY(./ SRC_LIST)

#需要引入我们头文件,以这个配置的目录为基准
include_directories(./include)

# 添加依赖 org.org.opencv.so 库
add_library(
        opencv_java
        SHARED
        IMPORTED)
set_target_properties(
        opencv_java
        PROPERTIES IMPORTED_LOCATION
        ../../../../libs/${ANDROID_ABI}/libopencv_java4.so)


add_library(
        opencvface

        SHARED

        ${SRC_LIST})


find_library(
        log-lib

        log)
target_link_libraries(
        opencvface
        opencv_java
        jnigraphics
        ${log-lib})
cmake_minimum_required(VERSION 3.22.1)

project("trinetra_opencv")

# Configure OpenCV C++ include directories and library paths
# Target ARM64 architecture for iQOO 15 (Snapdragon 8 Elite)
set(CMAKE_CXX_STANDARD 17)

add_library(
        trinetra_opencv
        SHARED
        native_opencv_processor.cpp
)

find_library(
        log-lib
        log
)

target_link_libraries(
        trinetra_opencv
        ${log-lib}
)

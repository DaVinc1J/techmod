cmake_minimum_required(VERSION 3.16)
project(techmod C)

set(CMAKE_C_STANDARD 11)

find_package(JNI REQUIRED)

# Detect platform output directory (mirrors NativeCore.java's lookup logic)
if(CMAKE_SYSTEM_NAME STREQUAL "Linux")
    if(CMAKE_SYSTEM_PROCESSOR MATCHES "aarch64|arm64")
        set(PLATFORM_DIR "linux-aarch64")
    else()
        set(PLATFORM_DIR "linux-x86_64")
    endif()
elseif(CMAKE_SYSTEM_NAME STREQUAL "Darwin")
    if(CMAKE_SYSTEM_PROCESSOR MATCHES "arm64|aarch64")
        set(PLATFORM_DIR "macos-aarch64")
    else()
        set(PLATFORM_DIR "macos-x86_64")
    endif()
elseif(CMAKE_SYSTEM_NAME STREQUAL "Windows")
    if(CMAKE_SYSTEM_PROCESSOR MATCHES "ARM64|aarch64")
        set(PLATFORM_DIR "windows-aarch64")
    else()
        set(PLATFORM_DIR "windows-x86_64")
    endif()
else()
    message(FATAL_ERROR "Unsupported platform: ${CMAKE_SYSTEM_NAME}")
endif()

set(NATIVES_OUT "${CMAKE_SOURCE_DIR}/../src/main/resources/natives/${PLATFORM_DIR}")
message(STATUS "Platform: ${PLATFORM_DIR}")
message(STATUS "Output:   ${NATIVES_OUT}")

add_library(techmod SHARED src/techmod.c)

target_include_directories(techmod PRIVATE
    include
    ${JNI_INCLUDE_DIRS}
)

set_target_properties(techmod PROPERTIES
    PREFIX ""                          # strip the extra "lib" on Windows
    LIBRARY_OUTPUT_DIRECTORY "${NATIVES_OUT}"
    RUNTIME_OUTPUT_DIRECTORY "${NATIVES_OUT}"  # .dll goes here on Windows
    C_VISIBILITY_PRESET hidden
)

if(MSVC)
    target_compile_options(techmod PRIVATE /W4)
else()
    target_compile_options(techmod PRIVATE -Wall -Wextra -fvisibility=hidden)
endif()

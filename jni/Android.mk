LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := JarSimulation

LOCAL_MODULE_FILENAME := libJarSimulation

LOCAL_SRC_FILES := JarSimulation.cpp 
                   
LOCAL_C_INCLUDES := $(LOCAL_PATH) 

LOCAL_STATIC_LIBRARIES := box2d_static
            
include $(BUILD_SHARED_LIBRARY)

$(call import-module,box2d)
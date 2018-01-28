LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libgl2jni
LOCAL_CFLAGS    := -Werror

LOCAL_C_INCLUDES := \
		$(LOCAL_PATH)/ \

LOCAL_SRC_FILES := \
			util/FileUtil.cpp\
			util/MatrixState.cpp\
			util/ShaderUtil.cpp\
			Triangle.cpp\
			gl_code.cpp
LOCAL_LDLIBS    := -llog -lGLESv3 -landroid

include $(BUILD_SHARED_LIBRARY)

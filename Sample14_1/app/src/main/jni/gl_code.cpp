#include <jni.h>//导入头文件
#include <GLES3/gl3.h>
#include <GLES3/gl3ext.h>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "util/MatrixState.h"
#include "util/FileUtil.h"
#include "Triangle.h"
#include <android/log.h>
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "native-activity", __VA_ARGS__))

Triangle *t;//三角形对象指针

bool setupGraphics(int w, int h)//初始化函数
{
    glViewport(0, 0, w, h);//设置视口
    float ratio = (float) w/h;//计算宽长比
    MatrixState::setProjectFrustum(-ratio, ratio, -1, 1, 1, 10);//设置投影矩阵
    MatrixState::setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);//设置摄像机矩阵
    MatrixState::setInitStack();//初始化变换矩阵
    glClearColor(0.5f, 0.5f, 0.5f, 1);//设置背景颜色
    t = new Triangle();	//创建三角形对象
    return true;
}

void renderFrame() {	//渲染函数
    glClear(GL_COLOR_BUFFER_BIT);//清空颜色缓冲
    t->drawSelf();//绘制三角形
    MatrixState::rotate(1,1,0,0);//绕x轴旋转一度
}
//对应于Java那边的本地方法的实现
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_bn_sample14_11_GL2JNILib_init
  (JNIEnv *, jclass, jint width, jint height)//调用初始化函数
{
    setupGraphics(width, height);
}

JNIEXPORT void JNICALL Java_com_bn_sample14_11_GL2JNILib_step
  (JNIEnv *, jclass)//调用渲染函数
{
    renderFrame();
}

JNIEXPORT void JNICALL Java_com_bn_sample14_11_GL2JNILib_nativeSetAssetManager
(JNIEnv* env, jclass cls, jobject assetManager)//调用加载着色器脚本函数
{
	AAssetManager* aamIn = AAssetManager_fromJava( env, assetManager );//初始化AAssetManager对象
    FileUtil::setAAssetManager(aamIn);//设置AAssetManager
}

#ifdef __cplusplus
}
#endif



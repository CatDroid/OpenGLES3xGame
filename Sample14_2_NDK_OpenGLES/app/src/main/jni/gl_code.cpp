#include <jni.h>
#include <android/log.h>
#include <GLES3/gl3.h>
#include <GLES3/gl3ext.h>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "Mountain.h"
#include "util/Matrix.h"
#include "util/MatrixState.h"
#include "util/FileUtil.h"
Mountain *m;
int mountainId;
int rockId;

bool setupGraphics(JNIEnv * env,jobject obj,int w, int h) {

    // 回到Java层的方法 用Bitmap解码图片并加载到纹理
	jclass cl = env->FindClass("com/bn/sample14_2/GL2JNIView");
	jmethodID id = env->GetStaticMethodID(cl,"initTextureRepeat","(Landroid/opengl/GLSurfaceView;Ljava/lang/String;)I");


	jstring name = env->NewStringUTF("grass.png");  // 解码草坪图片 到纹理
    mountainId = env->CallStaticIntMethod(cl,id,obj,name);


    name = env->NewStringUTF("rock.png");           // 解码岩石图片 到纹理
    rockId = env->CallStaticIntMethod(cl,id,obj,name);




    glViewport(0, 0, w, h);
    float ratio = (float) w/h;
    MatrixState::setProjectFrustum(  -ratio, ratio, -1, 1,      1, 1000   ); // 透视投影矩阵  一直不改变


    MatrixState::setCamera(0, 5, 20,/*初始x=0 z=20跟上层一样*/     0, 1, 0,      0, 1, 0);  // 九参数摄像机位置矩阵  后面会动态修改
    MatrixState::setInitStack();
    glEnable(GL_DEPTH_TEST);

    m = new Mountain();
    return true;
}

void renderFrame() {

	glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
	glClearColor(0.0f, 0.0f, 0.0f, 1);
	MatrixState::setInitStack();
    m->drawSelf(mountainId,rockId);
}
#ifdef __cplusplus
extern "C" {
#endif

//对应GL2JNILib类中init本地方法的函数
JNIEXPORT void JNICALL Java_com_bn_sample14_12_GL2JNILib_init(JNIEnv * env, jclass jc, jobject obj,  jint width, jint height)
{
    setupGraphics(env,obj,width, height);//调用初始化函数
}
//对应GL2JNILib类中step本地方法的函数
JNIEXPORT void JNICALL Java_com_bn_sample14_12_GL2JNILib_step(JNIEnv * env, jclass jc)
{
    renderFrame();//调用渲染函数
}
//对应GL2JNILib类中setCamera本地方法的函数
JNIEXPORT void JNICALL Java_com_bn_sample14_12_GL2JNILib_setCamera
  (JNIEnv * env, jclass jc, jfloat cx, jfloat cy, jfloat cz, jfloat tx, jfloat ty, jfloat tz, jfloat upx, jfloat upy, jfloat upz)
{
	MatrixState::setCamera( cx,5,cz,     tx,1,tz, /*y轴方向没有改变*/      0,1,0);//设置摄像机矩阵
}
//对应GL2JNILib类中nativeSetAssetManager本地方法的函数
JNIEXPORT void JNICALL Java_com_bn_sample14_12_GL2JNILib_nativeSetAssetManager
  (JNIEnv * env, jclass jc, jobject assetManager)
{
	AAssetManager* aamIn = AAssetManager_fromJava( env, assetManager );//获取AassetManager指针
	FileUtil::setAAssetManager(aamIn);//设置AAssetManager
}
#ifdef __cplusplus
}
#endif

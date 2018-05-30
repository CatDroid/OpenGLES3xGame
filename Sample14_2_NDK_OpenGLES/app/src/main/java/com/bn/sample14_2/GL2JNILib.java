package com.bn.sample14_2;

import android.content.res.AssetManager;
import android.opengl.GLSurfaceView;

public class GL2JNILib {
     static {
         System.loadLibrary("gl2jni");//加载so动态库
     }
     public static native void init(GLSurfaceView gsv,int width, int height);//本地初始化方法
     public static native void step();//本地刷新场景方法
     public static native void setCamera(    // 用于触摸时候 调整摄像机的位置 !!
    		float cx,	//摄像机位置x
     		float cy,   //摄像机位置y
     		float cz,   //摄像机位置z
     		float tx,   //摄像机目标点x
     		float ty,   //摄像机目标点y
     		float tz,   //摄像机目标点z
     		float upx,  //摄像机UP向量X分量
     		float upy,  //摄像机UP向量Y分量
     		float upz   //摄像机UP向量Z分量		
     		);
     //将AssetManager传入C++的方法
     public static native void nativeSetAssetManager(AssetManager am); 
}

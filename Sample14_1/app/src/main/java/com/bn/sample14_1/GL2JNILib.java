package com.bn.sample14_1;//声明包名

import android.content.res.AssetManager;

public class GL2JNILib 
{
     static 
     {
         System.loadLibrary("gl2jni");//加载so动态库
     }
     public static native void init(int width, int height);//本地初始化方法
     public static native void step();//本地刷新场景方法
     public static native void nativeSetAssetManager(AssetManager am); 	//将AssetManager传入C++的方法
}

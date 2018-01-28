package com.bn.sample14_2;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class Sample14_2Activity extends Activity {

    GL2JNIView mView;//声明GL2JNIView类的引用
	//屏幕对应的宽度和高度
	static float WIDTH;
	static float HEIGHT;
	
    @Override 
    protected void onCreate(Bundle icicle) {//继承Activity后重写的onCreate方法
        super.onCreate(icicle);
        GL2JNILib.nativeSetAssetManager(this.getAssets());//将AssetManager传入C++
        //设置为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
		              WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//获得系统的宽度以及高度
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        if(dm.widthPixels>dm.heightPixels)
        {
        	WIDTH=dm.widthPixels;
        	HEIGHT=dm.heightPixels;
        }
        else
        {
        	WIDTH=dm.heightPixels;
        	HEIGHT=dm.widthPixels; // 用于 GL2JNIView的触摸处理 !!!
        }
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 横屏!!!!
        mView = new GL2JNIView(getApplication());//创建GL2JNIView类的对象
        mView.requestFocus();//获取焦点
        mView.setFocusableInTouchMode(true);//设置为可触控  
        setContentView(mView);
    }

    @Override protected void onPause() {//继承Activity后重写的onPause方法法
        super.onPause();
        mView.onPause();				//调用GL2JNIView类对象的onPause方法
    }

    @Override protected void onResume() {//继承Activity后重写的onResume方法
        super.onResume();
        mView.onResume();				//调用GL2JNIView类对象的onResume方法
    }
}

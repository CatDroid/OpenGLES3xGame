package com.bn.Sample3_1;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class Sample3_1Activity extends Activity//创建继承Activity的主控制类
{
	MyTDView mview;//声明MyTDView类的引用
    @Override
    public void onCreate(Bundle savedInstanceState)//继承Activity后重写的方法
    {
        super.onCreate(savedInstanceState);//调用父类
        //设置为竖屏模式
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mview=new MyTDView(this);//创建MyTDView类的对象
        mview.requestFocus();//获取焦点
        mview.setFocusableInTouchMode(true);//设置为可触控
        setContentView(mview);
    }
    @Override
    public void onResume()//继承Activity后重写的onResume方法
    {
    	super.onResume();
    	mview.onResume();//通过MyTDView类的对象调用onResume方法
    }
    @Override
    public void onPause()//继承Activity后重写的onPause方法
    {
    	super.onPause();
    	mview.onPause();//通过MyTDView类的对象调用onPause方法
    }
}
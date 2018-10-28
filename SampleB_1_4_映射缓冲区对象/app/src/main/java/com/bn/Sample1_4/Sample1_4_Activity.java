package com.bn.Sample1_4;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;


public class Sample1_4_Activity extends Activity {
	MySurfaceView mySurfaceView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
		              WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//设置为竖屏模式
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		//初始化GLSurfaceView
		mySurfaceView = new MySurfaceView(this);
		mySurfaceView.requestFocus();//获取焦点
		mySurfaceView.setFocusableInTouchMode(true);//设置为可触控  
		//切换到主界面
		setContentView(mySurfaceView);		
    }
    @Override
    protected void onResume() {
        super.onResume();
        mySurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mySurfaceView.onPause();
    } 
    
    
    public boolean onKeyDown(int keyCode,KeyEvent e)
    {
    	switch(keyCode)
        	{
    	case 4:
    		System.exit(0);
    		break;
        	}
    	return true;
    };
}
package com.bn.Sample8_4;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class MyActivity extends Activity {
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
		//切换到主界面
		setContentView(R.layout.main);		
		//初始化GLSurfaceView
		mySurfaceView = new MySurfaceView(this);
		mySurfaceView.requestFocus();//获取焦点
		mySurfaceView.setFocusableInTouchMode(true);//设置为可触控  
        //将自定义的GLSurfaceView添加到外层LinearLayout中
        LinearLayout ll=(LinearLayout)findViewById(R.id.main_liner); 
        ll.addView(mySurfaceView); 
        
        
        //为RadioButton添加监听器及SxT选择代码
        RadioButton rab=(RadioButton)findViewById(R.id.RadioButton01);
        rab.setOnCheckedChangeListener(
            new OnCheckedChangeListener()
            {
     			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) 
     			{
     				//面填充
     				if(isChecked)
     				{
     					mySurfaceView.drawWhatFlag=true;
     				}
     			}        	   
            }         		
        );       
        rab=(RadioButton)findViewById(R.id.RadioButton02);
        
        rab.setOnCheckedChangeListener(
            new OnCheckedChangeListener()
            {
     			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) 
     			{
     				//线填充
     				if(isChecked)
     				{
     					mySurfaceView.drawWhatFlag=false;
     				}
     			}        	   
            }         		
        );         
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
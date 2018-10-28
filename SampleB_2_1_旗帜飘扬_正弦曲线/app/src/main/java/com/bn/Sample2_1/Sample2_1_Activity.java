package com.bn.Sample2_1;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Sample2_1_Activity extends Activity {
	private MySurfaceView mGLSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);         
        //设置为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
		              WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//设置为横屏模式
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		//切换到主界面
			
		//切换到主界面
		setContentView(R.layout.main);		
		//初始化GLSurfaceView
        mGLSurfaceView = new MySurfaceView(this);
        mGLSurfaceView.requestFocus();//获取焦点
        mGLSurfaceView.setFocusableInTouchMode(true);//设置为可触控  
        //将自定义的GLSurfaceView添加到外层LinearLayout中
        LinearLayout ll=(LinearLayout)findViewById(R.id.main_liner); 
        ll.addView(mGLSurfaceView);  
        //为RadioButton添加监听器及着色器选择代码
        RadioButton rb=(RadioButton)findViewById(R.id.x1);
        rb.setOnCheckedChangeListener(
            new OnCheckedChangeListener()
            {
     			@Override
     			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) 
     			{
     				if(isChecked)
     				{//设置波浪为X方向
     					mGLSurfaceView.mRenderer.texRect.currIndex=0;
     				}
     			}        	   
            }         		
        );    
        
        rb=(RadioButton)findViewById(R.id.x2);
        rb.setOnCheckedChangeListener(
            new OnCheckedChangeListener()
            {
     			@Override
     			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) 
     			{
     				if(isChecked)
     				{//设置波浪为斜向
     					mGLSurfaceView.mRenderer.texRect.currIndex=1;
     				}
     			}        	   
            }         		
        );   
        
        rb=(RadioButton)findViewById(R.id.x3);
        rb.setOnCheckedChangeListener(
            new OnCheckedChangeListener()
            {  
     			@Override
     			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) 
     			{
     				if(isChecked)
     				{//设置波浪为XY双向波浪
     					mGLSurfaceView.mRenderer.texRect.currIndex=2;
     				}
     			}        	   
            }         		
        );  
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
        Constant.threadFlag=true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
        Constant.threadFlag=false;
    }    
}




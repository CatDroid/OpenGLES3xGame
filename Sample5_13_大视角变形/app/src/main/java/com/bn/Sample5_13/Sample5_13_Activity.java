package com.bn.Sample5_13;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import static com.bn.Sample5_13.Constant.*;

public class Sample5_13_Activity extends Activity {
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
 		setContentView(R.layout.main);		
 		//初始化GLSurfaceView
         mGLSurfaceView = new MySurfaceView(this);
         mGLSurfaceView.requestFocus();//获取焦点
         mGLSurfaceView.setFocusableInTouchMode(true);//设置为可触控  
         //将自定义的GLSurfaceView添加到外层LinearLayout中
         LinearLayout ll=(LinearLayout)findViewById(R.id.main_liner); 
         ll.addView(mGLSurfaceView);        
         //控制是否打开背面剪裁的ToggleButton
         ToggleButton tb=(ToggleButton)this.findViewById(R.id.ToggleButton01);
         tb.setOnCheckedChangeListener(
             new OnCheckedChangeListener()
             {
 	 			@Override
 	 			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) 
 	 			{
 	 				if(isChecked)
 	 				{
 	 				    //视角不合适导致变形的情况
 	 		            //调用此方法计算产生透视投影矩阵
 	 		            MatrixState.setProjectFrustum(-ratio*0.7f, ratio*0.7f, -0.7f, 0.7f, 1, 10);
 	 		            //调用此方法产生摄像机观察矩阵
 	 		            MatrixState.setCamera(0,0.5f,4,0f,0f,0f,0f,1.0f,0.0f);

                        android.util.Log.w("TOM","视角不合适导致变形的情况 垂直视角 fovy = " + 2*Math.atan(ratio*0.7f/1) );
                        // 1.924942658315621
 	 				}
 	 				else
 	 				{
	 	 	             //视角合适不变形的情况
	 	 	             //调用此方法计算产生透视投影矩阵
	 	 	             MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 20, 100);
	 	 	             //调用此方法产生摄像机观察矩阵
	 	 	             MatrixState.setCamera(0,8f,45,0f,0f,0f,0f,1.0f,0.0f);

                        android.util.Log.w("TOM","视角合适不变形的情况 垂直视角 fovy = " + 2*Math.atan(ratio / 20 ) );
                        //  0.20441345360727425
 	 				}
 	 			}
             }
         );        
         
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause(); 
    } 
}
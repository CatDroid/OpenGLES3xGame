package com.bn.catcherFun;

import com.bn.constant.Constant;
import com.bn.util.manager.SoundManager;
import com.bn.util.screenscale.ScreenScaleUtil;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import static com.bn.constant.SourceConstant.*;
public class MainActivity extends Activity {
	private MySurfaceView mGLSurfaceView;
	public static SoundManager sound;
	public static SharedPreferences.Editor editor;
	public static SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		sp=this.getSharedPreferences("bn", Context.MODE_PRIVATE);    
        editor=sp.edit();	//取得编辑对象，来修改Preferences文件    
        String firstStr=sp.getString("count",null);//获取键为“time”的值  
//	         
        System.out.println("firstStr::"+firstStr);
        if(firstStr==null)
        {
        	
        	editor.putString("count", Integer.toString(20));
             editor.commit();	//提交修改                	
        }
        moneycount = Integer.parseInt(sp.getString("count",null));
        System.out.println("moneycount    moneycount:"+moneycount);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉标头
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constant.ssr=ScreenScaleUtil.calScale(dm.widthPixels, dm.heightPixels);
        
        sound=new SoundManager(this);
        mGLSurfaceView = new MySurfaceView(this);
        mGLSurfaceView.requestFocus();//获取焦点
        mGLSurfaceView.setFocusableInTouchMode(true);//设置为可触控
        
        setContentView(mGLSurfaceView);
        
       
        
//        String initmoneycount=sp.getString("count",null);//获取键为“time”的值  
//        editor=sp.edit();	//取得编辑对象，来修改Preferences文件     
//        if(initmoneycount==null)
//        {
//        	 editor.putString("count", Integer.toString(moneycount));
//
//             editor.commit();	//提交修改        
//        	
//        }
//        System.out.println("moneycount:   "+moneycount+"   initmoneycount  " +initmoneycount);
      
	}

	 @Override
    protected void onResume() {
        super.onResume();
//        mGLSurfaceView.onResume();
        if(MainActivity.sound.mp!=null)
   		{
        	MainActivity.sound.mp.start();
   		}
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mGLSurfaceView.onPause();
        if(MainActivity.sound.mp!=null)
   		{
        	MainActivity.sound.mp.pause();
   		}
    }   
}

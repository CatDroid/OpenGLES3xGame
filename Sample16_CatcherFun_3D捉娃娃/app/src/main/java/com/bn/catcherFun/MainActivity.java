package com.bn.catcherFun;

import com.bn.constant.Constant;
import com.bn.util.manager.SoundManager;
import com.bn.util.screenscale.ScreenScaleUtil;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import static com.bn.constant.SourceConstant.*;
public class MainActivity extends Activity {

    static private final String TAG = "MainActivity" ;

	private MySurfaceView mGLSurfaceView;

	public static SharedPreferences.Editor editor;
	public static SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

        Log.w(TAG,"[onCreate]");
		super.onCreate(savedInstanceState);

		sp=this.getSharedPreferences("bn", Context.MODE_PRIVATE);    
        editor=sp.edit();	//取得编辑对象，来修改Preferences文件    
        String firstStr=sp.getString("count",null);//获取键为“time”的值  
//	         
        System.out.println("firstStr::"+firstStr);
        if(firstStr==null) {
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
        // 华为V10 1080(1088)x2160 使用全屏返回的是 1080x2140
        Constant.ssr=ScreenScaleUtil. calScale(dm.widthPixels, dm.heightPixels);
        // 用于做触摸坐标转换 fromRealScreenXToStandardScreenX   物理屏幕坐标 --> 缩放后的屏幕坐标 -->  1080x1920标准屏幕的坐标


        SoundManager sm = SoundManager.getInstance(false);
        if(sm!=null)sm.init(getApplicationContext());

        mGLSurfaceView = new MySurfaceView(this);
        mGLSurfaceView.requestFocus();//获取焦点
        mGLSurfaceView.setFocusableInTouchMode(true);//设置为可触控
        
        setContentView(mGLSurfaceView);
        
       
        
//        String initmoneycount=sp.getString("count",null);//获取键为“time”的值  
//        editor=sp.edit();	//取得编辑对象，来修改Preferences文件     
//        if(initmoneycount==null)
//        {
//        	 editor.putString("count", Integer.toS tring(moneycount));
//
//             editor.commit();	//提交修改        
//        	
//        }
//        System.out.println("moneycount:   "+moneycount+"   initmoneycount  " +initmoneycount);
      
	}

	 @Override
    protected void onResume() {
        Log.w(TAG,"[onResume]");
        super.onResume();
         // GLSurfaceView.onPause会导致EGLContext销毁
         // 开启不保留活动会把Activity销毁导致GLSurfaceView也会销毁,会重新加载资源
//        mGLSurfaceView.onResume();

         SoundManager.instance().resumeBackGroundMusic();

    }

    @Override
    protected void onPause() {
        Log.w(TAG,"[onPause]");
        super.onPause();
//        mGLSurfaceView.onPause();
        SoundManager.instance().pauseBackGroundMusic();
    }

    @Override
    protected void onStart() {
        Log.w(TAG,"[onStart]");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.w(TAG,"[onRestart]");
        super.onRestart();
    }

    @Override
    protected void onStop() {
        Log.w(TAG,"[onStop]");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.w(TAG,"[onDestroy]");
        super.onDestroy();
        mGLSurfaceView.destroy();
        mGLSurfaceView = null;

        SoundManager.instance().destroy();
    }
}

package com.bn.catcherFun;

import static com.bn.constant.SourceConstant.*;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import com.bn.MatrixState.MatrixState2D;
import com.bn.MatrixState.MatrixState3D;
import com.bn.hand.R;
import com.bn.util.manager.ShaderManager;
import com.bn.view.BNAbstractView;
import com.bn.view.GameView;
import com.bn.view.LoadView;
import com.bn.view.MainView;
import com.bn.view.MenuView;
import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

public class MySurfaceView extends GLSurfaceView{
	public MainActivity  activity;
	private SceneRenderer mRenderer;//场景渲染器	
	public BNAbstractView currView;//当前界面
	public GameView gameView;//当前界面
	public boolean isInitOver = false;						//资源是否初始化完毕
	public MainView mainView;
	public BNAbstractView collectionview;
	public MenuView  menuview;
	public BNAbstractView YXJXView;//游戏教学界面
	public BNAbstractView GameAboutView;
	public BNAbstractView ScoreView;
	private static boolean isExit = false;
	public MySurfaceView(Context context) 
	{
		super(context);
		activity=(MainActivity) context;
        this.setEGLContextClientVersion(3);//设置GLES版本为3.0  
        
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
       
	}
	//触摸事件回调方法
    @Override
    public boolean onTouchEvent(MotionEvent e) 
    {
    	if(currView==null)
		{
			return false;
		}
		return currView.onTouchEvent(e);
    	
	
    }   
    public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if(currView==gameView)
			{
				if(gameView.isMenu)
				{
					currView=gameView;
					gameView.isMenu=false;
				}else
				{
					currView=mainView;
//					if(!isBGMusic){
//						//创建音乐
//						if(!musicOff){
//							MainActivity.sound.playBackGroundMusic(activity, R.raw.nogame);
//						}
//					}
				}
					
			}else if(currView==ScoreView)
			{
				currView=mainView;
			}else if(currView==YXJXView)
			{
				currView=mainView;
			}else if(currView==GameAboutView)
			{
				currView=mainView;
			}else if(currView==collectionview)
			{
				if(isCollection)
				{
					currView=gameView;
					isCollection=false;
	     		     gameView.isMenu=false;
	     			 gameView.reData();
				}else 
				{
					currView=mainView;
//					if(!isBGMusic){
//						//创建音乐
//						if(!musicOff){
//							MainActivity.sound.playBackGroundMusic(activity, R.raw.nogame);
//						}
//					}
				}
				 if(isSet)
	    		  {
	    			  isSet=false;
	    			  currView=mainView;
	    		  }
			
			}else if(currView==mainView)//只有处于主界面时才可以按返回键返回桌面
			{
				if(isSet)
				{
					isSet=false;
					currView=mainView;
				}else
				{
					exit();
				}
				
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void exit()
	{
		if (isExit == false) 
		{
			isExit = true; // 准备退出
			Toast.makeText(this.getContext(),"再按一次退出游戏", Toast.LENGTH_SHORT).show();
			new Handler().postDelayed(new Runnable()
			{
				public void run()
				{
					isExit = false;
					isBGMusic=true;
					effictOff=true;
				}
			}, 2500);
		}else
		{
			android.os.Process.killProcess(android.os.Process.myPid()); 
		}
	}
	
    private class SceneRenderer implements GLSurfaceView.Renderer 
    {
    	public void onDrawFrame(GL10 gl) 
		{
    		GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
    		if(currView != null)
			{
				currView.drawView(gl);//绘制界面信息
			}
        
		}
		public void onSurfaceChanged(GL10 gl, int width, int height) 
		{
			 //设置视口大小及位置 
			GLES30.glViewport
			(
					0,//(int)Constant.ssr.lucX,//x
					0,//(int)Constant.ssr.lucY,//y
					width,
					height
			);
			
			 float ratio= (float) width/height;
            screenWidth = width;
            screenHeight = height;
            MatrixState3D.setInitStack();
           
            //调用此方法计算产生透视投影矩阵
           
            
                MatrixState3D.setProjectFrustum(-ratio, ratio, -1, 1, 1.5f, 100);  
        
            MatrixState3D.setCamera( 
            		EYE_X,   //人眼位置的X
            		EYE_Y, 	//人眼位置的Y
            		EYE_Z,   //人眼位置的Z
            		TARGET_X, 	//人眼球看的点X
            		TARGET_Y,   //人眼球看的点Y
            		TARGET_Z,   //人眼球看的点Z
            		0, 
            		1, 
            		0);
            MatrixState2D.setInitStack();
        	MatrixState2D.setCamera(0,0,5,0f,0f,0f,0f,1f,0f);
        	MatrixState2D.setProjectOrtho(-ratio, ratio, -1, 1, 1, 100);
        	 //调用此方法产生摄像机9参数位置矩阵
	        MatrixState2D.setCamera(0,0,5,0f,0f,0f,0f,1f,0f);
			MatrixState2D.setLightLocation(0,50,0);
			if(currView==null){
				LoadView lv=new LoadView(MySurfaceView.this);
				if(!isBGMusic){
				//创建音乐
				if(!musicOff){
					MainActivity.sound.playBackGroundMusic(activity, R.raw.nogame);
				}
			}
				currView=lv;
				lv=null;
			}			
			
		}
		public void onSurfaceCreated(GL10 gl, EGLConfig config) 
		{
			//设置屏幕背景色RGBA
            GLES30.glClearColor(0.0f,0.0f,0.0f, 1.0f);
           
          //设置为打开背面剪裁
            GLES30.glEnable(GL10.GL_CULL_FACE);
            ShaderManager.loadCodeFromFile(activity.getResources());
            ShaderManager.compileShader();
		}
    }
    
}

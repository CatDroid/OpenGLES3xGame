package com.bn.interpolation;

import android.opengl.GLSurfaceView;
import android.opengl.GLES30;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


import android.content.Context;

class MySurfaceView extends GLSurfaceView 
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
    private SceneRenderer mRenderer;//场景渲染器    
    
    private float mPreviousY;//上次的触控位置Y坐标
    private float mPreviousX;//上次的触控位置X坐标
    
    int textureId;//系统分配的纹理id
	
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }
	


	private class SceneRenderer implements GLSurfaceView.Renderer 
    {  

    	
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear(  GLES30.GL_COLOR_BUFFER_BIT);
//            GLES30.glDisable(GLES30.GL_CULL_FACE);
//            GLES30.glDisable(GLES30.GL_DEPTH_TEST);
//            GLES30.glDisable(GLES30.GL_STENCIL_TEST);
//            GLES30.glEnable(GLES30.GL_DITHER);
            mInterpolation.draw();

        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Log.e("TOM",width+","+height);
        	GLES30.glViewport(0, 0, width, height);

        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) 
        {
            GLES30.glClearColor(0.0f,1.0f,1.0f,1.0f);


            mInterpolation = new Interpolation();
            mInterpolation.init(MySurfaceView.this.getResources());
        }
    }

    public Interpolation mInterpolation ;

}

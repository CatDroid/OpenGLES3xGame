package com.bn.Sample6_5;

import android.opengl.GLSurfaceView;
import android.opengl.GLES30;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;

class MySurfaceView extends GLSurfaceView 
{
    private SceneRenderer mRenderer;//场景渲染器
    
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {   
        GrainForDraw grainForDraw;	//粒子系统
		
        //测试刷帧频率的代码
		long olds;
    	long currs;
        
        public void onDrawFrame(GL10 gl) 
        {
        	//注释掉的为测试刷帧频率的代码
        	currs=System.nanoTime();
        	Log.d("FPS", (1000000000.0/(currs-olds)+"FPS"));
			olds=currs;
    	
        	
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            //保护现场
            MatrixState.pushMatrix();
            MatrixState.translate(0,1,0);
            //绘制粒子系统
            grainForDraw.drawSelf();
            //恢复现场
            MatrixState.popMatrix();
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 1000);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0,0,20,0,0,0,0f,1.0f,0.0f);
        }
        
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0.0f,0.0f,0.0f,1.0f);
            //创建焰火粒子系统
            grainForDraw=new GrainForDraw(MySurfaceView.this,
            			2,		//点的大小
            			400	//点的个数
            			);
            
            //打开深度检测  
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //关闭背面剪裁   
            GLES30.glEnable(GLES30.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();
        }
    }
}

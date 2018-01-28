package com.bn.Sample3_1;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

public class MyTDView extends GLSurfaceView
{
	final static String TAG = "MyTDView";
	final float ANGLE_SPAN = 0.375f;
	
	RotateThread rthread;
	SceneRenderer mRenderer;//自定义渲染器的引用
	public MyTDView(Context context)
	{
		super(context);
		this.setEGLContextClientVersion(3);
		mRenderer=new SceneRenderer();
		this.setRenderer(mRenderer);
		this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		//this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}
	private class SceneRenderer implements GLSurfaceView.Renderer
	{
		Triangle tle;
		public void onDrawFrame(GL10 gl)
		{
			//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            //绘制三角形对象
            tle.drawSelf();    
		}
		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
			//设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); // 这个是屏幕坐标系 原点是在左下角
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生             透视投影矩阵 (根据屏幕宽高比)  要沾满屏幕的高  视椎体
            Matrix.frustumM(Triangle.mProjMatrix, 0,    -ratio, ratio, -1 , 1,   1.0f /*near*/, 10/*far*/ /*视椎体是在摄像机的坐标系*/);
            //调用此方法产生                九参数 摄像机位置矩阵  setLookAtM就是坐标系转换成另外一3个基向量表示的坐标系
            Matrix.setLookAtM(Triangle.mVMatrix, 0,
									0,0,3,/*摄像机的世界坐标 注意跟物体旋转的距离*/
									0f,0f,0f,
									0,1,0 //  0.707f , 0.707f ,0.0f
									 );

			// Log.d(TAG, "mVMatrix " + ShaderUtil.printMatricx(Triangle.mVMatrix)); // 摄像机z轴方向跟世界坐标系一样   也是右手准则
		}
		public void onSurfaceCreated(GL10 gl, EGLConfig config)
		{
			// 设置屏幕背景色RGBA
            GLES30.glClearColor(0,0,0,1.0f);

			//GLES30.glFrontFace(GLES30.GL_CW); // 如果没有glEnable(GL_CULL_FACE)可以不用设置; 在默认情况下，mode是GL_CCW
            // 创建三角形对对象
            tle=new Triangle(MyTDView.this);
            // 打开深度检测  hhl:这个例子不使用也可以
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
    		rthread=new RotateThread();
    		rthread.start();
		}
	}
	public class RotateThread extends Thread//自定义的内部类线程
	{
		public boolean flag=true;
		@Override
		public void run()//重写的run方法
		{
			while(flag)
			{
				mRenderer.tle.xAngle=mRenderer.tle.xAngle+ANGLE_SPAN;
				try
				{
					Thread.sleep(20);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
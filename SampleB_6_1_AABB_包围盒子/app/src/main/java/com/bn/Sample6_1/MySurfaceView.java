package com.bn.Sample6_1;

import java.util.ArrayList;
import android.opengl.GLSurfaceView;
import android.opengl.GLES30;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;

class MySurfaceView extends GLSurfaceView 
{
    private SceneRenderer mRenderer;//场景渲染器
    LovoGoThread lgt;
    ArrayList<RigidBody> aList=new ArrayList<RigidBody>();

	public MySurfaceView(Context context)
    {
        super(context);
        this.setEGLContextClientVersion(3);                     // 设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	                    // 创建场景渲染器
        setRenderer(mRenderer);				                    // 设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);   // 设置渲染模式为主动渲染
    }
	
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {
    	// 从指定的obj文件中加载对象
		LoadedObjectVertexNormal ch;
		LoadedObjectVertexNormal pm;
    	
        public void onDrawFrame(GL10 gl) 
        { 
    		// 清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

        	for(int i=0;i<aList.size();i++)
        	{
        		aList.get(i).drawSelf();
        	}
        	pm.drawSelf();          
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {

            // 设置视窗大小及位置
        	GLES30.glViewport(0, 0, width, height);

            // 调用此方法计算产生透视投影矩阵
            float ratio = (float) width / height;
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 4, 100);

            // 调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0,13,40,0,0f,-10f,0f,1.0f,0.0f);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config)
        {

            // 设置屏幕背景色RGBA
            GLES30.glClearColor(0.0f,0.0f,0.0f,1.0f);    
            // 打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            // 打开背面剪裁
            GLES30.glEnable(GLES30.GL_CULL_FACE);

            // 初始化变换矩阵
            MatrixState.setInitStack();
            // 初始化光源位置
            MatrixState.setLightLocation(40, 10, 20);

            // 加载要绘制的物体
            ch = LoadUtil.loadFromFile("ch.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            pm = LoadUtil.loadFromFile("pm.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            aList.add(new RigidBody(ch,true,new Vector3f(-13f,0f,0),new Vector3f(0,0,0)));
            aList.add(new RigidBody(ch,true,new Vector3f(13f,0f,0),new Vector3f(0,0,0)));		
            aList.add(new RigidBody(ch,false,new Vector3f(0f,0f,0),new Vector3f(0.1f,0,0)));

            // 启动线程 更新物体位置 检查碰撞 更新速度方向
            lgt = new LovoGoThread(aList);
            lgt.start();
        }
    }
}
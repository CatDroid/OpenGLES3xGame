package com.bn.sample14_1;//声明包名

import android.content.Context;//相关类的引入
import android.opengl.GLSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class GL2JNIView extends GLSurfaceView 
{
    Renderer renderer;//自定义渲染器的引用

    public GL2JNIView(Context context) //构造器
    {
        super(context);
		this.setEGLContextClientVersion(3);//使用OpenGL ES 3.0需设置该参数为3
		renderer=new Renderer();//创建Renderer类的对象
		this.setRenderer(renderer);	//设置渲染器
		this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    private static class Renderer implements GLSurfaceView.Renderer 
    {
        public void onDrawFrame(GL10 gl) 
        {
            GL2JNILib.step();//调用本地方法刷新场景
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) 
        {
            GL2JNILib.init(width, height);//调用本地方法初始化
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) 
        {
        	
        }
    }
}

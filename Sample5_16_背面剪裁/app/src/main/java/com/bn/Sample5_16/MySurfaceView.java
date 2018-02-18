package com.bn.Sample5_16;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

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
	
	boolean cullFaceFlag=false;//是否开启背面剪裁的标志位
	//设置是否开启背面剪裁的标志位
	public void setCullFace(boolean flag)
	{
		cullFaceFlag=flag;
	}
	
	boolean cwCcwFlag=false;//是否打开自定义卷绕的标志位
	//设置是否打开自定义卷绕的标志位
	public void setCwOrCcw(boolean flag)
	{
		cwCcwFlag=flag;
	}
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {

		// 管线流程
		// 顶点/顶点相关属性cpu传到gpu --> 顶点着色器(坐标转换)-->图片装配(组装和剪裁)
		// --> 光栅化(图元成片元) -->  片元着色器 --> 裁剪测试(卷绕 CW CCW 背面裁剪) --> 深度测试/模板测试
		// --> 颜色混合 --> 抖动 --> 帧缓冲

		TrianglePair tp;//三角形对对象引用
		public void onDrawFrame(GL10 gl) 
		{
			if(cullFaceFlag)
			{									//判断是否要打开背面剪裁
				GLES30.glEnable(GLES30.GL_CULL_FACE);			//打开背面剪裁
			}
			else
			{
				GLES30.glDisable(GLES30.GL_CULL_FACE);			//关闭背面剪裁
			}
			if(cwCcwFlag) 
			{									//判断是否需要打开顺时针卷绕
				GLES30.glFrontFace(GLES30.GL_CCW);			//使用逆时针卷绕
			}
			else
			{
				GLES30.glFrontFace(GLES30.GL_CW);				//使用顺时针卷绕
			}
			GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT |GLES30.GL_COLOR_BUFFER_BIT);//清除深度缓冲与颜色缓冲
			MatrixState.pushMatrix();            					//保护现场
		 	MatrixState.translate(0, -1.4f, 0);						//沿y轴负方向平移
			tp.drawSelf();										//绘制三角形对
			MatrixState.popMatrix();        						//恢复现场
		}  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视口的大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算视口的宽高比
            Constant.ratio = (float) width / height;
			// 调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-Constant.ratio, Constant.ratio, -1, 1, 10, 100);
			// 调用此方法产生摄像机矩阵
			MatrixState.setCamera(0, 0f, 20, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            
            //初始化变换矩阵
            MatrixState.setInitStack();
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0.5f,0.5f,0.5f, 1.0f);  
            //创建三角形对对象
            tp=new TrianglePair(MySurfaceView.this);
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        }
    }
}

package com.bn.Sample3_3;
import java.io.IOException;
import java.io.InputStream;
import android.opengl.GLSurfaceView;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

class MySurfaceView extends GLSurfaceView 
{
    private SceneRenderer mRenderer;                            // 场景渲染器
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3);                     // 设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	                    // 创建场景渲染器
        setRenderer(mRenderer);				                    // 设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);   // 设置渲染模式为主动渲染
    }
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {
		TextureRect pmBase;
		TextureRectJJ pmJJ;
		int pmTexId;
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);            
            MatrixState.pushMatrix(); 
            MatrixState.translate(-RECT_LENGTH/2, 0, 0);
            pmBase.drawSelf(pmTexId);//平面
            MatrixState.popMatrix();    
            
            MatrixState.pushMatrix();  
            MatrixState.translate(RECT_LENGTH/2, 0, 0);
            pmJJ.drawSelf(pmTexId);//平面
            MatrixState.popMatrix();    
        }  

        // hhl 调整 位移 和 摄像机位置 的计算方式:
        final private float NEAR_Z = 2 ; // 投影近平面
        final private float RECT_LENGTH = 17.0f; // 矩形宽高

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //设置camera位置
            MatrixState.setCamera
            (
            		0,	//人眼位置的X
            		0, //人眼位置的Y
                    RECT_LENGTH * NEAR_Z / ratio , // hhl 按比例算出近平面
            		0, 	//人眼球看的点X
            		0,  //人眼球看的点Y
            		0,  //人眼球看的点Z
            		0, 	//up向量
            		1, 
            		0
            );
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);
        }
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(1.f,1.0f,1.0f,1.0f);    
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //打开背面剪裁   
            GLES30.glEnable(GLES30.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();  
    		pmBase = new TextureRect(MySurfaceView.this, RECT_LENGTH, RECT_LENGTH, 1, 1);
    		pmJJ = new TextureRectJJ(MySurfaceView.this, RECT_LENGTH, RECT_LENGTH, 1, 1);
    		pmTexId=initTexture(R.drawable.pm);
        }
    }

	public int initTexture(int drawableId)
	{

		int[] textures = new int[1];
		GLES30.glGenTextures(1, textures, 0);
		int textureId=textures[0];    
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);

		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);

		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_REPEAT);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_REPEAT);		

        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try {bitmapTmp = BitmapFactory.decodeStream(is);}
        finally {
            try {is.close();} catch(IOException e) {e.printStackTrace();}
        }

        // 实际加载纹理,换成这个方法后，如果图片格式有问题，会抛出图片格式异常，不再会误显示其他异常
	   	GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, GLUtils.getInternalFormat(bitmapTmp), bitmapTmp, GLUtils.getType(bitmapTmp), 0);

        // 自动生成Mipmap纹理 ??hhl  这个应该没有用 纹理采样方式没有用MIPMAP ??
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);


        bitmapTmp.recycle();
        return textureId;
	}	
}

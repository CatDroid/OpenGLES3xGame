package com.bn.Sample2_1;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;

@SuppressLint("NewApi")
class MySurfaceView extends GLSurfaceView 
{
    SceneRenderer mRenderer;//场景渲染器    
    int textureFlagId;//系统分配的国旗纹理id
    private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
	float xAngle;//整体场景绕X轴旋转的角度
	float yAngle;//整体场景绕Y轴旋转的角度
	private float mPreviousX;//上次的触控位置X坐标  
	private  float mPrevmiousY;//上次的触控位置X坐标  
	
	//触摸事件回调方法
    @Override 
    public boolean onTouchEvent(MotionEvent e) 
    {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {  
        case MotionEvent.ACTION_MOVE:
            float dx = x - mPreviousX;//计算触控笔X位移
            float dy = y - mPrevmiousY;//计算触控笔Y位移
            yAngle += dx * TOUCH_SCALE_FACTOR;//设置整体场景绕Y轴旋转角度     
            xAngle += dy * TOUCH_SCALE_FACTOR;//设置绕整体场景X轴旋转角度   
            requestRender();//重绘画面
        }
        mPreviousX = x;//记录触控笔位置
        mPrevmiousY = y;//记录触控笔位置
        return true;
    }
    
    @SuppressLint("NewApi")
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
        /**
         * 
         */
        setKeepScreenOn(true);//通过设置View.setKeepScreenOn(boolean)的方法可以永不锁屏
    }

	class SceneRenderer implements GLSurfaceView.Renderer 
    {   
    	TextureRect texRect;//表示国旗的纹理矩形
    	
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, -1);
            MatrixState.rotate(yAngle,0,1,0);
            MatrixState.rotate(xAngle,1,0,0);
            //绘制纹理矩形
            texRect.drawSelf(textureFlagId);
            MatrixState.popMatrix();            
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 4, 100);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0,0,5,0f,0f,0f,0f,1.0f,0.0f);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA--黑色
            GLES30.glClearColor(0.0f,0.0f,0.0f,1.0f);  
            //创建纹理矩形对对象 
            texRect=new TextureRect(MySurfaceView.this); 
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //初始化纹理
            textureFlagId=initTexture(R.drawable.android_flag);
            //关闭背面剪裁   
            GLES30.glDisable(GLES30.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();
        }
    }
	
	public int initTexture(int drawableId)//textureId
	{
		//生成纹理ID
		int[] textures = new int[1];
		GLES30.glGenTextures
		(
				1,          //产生的纹理id的数量
				textures,   //纹理id的数组
				0           //偏移量
		);    
		int textureId=textures[0];    
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
        
        //通过输入流加载图片===============begin===================
        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try 
        {
        	bitmapTmp = BitmapFactory.decodeStream(is);
        } 
        finally 
        {
            try 
            {
                is.close();
            } 
            catch(IOException e) 
            {
                e.printStackTrace();
            }
        }
        //实际加载纹理
        GLUtils.texImage2D
        (
        		GLES30.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  //纹理图像
        		0					  //纹理边框尺寸
        );
        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
        return textureId;
	}
}

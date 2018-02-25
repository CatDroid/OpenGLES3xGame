package com.bn.Sample7_3;
import java.io.IOException;
import java.io.InputStream;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;
import android.opengl.GLES30;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.bn.Sample7_3.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

class MySurfaceView extends GLSurfaceView 
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
    private SceneRenderer mRenderer;//场景渲染器
	
	private float mPreviousY;//上次的触控位置Y坐标
    private float mPreviousX;//上次的触控位置X坐标
    
    int textureCTId;//系统分配的拉伸纹理id
    int textureREId;//系统分配的重复纹理id
    int textureMIId;//系统分配的镜像纹理id
    int currTextureId;//当前纹理id  
    
    TextureRect[] texRect=new TextureRect[3];//纹理矩形数组
    int trIndex=2;//当前纹理矩形索引
	
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }
	
	//触摸事件回调方法
    @SuppressLint("ClickableViewAccessibility")
	@Override 
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dy = y - mPreviousY;//计算触控笔Y位移
            float dx = x - mPreviousX;//计算触控笔X位移
            for(TextureRect tr:texRect)
            {
            	tr.yAngle += dx * TOUCH_SCALE_FACTOR;//设置纹理矩形绕y轴旋转角度
                tr.zAngle+= dy * TOUCH_SCALE_FACTOR;//设置第纹理矩形绕z轴旋转角度
            }
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {      	
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            //绘制当前纹理矩形
            texRect[trIndex].drawSelf(currTextureId);             
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 10);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0,0,3,0f,0f,0f,0f,1.0f,0.0f);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0.5f,0.5f,0.5f, 1.0f);  
            //创建三个纹理矩形对对象 
            texRect[0]=new TextureRect(MySurfaceView.this,1,1);  
            texRect[1]=new TextureRect(MySurfaceView.this,4,2);  
            texRect[2]=new TextureRect(MySurfaceView.this,4,4);        
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);           
            //初始化系统分配的拉伸纹理id
            textureCTId=initTexture(1);
            //初始化系统分配的重复纹理id
            textureREId=initTexture(0);
            //初始化系统分配的镜像纹理id
            textureMIId=initTexture(2);
            //初始化当前纹理id
            currTextureId=textureREId;
            //关闭背面剪裁   
            GLES30.glDisable(GLES30.GL_CULL_FACE);
        }
    }
	
	//初始化纹理的方法
	public int initTexture(int RepeatIndex)//textureId
	{
		int[] textures = new int[1];  //用于记录生成的纹理id
		GLES30.glGenTextures
		(
				1,          //产生的纹理id的数量
				textures,   //纹理id的数组
				0           //偏移量 
		);    
		int textureId=textures[0];    
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
		//设置MIN采样方式
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
		//设置MAG采样方式
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
		
        if(RepeatIndex==0)//如果索引值等于0
        {
        	GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, //S轴为重复拉伸方式
        			GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_REPEAT);
    		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, //T轴为重复拉伸方式
    				GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_REPEAT);
        }
        else if(RepeatIndex==1)//如果索引值等于1
        {
        	GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, //S轴为截取拉伸方式
        			GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
    		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, //T轴为截取拉伸方式
    				GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
        }else if(RepeatIndex==2)//如果索引值等于2
        {
        	GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, //S轴为镜像重复拉伸方式
        			GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_MIRRORED_REPEAT);
    		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, //T轴为镜像重复拉伸方式
    				GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_MIRRORED_REPEAT);
        }
        
        //通过输入流加载图片===============begin===================
        InputStream is = this.getResources().openRawResource(R.drawable.robot);
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
        //通过输入流加载图片===============end=====================  
        
        //实际加载纹理进显存
        GLUtils.texImage2D
        (
        		GLES30.GL_TEXTURE_2D,   //纹理类型
        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  //纹理图像
        		0					  //纹理边框尺寸
        );
        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
        return textureId;
	}
}

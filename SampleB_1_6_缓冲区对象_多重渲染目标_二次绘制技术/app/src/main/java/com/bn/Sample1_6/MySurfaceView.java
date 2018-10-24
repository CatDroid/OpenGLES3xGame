package com.bn.Sample1_6;

import java.io.IOException;
import java.io.InputStream;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.bn.Sample1_6.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

@SuppressLint("NewApi")
class MySurfaceView extends GLSurfaceView 
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
    private SceneRenderer mRenderer;//场景渲染器
    
    private float mPreviousY;//上次的触控位置Y坐标
    private float mPreviousX;//上次的触控位置X坐标
    float ratio;
    
    static final int GEN_TEX_WIDTH=1024;
    static final int GEN_TEX_HEIGHT=512;
    
    int SCREEN_WIDTH;
    int SCREEN_HEIGHT;
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OpenGL ES 3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染
    }
	
	//触摸事件回调方法
    @SuppressLint("ClickableViewAccessibility") 
    @Override 
    public boolean onTouchEvent(MotionEvent e)
    {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dy = y - mPreviousY;//计算触控点Y位移
            float dx = x - mPreviousX;//计算触控点X位移
            mRenderer.yAngle += dx * TOUCH_SCALE_FACTOR;//设置沿y轴旋转角度
            mRenderer.xAngle+= dy * TOUCH_SCALE_FACTOR;//设置沿x轴旋转角度
            requestRender();//重绘画面
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {
		float yAngle;//绕Y轴旋转的角度
    	float xAngle; //绕X轴旋转的角度

		LoadedObjectVertexNormalTexture lovo;    	//从指定的obj文件中加载对象
		
		int[] textureIds = new int[4];//用于存放产生纹理id的数组
		int frameBufferId;//帧缓冲id
		int textureIdGHXP;//国画小品的纹理id
		TextureRect tr;//矩形绘制对象

		int renderDepthBufferId;//渲染深度缓冲id
		
		@SuppressLint("NewApi")
		public boolean initFBO()
		{
			int[] attachments=new int[]{
					GLES30.GL_COLOR_ATTACHMENT0,
					GLES30.GL_COLOR_ATTACHMENT1,					
					GLES30.GL_COLOR_ATTACHMENT2,
					GLES30.GL_COLOR_ATTACHMENT3
			};
			int tia[]=new int[1];//用于存放产生的帧缓冲id的数组
			
			// 帧缓冲
			GLES30.glGenFramebuffers(1, tia, 0);
			frameBufferId=tia[0];
			GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId);

			
			// 创建渲染缓冲  以及 为渲染缓冲初始化存储
			GLES30.glGenRenderbuffers(1, tia, 0);
			renderDepthBufferId=tia[0];
			GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, renderDepthBufferId);
			GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER,
										GLES30.GL_DEPTH_COMPONENT16, // 也可以是GL_DEPTH_COMPONENT
										GEN_TEX_WIDTH, GEN_TEX_HEIGHT);

			// 如果作为颜色渲染缓冲区 这样初始化存储
			// glRenderbufferStorage（GL_RENDERBUFFER，GL_RGBA，width，height）;

			// 作为模板渲染缓存
			// glRenderbufferStorage（GL_RENDERBUFFER，GL_STENCIL_INDEX，width，height）;

			//设置自定义帧缓冲的深度缓冲附件
			GLES30.glFramebufferRenderbuffer(
        		GLES30.GL_FRAMEBUFFER,
        		GLES30.GL_DEPTH_ATTACHMENT,		//深度缓冲附件
        		GLES30.GL_RENDERBUFFER,			//渲染缓冲 如果是纹理作为附件 就是GL_TEXTURE_2D
        		renderDepthBufferId				//渲染深度缓冲id
        	);


			// 产生4个纹理id 作为同一个FrameBuffer的4个颜色附件
			GLES30.glGenTextures(textureIds.length, textureIds, 0);
			for(int i=0;i<attachments.length;i++)
			{
				GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,textureIds[i]);//绑定纹理id
				GLES30.glTexImage2D//设置颜色附件纹理图的格式
	        	(
	        		GLES30.GL_TEXTURE_2D,
	        		0,						//层次
	        		GLES30.GL_RGBA, 		//内部格式
	        		GEN_TEX_WIDTH,			//宽度
	        		GEN_TEX_HEIGHT,			//高度
	        		0,						//边界宽度
	        		GLES30.GL_RGBA,			//格式 
	        		GLES30.GL_UNSIGNED_BYTE,//每个像素数据格式
	        		null
	        	);
				GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,//设置MIN采样方式
						GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
				GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,//设置MAG采样方式
						GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
				GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,//设置S轴拉伸方式
						GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
				GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,//设置T轴拉伸方式
						GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
				
				GLES30.glFramebufferTexture2D		//将指定纹理绑定到帧缓冲
	            (
	            	GLES30.GL_DRAW_FRAMEBUFFER,
	            	attachments[i],					//颜色附件
	            	GLES30.GL_TEXTURE_2D,
	            	textureIds[i], 					//纹理id
	            	0								//层次 (hhl 被关联的纹理的mipmap等级)
	            );
			}

			// hhl 设置要输出的颜色附件 OpenGL ES 3.0 才有的API
			// 		如果不设置这个,即使shader中有输出到fragColor0/1/2/3 也只会有一个有效果
			GLES30.glDrawBuffers(attachments.length, attachments,0);

			return GLES30.GL_FRAMEBUFFER_COMPLETE ==
					GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER);
		}
		
		@SuppressLint("NewApi") 
		public void generateTextImage()//通过绘制产生纹理
		{
			//设置屏幕背景色RGBA
        	GLES30.glClearColor(0.0f,0.0f,0.0f,1.0f);  
			//设置视窗大小及位置 
			GLES30.glViewport(0, 0, GEN_TEX_WIDTH, GEN_TEX_HEIGHT);
			//绑定帧缓冲id
			GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,frameBufferId );//frameBufferId
    		//清除深度缓冲与颜色缓冲
			GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            //设置透视投影
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0,0,3,0f,0f,-1f,0f,1.0f,0.0f);
            
            MatrixState.pushMatrix();//保护现场
            MatrixState.translate(0f, -15f, -70f);//坐标系推远
            //绕Y轴、X轴旋转
            MatrixState.rotate(yAngle, 0, 1, 0);
            MatrixState.rotate(xAngle, 1, 0, 0);
            if(lovo!=null)//若加载的物体部位空则绘制物体
            {
            	lovo.drawSelf(textureIdGHXP);
            }
            MatrixState.popMatrix();//恢复现场
		}
		
		@SuppressLint("NewApi")
		public void drawShadowTexture()//绘制生成的矩形纹理
		{
			//设置屏幕背景色RGBA
        	GLES30.glClearColor(0.5f,0.5f,0.5f,1.0f);  
			//设置视窗大小及位置 
			GLES30.glViewport(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
			GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);//绑定帧缓冲id
        	//清除深度缓冲与颜色缓冲
			GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT |GLES30.GL_COLOR_BUFFER_BIT);
            //设置正交投影
            MatrixState.setProjectOrtho(-ratio, ratio, -1, 1, 2, 100);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0,0,3,0f,0f,0f,0f,1.0f,0.0f);
            
            MatrixState.pushMatrix();
            MatrixState.translate(-ratio/2, 0.5f, 0);
            tr.drawSelf(textureIds[0]);//绘制纹理矩形
            MatrixState.popMatrix();
            
            MatrixState.pushMatrix();
            MatrixState.translate(ratio/2, 0.5f, 1);
            tr.drawSelf(textureIds[1]);//绘制纹理矩形
            MatrixState.popMatrix();
            
            MatrixState.pushMatrix();
            MatrixState.translate(-ratio/2, -0.5f, 0);
            tr.drawSelf(textureIds[2]);//绘制纹理矩形
            MatrixState.popMatrix();
            
            MatrixState.pushMatrix();
            MatrixState.translate(ratio/2, -0.5f, 1);
            tr.drawSelf(textureIds[3]);//绘制纹理矩形
            MatrixState.popMatrix();
		}

		public void onDrawFrame(GL10 gl) 
        {
        	generateTextImage();//通过绘制产生矩形纹理
        	drawShadowTexture();//绘制矩形纹理
        }
       
		public void onSurfaceChanged(GL10 gl, int width, int height)
        {
        	SCREEN_WIDTH=width;
        	SCREEN_HEIGHT=height;
            ratio = (float) width / height;//计算GLSurfaceView的宽高比
            initFBO();
            textureIdGHXP=initTexture(R.drawable.ghxp);//加载国画小品纹理图
            tr=new TextureRect(MySurfaceView.this,ratio);//创建矩形绘制对象
        }
        
		public void onSurfaceCreated(GL10 gl, EGLConfig config) 
        {
            //打开深度检测
        	GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //打开背面剪裁   
        	GLES30.glEnable(GLES30.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();
            //初始化光源位置
            MatrixState.setLightLocation(40, 100, 20);
            //加载要绘制的物体
            lovo=LoadUtil.loadFromFile("ch_t.obj", MySurfaceView.this.getResources(),
            		MySurfaceView.this);
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
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_REPEAT);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_REPEAT);
        
        //通过输入流加载图片===============begin===================
        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try{bitmapTmp = BitmapFactory.decodeStream(is);}
        finally{try{is.close();}catch(IOException e){e.printStackTrace();}
        }
        //通过输入流加载图片===============end===================== 
	   	GLUtils.texImage2D(
	    		GLES30.GL_TEXTURE_2D, //纹理类型
	     		0,						//层次
	     		GLUtils.getInternalFormat(bitmapTmp),//内部格式
	     		bitmapTmp, //纹理图像
	     		GLUtils.getType(bitmapTmp),//纹理类型
	     		0 //纹理边框尺寸
	     );
	    bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
        return textureId;//返回纹理id
	}
}

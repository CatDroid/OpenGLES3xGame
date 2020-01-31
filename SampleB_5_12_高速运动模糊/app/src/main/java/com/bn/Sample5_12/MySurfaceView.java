package com.bn.Sample5_12;

import java.io.IOException;
import java.io.InputStream;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;

import static com.bn.Sample5_12.Constant.*;

@SuppressLint("NewApi") 
class MySurfaceView extends GLSurfaceView
{
    private SceneRenderer mRenderer;//场景渲染器
    float ratio;
    
    float cz = 100f;//摄像机的z位置坐标
    float targetz = 0;//摄像机的z目标点坐标
    float preCZ = cz;//前一帧的摄像机的z位置坐标
    float preTargetZ = targetz;//前一帧的摄像机的z目标点坐标
    private final Object mLock = new Object();


    int SCREEN_WIDTH;//屏幕宽度
    int SCREEN_HEIGHT;//屏幕高度
    
    float[] mViewProjectionInverseMatrix;//当前观察-投影矩阵的逆阵
    float[] mPreviousProjectionMatrix;//前一帧的观察-投影矩阵
    
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {
		LoadedObjectVertexNormalTexture tree1;//第一种树绘制对象
		LoadedObjectVertexNormalTexture tree2;//第二种树绘制对象
    	Mountain mountain;//山地绘制对象
    	Sky_cloud cloud;//天空绘制对象
    	
		int frameBufferId;//帧缓冲id
		int renderDepthBufferId;//渲染深度缓冲id
		int[] tempIds = new int[2];//用于存放产生纹理id的数组
		
		int mountainTextId;//山地纹理id
		int tree1TextId;//第一种树的纹理id
		int tree2TextId;//第二种树的纹理id
		int cloudTextId;//天空纹理id
		TextureRect tr;//矩形绘制对象
		
		public boolean initFRBuffers()//初始化帧缓冲和渲染缓冲的方法
		{
			int attachment[]=new int[]{
					GLES30.GL_COLOR_ATTACHMENT0,
					GLES30.GL_COLOR_ATTACHMENT1
			};
			int tia[]=new int[1];//用于存放产生的帧缓冲id的数组
			GLES30.glGenFramebuffers(1, tia, 0);//产生一个帧缓冲id
			frameBufferId = tia[0];//将帧缓冲id记录到成员变量中
			// 绑定帧缓冲id
			GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId);
			
			GLES30.glGenRenderbuffers(1, tia, 0);//产生一个渲染缓冲id
			renderDepthBufferId=tia[0];// 将渲染缓冲id记录到成员变量中
			// 绑定指定id的渲染缓冲
			GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, renderDepthBufferId);
			// 为渲染缓冲初始化存储
			GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER,
					GLES30.GL_DEPTH_COMPONENT16, GEN_TEX_WIDTH, GEN_TEX_HEIGHT);
			GLES30.glFramebufferRenderbuffer	//设置自定义帧缓冲的深度缓冲附件
        	(
        		GLES30.GL_FRAMEBUFFER,
        		GLES30.GL_DEPTH_ATTACHMENT,		//深度缓冲附件
        		GLES30.GL_RENDERBUFFER,			//渲染缓冲
        		renderDepthBufferId				//渲染深度缓冲id
        	);
			
			GLES30.glGenTextures(2, tempIds, 0);
			GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,tempIds[0]);//绑定纹理id
			GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,//设置MIN采样方式
					GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_LINEAR);
			GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,//设置MAG采样方式
					GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
			GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,//设置S轴拉伸方式
					GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
			GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,//设置T轴拉伸方式
					GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE); 
			GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D,
					0, GLES30.GL_RGBA, GEN_TEX_WIDTH, GEN_TEX_HEIGHT, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
			GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, attachment[0], GLES30.GL_TEXTURE_2D, tempIds[0], 0);
			
			GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,tempIds[1]);//绑定纹理id
			GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,//设置MIN采样方式
					GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_LINEAR);
			GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,//设置MAG采样方式
					GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
			GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,//设置S轴拉伸方式
					GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
			GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,//设置T轴拉伸方式
					GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE); 
			GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D,
					0, GLES30.GL_R16F, GEN_TEX_WIDTH, GEN_TEX_HEIGHT, 0, GLES30.GL_RED, GLES30.GL_FLOAT, null);
			GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, attachment[1], GLES30.GL_TEXTURE_2D, tempIds[1], 0);

			// 设置两个渲染输出  多渲染目标
			GLES30.glDrawBuffers(attachment.length, attachment,0);
			
			if(GLES30.GL_FRAMEBUFFER_COMPLETE != 
					GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER))
			{
				return false;
			}
			return true;
		}
		
		public void generateTextImage()//通过绘制产生纹理
		{
			// 设置视口大小及位置
			GLES30.glViewport(0, 0, GEN_TEX_WIDTH, GEN_TEX_HEIGHT);
			// 绑定帧缓冲id
			GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId);
    		// 清除深度缓冲与颜色缓冲
			GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            // 设置透视投影
			MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 300);
			
			// 调用此方法产生摄像机9参数位置矩阵
			synchronized (mLock) {
				MatrixState.setCamera(0, 0, preCZ, 0, 0, preTargetZ, 0, 1, 0);
				// 获得前一帧的观察-投影矩阵
				mPreviousProjectionMatrix = MatrixState.getViewProjMatrix();
				// 调用此方法产生摄像机9参数位置矩阵
				MatrixState.setCamera(0, 0, cz, 0, 0, targetz, 0, 1, 0);
			}
            // 获得当前的观察-投影矩阵
            float[] mViewProjectionMatrix=new float[16];
            mViewProjectionMatrix=MatrixState.getViewProjMatrix();
            float[] tempM=new float[16];
            //对当前的观察-投影矩阵求逆矩阵
            Matrix.invertM(tempM, 0, mViewProjectionMatrix, 0);
            mViewProjectionInverseMatrix = tempM;
            
            // 绘制山地
            MatrixState.pushMatrix();//保护现场
            MatrixState.scale(1, -1, 1);
            mountain.drawSelf(mountainTextId);
            MatrixState.popMatrix();//恢复现场
            // 绘制云天空
            MatrixState.pushMatrix();
            MatrixState.translate(0,-10.0f,0);
            MatrixState.rotate(-90, 0, 1, 0);
            cloud.drawSelf(cloudTextId);
            MatrixState.popMatrix();
            // 绘制树1
            drawTrees(1,28f,-30.0f,-40f);
            drawTrees(1,-28f,-15.0f,-30f);
            drawTrees(1,30f,-18.0f,-10f);
            drawTrees(1,29f,-12.0f,0f);
            drawTrees(1,-28f,-37.5f,5f);
            drawTrees(1,-26f,-13.0f,40f);
            drawTrees(1,-30f,-25.0f,20f);
            drawTrees(1,30f,-30.0f,40f);
            drawTrees(1,-26f,-15.0f,52f);
            drawTrees(1,-30f,-20.0f,-45);
            drawTrees(1,30f,-29.0f,-45);
            drawTrees(1,26f,-15.0f,-55);
            drawTrees(1,2f,-16.0f,-55);
            drawTrees(1,-2f,-34.0f,0);
            
            // 绘制树2
            drawTrees(2,26f,-18.0f,-50);
            drawTrees(2,-26f,-14.0f,-40);
            drawTrees(2,28f,-13.0f,10);
            drawTrees(2,-30f,-26.0f,-20);
            drawTrees(2,-26f,-15.0f,30);
            drawTrees(2,26f,-10.0f,15);
            drawTrees(2,-28f,-10.0f,45);
            drawTrees(2,-30f,-10.0f,60);
            drawTrees(2,30f,-32.0f,55);
            drawTrees(2,30f,-20.0f,-55);
            drawTrees(2,-26f,-10.0f,-60);
            drawTrees(2,28f,-30.0f,60f);
            drawTrees(2,2f,-25.0f,35f);
		}
		public void drawShadowTexture()//绘制生成的矩形纹理
		{
			//设置视口大小及位置 
			GLES30.glViewport(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
			GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);//绑定帧缓冲id   	
        	//清除深度缓冲与颜色缓冲
			GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT |GLES30.GL_COLOR_BUFFER_BIT);
            //设置平行投影
            MatrixState.setProjectOrtho(-ratio, ratio, -1, 1, 1, 300);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0,0,3.0f,0f,0f,0f,0f,1.0f,0.0f);
            
            MatrixState.pushMatrix();
            tr.drawSelf(tempIds[0], tempIds[1], mPreviousProjectionMatrix, mViewProjectionInverseMatrix, SAMPLENUMBER);//绘制纹理矩形
            MatrixState.popMatrix();
		}


		private long mLastTimeStamp = 0 ;

		@Override
        public void onDrawFrame(GL10 gl) 
        {
        	generateTextImage();//通过绘制产生矩形纹理
        	drawShadowTexture();//绘制矩形纹理

			if (CONIFG_RENDER_THREAD_UPDATE)
			{
				long now =  SystemClock.currentThreadTimeMillis(); // 当前线程运行时间
				long duration = now - mLastTimeStamp;
				if (duration > 100) {
					mLastTimeStamp = now ;

					preCZ = cz;
					preTargetZ = targetz;

					cz -= SPAN;
					targetz -= SPAN;

					if(cz<=-35)
					{
						cz = preCZ = 100f;
						targetz = preTargetZ = 0f;
					}
				}
			}

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height)
        {
	    	SCREEN_WIDTH=width;
        	SCREEN_HEIGHT=height;
            ratio = (float) width / height;//计算GLSurfaceView的宽高比
            initFRBuffers();//初始化帧缓冲和渲染缓冲的方法
            tr = new TextureRect(MySurfaceView.this, ratio);//创建矩形绘制对象
        }

		@Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) 
        {   

        	GLES30.glClearColor(1.0f,1.0f,1.0f,1.0f);
        	GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        	GLES30.glEnable(GLES30.GL_CULL_FACE);

            MatrixState.setInitStack();
            MatrixState.setLightLocation(0, 80, 100);


            yArray = loadLandforms(MySurfaceView.this.getResources(), R.drawable.hd1);
            mountain = new Mountain(MySurfaceView.this,yArray,yArray.length-1,yArray[0].length-1);
            cloud = new Sky_cloud(MySurfaceView.this);
            // 创建树的绘制对象
            tree1 = LoadUtil.loadFromFile("tree.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            tree2 = LoadUtil.loadFromFile("tree01.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            
            mountainTextId = initTexture(R.drawable.grass);
            tree1TextId = initTexture(R.drawable.tree);
            tree2TextId = initTexture(R.drawable.tree1);
            cloudTextId = initTexture(R.drawable.sky_cloud);

			mLastTimeStamp = SystemClock.currentThreadTimeMillis();

			if (!CONIFG_RENDER_THREAD_UPDATE)
			{
				new Thread()
				{
					public void run()
					{
						while(true)
						{
							synchronized (mLock)
							{
								preCZ = cz;
								preTargetZ = targetz;

								cz -= SPAN;
								targetz -= SPAN;

								if(cz<=-35)
								{
									cz = preCZ = 100f;
									targetz = preTargetZ = 0f;
								}
							}

							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}.start();
			}

        }
        
        private void drawTrees(int treeIndex,float transX,float transY,float transZ)
        {
        	if(treeIndex==1)//绘制树1
        	{
        		MatrixState.pushMatrix();//保护现场
        		MatrixState.translate(transX, transY, transZ);
        		 if(tree1!=null){
                 	tree1.drawSelf(tree1TextId);
                 }
        		MatrixState.popMatrix();
        	}else//绘制树2
        	{
        		MatrixState.pushMatrix();//保护现场
        		MatrixState.translate(transX, transY, transZ);
        		 if(tree2!=null){
                 	tree2.drawSelf(tree2TextId);
                 }
        		MatrixState.popMatrix();
        	}
        }
    }

  	private int initTexture(int drawableId)
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
        try{bitmapTmp = BitmapFactory.decodeStream(is);}
        finally{try{is.close();}catch(IOException e){e.printStackTrace();}
        }

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmapTmp, 0);

        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
	    bitmapTmp.recycle();
        return textureId;
	}
}

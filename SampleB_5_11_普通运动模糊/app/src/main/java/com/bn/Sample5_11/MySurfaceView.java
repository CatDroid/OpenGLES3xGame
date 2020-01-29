package com.bn.Sample5_11;

import java.io.IOException;
import java.io.InputStream;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.bn.Sample5_11.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import static com.bn.Sample5_11.Constant.*;

@SuppressLint("NewApi") 
class MySurfaceView extends GLSurfaceView
{
    private SceneRenderer mRenderer;//场景渲染器
    float ratio;
    int SCREEN_WIDTH;
    int SCREEN_HEIGHT;
    
    float cz=100f;
    float targetz=0;
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
    	
		int[] frameBufferIds=new int[5];//帧缓冲id
		int[] renderDepthBufferIds=new int[5];//渲染深度缓冲id
		int[] textureIds=new int[5];//最后生成的纹理id
		
		int mountainTextId;//山地纹理id
		int tree1TextId;//第一种树的纹理id
		int tree2TextId;//第二种树的纹理id
		int cloudTextId;//天空纹理id
		TextureRect tr;//矩形绘制对象
		
		private void initFRBuffers()
		{
			int tia[]=new int[5];
			GLES30.glGenFramebuffers(tia.length, tia, 0);
			frameBufferIds=tia;
			
			GLES30.glGenRenderbuffers(tia.length, tia, 0);
			renderDepthBufferIds=tia;
			
			int[] tempIds = new int[5];
			GLES30.glGenTextures(5, tempIds, 0);
			textureIds = tempIds;
			for(int i=0;i<textureIds.length;i++)
			{

				GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferIds[i]);

				GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, renderDepthBufferIds[i]);

				GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, 
						GLES30.GL_DEPTH_COMPONENT16,GEN_TEX_WIDTH, GEN_TEX_HEIGHT);
				
				GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,textureIds[i]);
				GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
						GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_LINEAR);
				GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
						GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
				GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
						GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
				GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
						GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE); 
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
				GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, textureIds[i], 0);
				GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT, GLES30.GL_RENDERBUFFER, renderDepthBufferIds[i]);
			}
		}
		
		public void generateTextImage()//通过绘制产生纹理
		{
			float czCurrTemp = cz;
			float targetzCurrTemp = targetz;
			for(int i=0;i<frameBufferIds.length;i++)
			{

				GLES30.glViewport(0, 0, GEN_TEX_WIDTH, GEN_TEX_HEIGHT);
				GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferIds[i]);
				GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

				MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 300);

				// 每次循环 远离摄像头
				// 调用此方法产生摄像机9参数位置矩阵
	            MatrixState.setCamera(
	            		0,0, czCurrTemp - TIMESPAN * i,  // TIMESPAN = 0.75
						0,0, targetzCurrTemp- TIMESPAN * i,
						0, 1, 0);

	            //绘制山地
	            MatrixState.pushMatrix();//保护现场
	            MatrixState.scale(1, -1, 1);
	            mountain.drawSelf(mountainTextId);
	            MatrixState.popMatrix();//恢复现场
	            //绘制云天空
	            MatrixState.pushMatrix();
	            MatrixState.translate(0,-10.0f,0);
	            MatrixState.rotate(-90, 0, 1, 0);
	            cloud.drawSelf(cloudTextId);
	            MatrixState.popMatrix();
	            //绘制树1
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
	            //绘制树2
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
		}


		private void drawSceneTexture()
		{

			GLES30.glViewport(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
			GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);//绑定帧缓冲id   	

			GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT |GLES30.GL_COLOR_BUFFER_BIT);

			MatrixState.setProjectOrtho(-ratio, ratio, -1, 1, 2, 100);
			MatrixState.setCamera(0,0,3,0f,0f,0f,0f,1.0f,0.0f);
			MatrixState.pushMatrix();
			tr.drawSelf(textureIds[0],textureIds[1],textureIds[2],textureIds[3],textureIds[4]);//绘制纹理矩形
			MatrixState.popMatrix();
		}

		@Override
        public void onDrawFrame(GL10 gl) 
        {
        	generateTextImage();	// 通过绘制产生矩形纹理
        	drawSceneTexture();		// 绘制矩形纹理
        }

		@Override
        public void onSurfaceChanged(GL10 gl, int width, int height)
        {
        	SCREEN_WIDTH = width;
        	SCREEN_HEIGHT = height;
            ratio = (float) width / height;
            initFRBuffers();
            tr = new TextureRect(MySurfaceView.this,ratio); // 矩形的尺寸 刚好就是 正交投影的视椎体的width和height
        }

		@Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) 
        {   
			//设置屏幕背景色RGBA
        	GLES30.glClearColor(0.0f,0.0f,0.0f,1.0f);  
            //打开深度检测
        	GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //打开背面剪裁   
        	GLES30.glEnable(GLES30.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();
            //初始化光源位置
            MatrixState.setLightLocation(0, 80, 100);

            //创建山地绘制对象 (灰度图作为高度)
            yArray=loadLandforms(MySurfaceView.this.getResources(), R.drawable.hd1);
            mountain=new Mountain(MySurfaceView.this,yArray,yArray.length-1,yArray[0].length-1);

            // 球状太空盒
            cloud=new Sky_cloud(MySurfaceView.this);

            //创建树的绘制对象
            tree1=LoadUtil.loadFromFile("tree.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            tree2=LoadUtil.loadFromFile("tree01.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            
            mountainTextId=initTexture(R.drawable.grass);
            tree1TextId=initTexture(R.drawable.tree);
            tree2TextId=initTexture(R.drawable.tree1);
            cloudTextId=initTexture(R.drawable.sky_cloud);
            
            new Thread(){
            	public void run(){
            		while(true){
            			cz-=SPAN;
            			targetz-=SPAN; // 目标和摄像机位置 同时增加
            			if(cz<=-35){
            				cz=95;
            				targetz=0f;
            			}
            			try {Thread.sleep(20);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
            		}
            	}
            }.start();
        }
        
        public void drawTrees(int treeIndex,float transX,float transY,float transZ)
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

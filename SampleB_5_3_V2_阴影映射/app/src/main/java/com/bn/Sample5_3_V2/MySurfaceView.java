package com.bn.Sample5_3_V2;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

class MySurfaceView extends GLSurfaceView 
{
    private SceneRenderer mRenderer;//场景渲染器       
    
    //摄像机位置相关
    float cx = 0;
    float cy = 20;
    float cz = 50;
    float cAngle = -60;
    final float cR = 50;
      
    //灯光位置
	float lx = 0;
	final float ly = 10;
	float lz = 85;
	float lAngle = 0;
	final float lR = 85;
    final float cDis = 15;

    // 光源视椎体最远处
    private final float zFar = 400 ;
    
    
    //光源总变换矩阵
    float[] mMVPMatrixGY;

	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES2.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
        
//        new Thread()
//        {
//        	public void run()
//        	{
//        		while(true)
//        		{
//        			lAngle += 0.5;//设置沿x轴旋转角度
//                    lx=(float) Math.sin(Math.toRadians(lAngle))*lR;
//                    lz=(float) Math.cos(Math.toRadians(lAngle))*lR;
//                    try {
//   					Thread.sleep(80);
//	   				} catch (InterruptedException e) {
//	   					e.printStackTrace();
//	   				}
//        		}
//        	}
//        }.start();
    }

    float mPreviousY = 0;//记录触控笔位置
    float mPreviousX = 0;//记录触控笔位置


    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dy = y - mPreviousY;
                float dx = x - mPreviousX;

                MatrixState.sDiff += (dx / Constant.SCREEN_WIDTH) * 1.0 ;

                if (MatrixState.sDiff < 0)
                {
                    MatrixState.sDiff = 0 ;
                }
                else if (MatrixState.sDiff > Constant.MAX_DIFF)
                {
                    MatrixState.sDiff = Constant.MAX_DIFF ;
                }


                cAngle +=  dy / Constant.SCREEN_HEIGHT * 60.0f ;

                cAngle =  cAngle % 360 ;

                lx = (float) Math.sin(Math.toRadians(cAngle)) *lR;
                lz = (float) Math.cos(Math.toRadians(cAngle)) *lR;

                Log.e("TOM", "diff=" +MatrixState.sDiff + ", cAngle=" + cAngle );

        }
        mPreviousY = y;
        mPreviousX = x;

        return true;
    }

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {  
    	//从指定的obj文件中加载对象
		LoadedObjectVertexNormal lovo_pm;//平面
		LoadedObjectVertexNormal lovo_ch;//茶壶
		LoadedObjectVertexNormal lovo_cft;//长方体
		LoadedObjectVertexNormal lovo_qt;//球体
		LoadedObjectVertexNormal lovo_yh;//圆环

		int frameBufferId;      // FBO 两个附件:
		int shadowId;           // 颜色附件 距离纹理R16F
		int renderDepthBufferId;// 深度附件 RBO

		//初始化帧缓冲和渲染缓冲
		public void initFRBuffers()
		{
			int[] tia = new int[1];
			GLES30.glGenFramebuffers(1, tia, 0);
			frameBufferId = tia[0];
			
			GLES30.glGenRenderbuffers(1, tia, 0);
			renderDepthBufferId = tia[0];
			GLES30.glBindRenderbuffer( GLES30.GL_RENDERBUFFER, renderDepthBufferId);
        	GLES30.glRenderbufferStorage( GLES30.GL_RENDERBUFFER,
                    GLES30.GL_DEPTH_COMPONENT16,
                    Constant.SHADOW_TEX_WIDTH,
                    Constant.SHADOW_TEX_HEIGHT );
			
			int[] tempIds = new int[1];
    		GLES30.glGenTextures
    		(
    				1,       // 产生的纹理id的数量
    				tempIds,    // 纹理id的数组
    				0     // 偏移量
    		);   
    		
    		shadowId=tempIds[0];
    		
    		//初始化颜色附件纹理
        	GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, shadowId);        	
        	GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_LINEAR);
    		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
    		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
    		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE); 
    		
    		GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0,
                    GLES30.GL_R16F, Constant.SHADOW_TEX_WIDTH, Constant.SHADOW_TEX_HEIGHT, 0,
                    GLES30.GL_RED, GLES30.GL_FLOAT, null
        	);


        	GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId); 
            GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, shadowId, 0);
        	GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT, GLES30.GL_RENDERBUFFER, renderDepthBufferId);
		}
		
        //通过绘制产生阴影纹理        
        public void generateShadowImage()
        {

            // 深度纹理 尺寸是正方形 4096 x 4096  FBO的深度附件RBO和浮点纹理R16F 尺寸都是这个
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId);
            GLES30.glViewport(0, 0, Constant.SHADOW_TEX_WIDTH, Constant.SHADOW_TEX_HEIGHT);


            GLES30.glClearColor(zFar,zFar,zFar,zFar); // 这样clear R16F浮点纹理后 shader获取到的就是zFar
            // 清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

            // 设置光源虚拟摄像头 的 摄像机矩阵 和 投影视椎体(近平面是正方形)
            // 调用此方法产生 摄像机 9参数位置矩阵
            MatrixState.setCamera(lx,ly,lz,0f,0f,0f,0f,1,0);
            MatrixState.setProjectFrustum(-1, 1, -1.0f, 1.0f, 1.5f, zFar);
            // 保存这个,在物体渲染的时候，还需要计算顶点/片元 与 光源之间的距离
            mMVPMatrixGY = MatrixState.getViewProjMatrix();

            // 绘制最下面的平面
            lovo_pm.drawSelfForShadow();

            // 绘制球体
            MatrixState.pushMatrix();
            MatrixState.translate(-cDis, 0, 0);
            //若加载的物体部位空则绘制物体
            lovo_qt.drawSelfForShadow();
            MatrixState.popMatrix();

            // 绘制圆环
            MatrixState.pushMatrix();
            MatrixState.translate(cDis, 0, 0);
            MatrixState.rotate(30, 0, 1, 0);
            // 若加载的物体部位空则绘制物体
            lovo_yh.drawSelfForShadow();
            MatrixState.popMatrix();



            //绘制长方体
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, -cDis);
            //若加载的物体部位空则绘制物体
            lovo_cft.drawSelfForShadow();
            MatrixState.popMatrix();


            //绘制茶壶
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, cDis);
            //若加载的物体部位空则绘制物体
            lovo_ch.drawSelfForShadow();
            MatrixState.popMatrix();
        }
        
        public void drawScene(GL10 gl) 
        {         	
        	// 设置视口
        	GLES30.glViewport(0, 0, (int)Constant.SCREEN_WIDTH, (int)Constant.SCREEN_HEIGHT);
        	GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        	
        	// 清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);


            // 设置摄像机 和 投影矩阵
            MatrixState.setCamera(cx,cy,cz,0f,0f,0f,0f,1f,0f);
            MatrixState.setProjectFrustum(
                    -Constant.SCREEN_RATIO, Constant.SCREEN_RATIO,
                    -1.0f, 1.0f, 2, 1000);

            //绘制最下面的平面  平面也计算阴影~~
            lovo_pm.drawSelf(shadowId, mMVPMatrixGY);
            
            //绘制球体
            MatrixState.pushMatrix(); 
            MatrixState.translate(-cDis, 0, 0);
            //若加载的物体部位空则绘制物体
            lovo_qt.drawSelf(shadowId, mMVPMatrixGY);
            MatrixState.popMatrix();    
            
            //绘制圆环
            MatrixState.pushMatrix(); 
            MatrixState.translate(cDis, 0, 0);
            MatrixState.rotate(30, 0, 1, 0);
            //若加载的物体部位空则绘制物体
            lovo_yh.drawSelf(shadowId,mMVPMatrixGY);
            MatrixState.popMatrix(); 
            
            
            //绘制长方体
            MatrixState.pushMatrix(); 
            MatrixState.translate(0, 0, -cDis);
            //若加载的物体部位空则绘制物体
            lovo_cft.drawSelf(shadowId,mMVPMatrixGY);
            MatrixState.popMatrix();
            
            //绘制茶壶
            MatrixState.pushMatrix(); 
            MatrixState.translate(0, 0, cDis);
            //若加载的物体部位空则绘制物体
            lovo_ch.drawSelf(shadowId,mMVPMatrixGY);
            MatrixState.popMatrix(); 
            
        }
        
        //绘制一帧画面方法
        public void onDrawFrame(GL10 gl)
        {        	
        	MatrixState.setLightLocation(lx, ly, lz);// 设置光源位置
            if(Constant.USING_FRONT_CULL) GLES30.glCullFace(GLES30.GL_FRONT); // Fix 引入偏移之后导致的 悬浮(Peter Panning) 失真
            generateShadowImage();  // 通过绘制产生距离纹理
            if(Constant.USING_FRONT_CULL) GLES30.glCullFace(GLES30.GL_BACK);
        	drawScene(gl);          // 绘制场景
        }


        public void onSurfaceChanged(GL10 gl, int width, int height) 
        {
            //设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height);

        	//计算GLSurfaceView的宽高比
            Constant.SCREEN_RATIO  = (float) width / height;
            Constant.SCREEN_HEIGHT = height;
            Constant.SCREEN_WIDTH  = width;
            
            //初始化帧缓冲
            initFRBuffers();
        }

       
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {     	
        	//设置屏幕背景色RGBA
            GLES30.glClearColor(0f,0f,0f,1.0f);    
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //打开背面剪裁   
            GLES30.glEnable(GLES30.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();
            //初始化光源位置
            lx = (float) Math.sin(Math.toRadians(cAngle)) *lR;
            lz = (float) Math.cos(Math.toRadians(cAngle)) *lR;
            MatrixState.setLightLocation(lx, ly, lz);  
            //加载要绘制的物体
            lovo_ch=LoadUtil.loadFromFileVertexOnly("ch.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            lovo_pm=LoadUtil.loadFromFileVertexOnly("pm.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            lovo_cft=LoadUtil.loadFromFileVertexOnly("cft.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            lovo_qt=LoadUtil.loadFromFileVertexOnly("qt.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            lovo_yh=LoadUtil.loadFromFileVertexOnly("yh.obj", MySurfaceView.this.getResources(),MySurfaceView.this);       
        }
    }
}

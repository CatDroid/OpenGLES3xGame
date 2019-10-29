package com.bn.Sample5_3_V1;
import static com.bn.Sample5_3_V1.Constant.SCREEN_HEIGHT;
import static com.bn.Sample5_3_V1.Constant.SCREEN_WIDTH;
import static com.bn.Sample5_3_V1.Constant.SHADOW_TEX_HEIGHT;
import static com.bn.Sample5_3_V1.Constant.SHADOW_TEX_WIDTH;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
 
class MySurfaceView extends GLSurfaceView   
{
    private SceneRenderer mRenderer;//场景渲染器         
    //灯光位置
	private float mLightPosX = 0;
	private final float mLightPosY = 10;
	private float mLightPosZ = 45;
	private float mLightRotateAngle =0;
	final float mLightRotateRadius = 45;

    final float cDis=15;    
    float[] mMVPMatrixGY;//光源投影、观察组合矩阵
	
	public MySurfaceView(Context context) {

        super(context);

        this.setEGLContextClientVersion(3); // 设置使用OPENGL ES 3.0
        mRenderer = new SceneRenderer();	// 创建场景渲染器
        setRenderer(mRenderer);				// 设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);// 设置渲染模式为主动渲染

        new Thread()
        {
        	public void run()
        	{
        		while(true) 
        		{
        			// 灯光在Y=mLightPosY的高度上的XOZ平面，以半径为 mLightRotateRadius，水平角mLightRotateAngle做旋转
        			mLightRotateAngle += 0.5;//设置沿x轴旋转角度
                    mLightPosX =(float) Math.sin(Math.toRadians(mLightRotateAngle))* mLightRotateRadius;
                    mLightPosZ =(float) Math.cos(Math.toRadians(mLightRotateAngle))* mLightRotateRadius;
                    try {
   						Thread.sleep(40);
	   				} catch (InterruptedException e) {
	   					e.printStackTrace();
	   				}
        		}
        	}
        }.start();

    }

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {  
    	//从指定的obj文件中加载对象
		LoadedObjectVertexNormal lovo_pm;//平面
		LoadedObjectVertexNormal lovo_ch;//茶壶
		LoadedObjectVertexNormal lovo_cft;//长方体
		LoadedObjectVertexNormal lovo_qt;//球体
		LoadedObjectVertexNormal lovo_yh;//圆环
		
		TextureRect tr;		
		int frameBufferId;
		int shadowId;//距离纹理的纹理Id
		int renderDepthBufferId;//用作深度缓冲的渲染缓冲对象
		
		//在屏幕上绘制画面
        public void onDrawFrame(GL10 gl)
        {    
        	//设置光源位置
        	MatrixState.setLightLocation(mLightPosX, mLightPosY, mLightPosZ);
        	//通过绘制产生距离纹理
            generateShadowImage();
            //绘制距离纹理到屏幕
            drawShadowTexture();
        }
				
        //通过绘制产生距离纹理        
        public void generateShadowImage() 
        {
        	//设置视口
        	GLES30.glViewport(0, 0, SHADOW_TEX_WIDTH, SHADOW_TEX_HEIGHT);  
        	//绑定帧缓冲
        	GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId); 
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

            // 点光源 没有方向
            //设置摄像机
            MatrixState.setCamera(mLightPosX, mLightPosY, mLightPosZ,0f,0f,0f,0f,1,0);
            //设置透视矩阵
            MatrixState.setProjectFrustum(-1, 1, -1.0f, 1.0f, 1.5f, 400); 
            //获取摄像机投影组合矩阵
            mMVPMatrixGY=MatrixState.getViewProjMatrix();
            
            //绘制最下面的平面
            lovo_pm.drawSelfForShadow();  
            
            //绘制球体
            MatrixState.pushMatrix(); 
            MatrixState.translate(-cDis, 0, 0);
            //若加载的物体部位空则绘制物体
            lovo_qt.drawSelfForShadow();
            MatrixState.popMatrix();    
            
            //绘制圆环
            MatrixState.pushMatrix();            
            MatrixState.translate(cDis, 0, 0);
            MatrixState.rotate(30, 0, 1, 0);
            //若加载的物体部位空则绘制物体
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
        
        public void drawShadowTexture(){//将距离纹理呈现到屏幕上的方法

        	// 设置视口大小及位置
            float display_port_w = SCREEN_WIDTH;
            float display_port_h = SCREEN_HEIGHT;

            float ratio_w = SCREEN_WIDTH / SHADOW_TEX_WIDTH;
            float radio_h = SCREEN_HEIGHT / SHADOW_TEX_HEIGHT;
            if (  ratio_w  > radio_h ) {

                display_port_w = radio_h * SHADOW_TEX_WIDTH;
            } else {
                display_port_h = ratio_w * SHADOW_TEX_HEIGHT;
            }

            int off_x = (int)((SCREEN_WIDTH - display_port_w) / 2.0);
			int off_y = (int)((SCREEN_HEIGHT - display_port_h) / 2.0);

        	GLES30.glViewport(off_x, off_y, (int)display_port_w, (int)display_port_h);
			// 绑定帧缓冲 渲染到屏幕???
        	GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);

        	// 清除深度缓冲与颜色缓冲-设置背景色
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            GLES30.glClearColor(1,1,1,1);

            // 下面两个操作，会把 MatrixState 中的 投影矩阵和视图矩阵 直接覆盖修改了
            // 设置投影矩阵 是 正交投影 是因为只是为了显示阴影映射图

            // 设置摄像机
            MatrixState.setCamera(0,0,1.5f,0f,0f,-1f,0f,1.0f,0.0f);
			// 设置投影
            MatrixState.setProjectOrtho(-0.6f,0.6f, -0.6f, 0.6f, 1, 10);


			// 渲染用于显示距离纹理的纹理矩形 shadowId 内部格式是 GL_R16F
            tr.drawSelf(shadowId);
        }
        
        public void onSurfaceChanged(GL10 gl, int width, int height) 
        {
            // 设置视口大小及位置
        	GLES30.glViewport(0, 0, width, height); 
        	Constant.SCREEN_HEIGHT = height;
        	Constant.SCREEN_WIDTH = width;
        	
        	// 初始化帧缓冲
        	initFRBuffers();
        }
       
        public void onSurfaceCreated(GL10 gl, EGLConfig config) 
        {     	
        	// 设置屏幕背景色RGBA
            GLES30.glClearColor(0,0,0,1);    
            // 打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            // 打开背面剪裁
            GLES30.glEnable(GLES30.GL_CULL_FACE);
            // 初始化变换矩阵
            MatrixState.setInitStack();
            // 初始化光源位置
            MatrixState.setLightLocation(mLightPosX, mLightPosY, mLightPosZ);
            // 加载要绘制的物体
            lovo_ch = LoadUtil.loadFromFileVertexOnly("ch.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            lovo_pm = LoadUtil.loadFromFileVertexOnly("pm.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            lovo_cft= LoadUtil.loadFromFileVertexOnly("cft.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            lovo_qt = LoadUtil.loadFromFileVertexOnly("qt.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            lovo_yh = LoadUtil.loadFromFileVertexOnly("yh.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            //显示阴影贴图的纹理矩形
            tr = new TextureRect(MySurfaceView.this);
        }
		//初始化帧缓冲和渲染缓冲的方法
		public void initFRBuffers()
		{
			int[] tia=new int[1];//用于存放产生的帧缓冲id的数组
			GLES30.glGenFramebuffers(1, tia, 0);//产生一个帧缓冲id
			frameBufferId=tia[0];//将帧缓冲id记录到成员变量中
        	 
			
			GLES30.glGenRenderbuffers(1, tia, 0);//产生一个帧缓冲id
			renderDepthBufferId=tia[0];//将渲染缓冲id记录到成员变量中
			//绑定指定id的渲染缓冲
			GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, renderDepthBufferId);
			//为渲染缓冲初始化存储
        	GLES30.glRenderbufferStorage
        	(
        			GLES30.GL_RENDERBUFFER, 
        			GLES30.GL_DEPTH_COMPONENT16, 
        			SHADOW_TEX_WIDTH, 
        			SHADOW_TEX_HEIGHT
        	);
			
        	
			int[] tempIds = new int[1];//用于存放产生纹理id的数组
    		GLES30.glGenTextures//产生一个纹理id
    		(
    				1,          //产生的纹理id的数量
    				tempIds,   //纹理id的数组
    				0           //偏移量
    		);   
    		
    		shadowId=tempIds[0];//将纹理id记录到距离纹理id成员变量
    		
    		//初始化颜色附件纹理
        	GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, shadowId);//绑定纹理id 
        	//设置min、mag的采样方式
        	GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_LINEAR);
    		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
    		//设置纹理s、t轴的拉伸方式
    		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
    		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE); 
    		GLES30.glTexImage2D//设置颜色附件纹理图的格式
        	(
        		GLES30.GL_TEXTURE_2D, 
        		0,              // 层次
        		GLES30.GL_R16F,      // 内部格式
        		SHADOW_TEX_WIDTH,    // 宽度
        		SHADOW_TEX_HEIGHT,   // 高度
        		0,                // 边界宽度
        		GLES30.GL_RED,          // 格式
        		GLES30.GL_FLOAT,        // 每像素数据格式
        		null
        	);        	
        	
    		GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId);
            GLES30.glFramebufferTexture2D//设置自定义帧缓冲的颜色附件
            (
            	GLES30.GL_FRAMEBUFFER, 
            	GLES30.GL_COLOR_ATTACHMENT0,//颜色附件
            	GLES30.GL_TEXTURE_2D,//类型为2D纹理 
            	shadowId, //纹理id
            	0	//层次
            );       
        	GLES30.glFramebufferRenderbuffer//设置自定义帧缓冲的深度缓冲附件
        	(
        		GLES30.GL_FRAMEBUFFER, 
        		GLES30.GL_DEPTH_ATTACHMENT,//深度缓冲附件
        		GLES30.GL_RENDERBUFFER, //渲染缓冲
        		renderDepthBufferId//渲染缓冲id
        	);
		}
    }
}

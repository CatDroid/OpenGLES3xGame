package com.bn.Sample5_3_V1;
import static com.bn.Sample5_3_V1.Constant.SCREEN_HEIGHT;
import static com.bn.Sample5_3_V1.Constant.SCREEN_WIDTH;
import static com.bn.Sample5_3_V1.Constant.SHADOW_TEX_HEIGHT;
import static com.bn.Sample5_3_V1.Constant.SHADOW_TEX_WIDTH;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

class MySurfaceView extends GLSurfaceView   
{
    private SceneRenderer mRenderer;//场景渲染器         
    //灯光位置
	private float mLightPosX = 0;
	private final float mLightPosY = 10;
	private float mLightPosZ = 45;
	private float mLightRotateAngle =0;
	final float mLightRotateRadius = 45;

	// 光源最远出
    private final float zFar = 400 ;

    final float cDis=15;    
    float[] mMVPMatrixGY;//光源投影、观察组合矩阵

    private Handler mUIHandler = new Handler();
	
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
    	// 从指定的obj文件中加载对象
		LoadedObjectVertexNormal lovo_pm;// 平面
		LoadedObjectVertexNormal lovo_ch;// 茶壶
		LoadedObjectVertexNormal lovo_cft;//长方体
		LoadedObjectVertexNormal lovo_qt;// 球体
		LoadedObjectVertexNormal lovo_yh;// 圆环
		
		TextureRect tr;		
		int frameBufferId;
		int shadowId;           // 距离纹理的纹理Id
		int renderDepthBufferId;// 用作深度缓冲的渲染缓冲对象
		
		// 在屏幕上绘制画面
        public void onDrawFrame(GL10 gl)
        {    
        	// 设置光源位置
        	MatrixState.setLightLocation(mLightPosX, mLightPosY, mLightPosZ);
        	// 通过绘制产生距离纹理
            generateShadowImage();
            // 绘制距离纹理到屏幕
            drawShadowTexture();
        }
				
        // 通过绘制产生距离纹理
        private void generateShadowImage()
        {
            // 摄像机的位置(点光源) 和 透视投影的视椎体 构成了投影的范围

        	// 设置视口
            //      这个跟透视投影的视椎体一样比例
            //      正方形的距离纹理 和  视口和近平面都是正方形
        	GLES30.glViewport(0, 0, SHADOW_TEX_WIDTH, SHADOW_TEX_HEIGHT);

        	// 绑定帧缓冲
        	GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId);
            // 设置屏幕背景色RGBA
            if (Constant.USING_DEPTH_TEXTURE)
            {
                GLES30.glClearColor(0,0,0,1);
            }
            else
            {
                // 如果是R16F 这里clearcolor直接写入R16F每个纹素
                // 如果不做这一步的话,会发现最后的背景是黑色的 R=0 而实际应该是无穷远的 这里是R=400 跟虚拟摄像头视椎体zFar一样
                GLES30.glClearColor(zFar,zFar,zFar,zFar);
            }
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);


            // 设置摄像机/光源摄像机
            // 点光源,这里有点像聚光灯(因为是透视投影,视椎体就相当于聚光灯)
            //       LookAt: 始终目标坐标始终是原点
            //       Pos  :  位置跟随点光源  在一个XOZ平面上旋转 y一定
            MatrixState.setCamera(mLightPosX, mLightPosY, mLightPosZ,0f,0f,0f,0f,1,0);

            // 设置透视矩阵
            //      近平面时一个正方形
            //      距离是1.5f ~ zFar=400f
            MatrixState.setProjectFrustum(-1, 1, -1.0f, 1.0f, 1.5f, zFar);


            // 获取摄像机投影组合矩阵
            mMVPMatrixGY=MatrixState.getViewProjMatrix();



            //绘制最下面的平面
            lovo_pm.drawSelfForShadow();  
            
            // 绘制球体
            MatrixState.pushMatrix(); 
            MatrixState.translate(-cDis, 0, 0);
            // 若加载的物体部位空则绘制物体
            lovo_qt.drawSelfForShadow();
            MatrixState.popMatrix();    
            
            // 绘制圆环
            MatrixState.pushMatrix();            
            MatrixState.translate(cDis, 0, 0);
            MatrixState.rotate(30, 0, 1, 0);
            // 若加载的物体部位空则绘制物体
            lovo_yh.drawSelfForShadow();
            MatrixState.popMatrix();  
            
            // 绘制长方体
            MatrixState.pushMatrix(); 
            MatrixState.translate(0, 0, -cDis);
            // 若加载的物体部位空则绘制物体
            lovo_cft.drawSelfForShadow();
            MatrixState.popMatrix();
            
            // 绘制茶壶
            MatrixState.pushMatrix(); 
            MatrixState.translate(0, 0, cDis);
            // 若加载的物体部位空则绘制物体
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

			// 绑定帧缓冲 把阴影映射图渲染到屏幕
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
            if (Constant.USING_DEPTH_TEXTURE) {
                //Log.d("TOM", "depth " + renderDepthBufferId); // 深度纹理

                tr.drawSelf(renderDepthBufferId);
            } else {
                tr.drawSelf(shadowId);  // R16F纹理
            }
            checkError();

        }
        
        public void onSurfaceChanged(GL10 gl, int width, int height) 
        {
            // 设置视口大小及位置
        	GLES30.glViewport(0, 0, width, height); 
        	Constant.SCREEN_HEIGHT = height;
        	Constant.SCREEN_WIDTH = width;

            checkError();

        	// 初始化帧缓冲
        	initFRBuffers();
        }
       
        public void onSurfaceCreated(GL10 gl, EGLConfig config) 
        {
            // 获取扩展支持 R16F
            String exts = GLES30.glGetString(GLES30.GL_EXTENSIONS);
            boolean hasR16F_asRenderTarget = exts.contains("GL_EXT_color_buffer_half_float");
            final boolean supportDepthTexture = exts.contains("GL_OES_depth_texture");
            if (hasR16F_asRenderTarget)
            {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String msg = "support R16F " + (supportDepthTexture?"+支持深度纹理":"");
                        Log.e("TOM", msg );
                        Toast.makeText(MySurfaceView.this.getContext(),msg ,Toast.LENGTH_LONG).show();
                    }
                });
            }
            else
            {
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String msg = "not support R16F" + (supportDepthTexture?"+支持深度纹理":"");
                        Log.e("TOM", msg );
                        Toast.makeText(MySurfaceView.this.getContext(),msg ,Toast.LENGTH_LONG).show();
                    }
                });
            }
            Log.e("TOM","GLES ext " + exts);


        	// 设置屏幕背景色RGBA
            if (Constant.USING_DEPTH_TEXTURE)
            {
                GLES30.glClearColor(0,0,0,1);
            }
            else
            {
                // 如果是R16F 这里clearcolor直接写入R16F每个纹素
                GLES30.glClearColor(400,400,400,400);
            }

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
            checkError();

            //显示阴影贴图的纹理矩形
            tr = new TextureRect(MySurfaceView.this);
            checkError();
        }



        void initFRBuffers()
		{
			int[] tia=new int[1];
			GLES30.glGenFramebuffers(1, tia, 0);
			frameBufferId=tia[0];



			// 渲染到的FBO组成: -- 颜色 texture R16F  深度 RBO  GL_DEPTH_COMPONENT16
            // 渲染到的FBO组成: -- 颜色 texture RGBA  深度 texture GL_DEPTH_COMPONENT32F

            if (Constant.USING_DEPTH_TEXTURE) {

                // 深度 使用的是纹理


                int[] tempIds = new int[1];
                GLES30.glGenTextures( 1, tempIds, 0);
                renderDepthBufferId = tempIds[0];

                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, renderDepthBufferId);

                // https://stackoverflow.com/questions/17707638/getting-black-color-from-depth-buffer-in-open-gl-es-2-0
                // 如果要显示深度纹理  必须设置为 GL_NEAREST  ， 如果是GL_LINEAR会是全部黑色的 !!
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST );
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST );

                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);


//                GLES30.glTexParameteri( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_COMPARE_MODE, GLES30.GL_NONE );
                //GLES30.glTexParameteri( GLES30.GL_TEXTURE_2D, GLES30.GL_DEPTH_TEXTURE_MODE, GLES30.GL_LUMINANCE );



                // https://www.khronos.org/registry/OpenGL-Refpages/es2.0/xhtml/glTexImage2D.xml
                // https://www.khronos.org/registry/OpenGL-Refpages/es3.0/html/glTexImage2D.xhtml
                // 2.0 允许的内部格式有 GL_ALPHA, GL_LUMINANCE, GL_LUMINANCE_ALPHA, GL_RGB, GL_RGBA.
                // 3.0 允许有 Unsized Internal Format (GL_RGBA) 和 Sized Internal Format (GL_RGB565 GL_DEPTH_COMPONENT24)

                GLES30.glTexImage2D//设置颜色附件纹理图的格式
                        (
                            GLES30.GL_TEXTURE_2D,
                            0,
                            GLES30.GL_DEPTH_COMPONENT32F,   // 内部格式 Sized Internal Format
                            SHADOW_TEX_WIDTH,
                            SHADOW_TEX_HEIGHT,
                            0,
                            GLES20.GL_DEPTH_COMPONENT,      // 格式 Format f32
                            GLES20.GL_FLOAT,                // 数据类型 Type
                            null
                        );


            } else {

                // 深度 用的是 RBO  只用来渲染时候给渲染管线做深度检测 不做其他用途

                GLES30.glGenRenderbuffers(1, tia, 0);
                renderDepthBufferId = tia[0];

                GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, renderDepthBufferId);
                GLES30.glRenderbufferStorage
                        (
                            GLES30.GL_RENDERBUFFER,
                            GLES30.GL_DEPTH_COMPONENT16,
                            SHADOW_TEX_WIDTH,
                            SHADOW_TEX_HEIGHT
                        );
            }
            //Log.d("TOM", "create depth texutre " + renderDepthBufferId);

            checkError();

			int[] tempIds = new int[1];
    		GLES30.glGenTextures(1, tempIds, 0);
    		shadowId= tempIds[0];


    		//初始化颜色附件纹理
        	GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, shadowId);//绑定纹理id 
        	//设置min、mag的采样方式
        	GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_LINEAR);
    		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
    		//设置纹理s、t轴的拉伸方式
    		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
    		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);

    		if (Constant.USING_DEPTH_TEXTURE) {
                GLES30.glTexImage2D//设置颜色附件纹理图的格式
                        (
                                GLES30.GL_TEXTURE_2D,
                                0,              // 层次
                                GLES30.GL_RGBA,      // 内部格式
                                SHADOW_TEX_WIDTH,    // 宽度
                                SHADOW_TEX_HEIGHT,   // 高度
                                0,                // 边界宽度
                                GLES30.GL_RGBA,          // 格式
                                GLES30.GL_UNSIGNED_BYTE, // 每像素数据格式
                                null
                        );
            } else {

    		    // https://www.khronos.org/registry/OpenGL/extensions/EXT/EXT_color_buffer_half_float.txt
                // R16F 内部格式 是否可以作为 RenderTarget 需要查询扩展支持
                // 浮点纹理
                GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D,
                        0,
                        GLES30.GL_R16F,
                        SHADOW_TEX_WIDTH,
                        SHADOW_TEX_HEIGHT,
                        0,
                        GLES30.GL_RED,
                        GLES30.GL_FLOAT,
                        null
                );

            }

            // 绑定FBO附件的两个方法: 分别是把texture或者RBO作为framebuffer的附件
            // glFramebufferTexture2D
            // glFramebufferRenderbuffer

            // https://www.khronos.org/registry/OpenGL-Refpages/es2.0/xhtml/glFramebufferTexture2D.xml
            // glFramebufferTexture2D
            // attachment 可以是 GL_COLOR_ATTACHMENT0, GL_DEPTH_ATTACHMENT, or GL_STENCIL_ATTACHMENT.
            // textarget  可以是 GL_TEXTURE_2D 或者 是 GL_TEXTURE_CUBE_MAP_?

            // es3.0
            // attachment 可以是 GL_COLOR_ATTACHMENTi GL_DEPTH_ATTACHMENT GL_STENCIL_ATTACHMENT  GL_DEPTH_STENCIL_ATTACHMENT
            //                  支持 多渲染目标(Multiple Render Targets)
            //                  支持 深度和模板共用一个纹理

    		GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId);
            GLES30.glFramebufferTexture2D(
            	GLES30.GL_FRAMEBUFFER, 
            	GLES30.GL_COLOR_ATTACHMENT0,
            	GLES30.GL_TEXTURE_2D,
            	shadowId, // 2D纹理 内部格式 是 GL_RGBA or GL_R16F
            	0
            );

            if (Constant.USING_DEPTH_TEXTURE) {
                // 使用深度纹理方式，颜色附件 使用普通的 GL_RGBA 纹理
                GLES30.glFramebufferTexture2D(
                        GLES30.GL_FRAMEBUFFER,
                        GLES30.GL_DEPTH_ATTACHMENT, // 2.0
                        GLES30.GL_TEXTURE_2D,
                        renderDepthBufferId,
                        0 );

            } else {
                // 使用深度RBO方式，阴影在颜色附件，那么 颜色附件 使用GL_R16F浮点纹理
                GLES30.glFramebufferRenderbuffer (
                        GLES30.GL_FRAMEBUFFER,
                        GLES30.GL_DEPTH_ATTACHMENT,
                        GLES30.GL_RENDERBUFFER,
                        renderDepthBufferId );
            }

            int status = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER);

            if( status == GLES30.GL_FRAMEBUFFER_COMPLETE)
            {
                Log.e ("TOM", "framebuffer complete");
            }
            else
            {
                Log.e ("TOM", "framebuffer in not complete status = " + status );
            }


            checkError();

		}
    }


    public boolean checkError(){
	    int error = GLES30.glGetError() ;
	    if ( error != GLES30.GL_NO_ERROR) {
	        Log.e("TOM","[checkError] " + error );
	        throw  new RuntimeException("gl error " + error );
        }
	    return true ;
    }
}

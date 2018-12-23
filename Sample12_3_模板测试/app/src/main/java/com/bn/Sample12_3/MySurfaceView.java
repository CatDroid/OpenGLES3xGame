package com.bn.Sample12_3;
import java.io.IOException;
import java.io.InputStream;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import static com.bn.Sample12_3.Constant.*;

class MySurfaceView extends GLSurfaceView   
{
    private SceneRenderer mRenderer;//场景渲染器    
    int textureFloor;//系统分配的不透明地板纹理id
    int textureFloorBTM;//系统分配的半透明地板纹理id
    int textureBallId;//系统分配的篮球纹理id
	 
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        //this.setEGLConfigChooser(8, 8, 8, 8, 16, 8);
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {   
    	TextureRect texRect;//表示地板的纹理矩形
    	BallTextureByVertex btbv;//用于绘制的球
    	BallForControl bfd;//用于控制的球
    	
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲

            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,mDepthStenilFBO);
            GLES30.glClearColor(1.0f,0.0f,0.0f,1.0f);
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT );
             
            MatrixState.pushMatrix();
            MatrixState.translate(0, -2, 0);
            

            GLES30.glClear(GLES30.GL_STENCIL_BUFFER_BIT);   // 清除模板缓存
            GLES30.glEnable(GLES30.GL_STENCIL_TEST);        // 允许模板测试
            GLES30.glStencilFunc(GLES30.GL_ALWAYS, 1, 1);   // 设置模板测试参数 hhl:模板测试总是通过 参考值是1  掩码是1
            GLES30.glStencilOp(GLES30.GL_KEEP, GLES30.GL_KEEP, GLES30.GL_REPLACE);
                                                            // 设置模板测试后的操作 没通过模板:保留 通过模板没通过深度:保留 都通过:从参考值替换
            texRect.drawSelf(textureFloor);  // 绘制反射面地板
            

            GLES30.glStencilFunc(GLES30.GL_EQUAL,1, 1);
            GLES30.glStencilOp(GLES30.GL_KEEP, GLES30.GL_KEEP, GLES30.GL_KEEP);
            bfd.drawSelfMirror( textureBallId);         // 绘制镜像体
            GLES30.glDisable(GLES30.GL_STENCIL_TEST);   // 禁用模板测试
            


            GLES30.glEnable(GLES30.GL_BLEND);   // 开启混合
            GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);// 设置混合因子
            texRect.drawSelf(textureFloorBTM);  // 绘制半透明地板
            GLES30.glDisable(GLES30.GL_BLEND);  // 关闭混合


            bfd.drawSelf(textureBallId);        // 绘制实际物体
            MatrixState.popMatrix();

            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,0);
            mDrawer.draw( mColorTexture);

        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) 
        {
            //设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 3, 100);
            //设置摄像机观察矩阵
            MatrixState.setCamera(0.0f,8.0f,8.0f,0,0f,0,0,1,0);

            if(mDepthStenilFBO!=0) GLES30.glDeleteFramebuffers(1,new int[]{mDepthStenilFBO},0);
            if(mColorTexture!=0) GLES30.glDeleteTextures(1,new int[]{mColorTexture},0);
            if(mDepthStencilTexture!=0) GLES30.glDeleteTextures(1,new int[]{mDepthStencilTexture},0);

            mDepthStenilFBO = 0;
            mColorTexture = 0 ;
            mDepthStencilTexture = 0 ;


            int temp[] = new int[1] ;
            GLES30.glGenFramebuffers(1,temp,0);
            mDepthStenilFBO = temp[0];
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,mDepthStenilFBO);

            GLES30.glGenTextures(1,temp,0);
            mColorTexture = temp[0];
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,mColorTexture);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexImage2D( GLES30.GL_TEXTURE_2D,0,GLES30.GL_RGBA,width,height,0,GLES30.GL_RGBA,GLES30.GL_UNSIGNED_BYTE,null);

            GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER,GLES30.GL_COLOR_ATTACHMENT0,GLES30.GL_TEXTURE_2D,mColorTexture,0 );

            GLES30.glGenTextures(1,temp,0);
            mDepthStencilTexture = temp[0];
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,mDepthStencilTexture);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexImage2D( GLES30.GL_TEXTURE_2D, 0, GLES30.GL_DEPTH24_STENCIL8, width, height, 0,
                    GLES30.GL_DEPTH_STENCIL, GLES30.GL_UNSIGNED_INT_24_8, null );

            // glTexImage2D 内部格式 和 格式 :

            // 格式 和 数据类型  告诉OpenGL CPU传递给这个纹理的数据  具体包含什么成分和顺序(比如GL_DEPTH_STENCIL,GL_BGR,GL_RGB) 和 每个成分占多大(GL_UNSIGNED_INT_24_8 GL_UNSIGNED_INT_5_6_6)
            // 内部格式   就是OpenGL内部(shader)需要用到的具体格式(GL_RGB RGB三个成分都是8bit)，注意内部格式不会是GL_BGR，但可以是GL_RED(只要8bit作为红色)或者有大小的GL_RGB565

            // 对照表格 https://www.khronos.org/registry/OpenGL-Refpages/es3.0/html/glTexImage2D.xhtml


            GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_STENCIL_ATTACHMENT, GLES30.GL_TEXTURE_2D, mDepthStencilTexture, 0);



            int result = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER);
            if( result != GLES30.GL_FRAMEBUFFER_COMPLETE){
                throw new RuntimeException("glCheckFramebufferStatus Fail " + result );
            }

                // RBO作为深度和模板附件
//                glGenRenderbuffers(1, &rbo);
//                glBindRenderbuffer(GL_RENDERBUFFER, rbo);
//                glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, 800, 600);
//                glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo);

            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,0);

            mDrawer.init(width, height);


        }

        private int mDepthStenilFBO = 0 ;
        private int mColorTexture = 0;
        private int mDepthTexture = 0 ;
        private int mStencilTexture = 0 ;
        private int mDepthStencilTexture = 0 ;
        private SimpleDrawer mDrawer = new SimpleDrawer();

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0.0f,0.0f,0.0f,1.0f);
            //创建纹理矩形对对象 
            texRect=new TextureRect(MySurfaceView.this,4,2.568f);  
            //创建用于绘制的篮球对象
            btbv=new BallTextureByVertex(MySurfaceView.this,BALL_SCALE);
            //创建用于控制的篮球对象
            bfd=new BallForControl(btbv,3f);
            //关闭深度检测
            GLES30.glDisable(GLES30.GL_DEPTH_TEST);
            //初始化纹理
            textureFloor=initTexture(R.drawable.mdb);
            textureFloorBTM=initTexture(R.drawable.mdbtm);
            textureBallId=initTexture(R.drawable.basketball);            
            //打开背面剪裁   
            GLES30.glEnable(GLES30.GL_CULL_FACE);
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
        //通过输入流加载图片===============end=====================  
        
        //实际加载纹理
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

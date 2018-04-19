package com.bn.Sample10_2;
import java.io.IOException;
import java.io.InputStream;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;
import android.opengl.GLES30;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import static com.bn.Sample10_2.Constant.*;

class MySurfaceView extends GLSurfaceView 
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
    private SceneRenderer mRenderer;//场景渲染器
    
    private float mPreviousX;//上次的触控位置X坐标
    private float mPreviousY;//上次的触控位置Y坐标
    
    int textureIdEarth;//地球白天纹理id
    int textureIdEarthNight;//地球夜晚纹理id
    int textureIdMoon;//月球纹理id    
    int textureIdCloud;//云层纹理id    

    float yAngle=0;//太阳灯光绕y轴旋转的角度
    float xAngle=0;//摄像机绕X轴旋转的角度
    
    float eAngle=0;//地球自转角度    
    float cAngle=0;//天球自转的角度
	
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }
	
	//触摸事件回调方法
    @Override 
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
        	//触控横向位移太阳绕y轴旋转
            float dx = x - mPreviousX;//计算触控笔X位移 
            yAngle += dx * TOUCH_SCALE_FACTOR;//设置太阳绕y轴旋转的角度
            float sunx=(float)(Math.cos(Math.toRadians(yAngle))*100);
            float sunz=-(float)(Math.sin(Math.toRadians(yAngle))*100);
            MatrixState.setLightLocationSun(sunx,5,sunz);  
            
            //触控纵向位移摄像机绕x轴旋转 -90～+90
            float dy = y - mPreviousY;//计算触控笔Y位移 
            xAngle += dy * TOUCH_SCALE_FACTOR;//设置太阳绕y轴旋转的角度
            if(xAngle>90)
            {
            	xAngle=90;
            }
            else if(xAngle<-90)
            {
            	xAngle=-90;
            }
            float cy=(float) (7.2*Math.sin(Math.toRadians(xAngle)));
            float cz=(float) (7.2*Math.cos(Math.toRadians(xAngle)));
            float upy=(float) Math.cos(Math.toRadians(xAngle));
            float upz=-(float) Math.sin(Math.toRadians(xAngle));
            MatrixState.setCamera(0, cy, cz, 0, 0, 0, 0, upy, upz);           
        }
        mPreviousX = x;//记录触控笔位置
        mPreviousY = y;
        return true; 
    } 

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {   
    	Earth earth;//地球
    	Moon moon;//月球
    	Celestial cSmall;//小星星天球
    	Celestial cBig;//大星星天球
    	Cloud cloud;//云层
    	
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

            //保护现场
            MatrixState.pushMatrix();
            //地球自转
            MatrixState.rotate(eAngle, 0, 1, 0);
        	//绘制地球
            earth.drawSelf(textureIdEarth,textureIdEarthNight);
            
            //开启混合
            GLES30.glEnable(GLES30.GL_BLEND);
            //设置混合因子
            GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);
            cloud.drawSelf(textureIdCloud);
            //绘制云层

            // hhl: 即使纹理图片没有alpha通道(不是透明) 也可以通过RGB计算出一个alpha值

            // hhl: 云层的图片是类似黑白的, 白色的代表云,颜色是接近1.0f(归一化) 这样通过(R+G+B)/3 就作为alpha值 就可以实现白色的云alpha通道为1.0f 完全不透明

            // hhl: 1.0f 完全不透明  0.0f 完全透明


            //关闭混合
            GLES30.glDisable(GLES30.GL_BLEND);
            
            
            //推坐标系到月球位置            
            MatrixState.transtate(2f, 0, 0);  
            //月球自转     
            MatrixState.rotate(eAngle, 0, 1, 0);
            //绘制月球
            moon.drawSelf(textureIdMoon);
            //恢复现场
            MatrixState.popMatrix();
            
            //保护现场
            MatrixState.pushMatrix();  
            MatrixState.rotate(cAngle, 0, 1, 0);
            cSmall.drawSelf();
            cBig.drawSelf();
            //恢复现场
            MatrixState.popMatrix();
        }   

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            ratio= (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 4f, 100);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0,0,7.2f,0f,0f,0f,0f,1.0f,0.0f);       
            //打开背面剪裁
            GLES30.glEnable(GLES30.GL_CULL_FACE);  
            //初始化纹理
            textureIdEarth=initTexture(R.drawable.earth);
            textureIdEarthNight=initTexture(R.drawable.earthn);
            textureIdMoon=initTexture(R.drawable.moon);        
            textureIdCloud=initTexture(R.drawable.cloud); 
            //设置太阳灯光的初始位置
            MatrixState.setLightLocationSun(100,5,0);       
            
            //启动一个线程定时旋转地球、月球
            new Thread()
            {
            	public void run()
            	{
            		while(threadFlag)
            		{
            			//地球自转角度
            			eAngle=(eAngle+2)%360;
            			//天球自转角度
            			cAngle=(cAngle+0.2f)%360;
            			try {
							Thread.sleep(100);
						} catch (InterruptedException e) {				  			
							e.printStackTrace();
						}
            		}
            	}
            }.start();            
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0.0f,0.0f,0.0f, 1.0f);  
            //创建地球对象 
            earth=new Earth(MySurfaceView.this,2.0f);
            cloud=new Cloud(MySurfaceView.this,2.02f);
            //创建月球对象 
            moon=new Moon(MySurfaceView.this,1.0f);
            //创建小星星天球对象
            cSmall=new Celestial(1,0,1000,MySurfaceView.this);
            //创建大星星天球对象
            cBig=new Celestial(2,0,500,MySurfaceView.this);
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
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

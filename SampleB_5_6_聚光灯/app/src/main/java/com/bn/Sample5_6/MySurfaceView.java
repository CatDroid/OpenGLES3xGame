package com.bn.Sample5_6;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLSurfaceView;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
 
@SuppressLint("NewApi") class MySurfaceView extends GLSurfaceView 
{
    private SceneRenderer mRenderer;//场景渲染器    
     //摄像机位置相关
    float cx=0;
    float cy=30;
    float cz=60;
    float cAngle=0;
    final float cR=60;
    
    //灯光位置
	float lx=50;
	float ly=40;
	float lz=30;  
	//向量目标点
	float disx=0;
	float disy=0;
	float disz=0;
	
	float lAngle=0;
	final float lR=1;
	//灯光投影Up向量   
	float ux=-3;
	float uy=2;
	float uz=0;
	
	float tx=0;
	float ty=0;
	float tz=0;
	int angle=0;  
    //光源总变换矩阵
    float[] mMVPMatrixGY;
	static int width=0;//屏幕的宽度
	static int height=0;//屏幕的高度
	int move_x=0;//人体模型移动标志位
	int move_z=0;//人体模型移动标志位
	
	public static FloatBuffer dis; 
	
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }
	
	//触摸事件回调方法
    @SuppressLint("ClickableViewAccessibility") @Override 
    public boolean onTouchEvent(MotionEvent e) 
    {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_DOWN:
        {
        	if(x<MySurfaceView.width*0.5f&&y<MySurfaceView.height*0.5f)
        	{
        		move_x=1;
        	}else if(x<MySurfaceView.width*0.5f&&y>MySurfaceView.height*0.5f)
        	{
        		move_x=-1;
        	}else if(x>MySurfaceView.width*0.5f&&y<MySurfaceView.height*0.5f)
        	{
        		move_z=1;
        	}else if(x>MySurfaceView.width*0.5f&&y>MySurfaceView.height*0.5f)
        	{
        		move_z=-1;
        	}
        }
        break;
        case MotionEvent.ACTION_UP:
        {
        	move_x=0;
        	move_z=0;
        } 
        break;
       }
        return true;
    }

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {  
    	//从指定的obj文件中加载对象
		LoadeObjectVertexLand lovo_pm;//平面
		LoadedObjectVertexNormal lovo_ch;//人体模型	
		//纹理Id
		//int tyTexId;
        int roate=90;
        public void onDrawFrame(GL10 gl)
        {        	   
//        	//产生灯光位置的投影、摄像机矩阵
//            MatrixState.setCamera(lx,ly,lz,0,0f,0f,ux,uy,uz);
//            MatrixState.setProjectFrustum(-0.5f, 0.5f, -0.5f, 0.5f, 0.14f, 400);  
//            mMVPMatrixGY=MatrixState.getViewProjMatrix();
            
            
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(cx,cy,cz,0f,0f,0f,0f,1f,0f);
            MatrixState.setProjectFrustum(-ratio, ratio, -1.0f, 1.0f, 2, 1000);  
            MatrixState.setLightLocation(lx, ly, lz); 
            
            float light[]=new float[3];
            light[0]=disx-lx;
            light[1]=disy-ly;
            light[2]=disz-lz;
        	ByteBuffer llbb = ByteBuffer.allocateDirect(3*4);
            llbb.order(ByteOrder.nativeOrder());//设置字节顺序
            dis=llbb.asFloatBuffer();
            dis.put(light);
            dis.position(0); 
            
            //绘制人体模型
            MatrixState.pushMatrix(); 
            MatrixState.translate(tx, 0,tz);
            MatrixState.rotate(-roate, 0, 1,0);
            //roate-=3;
            MatrixState.scale(2f,2f,2f);
            //若加载的物体部位空则绘制物体
            lovo_ch.drawSelf(0);
            lovo_ch.drawSelf(1);
            MatrixState.popMatrix(); 
            
            //绘制最下面的平面
            MatrixState.pushMatrix();
            MatrixState.scale(1f,1f,1f);
            lovo_pm.drawSelf();  
            MatrixState.popMatrix(); 
            
 
            


        }

        float ratio;
        public void onSurfaceChanged(GL10 gl, int width, int height) 
        {
        	MySurfaceView.width=width;
        	MySurfaceView.height=height;
        	//设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            ratio = (float) width / height; 
            //设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        }
       
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {     	
        	//设置屏幕背景色RGBA
            GLES30.glClearColor(0.0f,0.0f,0.0f,1.0f);    
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //打开背面剪裁   
            GLES30.glEnable(GLES30.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();
            //加载要绘制的物体
            lovo_ch=LoadUtil.loadFromFileVertexOnly("ver.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            lovo_pm=LoadUtil.loadFromFileVertexLand("pm.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            Move_Thread mt=new Move_Thread();
            mt.start();

            
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
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_LINEAR);
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
        		GLES30.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  //纹理图像
        		0					  //纹理边框尺寸
        ); 
        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
        return textureId;
	}
    class Move_Thread extends Thread
    {
    	public void run()
    	{
    		while(true)
    		{
    			if(move_x==1)
    			{
    				//tx+=1;
    				disx+=1;
    			}else if(move_x==-1)
    			{
    				//tx-=1;
    				disx-=1;
    			}else if(move_z==1)
    			{
    				//tz-=1;
    				disz-=1;
    			}else if(move_z==-1)
    			{
    				//tz+=1;
    				disz+=1;
    			}
    			try {
					sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    }
}


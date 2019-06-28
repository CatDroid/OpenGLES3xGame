package com.bn.Sample4_7;

import static com.bn.Sample4_7.Constant.*;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;

public class MySurfaceView extends GLSurfaceView
{
	static float direction=0;//视线方向
    static float cx=0;//摄像机x坐标 
    static float cz=30;//摄像机z坐标
    
    static float tx=0;//观察目标点x坐标
    static float tz=0;//观察目标点z坐标
    static final float DEGREE_SPAN=(float)(3.0/180.0f*Math.PI);//摄像机每次转动的角度
    //线程循环的标志位  
    boolean flag=true;
    float Offset=30;
	SceneRenderer mRender;
	float preX;
	float preY;

	public MySurfaceView(Context context)
	{
		super(context);
		this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRender = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRender);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染 
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				preX=event.getX();
				preY=event.getY();
			break;
			case MotionEvent.ACTION_MOVE:
				float x=event.getX();
				float y=event.getY();
				float disX=x-preX;
				float disY=y-preY;
				cx-=disX*0.1f;
				cz-=disY*0.1f;	
				preX=x;
				preY=y;
			break;
		}
		tx=cx;//观察目标点x坐标 
        tz=cz-Offset;//观察目标点z坐标
        //设置新的摄像机位置
        MatrixState.setCamera(cx,105,cz,tx,75,tz,0,1,0);
		return true;
	}
	
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {
		Mountion mountion;
		int ssTexId;//砂石
		int lcpTexId;//绿草皮
		int dlTexId;//道路		
		int hcpTexId;//黄草皮
		int rgbTexId;
		@Override
		public void onDrawFrame(GL10 gl)
		{
			//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
        
            MatrixState.pushMatrix();
            mountion.drawSelf(ssTexId,lcpTexId,dlTexId,hcpTexId,rgbTexId);
            MatrixState.popMatrix(); 
		}
		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
        	GLES30.glViewport(0, 0, width, height);
            float ratio = (float) width / height;
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 2000);
            MatrixState.setCamera(cx,105,cz,tx,75,tz,0,1,0);
            MatrixState.setLightLocation(-400, 1000, -400); // Note: 光源的位置在场景的左后方
		}
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config)
		{
			// 设置屏幕背景色RGBA
			GLES30.glClearColor(0.0f,0.0f,0.0f,1.0f);

			// 打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);

            // 初始化MVP状态
            MatrixState.setInitStack();


            //从灰度图片中加载陆地上每个顶点的高度
    		yArray = loadLandforms(MySurfaceView.this.getResources(), R.drawable.heightmap16);// 64x64
            normols = caleNormal(yArray);
            mountion = new Mountion(
            		MySurfaceView.this,
					yArray,
					normols,
					yArray.length-1,
					yArray[0].length-1);


            // 初始化纹理
            ssTexId=initTexture(R.drawable.ss);
            lcpTexId=initTexture(R.drawable.lcp);
            dlTexId=initTexture(R.drawable.dl);
            hcpTexId=initTexture(R.drawable.hcp);
            rgbTexId=initTextureSingle(R.drawable.rgb);
		}
    }
	//生成纹理Id的方法
	public int initTextureSingle(int drawableId)
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
		  GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);	//绑定纹理
		  GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,
					GLES30.GL_LINEAR_MIPMAP_LINEAR);   		//使用MipMap线性纹理采样
		  GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MIN_FILTER,
			          GLES30.GL_LINEAR_MIPMAP_NEAREST);		//使用MipMap最近点纹理采样
		//ST方向纹理拉伸方式
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);		
        
        //通过输入流加载图片
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
        
        //实际加载纹理
        GLUtils.texImage2D
        (
        		GLES30.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  //纹理图像
        		0					  //纹理边框尺寸
        );   
        //自动生成Mipmap纹理
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
        //释放纹理图
        bitmapTmp.recycle();
        //返回纹理ID
        return textureId;
	}
	//生成纹理Id的方法
	public int initTexture(int drawableId)
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
		  GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);	//绑定纹理
		  GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,
					GLES30.GL_LINEAR_MIPMAP_LINEAR);   		//使用MipMap线性纹理采样
		  GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MIN_FILTER,
			          GLES30.GL_LINEAR_MIPMAP_NEAREST);		//使用MipMap最近点纹理采样
		//ST方向纹理拉伸方式
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_REPEAT);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_REPEAT);		
        
        //通过输入流加载图片
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
        
        //实际加载纹理
        GLUtils.texImage2D
        (
        		GLES30.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  //纹理图像
        		0					  //纹理边框尺寸
        );   
        //自动生成Mipmap纹理
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
        //释放纹理图
        bitmapTmp.recycle();
        //返回纹理ID
        return textureId;
	}
}
package com.bn.sample14_2;

import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES30;
import android.opengl.GLUtils;

import static com.bn.sample14_2.Sample14_2Activity.*;

class GL2JNIView extends GLSurfaceView {
    Renderer renderer;
    
	static float direction=0; 	//  视线方向   初始 x=0,z=20 -> 指向 x=0,z=0 为零度

    static float cx=0;			//	摄像机x坐标
    static float cz=20;			//	摄像机z坐标   跟 setupGraphics @ gl_code.cpp 初始化  九参数摄像头位置矩阵 要一样

	// 目前底层设置为 摄像机y坐标保持在 5 ，  (0,5,20) --> (cx,5,cz)

	// 底层 目标点  y坐标一直保持在 1 ，     (0,1,0) --> (tx,1,tz)

	// x-z 平面  从  0,20 到 0,0 为默认正方向
    
    static float tx=0;			//	观察目标点x坐标
    static float tz=0;			//	观察目标点z坐标   setupGraphics 初始化  九参数摄像头位置矩阵 要一样


    static final float DEGREE_SPAN=(float)(3.0/180.0f*Math.PI);//摄像机每次转动的角度  3.0度  这里转成弧度
    //线程循环的标志位  
    boolean flag=true;
    float x;
    float y;
    float Offset=20; // 摄像机的观察位置 与 目标位置 在水平方向上 要有20单位的距离
    
    @SuppressLint("ClickableViewAccessibility") @Override
	public boolean onTouchEvent(MotionEvent event) {
    	x=event.getX();
		y=event.getY();
		switch(event.getAction())
		{
			/*
				实现的效果是：
				横屏，
				按住屏幕
				左上  摄像机向前           右上  摄像机向后
				左下  摄像机右旋			右下  摄像机左旋

				注意y方向上(垂直方向) 不会移动

			 */
			case MotionEvent.ACTION_DOWN:
				flag=true;
				new Thread() // 只要按下 就立刻启动线程
				{
					@Override
					public void run()
					{
						while(flag) // 保持按住的话，一直执行线程
						{
							if(x>0&&x<WIDTH/2&&y>0&&y<HEIGHT/2) // 屏幕左上角   WIDTH  HEIGHT 是Activity在OnCreate时候获取的
							{//向前
								cx=cx-(float)Math.sin(direction)*1.0f; // 每次只往观察方向 前进一个单位
								cz=cz-(float)Math.cos(direction)*1.0f; // direction 是当前的方向 ; 目前是以 z正轴指向原点 为原始方向 左转为正
							}
							else if(x>WIDTH/2&&x<WIDTH&&y>0&&y<HEIGHT/2)
							{//向后
								cx=cx+(float)Math.sin(direction)*1.0f;
								cz=cz+(float)Math.cos(direction)*1.0f;
							}
							else if(x>0&&x<WIDTH/2&&y>HEIGHT/2&&y<HEIGHT) // 按下屏幕右下方   视线方向左移
							{											// 更新角度  摄像头位置不用改 但是目标点位置要更新
								//direction=direction-DEGREE_SPAN;	 // 右转为正  direction表示当前的角度 而不是旋转了多少角度
								direction=direction+DEGREE_SPAN;	// 注意direction旋转的正方向 (左转为正)  这个会影响后面目标点更新的公式
							}
							else if(x>WIDTH/2&&x<WIDTH&&y>HEIGHT/2&&y<HEIGHT)
							{
								//direction=direction+DEGREE_SPAN;
								direction=direction-DEGREE_SPAN;
							}
							try
							{
								Thread.sleep(100);
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						}
					}
				}.start();
			break;
			case MotionEvent.ACTION_UP:
				flag=false;
			break;
		}

		//设置新的观察目标点XZ坐标
//		tx=(float)(  cx +  Math.sin(direction)*Offset); // 右转为正
//		tz=(float)(  cz -  Math.cos(direction)*Offset);

		tx=(float)(  cx -  Math.sin(direction)*Offset);//观察目标点x坐标  // 左转为正
		tz=(float)(  cz -  Math.cos(direction)*Offset);//观察目标点z坐标

		//设置新的摄像机位置
		GL2JNILib.setCamera(   cx,5,cz,/*摄像机位置*/   tx,1,tz,/*目标位置*/    0,1,0);
		return true;
	}

	public GL2JNIView(Context context) {
        super(context);
		this.setEGLContextClientVersion(3);//使用OpenGL ES 3.0需设置该参数为3
		renderer=new Renderer();//创建Renderer类的对象
		this.setRenderer(renderer);//设置渲染器
		this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    private class Renderer implements GLSurfaceView.Renderer {
        public void onDrawFrame(GL10 gl) {
            GL2JNILib.step();//调用本地方法刷新场景
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GL2JNILib.init(GL2JNIView.this,width, height);//调用本地方法初始化
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) 
        {

        }
    }

    // 从gl_code.cpp NDK调用这个方法  用于解码图片 并且加载到纹理  返回纹理ID
	//加载纹理的方法
	@SuppressLint("NewApi")
	public static int initTextureRepeat(GLSurfaceView gsv,String pname)
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
		InputStream is = null;
		try {
			is = gsv.getResources().getAssets().open(pname);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        Bitmap bitmapTmp;
        try {
        	bitmapTmp = BitmapFactory.decodeStream(is);
        } 
        finally {
            try {
                is.close();
            } 
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        //实际加载纹理
        GLUtils.texImage2D
        (
        		GLES30.GL_TEXTURE_2D,   //纹理类型
        		0, 					  	//纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  	//纹理图像
        		0					  	//纹理边框尺寸
        );
        bitmapTmp.recycle(); 		  	//纹理加载成功后释放图片 
        return textureId;
	}
}

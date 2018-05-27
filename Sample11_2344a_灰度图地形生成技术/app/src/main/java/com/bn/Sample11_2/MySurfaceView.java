package com.bn.Sample11_2;

import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES31.GL_TEXTURE_HEIGHT;
import static android.opengl.GLES31.GL_TEXTURE_WIDTH;
import static com.bn.Sample11_2.Constant.*;
import static com.bn.Sample11_2.Sample11_2Activity.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLES31;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;

public class MySurfaceView extends GLSurfaceView
{
	float direction=0;//视线方向

	float radium = 20  ; // 摄像机旋转的半径
	float Offset = radium; // 摄像机位置距离观察点距离
	float cx=0;//摄像机x坐标
	float cz= radium;//12 //摄像机z坐标
	final float CameraYPosition = 5.0f; // 摄像机位置高度
	final float CameraYLockAt = 1.0f ;  // 摄像机望向的目标高度

	float tx=0;//观察目标点x坐标
	float tz=0;//观察目标点z坐标
    static final float DEGREE_SPAN=(float)(3.0/180.0f*Math.PI);//摄像机每次转动的角度
    //线程循环的标志位
    boolean flag=true;
    float x;
    float y;

	SceneRenderer mRender;
	float preX;  
	float preY;

	int colsPlusOne ; // 灰度图行列数 用在顶点着色器生成地形
	int rowsPlusOne ;
	
	public MySurfaceView(Context context)
	{
		super(context);
		this.setEGLContextClientVersion(3); 	// 设置使用OPENGL ES3.0
        mRender = new SceneRenderer();			// 创建场景渲染器
        setRenderer(mRender);					// 设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);// 设置渲染模式为主动渲染
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		x=event.getX();
		y=event.getY();
		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				flag=true;
				new Thread()
				{
					@Override
					public void run()
					{
						while(flag)
						{
							if(x>0&&x<WIDTH/2&&y>0&&y<HEIGHT/2)
							{//向前
								//cx=cx-(float)Math.sin(direction)*1.0f;
								//cz=cz-(float)Math.cos(direction)*1.0f;
								radium -= 1 ;
							}
							else if(x>WIDTH/2&&x<WIDTH&&y>0&&y<HEIGHT/2)
							{//向后
								//cx=cx+(float)Math.sin(direction)*1.0f;
								//cz=cz+(float)Math.cos(direction)*1.0f;
								radium += 1 ;
							}
							else if(x>0&&x<WIDTH/2&&y>HEIGHT/2&&y<HEIGHT)
							{
								direction=direction+DEGREE_SPAN;
							}
							else if(x>WIDTH/2&&x<WIDTH&&y>HEIGHT/2&&y<HEIGHT)
							{
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
		//设置新的摄像机目标点位置
		cx = (float)Math.sin(direction)*radium;
		cz = (float)Math.cos(direction)*radium;

		//设置新的观察目标点XZ坐标
		tx=(float)(cx-Math.sin(direction)*Offset);//观察目标点x坐标
        tz=(float)(cz-Math.cos(direction)*Offset);//观察目标点z坐标
        //设置新的摄像机位置
        MatrixState.setCamera(cx,CameraYPosition,cz,tx,CameraYLockAt,tz,0,1,0);
		return true;
	}
	
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {
		Mountion mountion;	// 山地地形对象引用

		int mountionId;		// 山的纹理id
		int rockId;			// 石头的纹理id
		int landId;			// 地形图的纹理id用于顶点着色器生成地形图

		@Override
		public void onDrawFrame(GL10 gl)
		{
			//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            
            MatrixState.pushMatrix();
			mountion.drawSelf(mountionId,rockId,landId);
			MatrixState.popMatrix();
		}
		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
			//设置视窗大小及位置
        	GLES30.glViewport(0, 0, width, height);
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 300); //100  hhl 如果UnitSize改成了2 那么land.png是64*64 就会64*2=128 如果这里还是100的话，远处的山头就很容易消失
            //调用此方法产生摄像机9参数位置矩阵
			cx = (float)Math.sin(direction)*radium; // hhl 通过修改半径的方法更新摄像头的位置
			cz = (float)Math.cos(direction)*radium;
			MatrixState.setCamera(cx,CameraYPosition,cz,tx,CameraYLockAt,tz,0,1,0);
		}
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config)
		{
			//设置屏幕背景色RGBA
			GLES30.glClearColor(0.0f,0.0f,0.0f,1.0f);
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            MatrixState.setInitStack();

			if(CONFIG_TEXTRUE==RENDER_TYPE.Using_Texture_In_VertexShader){
				landId=initTexture(LAND_ID);
				/*
				* BitmapFactory.decodeStream 解码出来的是原图大小 64*64
				* BitmapFactory.decodeResource 解码出来的是缩放的
				* 								在480dpi的屏幕上，
				* 								放在 drawable/raw(默认成160dpi) 放大成 192*192
				* 								ldpi 放大成 256*256
				* 								hdpi 放大成 128*128
				* 								mdpi 放大成 192*192
				*
				* */
				Bitmap temp = BitmapFactory.decodeResource(MySurfaceView.this.getResources(), LAND_ID);
				colsPlusOne = temp.getWidth();
				rowsPlusOne = temp.getHeight();
				temp.recycle();
				temp = null;
				mountion=new Mountion(MySurfaceView.this,rowsPlusOne-1,colsPlusOne-1);// 减去1是因为横向最后一个 和竖向最低一个 跟前面的一个顶点生成一个矩形/三角形

			}else{
				yArray=loadLandforms(MySurfaceView.this.getResources(), LAND_ID);
				mountion=new Mountion(MySurfaceView.this,yArray,yArray.length-1,yArray[0].length-1);
			}

            //加载山地地形草皮纹理
            mountionId=initTexture(GRASS_R_ID);
			if(CONFIG_TEXTRUE!=RENDER_TYPE.One_Texture){
				rockId=initTexture(ROCK_R_ID);
			}


		}
    }
	//生成纹理Id的方法
	public int initTexture(int drawableId)
	{

		//生成纹理ID
		int[] textures = new int[1];
		GLES30.glGenTextures(1, textures, 0);
		int textureId=textures[0];

		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);

		switch(CONFIG_TEXTRUE){
			case One_Texture:
			case Procedural_Texture:
				GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
				GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
				break;
			case MipMap_Texture:
			case Using_Texture_In_VertexShader:
				//GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
				//ShaderUtil.checkGlError("before");
				GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,
						GLES30.GL_LINEAR_MIPMAP_LINEAR);   		//使用MipMap线性纹理采样
				Log.e(TAG,"MAG MIPMAP ERROR = " +GLES30.glGetError());
				//ShaderUtil.checkGlError("glTexParameteri GL_LINEAR_MIPMAP_LINEAR error ");
				GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MIN_FILTER,
						GLES30.GL_LINEAR_MIPMAP_NEAREST);		//使用MipMap最近点纹理采样
//				 ShaderUtil.checkGlError("glTexParameteri");
				break;
			default:
				Log.e(TAG,"initTexture fail");
				throw new RuntimeException("Fail");
		}


		GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_REPEAT);
		GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_REPEAT);

        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
			opt.inPremultiplied = false ; // 不要预乘alpha 在要使用原图片 比如RenderScript或者自定义OpenGL上
			opt.inDither = false ; // 不去抖动 解决显示在平面上实际上RGB565 而不是RGB888 导致过渡带的颜色会有断层
			bitmapTmp = BitmapFactory.decodeStream(is,null,opt);
			Log.w(TAG," hasAlpha = " + bitmapTmp.hasAlpha()
					+ " isPremultiplied = " + bitmapTmp.isPremultiplied()
					+ " land? " + (drawableId==LAND_ID) );
        } finally {try {is.close();} catch(IOException e) {e.printStackTrace();}}

        GLUtils.texImage2D( 			// 实际加载纹理
        		GLES30.GL_TEXTURE_2D, 	// 纹理类型
        		0, 					  	// 纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  	// 纹理图像
        		0);					  	// 纹理边框尺寸


		if(CONFIG_TEXTRUE == RENDER_TYPE.MipMap_Texture){

			GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);// 生成一组Mipmap纹理图像

			if(drawableId == R.raw.grass){

				if(USING_MY_MIPMAP) {
					InputStream is128 = this.getResources().openRawResource(R.raw.grass_fake128128);
					Bitmap temp;
					try {	temp = BitmapFactory.decodeStream(is128);
					} finally {try {is128.close();} catch (IOException e) {e.printStackTrace();}}

					GLUtils.texImage2D(
							GLES30.GL_TEXTURE_2D,
							1, 		// 加载到mipmap纹理的第一层  grass原来是256*256 第一层是128*128
							temp,
							0
					);
					temp.recycle();
					temp = null;

					is128 = this.getResources().openRawResource(R.raw.grass_fake6464);
					try {	temp = BitmapFactory.decodeStream(is128);
					} finally {try {is128.close();} catch (IOException e) {e.printStackTrace();}}

					GLUtils.texImage2D(
							GLES30.GL_TEXTURE_2D,
							2, 					  // 加载到mipmap纹理的第一层  grass原来是256*256 第二层是64*64
							temp,
							0
					);
					temp.recycle();
					temp = null;
				}
			}

			int[] texDims = new int[2];
			GLES31.glGetTexLevelParameteriv(GLES31.GL_TEXTURE_2D,1,GL_TEXTURE_WIDTH,texDims,0);
			GLES31.glGetTexLevelParameteriv(GLES31.GL_TEXTURE_2D,1,GL_TEXTURE_HEIGHT,texDims,1);
			// 第0层是原图 第1层宽高减少一半

			int mipmap_w = texDims[0];
			int mipmap_h = texDims[1];
			Log.i(TAG, String.format("mipmap level 1 %d,%d -> %d %d ",bitmapTmp.getWidth() ,bitmapTmp.getHeight() , mipmap_w,mipmap_h));

			//GLES30.glViewport(texDims[0]/2,texDims[1]/2 ,texDims[0],texDims[1] );// 右下角 并不会影响glReadPixels

			// GLES30.glGetTexImage(GLES30.GL_TEXTURE_2D, 2, GLES30.GL_RGB, GLES30.GL_UNSIGNED_BYTE, result);//获取第二层mipmap数据
			// glGetTexImage ES 不支持

			int[] offscreen_fbo = new int[1];
			GLES30.glGenFramebuffers(1, offscreen_fbo,0);
			GLES30.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, offscreen_fbo[0]);

			// ES2.0 没有 GL_COLOR_ATTACHMENT1...15 只有GL_COLOR_ATTACHMENT0
			// 纹理的第二层 作为 FBO的附件
			GLES30.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, textures[0], 1);

			int status = GLES30.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
			if(status != GLES30.GL_FRAMEBUFFER_COMPLETE){
				Log.e(TAG," glCheckFramebufferStatus fail ...");
			}else{

				ByteBuffer pack2cpu = ByteBuffer.allocateDirect(mipmap_w*mipmap_h*4 );
				GLES30.glReadPixels(0,0,mipmap_w,mipmap_h,GL_RGBA,GL_UNSIGNED_BYTE, pack2cpu );

				FileChannel os = null;
				try {
					os = new FileOutputStream(new File(Environment.getExternalStorageDirectory()+"/mipmap_" + drawableId + "_level1.rgba")).getChannel();
					try {os.write(pack2cpu);} catch (IOException e) {e.printStackTrace();}
					try {os.close();} catch (IOException e) {e.printStackTrace();}
					Log.i(TAG,"save mipmap done!");
				} catch (FileNotFoundException e) {
					Log.e(TAG, "FileNotFoundException !!" + e.getMessage() );// 可能有权限要求
					e.printStackTrace();
				}
			}
			GLES30.glDeleteFramebuffers(1,offscreen_fbo,0);

		}

		if(CONFIG_TEXTRUE == RENDER_TYPE.Using_Texture_In_VertexShader){
			// 如果使用MIPMAP 但是不调用这个 会导致贴图黑色的
			GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
			if( drawableId == LAND_ID){
//				colsPlusOne = bitmapTmp.getWidth();
//				rowsPlusOne = bitmapTmp.getHeight();

				//bitmapTmp.copyPixelsToBuffer();
//				int bytes = bitmapTmp.getByteCount();
//				ByteBuffer buf = ByteBuffer.allocate(bytes);
//				bitmapTmp.copyPixelsToBuffer(buf);
//				buf.flip();
//
//				try {
//					FileChannel os = new FileOutputStream(new File(Environment.getExternalStorageDirectory()+"/temp4.rgba")).getChannel();
//					os.write(buf);
//					os.close();
//					Log.d(TAG,"save file " + bytes );
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}

			}
		}



        bitmapTmp.recycle();
        return textureId;
	}
}
package com.bn.Sample11_9;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Environment;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MySurfaceView extends GLSurfaceView
{
	private final static String TAG = "MySurfaceView";
	private final float mCameraZ = 1.0f;
	private final float mRectZ = -10.0f;

	SceneRenderer mRender;//渲染器引用
	public MySurfaceView(Context context)
	{
		super(context);
		this.setEGLContextClientVersion(3); // 设置使用OPENGL ES3.0
        mRender = new SceneRenderer();		// 创建场景渲染器
        setRenderer(mRender);				// 设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);// 设置渲染模式为主动渲染
	}
	
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {
		TextRect tRect;
		int wlWidth=512;	// 文字纹理图的宽度  !!!! hhl 这里固定的宽高 每次生成的Bitmap都是512*512的
		int wlHeight=512;	// 文字纹理图的高度
		long timeStamp=System.currentTimeMillis();// 获取当前系统的时间
		int texId=-1;		// 当前的纹理id
		@Override
		public void onDrawFrame(GL10 gl)
		{
			long tts=System.currentTimeMillis();// 获取绘制此帧时的系统时间
        	if(tts-timeStamp>500){	// 判断时间差是否大于500ms，如果大于500ms则更新文本 和 重新生成画笔颜色
        		timeStamp=tts;
        		FontUtil.cIndex=(FontUtil.cIndex+1)%FontUtil.content.length;
            	FontUtil.updateRGB();
        	}
        	if(texId!=-1) { // 每次都删除之前的纹理
        		GLES30.glDeleteTextures(1, new int[]{texId}, 0);
        	}

        	// hhl. 每次都重新生成512*512的bitmap和纹理,从0到cIndex行的全部字
        	Bitmap bm=FontUtil.generateWLT(FontUtil.getContent(FontUtil.cIndex, FontUtil.content), wlWidth, wlHeight);
        	texId=initTexture(bm);//将生成的纹理图加载


			GLES30.glClearColor(0.0f,0.5f,0.5f,1.0f);
            //GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
			GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
            
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, mRectZ);
			// hhl. bitmap带有alpha但是黑色背景的问题 开启blend bitmap是预乘的
			GLES30.glEnable(GLES30.GL_BLEND);
			GLES30.glBlendFunc(GLES30.GL_ONE,GLES30.GL_ONE_MINUS_SRC_ALPHA);
            tRect.drawSelf(texId);
			GLES30.glDisable(GLES30.GL_BLEND);
            MatrixState.popMatrix();
		}
		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
        	GLES30.glViewport(0, 0, width, height);
            float ratio = (float) width / height;

			// hhl. 原来是使用正交投影 所以不会出现近大远小效果
            //MatrixState.setProjectOrtho(-ratio, ratio, -1, 1, 1, 100);
			MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 100);
            MatrixState.setCamera(0,0,mCameraZ,   0,0,0,   0f,1.0f,0.0f);

			// hhl. 按照比例 计算出 适合的大小 的 顶点坐标 ; 适合指 宽度刚好能够投影到屏幕上
			tRect=new TextRect(MySurfaceView.this , mCameraZ * ratio * Math.abs( mCameraZ - mRectZ ) );


			// hhl. 使用View.draw的方法，把整个view渲染到Bitmap上，好处是可以调整文字位置
			Bitmap bmp = FontUtil.getSuitable(
					getContext(),
					R.string.bitmapTexture,
					//R.string.bitmapTextureLong,
					width,80); // 58 的时候 上下居中有点差别

			if(bmp!=null){
				try {
					FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory()+"/textViewBitmap.png");
					bmp.compress(Bitmap.CompressFormat.PNG,100,fos);
					fos.close();
					Log.w(TAG,"save bitmap done");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				bmp.recycle();
				bmp = null;
			}else{
				Log.e(TAG,"getSuitable TextView Bitmap Failure");
			}



		}
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config)
		{
			GLES30.glClearColor(0.0f,0.5f,0.5f,1.0f); //设置屏幕背景色RGBA hhl. 修改成非黑色，可以看到alpha效果
            //GLES30.glEnable(GLES30.GL_DEPTH_TEST);//打开深度检测 hhl. 没有使用深度，去掉
            GLES30.glEnable(GLES30.GL_CULL_FACE);//打开背面剪裁
            MatrixState.setInitStack();

		}
    }

	public int initTexture(Bitmap bitmap) {
		int[] textures = new int[1];
		GLES30.glGenTextures(1, textures, 0);
		int textureId=textures[0];    
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_REPEAT);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_REPEAT);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        return textureId;
	}
}
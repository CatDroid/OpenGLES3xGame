package com.bn.Sample12_4;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;

class MySurfaceView extends GLSurfaceView {
	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;//角度缩放比例
	private SceneRenderer mRenderer;//场景渲染器

	private float mPreviousY;//上次的触控位置Y坐标
	private float mPreviousX;//上次的触控位置X坐标

	public MySurfaceView(Context context) {
		super(context);
		this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
		mRenderer = new SceneRenderer(); //创建场景渲染器
		setRenderer(mRenderer); //设置渲染器
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染
	}

	//触摸事件回调方法
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		float y = e.getY();
		float x = e.getX();
		switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:
			float dy = y - mPreviousY;//计算触控笔Y位移
			float dx = x - mPreviousX;//计算触控笔X位移
			mRenderer.yAngle += dx * TOUCH_SCALE_FACTOR;//设置沿y轴旋转角度
			mRenderer.xAngle += dy * TOUCH_SCALE_FACTOR;//设置沿x轴旋转角度
			requestRender();//重绘画面
		}
		mPreviousY = y;//记录触控笔位置
		mPreviousX = x;//记录触控笔位置
		return true;
	}

	private class SceneRenderer implements GLSurfaceView.Renderer {
		float yAngle;//绕Y轴旋转的角度
		float xAngle; //绕X轴旋转的角度
		float countE = 0;
		float spanE = 0.01f;
		//从指定的obj文件中加载对象
		LoadedObjectVertexNormal lovo;

		public void onDrawFrame(GL10 gl) {


			if (countE >= 2) { // 若参考值大于2 则步进变为-0.01
				spanE = -0.01f;
			} else if (countE <= 0) { // 若参考值小于0，则步进步进变为0.01
				spanE = 0.01f;
			}// hhl 也就是conntE在 0到2之间徘徊 步进绝对值是0.01
			countE = countE + spanE;
			float e[] = { 1, countE - 1, -countE + 1, 0 };//定义裁剪平面解析方程组中4个参数
			// hhl D总是0
			// 也就是是 Ax+By+Cy+D=0 D=0 A=1 ==> x+By+Cz=0;  平面在x轴交叉x=1 y轴从-1到1 同时z轴从1到-1

			GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT//清除深度缓冲与颜色缓冲
					| GLES30.GL_COLOR_BUFFER_BIT);
			MatrixState.pushMatrix();
			MatrixState.translate(0, -2f, -25f); 	//	ch.obj
			MatrixState.rotate(yAngle, 0, 1, 0);	//	绕Y轴、X轴旋转
			MatrixState.rotate(xAngle, 1, 0, 0);			
			if (lovo != null) {
				lovo.drawSelf(e);
			}
			MatrixState.popMatrix();

			MatrixState.pushMatrix();
			MatrixState.translate(8f, 8f, -25f);
			MatrixState.rotate(yAngle, 1, 0, 0);
			MatrixState.rotate(xAngle, 0, 1, 0);
			if (lovo != null) {
				lovo.drawSelf(e);
			}
			MatrixState.popMatrix();

		}

		public void onSurfaceChanged(GL10 gl, int width, int height) {
			//设置视窗大小及位置
			GLES30.glViewport(0, 0, width, height);
			//计算GLSurfaceView的宽高比
			float ratio = (float) width / height;
			//调用此方法计算产生透视投影矩阵
			MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);
			//调用此方法产生摄像机9参数位置矩阵
			MatrixState.setCamera(0, 0, 0, 0f, 0f, -1f, 0f, 1.0f, 0.0f);
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			//设置屏幕背景色RGBA
			GLES30.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
			//打开深度检测
			GLES30.glEnable(GLES30.GL_DEPTH_TEST);
			//打开背面剪裁
			GLES30.glEnable(GLES30.GL_CULL_FACE);
			//初始化变换矩阵
			MatrixState.setInitStack();
			//初始化光源位置
			MatrixState.setLightLocation(40, 10, 20);
			//加载要绘制的物体
			lovo = LoadUtil.loadFromFileVertexOnly("ch.obj",
					MySurfaceView.this.getResources(), MySurfaceView.this);
		}
	}
}

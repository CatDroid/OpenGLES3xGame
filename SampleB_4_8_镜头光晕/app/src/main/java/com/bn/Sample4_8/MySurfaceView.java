package com.bn.Sample4_8;//声明包

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;
import static com.bn.Sample4_8.Constant.*;

public class MySurfaceView extends GLSurfaceView
{	
	private SceneRenderer mRenderer;//场景渲染器    
	public Flare mFlareCollect;//光晕对象
	
	public float lpx;//太阳位置x坐标
	public float lpy;//太阳位置y坐标
	float preX;//记录触控点x坐标
    float preY;//记录触控点y坐标
	public MySurfaceView(Context context){
		super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
	}
    
    @Override 
    public boolean onTouchEvent(MotionEvent e) 
    {//触摸事件回调方法         
    	float x=e.getX();//获取触控点x坐标
    	float y=e.getY();//获取触控点y坐标
    	
    	int action=e.getAction()&MotionEvent.ACTION_MASK;		
		switch(action)
		{
			case MotionEvent.ACTION_DOWN: //主点down
				preX=x;//记录当前触控点x坐标
				preY=y;//记录当前触控点y坐标
			break;	
			case MotionEvent.ACTION_MOVE://移动
				float dx=x-preX;//计算x轴移动位移
				float dy=y-preY;//计算y轴移动位移				
				CameraUtil.changeDirection(dx*0.1f);//改变摄像机方位角
				CameraUtil.changeYj(dy*0.1f);//改变摄像机仰角
				preX=x;//记录当前触控点x坐标
				preY=y;//记录当前触控点y坐标
			break;
		}
		return true;//返回true
    }
    
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {

		int[] mFlareTextureIds =new int[3];
		DrawFlare mFlareDrawer;
		TextureRect mSkyRectDrawer;        // 纹理矩形
		int[] mSkyRect6TextureIds =new int[6];// 天空盒六面的纹理

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config)
		{			
			// 设置管线状态  屏幕背景色RGBA 深度检测  背面剪裁
            GLES30.glClearColor(0.0f,0.0f,0.0f,1.0f);
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            GLES30.glEnable(GLES30.GL_CULL_FACE);

            // 初始化变换矩阵 stack为空  currMatrix=单位矩阵
            MatrixState.setInitStack();

            // 光晕的3个纹理
			mFlareTextureIds[0] = initTexture(R.drawable.flare1);
			mFlareTextureIds[1] = initTexture(R.drawable.flare2);
			mFlareTextureIds[2] = initTexture(R.drawable.flare3);

            mFlareCollect = new Flare(mFlareTextureIds); // 所有光晕对象集合,每个光晕的状态:显示大小,位置,颜色
            mFlareDrawer = new DrawFlare(MySurfaceView.this); // 只做渲染 传入颜色 外部修改

			// 天空盒的6个纹理
            mSkyRect6TextureIds[0] = initTexture(R.raw.skycubemap_back);
            mSkyRect6TextureIds[1] = initTexture(R.raw.skycubemap_left);
            mSkyRect6TextureIds[2] = initTexture(R.raw.skycubemap_right);
            mSkyRect6TextureIds[3] = initTexture(R.raw.skycubemap_down);
            mSkyRect6TextureIds[4] = initTexture(R.raw.skycubemap_up);
            mSkyRect6TextureIds[5] = initTexture(R.raw.skycubemap_front);

			// 创建纹理矩形对对象
            mSkyRectDrawer =new TextureRect(MySurfaceView.this);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height){

			// 设置视窗大小及位置
        	GLES30.glViewport(0, 0, width, height);

        	// 计算GLSurfaceView的宽高比
        	RATIO = (float) width / height;
            DIS_MAX = (int)Math.sqrt(RATIO*RATIO + 1);

            // 初始化 全局的摄像头矩阵 mVMatrix
        	CameraUtil.init3DCamera();

		}

		@Override
		public void onDrawFrame(GL10 gl)
		{
			//清除深度缓冲与颜色缓冲
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            
            // 更新 全局的透视投影矩阵 mProjMatrix
            MatrixState.setProjectFrustum(-RATIO, RATIO, -1.0f, 1.0f, 2, 1000);//设置透视投影
            // 更新 全局的摄像头矩阵 mVMatrix
            CameraUtil.flush3DCamera();

            // 这里没有做颜色混合 glDisable Blend

            // 天空盒六面调整值
            final float tzz=0.4f;
            // 绘制天空盒后面
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, -UNIT_SIZE+tzz);
            mSkyRectDrawer.drawSelf(mSkyRect6TextureIds[0]);
            MatrixState.popMatrix();              
            // 绘制天空盒前面
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, UNIT_SIZE-tzz);
            MatrixState.rotate(180, 0, 1, 0);
            mSkyRectDrawer.drawSelf(mSkyRect6TextureIds[5]);
            MatrixState.popMatrix(); 
            // 绘制左墙
            MatrixState.pushMatrix();
            MatrixState.translate(-UNIT_SIZE+tzz, 0, 0);
            MatrixState.rotate(90, 0, 1, 0);
            mSkyRectDrawer.drawSelf(mSkyRect6TextureIds[1]);
            MatrixState.popMatrix(); 
            // 绘制右墙
            MatrixState.pushMatrix();
            MatrixState.translate(UNIT_SIZE-tzz, 0, 0);
            MatrixState.rotate(-90, 0, 1, 0);
            mSkyRectDrawer.drawSelf(mSkyRect6TextureIds[2]);
            MatrixState.popMatrix();
            // 绘制下墙
            MatrixState.pushMatrix();
            MatrixState.translate(0, -UNIT_SIZE+tzz, 0);
            MatrixState.rotate(-90, 1, 0, 0);
            mSkyRectDrawer.drawSelf(mSkyRect6TextureIds[3]);
            MatrixState.popMatrix(); 
            // 绘制上墙
            MatrixState.pushMatrix();
            MatrixState.translate(0, UNIT_SIZE-tzz, 0);
            MatrixState.rotate(90, 1, 0, 0);
            mSkyRectDrawer.drawSelf(mSkyRect6TextureIds[4]);
            MatrixState.popMatrix();
            
            // 获取光源在屏幕上的坐标
        	float[] ls = CameraUtil.calLightScreen(RATIO);//计算在当前摄像机观察情况下光源点的屏幕坐标
			lpx = ls[0];//获取太阳位置x坐标
			lpy = ls[1];//获取太阳位置y坐标

            // 太阳超出屏幕 就不用绘制光晕
			if (lpx > RATIO||lpy > 1)
			{
				return;
			}

            // 更新 光晕绘制位置 和 光晕最终缩放大小
			mFlareCollect.update(lpx, lpy);
			
			// 绘制光晕 使用平行投影 + 使用混合
            MatrixState.setProjectOrtho(-RATIO, RATIO, -1.0f, 1.0f, 2, 1000);
            MatrixState.setCamera(0,0,0, 0,0,-1, 0,1,0);

            MatrixState.pushMatrix();// 保护现场
            
            GLES30.glEnable(GLES30.GL_BLEND);// 打开混合
            GLES30.glBlendFunc(GLES30.GL_SRC_COLOR, GLES30.GL_ONE);// 设置混合因子  s*s + 1*d
            //GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);// 不能用这个 flare.png没有alpha通道，而具体的光晕元素有些color的alpha是1.0 导致了黑块
            for(SingleFlare ss: mFlareCollect.sFl)    // 循环遍历光晕元素列表-进行绘制
            {
            	MatrixState.pushMatrix();   // 保护现场
            	MatrixState.translate(ss.px, ss.py, -100+ss.distance);          // 平移到指定位置  -100+ss.distance 光晕深度
            	MatrixState.scale(ss.displaySize, ss.displaySize, ss.displaySize); // 按比例缩放
            	mFlareDrawer.drawSelf(ss.texture,ss.color); //    绘制光晕元素
            	MatrixState.popMatrix();    // 恢复现场
            }
            GLES30.glDisable(GLES30.GL_BLEND);// 关闭混合
            
            MatrixState.popMatrix(); //恢复现场
     
		} 
    }


  	public int initTexture(int drawableId)
	{

		int[] textures = new int[1];
		GLES30.glGenTextures(1, textures, 0);
		int textureId=textures[0];    
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_REPEAT);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_REPEAT);

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

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0,  bitmapTmp, 0 );
        bitmapTmp.recycle();

        return textureId;
	}
}

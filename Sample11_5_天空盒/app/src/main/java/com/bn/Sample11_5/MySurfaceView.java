package com.bn.Sample11_5;

import java.io.IOException;
import java.io.InputStream;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import static com.bn.Sample11_5.Constant.*;

class MySurfaceView extends GLSurfaceView 
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
    private SceneRenderer mRenderer;//场景渲染器
    
    private float mPreviousY;//上次的触控位置Y坐标
    private float mPreviousX;//上次的触控位置X坐标
    
    //摄像机的位置角度
    float cx=0;
    float cy=2;
    float cz=24;
    float cr=24;//摄像机半径
    float cAngle=0;
    int[] textureIdA=new int[6];//存放天空盒6面的纹理id的数组
	
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染
    }
	
	//触摸事件回调方法
    @Override
    public boolean onTouchEvent(MotionEvent e) 
    {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dy = y - mPreviousY;//计算触控笔Y位移
            float dx = x - mPreviousX;//计算触控笔X位移
            cAngle+=dx * TOUCH_SCALE_FACTOR;
            cx=(float) (Math.sin(Math.toRadians(cAngle))*cr);
            cz=(float) (Math.cos(Math.toRadians(cAngle))*cr);
            cy+=dy/10.0f;
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }

	private class SceneRenderer implements GLSurfaceView.Renderer 
    { 
		TextureRect texRect;//用于绘制天空盒各个面的纹理矩形
        public void onDrawFrame(GL10 gl) //绘制一帧画面的方法
        { 
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            //设置摄像机
            MatrixState.setCamera(cx,cy,cz,0f,0f,0f,0f,1.0f,0.0f);  
            //天空盒6个面的调整值
            final float tzz=0.4f;            
            //绘制天空盒后面
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, -UNIT_SIZE+tzz);
            texRect.drawSelf(textureIdA[0]);
            MatrixState.popMatrix();              
            //绘制天空盒前面
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, UNIT_SIZE-tzz);
            MatrixState.rotate(180, 0, 1, 0);
            texRect.drawSelf(textureIdA[5]);
            MatrixState.popMatrix(); 
            //绘制天空盒左面
            MatrixState.pushMatrix();
            MatrixState.translate(-UNIT_SIZE+tzz, 0, 0);
            MatrixState.rotate(90, 0, 1, 0);
            texRect.drawSelf(textureIdA[1]);
            MatrixState.popMatrix(); 
            //绘制天空盒右面
            MatrixState.pushMatrix();
            MatrixState.translate(UNIT_SIZE-tzz, 0, 0);
            MatrixState.rotate(-90, 0, 1, 0);
            texRect.drawSelf(textureIdA[2]);
            MatrixState.popMatrix();
            //绘制天空盒下面
            MatrixState.pushMatrix();
            MatrixState.translate(0, -UNIT_SIZE+tzz, 0);
            MatrixState.rotate(-90, 1, 0, 0);
            texRect.drawSelf(textureIdA[3]);
            MatrixState.popMatrix(); 
            //绘制天空盒上面
            MatrixState.pushMatrix();
            MatrixState.translate(0, UNIT_SIZE-tzz, 0);
            MatrixState.rotate(90, 1, 0, 0);
            texRect.drawSelf(textureIdA[4]);
            MatrixState.popMatrix(); 
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 1000);
        }  

        public void onSurfaceCreated(GL10 gl, EGLConfig config) 
        {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0.0f,0.0f,0.0f,1.0f);    
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //打开背面剪裁   
            GLES30.glEnable(GLES30.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();
            //创建纹理矩形对对象 
            texRect=new TextureRect(MySurfaceView.this);   
            //加载纹理   
            textureIdA[0]=initTexture(R.raw.skycubemap_back);
            textureIdA[1]=initTexture(R.raw.skycubemap_left);
            textureIdA[2]=initTexture(R.raw.skycubemap_right);
            textureIdA[3]=initTexture(R.raw.skycubemap_down);
            textureIdA[4]=initTexture(R.raw.skycubemap_up);
            textureIdA[5]=initTexture(R.raw.skycubemap_front);
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
        		GLES30.GL_TEXTURE_2D,   //纹理类型
        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  //纹理图像
        		0					  //纹理边框尺寸
        );
        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
        return textureId;
	}
}

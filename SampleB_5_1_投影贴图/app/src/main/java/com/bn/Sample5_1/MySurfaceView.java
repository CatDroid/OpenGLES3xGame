package com.bn.Sample5_1;
import java.io.IOException;
import java.io.InputStream;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
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
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
    private SceneRenderer mRenderer;//场景渲染器    
    
    private float mPreviousY;//上次的触控位置Y坐标
    private float mPreviousX;//上次的触控位置X坐标    
    
    //摄像机位置相关
    float cx=0;
    float cy=30;
    float cz=60;
    float cAngle=0;
    final float cR=60;
    
    //灯光位置
	float lx=60;
	float ly=80;
	float lz=60;   
	float lAngle=0;
	final float lR=1;
	//灯光投影Up向量   
	float ux=0;
	float uy=0;
	float uz=1;
	
      
    //光源总变换矩阵
    float[] mMVPMatrixGY;
	
	public MySurfaceView(Context context) {

        super(context);
        this.setEGLContextClientVersion(3); // 设置使用OpenGL ES3.0
        mRenderer = new SceneRenderer();	// 创建场景渲染器
        setRenderer(mRenderer);				// 设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                                            // 设置渲染模式为主动渲染
        
        new Thread()  
        {
        	public void run()
        	{
        		while (true)
        		{
        			lAngle += 0.5;// 改变up向量绕Y轴的旋转角度
                    ux = (float) Math.sin(Math.toRadians(lAngle)) * lR;// 根据角度计算当前up向量的X分量
                    uz = (float) Math.cos(Math.toRadians(lAngle)) * lR;// 根据角度计算当前up向量的Z分量
                    try {
   					    Thread.sleep(20);// 线程休眠
	   				} catch (InterruptedException e) {
	   					e.printStackTrace();
	   				}
        		}
        	}
        }.start();
    }
	
	//触摸事件回调方法
    @SuppressLint("ClickableViewAccessibility") @Override 
    public boolean onTouchEvent(MotionEvent e) 
    {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {

        case MotionEvent.ACTION_MOVE:

            float dy = y - mPreviousY;          // 计算触控笔Y位移
            float dx = x - mPreviousX;          // 计算触控笔X位移
            cAngle += dx * TOUCH_SCALE_FACTOR;  // 设置沿x轴旋转角度
              
            cx = (float) Math.sin(Math.toRadians(cAngle))*cR;
            cz = (float) Math.cos(Math.toRadians(cAngle))*cR;
            cy += dy/10.0f;//设置沿z轴移动

            requestRender();//重绘画面

        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置

        return true;
    }

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {  
    	//从指定的obj文件中加载对象
		LoadedObjectVertexNormal lovo_pm;//平面
		LoadedObjectVertexNormal lovo_ch;//茶壶
		LoadedObjectVertexNormal lovo_cft;//长方体
		LoadedObjectVertexNormal lovo_qt;//球体
		LoadedObjectVertexNormal lovo_yh;//圆环		
		//纹理Id
		int tyTexId;
        
        public void onDrawFrame(GL10 gl)
        {        	   
        	//产生位于光源处虚拟摄像机的观察矩阵
            MatrixState.setCamera(
                    lx,ly,lz,
                    10f,0f,10f,
                    ux,uy,uz);


            // 应该按照胶片的比例 来设置 近平面的宽高，相当于限制可以投影
            float ty_ratio = (float) tyTexWidth / tyTexHeight ;

//            MatrixState.setProjectFrustum(
//                    -0.5f,
//                    0.5f ,
//                    -0.5f,
//                    0.5f,
//                    1f,
//                    400);

            MatrixState.setProjectFrustum(
                    -ty_ratio/2.0f,
                    ty_ratio/2.0f ,
                    -0.5f,
                    0.5f,
                    1f,
                    400);  // 产生位于光源处虚拟摄像机的投影矩阵

            mMVPMatrixGY = MatrixState.getViewProjMatrix();// 获取虚拟摄像机的观察、投影组合矩阵
        	
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            //生成实际摄像机观察矩阵
            MatrixState.setCamera(cx,cy,cz,0f,0f,0f,0f,1f,0f);
            //生成实际摄像机投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1.0f, 1.0f, 2, 1000);  
            //设置光源位置  点光源
            MatrixState.setLightLocation(lx, ly, lz); 
            //绘制最下面的平面
            lovo_pm.drawSelf(tyTexId, mMVPMatrixGY);
            
            //绘制茶壶
            MatrixState.pushMatrix(); 
            MatrixState.translate(0, 0, 30);
            MatrixState.scale(2,2,2);
            //若加载的物体部位空则绘制物体
            lovo_ch.drawSelf(tyTexId, mMVPMatrixGY);
            MatrixState.popMatrix();     
            
            //绘制球体
            MatrixState.pushMatrix(); 
            MatrixState.translate(-30, 0, 0);
            MatrixState.scale(2,2,2);
            //若加载的物体部位空则绘制物体
            lovo_qt.drawSelf(tyTexId, mMVPMatrixGY);
            MatrixState.popMatrix();    
            
            //绘制圆环
            MatrixState.pushMatrix(); 
            MatrixState.translate(30, 0, 0);
            MatrixState.scale(2,2,2);
            MatrixState.rotate(30, 0, 1, 0);
            //若加载的物体部位空则绘制物体
            lovo_yh.drawSelf(tyTexId, mMVPMatrixGY);
            MatrixState.popMatrix();  
            
            //绘制长方体
            MatrixState.pushMatrix(); 
            MatrixState.translate(0, 0, -30);
            MatrixState.scale(2,2,2);
            //若加载的物体部位空则绘制物体
            lovo_cft.drawSelf(tyTexId, mMVPMatrixGY);
            MatrixState.popMatrix();
        }

        float ratio;
        public void onSurfaceChanged(GL10 gl, int width, int height) 
        {
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
            lovo_ch=LoadUtil.loadFromFileVertexOnly("ch.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            lovo_pm=LoadUtil.loadFromFileVertexOnly("pm.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            lovo_cft=LoadUtil.loadFromFileVertexOnly("cft.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            lovo_qt=LoadUtil.loadFromFileVertexOnly("qt.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            lovo_yh=LoadUtil.loadFromFileVertexOnly("yh.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            //显示阴影贴图的纹理矩形
            tyTexId = initTexture(R.raw.ratio4p3);
        }
    }

    private float tyTexWidth = 0 ;
	private float tyTexHeight = 0 ;
	
 	public int initTexture(int drawableId)//textureId
	{
		//生成纹理ID  
		int[] textures = new int[1];
		GLES30.glGenTextures (1, textures, 0);
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
            tyTexWidth = bitmapTmp.getWidth();
            tyTexHeight = bitmapTmp.getHeight();
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

        GLUtils.texImage2D
        (
        		GLES30.GL_TEXTURE_2D,
        		0,
        		bitmapTmp,
        		0
        ); 
        bitmapTmp.recycle();
        return textureId;
	}
}

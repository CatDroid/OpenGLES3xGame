package com.bn.Sample5_7;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
 
class MySurfaceView extends GLSurfaceView 
{
    private SceneRenderer mRenderer;//场景渲染器    
     //摄像机位置相关
    float cx=0;
    float cy=30;
    float cz=60;
    
    //灯光位置
	float lx=0;//50;
	float ly=40;
	float lz=0;//30;  

    private float mPreviousY;//上次的触控位置Y坐标
    float xAngle=0;//摄像机绕X轴旋转的角度
    private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
   
    float roate=0;
    
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
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
        	
            //触控纵向位移摄像机绕x轴旋转 -90～+90
            float dy = y - mPreviousY;//计算触控笔Y位移 
            xAngle += dy * TOUCH_SCALE_FACTOR;//将Y位移折算成绕X轴旋转的角度
            if(xAngle>90)
            {
            	xAngle=90;
            }
            else if(xAngle<-90)
            {
            	xAngle=-90;
            }
            cy=(float) (7.2*Math.sin(Math.toRadians(xAngle)));
            cz=(float) (7.2*Math.cos(Math.toRadians(xAngle)));
            float upy=(float) Math.cos(Math.toRadians(xAngle));
            float upz=-(float) Math.sin(Math.toRadians(xAngle));
            MatrixState.setCamera(0, cy, cz, 0, 0, 0, 0, upy, upz);           
        }
        mPreviousY = y;//记录触控笔位置
        return true; 
    }

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {  
    	//从指定的obj文件中加载对象
		LoadedObjectVertexNormal lovo_ch;//人体模型
		
        public void onDrawFrame(GL10 gl)
        {        	   
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            
            //绘制人体模型
            MatrixState.pushMatrix(); 
            MatrixState.rotate(roate, 0, 1,0);
            //若加载的物体部位空则绘制物体
            lovo_ch.drawSelf();
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
        	
        	MatrixState.setCamera(0,0,7.2f,0f,0f,0f,0f,1.0f,0.0f);
            MatrixState.setProjectFrustum(-ratio, ratio, -1.0f, 1.0f, 2, 100); 
            MatrixState.setLightLocation(lx, ly, lz); 
            
            //启动一个线程定时旋转地球、月球
            new Thread()
            {
            	public void run()
            	{
            		while(true)
            		{
            			//地球自转角度
            			roate=(roate+2)%360;
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
            GLES30.glClearColor(0.0f,0.0f,0.0f,1.0f);    
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //打开背面剪裁   
            GLES30.glEnable(GLES30.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();
            //加载要绘制的物体
            lovo_ch=LoadUtil.loadFromFile("rw1.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
        }
    }
}


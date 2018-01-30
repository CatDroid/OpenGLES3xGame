package com.bn.Sample5_2;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;

class MySurfaceView extends GLSurfaceView 
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
    private SceneRenderer mRenderer;//场景渲染器
	 
	private float mPreviousY;//上次的触控位置Y坐标
    private float mPreviousX;//上次的触控位置X坐标
	
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }
	
	//触摸事件回调方法
    @Override 
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();//获取此次触控的y坐标
        float x = e.getX();//获取此次触控的x坐标
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE://若为移动动作
            float dy = y - mPreviousY;//计算触控位置的Y位移
            float dx = x - mPreviousX;//计算触控位置的X位移            
            for(SixPointedStar h:mRenderer.ha)//设置各个六角星绕x轴、y轴旋转的角度
            {
            	h.yAngle += dx * TOUCH_SCALE_FACTOR;
                h.xAngle+= dy * TOUCH_SCALE_FACTOR;
            }
        }
        mPreviousY = y;//记录触控笔y坐标
        mPreviousX = x;//记录触控笔x坐标
        return true;
    }
    

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {   
    	SixPointedStar[] ha=new SixPointedStar[6];//六角星数组
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
          //循环绘制各个六角星
            for(SixPointedStar h:ha)
            {
            	h.drawSelf();
            }
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视口的大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算视口的宽高比
        	float ratio= (float) width / height;
            //设置透视投影
        	MatrixState.setProjectFrustum(-ratio*0.4f + 0.2f , ratio*0.4f  + 0.2f, -1*0.4f   , 1*0.4f   , 1, 50);
            // frustum 只要保证  left-right /  top - bottom 能够是屏幕的比例 就不会变形
            // left 和 right 可以不是互为相反数  只是看到的移动了位置 如果left right + 的话 原图案移到左边
        	
            //设置摄像机
            MatrixState.setCamera(0,0,6,0f,0f,0f,0f,1.0f,0.0f);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        	GLES30.glClearColor(0.5f, 0.5f, 0.5f, 1.0f); 				//设置屏幕背景色RGBA
        	for (int i = 0; i < ha.length; i++) 
        	{//创建六角星数组中的各个对象
        			ha[i] = new SixPointedStar(MySurfaceView.this, 0.4f, 1.0f,-1.0f * i); // 物体坐标系中 z 在0 -1 -2 -3
        	}
        	GLES30.glEnable(GLES30.GL_DEPTH_TEST);			//打开深度检测
        }
    }
}

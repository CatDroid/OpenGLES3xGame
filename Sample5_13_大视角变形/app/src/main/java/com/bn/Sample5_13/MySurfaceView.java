package com.bn.Sample5_13;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import static com.bn.Sample5_13.Constant.*;

class MySurfaceView extends GLSurfaceView 
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
    private SceneRenderer mRenderer;//场景渲染器
	
    private float mPreviousX;//上次的触控位置X坐标
    
    float yAngle=0;//总场景绕y轴旋转的角度
	
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
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dx = x - mPreviousX;//计算触控位置X位移
            yAngle += dx * TOUCH_SCALE_FACTOR;//设置三角形对绕y轴旋转角度
        }
        mPreviousX=x;
        return true;
    }

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {   
    	Cube cube;//立方体对象引用
    	
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            //保护现场
            MatrixState.pushMatrix();
          //绕Y轴旋转（实现总场景旋转）
            MatrixState.rotate(yAngle, 0, 1, 0);
            
            //绘制左侧立方体
            MatrixState.pushMatrix();
            MatrixState.translate(-3, 0, 0);
            MatrixState.rotate(60, 0, 1, 0); // Mark.1 矩阵都是右乘的 所以 这里是 先做旋转后移位
            cube.drawSelf();// hhl. 这里使用了两个push push  第一个是整个场景的公共Matrix(View/Projective) 第二个是整个立方体的
                            //      每个立方体里面使用四边形绘画  但是每个四边形有自己的Matrix 基于整个立方体的
            MatrixState.popMatrix();
            
            //绘制右侧立方体
            MatrixState.pushMatrix();
            MatrixState.translate(3, 0, 0);
            MatrixState.rotate(-60, 0, 1, 0);
            cube.drawSelf();
            MatrixState.popMatrix();
            
            //恢复现场
            MatrixState.popMatrix();
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视口的大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算视口的宽高比
            ratio = (float) width / height;
            
	        //调用此方法计算产生透视投影矩阵
	        MatrixState.setProjectFrustum(-ratio*0.7f, ratio*0.7f, -0.7f, 0.7f, 1, 10);
	        //调用此方法产生摄像机矩阵
	        MatrixState.setCamera(0,0.5f,4,0f,0f,0f,0f,1.0f,0.0f);
            
            //初始化变换矩阵
            MatrixState.setInitStack();
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0.5f,0.5f,0.5f, 1.0f);  
            //创建立方体对象
            cube=new Cube(MySurfaceView.this);
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //打开背面剪裁   
            GLES30.glEnable(GLES30.GL_CULL_FACE);  
        }
    }
}

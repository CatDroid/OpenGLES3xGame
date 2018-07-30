package com.bn.Sample9_1;//声明包
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.view.SurfaceHolder;

class MySurfaceView extends GLSurfaceView 
{

    private SceneRenderer mRenderer;//场景渲染器    
    private RotateThread mRotateThread = null;
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(mRotateThread != null){
            mRotateThread.setStop();
            try {
                mRotateThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mRotateThread = null;
        }

        super.surfaceDestroyed(holder);
    }

    private class RotateThread extends Thread{

        private float CONFIG_X_ANGLE = 90 ; // 欧拉角(90,180,180)
        private float CONFIG_Y_ANGLE = 180;
        private float CONFIG_Z_ANGLE = 180;
        private boolean stop = false;
        @Override
        public void run() { // 万向节死锁 有三个姿态角 对应yaw pitch roll 但实际执行效果只有两个方向在变化
            while(!stop){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(mRenderer!=null){
                    if(  mRenderer.xAngle < CONFIG_X_ANGLE ){
                        mRenderer.xAngle += 2 ;
                        continue;
                    }
                    if(  mRenderer.yAngle < CONFIG_Y_ANGLE ){
                        mRenderer.yAngle += 2 ;
                        continue;
                    }
                    if(  mRenderer.zAngle < CONFIG_Z_ANGLE ){
                        mRenderer.zAngle += 2 ;
                        continue;
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mRenderer.xAngle = 0 ;
                    mRenderer.yAngle = 0 ;
                    mRenderer.zAngle = 0 ;

                }
            }
        }

        public void setStop(){
            stop = true ;
        }
    }

    private class SceneRenderer implements GLSurfaceView.Renderer
    {  
		float yAngle;
    	float xAngle;
        float zAngle;


		LoadedObjectVertexOnly lovo;



        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

            MatrixState.pushMatrix();//保护现场

            // 模拟万向节死锁  顺规 ZXY
            MatrixState.rotate(zAngle, 0, 0, 1);
            MatrixState.rotate(xAngle, 1, 0, 0);//绕X轴旋转
            MatrixState.rotate(yAngle, 0, 1, 0);//绕Y轴旋转

            
            //若加载的物体不为空则绘制物体
            if(lovo!=null)
            {
            	lovo.drawSelf();
            }   
            MatrixState.popMatrix(); //恢复现场                 
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0,0,25.0f,  0f,0f,0.0f,  0f,1.0f,0.0f);
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
            lovo=LoadUtil.loadFromFile("ch.obj", MySurfaceView.this.getResources(),MySurfaceView.this);


            if(mRotateThread != null){
                mRotateThread.setStop();
                try {
                    mRotateThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mRotateThread = null;
            }
            mRotateThread = new RotateThread();
            mRotateThread.start();

        }
    }
}

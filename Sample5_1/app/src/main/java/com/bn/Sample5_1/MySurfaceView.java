package com.bn.Sample5_1;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;



class MySurfaceView extends GLSurfaceView 
{
    private final String TAG = "MySurfaceView";
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
            //设置正交投影
        	MatrixState.setProjectOrtho(-ratio*2, ratio*2, -2, 2, 1, 10);
        	//  ratio , -ratio  -1 1 是 长方体视椎体的 近平面 的 Left right top bottom  经过投影后 到 x=[-1,1] y=[-1,1]范围的屏幕上
            //  也就是说这个框出来的区域 要满足 视口(viewport)的宽高比   只要满足视口宽高比 那么实际大小就是影响视角的大小
            //  视角越大 物体越小  因为屏幕就这么大 要显示更大视角的区域

        	//设置摄像机
			MatrixState.setCamera(
					0, 0, 3f, 
					0, 0, 0f, 
					0f, 1.0f, 0.0f
					);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0.5f,0.5f,0.5f, 1.0f);  
            //创建六角星数组中的各个六角星 
            for(int i=0;i<ha.length;i++)
            {
            	ha[i]=new SixPointedStar(MySurfaceView.this,0.2f/*六角形内圆半径*/,0.5f /*六角形外圆半径*/,

                        -0.3f*i /* 每个六角形 距离摄像机位置不一样, 实际是他们在物体坐标系中 不是在x-y平面 也就是深度不一样
                                    不在x-y平面导致 旋转的时候 每个星星都转到不同的位置
                                    导致在世界坐标系也就不一样
                                    */);
            }            
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);

            float[] test = new float[16];
            Matrix.setIdentityM(test,0);
            //设置绕y轴旋转yAngle度
            Matrix.rotateM(test,0,30,0,1,0);
            //设置绕x轴旋转xAngle度
            Matrix.rotateM(test,0,45,1,0,0);

            Log.d(TAG,"test rotateM  " +  ShaderUtil.printMatricx(test) );
            /*

            rotateM(test,0,45,0,1,0);
            rotateM(test,0,30,1,0,0);

            0.70710677 ,    0.35355338 ,    0.6123724 ,     0.0
            0.0 ,           0.8660254 ,     -0.5 ,          0.0
            -0.70710677 ,   0.35355338 ,    0.6123724 ,     0.0
            0.0 ,           0.0 ,           0.0 ,           1.0

            rotateM(test,0,30,0,1,0);
            rotateM(test,0,45,1,0,0);


            0.8660254 , 0.35355338 , 0.35355338 , 0.0
            0.0 , 0.70710677 , -0.70710677 , 0.0
            -0.5 , 0.6123724 , 0.6123724 , 0.0
            0.0 , 0.0 , 0.0 , 1.0


             */
        }
    }
}

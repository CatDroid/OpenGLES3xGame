package com.bn.Sample11_13;
import android.opengl.GLSurfaceView;
import android.opengl.GLES30;
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
    //关于摄像机的变量
  	float cx=0;//摄像机x位置
  	float cy=0;//摄像机y位置
  	float cz=60;//摄像机z位置
  	
  	float tx=0;//目标点x位置
  	float ty=12;//目标点y位置
  	float tz=0;//目标点z位置
  	public float currSightDis=50;//摄像机和目标的距离
	public float angdegAzimuth=180;//方位角	
	
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
             //不超过阈值不移动摄像机
             if(Math.abs(dx)<7f && Math.abs(dy)<7f){
             	break;
             }            
             angdegAzimuth += dx * TOUCH_SCALE_FACTOR;//设置沿x轴旋转角度
             //设置摄像机的位置
             setCameraPostion();
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }

    //设置摄像机位置的方法
  	public void setCameraPostion() {
  		//计算摄像机的位置
  		double angradAzimuth = Math.toRadians(angdegAzimuth);//方位角
  		cx = (float) (tx - currSightDis * Math.sin(angradAzimuth));
  		cy = (float) (ty + currSightDis);
  		cz = (float) (tz - currSightDis * Math.cos(angradAzimuth));
  	}
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {
    	//从指定的obj文件中加载对象
		LoadedObjectVertexNormal lovo[]=new LoadedObjectVertexNormal[2];//0---原本物体   1---描边                茶壶
		LoadedObjectVertexNormal lovo0[]=new LoadedObjectVertexNormal[2];//0---原本物体   1---描边                长方体
    	
        public void onDrawFrame(GL10 gl) 
        { 
        	 //设置camera位置
			MatrixState.setCamera(cx, ty, cz, tx, ty, tz, 0, 1, 0);
			MatrixState.setLightLocation(cx,cy,cz);
			
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

            MatrixState.pushMatrix();
            
            //茶壶1
            MatrixState.pushMatrix();
            MatrixState.translate(15f, 0f, -25f);   //ch.obj
            //仅绘制背面
            GLES30.glFrontFace(GLES30.GL_CW);
            lovo[1].drawSelfEdge();//绘制描边
            //绘制正面
            GLES30.glFrontFace(GLES30.GL_CCW);
            lovo[0].drawSelf();//绘制原本物体
            MatrixState.popMatrix();
            
            //茶壶2
            MatrixState.pushMatrix();
            MatrixState.translate(15f,0f, 5f);   //ch.obj
            //仅绘制背面
            GLES30.glFrontFace(GLES30.GL_CW);
            lovo[1].drawSelfEdge();//绘制描边
            //绘制正面
            GLES30.glFrontFace(GLES30.GL_CCW);
            lovo[0].drawSelf();//绘制原本物体
            MatrixState.popMatrix();
            
            
            //圆1
            MatrixState.pushMatrix();
            MatrixState.translate(-15f, 0f, 8f);
            //仅绘制背面
            GLES30.glFrontFace(GLES30.GL_CW);
            lovo0[1].drawSelfEdge();//绘制描边
            //绘制正面
            GLES30.glFrontFace(GLES30.GL_CCW);
            lovo0[0].drawSelf();//绘制原本物体
            MatrixState.popMatrix();
            
            //圆2
            MatrixState.pushMatrix();
            MatrixState.translate(-15f,3f, -2f);
            //仅绘制背面
            GLES30.glFrontFace(GLES30.GL_CW);
            lovo0[1].drawSelfEdge();//绘制描边
            //绘制正面
            GLES30.glFrontFace(GLES30.GL_CCW);
            lovo0[0].drawSelf();//绘制原本物体
            MatrixState.popMatrix();
            
            MatrixState.popMatrix();  
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 500);
            //计算摄像机的位置
            setCameraPostion();	
           
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
        	GLES30.glClearColor(0f,0f,0f,1.0f);        
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //打开背面剪裁   
            GLES30.glEnable(GLES30.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();
            //初始化光源位置
            MatrixState.setLightLocation(400, 100, 200);
            //加载要绘制的物体
            lovo=LoadUtil.loadFromFile("ch.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            lovo0=LoadUtil.loadFromFile("qt.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
        }
    }
}

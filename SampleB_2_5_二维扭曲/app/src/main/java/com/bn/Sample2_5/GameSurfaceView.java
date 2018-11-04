package com.bn.Sample2_5;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import static com.bn.Sample2_5.Constant.*;
import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
class GameSurfaceView extends GLSurfaceView
{
    private SceneRenderer mRenderer;//场景渲染器
    float mPreviousY;
    float mPreviousX;
    //-----纹理
    int tex_trangleId;//系统分配的草地纹理id
    //-----物体对象组件
    MultiTrangle trangle;
	  static float tx=0;//观察目标点x坐标  
	  static float ty=0;//观察目标点y坐标
	  static float tz=0;//观察目标点z坐标  
	  static float cx=0;//摄像机x坐标
	  static float cy=0;//摄像机y坐标
	  static float cz=30;//摄像机z坐标
	  float currY;
	  float rotation;
	  float twistingRatio;//三角形扭转的缩放比例
	  int symbol=1;
  float currRatio=0.05f;
	public GameSurfaceView(Context context) 
	{
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
        setKeepScreenOn(true);
    }
	//触摸事件回调方法
    @Override 
    public boolean onTouchEvent(MotionEvent e) 
    {   
        float x = e.getX();
        switch (e.getAction()) 
        {
	        case MotionEvent.ACTION_DOWN://按下动作
	        break;
	        case MotionEvent.ACTION_UP://抬起动作
	        break;
	        case MotionEvent.ACTION_MOVE:
	            float dx = x - mPreviousX;//计算触控笔X位移 
	        	if(dx<-10)//左转弯
	        	{
	        		rotation-=5f;
	        	}
	        	else if(dx>10)//右转弯
	        	{
	        		rotation+=5f;
	        	}
	        break;
        }
        mPreviousX = x;//记录触控笔位置
        return true;
    }
	class SceneRenderer implements GLSurfaceView.Renderer 
    {   
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            MatrixState.setCamera(cx,cy,cz,tx,ty,tz,0f,1.0f,0.0f); // hhl 摄像头9参数矩阵从没有改变
            MatrixState.pushMatrix();
            //将三角形移动到屏幕中心
            float upOffset= (float) (triangle_edgeLength/2/Math.cos(Math.PI/6)); // hhl 因为等边三角形的上顶点是在物体坐标系的原点
            MatrixState.translate(0, upOffset, 0);
            MatrixState.rotate(rotation, 0, 1, 0);
            trangle.drawSelf(tex_trangleId,twistingRatio);
            MatrixState.popMatrix();
        }  
        public void onSurfaceChanged(GL10 gl, int width, int height) 
        {
            //设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 3, 1000);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(cx,cy,cz,tx,ty,tz,0f,1.0f,0.0f);    
        }
        public void onSurfaceCreated(GL10 gl, EGLConfig config)
        {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0.0f,0.0f,0.0f,1.0f); 
            //打开深度检测 
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //打开背面剪裁   
            GLES30.glDisable(GLES30.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();
            //加载shader
            ShaderManager.loadCodeFromFile(GameSurfaceView.this.getResources());
            //编译shader
            ShaderManager.compileShader();
            initAllObject();//初始化纹理组件
            initAllTexture();//初始化纹理
            
            //创建一个线程，定时摆动树干
            new Thread()
            {
        		@Override
            	public void run()
            	{
        			while(true)
        			{
        				twistingRatio=twistingRatio+symbol*currRatio;
        				if(twistingRatio>1.0f)
        				{
        					twistingRatio=1.0f; // hhl twistingRatio=最大角度/半径 (就是每单位长度 旋转的弧度)
        					symbol=-symbol;		// 		按照等边三角形边长为8f 中心点到一个角的距离为半径  计算结果为8 / (4/cos30) ~= 4.6
        				}						//		1.0 = 最大角度 / 4.6  最大角度(弧度) = 4.6 *1.0 = 4.6弧 = 260度
        				if(twistingRatio<-1.0f) //		也就是在-260到260之间变化
        				{
        					twistingRatio=-1.0f;
        					symbol=-symbol;
        				}
        				try
        				{
        					Thread.sleep(100);
        				}
        				catch(Exception e)
        				{
        					e.printStackTrace();
        				}
        			}
            	}
            }.start();
        }
    }
	//创建所有的物体组件对象
	public void initAllObject()
	{
		 trangle=new MultiTrangle(ShaderManager.getTrangleShaderProgram(),
				 triangle_edgeLength,triangle_levelNum); //
	}
	//初始化所有的纹理图形
	public void initAllTexture()
	{
		Resources r=this.getResources();//获取资源
        tex_trangleId=initTexture(r,R.drawable.android);//草地纹理
	}
}

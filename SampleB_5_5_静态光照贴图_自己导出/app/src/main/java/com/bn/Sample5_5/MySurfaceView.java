package com.bn.Sample5_5;//声明包
import java.io.IOException;
import java.io.InputStream;

import android.opengl.GLSurfaceView;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
	float ty=0;//目标点y位置
	float tz=0;//目标点z位置
	float upX=0;
	float upY=1;
	float upZ=0;

	float tempx=upX+cx;//中间值x
	float tempz=upZ+cy;//中间值z
	float tempLimit=tempz;
	public float currSightDis=150;//摄像机和目标的距离
	float angdegElevation=30;//仰角
	public float angdegAzimuth=180;//方位角	

    int[] TexId = new int[Constants.SIZE];

  	float ratio;
  	
	
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
            angdegElevation += dy * TOUCH_SCALE_FACTOR;//设置沿z轴旋转角度
            //将仰角限制在5～90度范围内
            angdegElevation = Math.max(angdegElevation, 5);
            angdegElevation = Math.min(angdegElevation, 90);
            //设置摄像机的位置
            setCameraPostion();
        break;
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }
    //设置摄像机位置的方法
   	public void setCameraPostion() {
   		//计算摄像机的位置
   		double angradElevation = Math.toRadians(angdegElevation);//仰角（弧度）
   		double angradAzimuth = Math.toRadians(angdegAzimuth);//方位角
   		cx = (float) (tx - currSightDis * Math.cos(angradElevation)	* Math.sin(angradAzimuth));
   		cy = (float) (ty + currSightDis * Math.sin(angradElevation));
   		cz = (float) (tz - currSightDis * Math.cos(angradElevation) * Math.cos(angradAzimuth));
   		
   		tempx=(float) (Math.sin(angradAzimuth)*tempLimit);
		tempz=(float) (Math.cos(angradAzimuth)*tempLimit);
		//计算up向量值
		upX=tempx-cx;
		upZ=tempz-cz;
		MatrixState.setCamera(cx, cy, cz, tx, ty, tz, upX,upY, upZ);
   	}

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {  

		LoadedObjectVertexNormalTexture lovo[]=new LoadedObjectVertexNormalTexture[Constants.SIZE];


        public void onDrawFrame(GL10 gl) 
        {

            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

            MatrixState.pushMatrix();
            lovo[0].drawSelf1(TexId[0]); // 导出的obj文件的模型已经是世界坐标系
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            lovo[1].drawSelf1(TexId[1]);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            lovo[2].drawSelf1(TexId[2]);
            MatrixState.popMatrix();


            MatrixState.pushMatrix();
            MatrixState.translate(30,0,-30); // 球的半径是20 而且导出的obj文件已经把模型移到世界坐标系 顶点坐标是在世界坐标系
            lovo[3].drawSelf(TexId[3],0);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(30,0,-30); // 平面阴影
            lovo[3].drawSelf(TexId[3],1);
            MatrixState.popMatrix();

        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {

        	GLES30.glViewport(0, 0, width, height); 

            ratio = (float) width / height;

            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 1000);

            setCameraPostion();
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) 
        {

            GLES30.glClearColor(0.5f,0.5f,1.0f,1.0f);    

            GLES30.glEnable(GLES30.GL_DEPTH_TEST);

            GLES30.glEnable(GLES30.GL_CULL_FACE);

            MatrixState.setInitStack();

            MatrixState.setLightLocation(-209,168,-131); // 3ds max 导出时z和y轴交换 z=-y y=z


            // true jtvertex.glsl/jtfrag.glsl 静态光照贴图
            lovo[0] = LoadUtil.loadFromFile("plane.obj", MySurfaceView.this.getResources(),
                    MySurfaceView.this, true);
            lovo[1] = LoadUtil.loadFromFile("box.obj", MySurfaceView.this.getResources(),
                    MySurfaceView.this,true);
            lovo[2] = LoadUtil.loadFromFile("sphere.obj", MySurfaceView.this.getResources(),
                    MySurfaceView.this,true);

            lovo[3] = LoadUtil.loadFromFile("sphere.obj", MySurfaceView.this.getResources(),
                    MySurfaceView.this,false); // 使用顶点光照 需要加载法线


           
            TexId[0] = initTexture(R.drawable.plane);
            TexId[1] = initTexture(R.drawable.box);
            TexId[2] = initTexture(R.drawable.sphere);  // 静态光照贴图
            TexId[3] = initTexture(R.drawable.sphere2); // 实时顶点光照+平面阴影

        }
    }

  	private int initTexture(int drawableId)
	{
		int[] textures = new int[1];
		GLES30.glGenTextures(1, textures, 0);
		int textureId=textures[0];    
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_NEAREST);
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

	   	GLUtils.texImage2D(GLES30.GL_TEXTURE_2D,0, GLUtils.getInternalFormat(bitmapTmp), bitmapTmp, GLUtils.getType(bitmapTmp), 0);
	    bitmapTmp.recycle();
        return textureId;
	}
}

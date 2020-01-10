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
	public float currSightDis=100;//摄像机和目标的距离
	float angdegElevation=30;//仰角
	public float angdegAzimuth=180;//方位角	
	final int size=4;
    int[] TexId=new int[size];//场景中有四个物体

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
    	//从指定的obj文件中加载的对象
		LoadedObjectVertexNormalTexture lovo[]=new LoadedObjectVertexNormalTexture[5];
    	float[] trans={18,20,0};//普通茶壶位置
    	
        public void onDrawFrame(GL10 gl) 
        {
             
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            
            
            
            //绘制具有静态光照贴图茶壶
            for(int i=0;i<2;i++) // lovo[0]  lovo[1]
            {
	    		MatrixState.pushMatrix();
	        	lovo[i].drawSelf1(TexId[i]);
	        	MatrixState.popMatrix();  
            }
            
            //绘制平板
            MatrixState.pushMatrix();
            lovo[2].drawSelf1(TexId[2]);
            MatrixState.popMatrix();
            
            //普通茶壶绘制--阴影
    		MatrixState.pushMatrix();
    		MatrixState.scale(1.2f, 1.2f,1.2f);
   		 	MatrixState.translate(trans[0],trans[2],trans[1]);
   		 	lovo[3].drawSelf(TexId[3],1);
   		 	MatrixState.popMatrix();
            
            //普通茶壶绘制
    		MatrixState.pushMatrix();
    		MatrixState.scale(1.2f, 1.2f, 1.2f);
   		 	MatrixState.translate(trans[0],trans[2],trans[1]);
   		 	lovo[3].drawSelf(TexId[3],0);
   		 	MatrixState.popMatrix();
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
        	 ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 1000);
            //计算摄像机的位置
            setCameraPostion();
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) 
        {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0.5f,0.5f,1.0f,1.0f);    
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //打开背面剪裁   
            GLES30.glEnable(GLES30.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();
            //初始化光源位置
            MatrixState.setLightLocation(186,111,104);


            // true jtvertex.glsl/jtfrag.glsl 静态光照贴图
            lovo[0]=LoadUtil.loadFromFile("ch1.obj", MySurfaceView.this.getResources(),MySurfaceView.this,true);
            lovo[1]=LoadUtil.loadFromFile("ch2.obj", MySurfaceView.this.getResources(),MySurfaceView.this,true);
            lovo[2]=LoadUtil.loadFromFile("pm.obj", MySurfaceView.this.getResources(),MySurfaceView.this,true);
            lovo[3]=LoadUtil.loadFromFile("ptch.obj", MySurfaceView.this.getResources(),MySurfaceView.this,false);
           
            TexId[0]=initTexture(R.drawable.c1);//茶壶1静态光照贴图
            TexId[1]=initTexture(R.drawable.c2);//茶壶2静态光照贴图
            TexId[2]=initTexture(R.drawable.pm);//平面贴图
            TexId[3]=initTexture(R.drawable.ghxp);//普通茶壶贴图
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
        
        //通过输入流加载图片===============begin===================
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
        //通过输入流加载图片===============end===================== 
	   	GLUtils.texImage2D
	    (
	    		GLES30.GL_TEXTURE_2D, //纹理类型
	     		0, 
	     		GLUtils.getInternalFormat(bitmapTmp), 
	     		bitmapTmp, //纹理图像
	     		GLUtils.getType(bitmapTmp), 
	     		0 //纹理边框尺寸
	     );
	    bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
        return textureId;
	}
}

package com.bn.Sample12_2;

import static com.bn.Sample12_2.Sample12_2Activity.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;

public class MySurfaceView extends GLSurfaceView
{
	static final float UNIT_SIZE=1f;
	static float direction=0;//视线方向
    static float cx=0;//摄像机x坐标
    static float cz=15;//摄像机z坐标
    static final float DEGREE_SPAN=(float)(3.0/180.0f*Math.PI);//摄像机每次转动的角度
	static final boolean CONFIG_ENABLE_SORT = true ;


    //线程循环的标志位
    boolean flag=true;
    float x;
    float y;
    float Offset=15;
	SceneRenderer mRender;
	float preX;
	float preY;
	float ratio;
	public MySurfaceView(Context context)
	{
		super(context);
		this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRender = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRender);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染 
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		x=event.getX();
		y=event.getY();
		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				flag=true;
				new Thread()
				{
					@Override
					public void run()
					{
						while(flag)
						{
							if(x>0&&x<WIDTH/2&&y>0&&y<HEIGHT/2)
							{//向前
								Offset=Offset-0.5f;
							}
							else if(x>WIDTH/2&&x<WIDTH&&y>0&&y<HEIGHT/2)
							{//向后
								Offset=Offset+0.5f;
							}
							else if(x>0&&x<WIDTH/2&&y>HEIGHT/2&&y<HEIGHT)
							{
								direction=direction+DEGREE_SPAN;
							}
							else if(x>WIDTH/2&&x<WIDTH&&y>HEIGHT/2&&y<HEIGHT)
							{
								direction=direction-DEGREE_SPAN;
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
			break;
			case MotionEvent.ACTION_UP:
				flag=false;
			break;
		}
		//设置新的观察目标点XZ坐标
		cx=(float)(Math.sin(direction)*Offset);//观察目标点x坐标 
        cz=(float)(Math.cos(direction)*Offset);//观察目标点z坐标     
        
        //计算所有树的朝向
        mRender.tg.calculateBillboardDirection();
        
        //给树按照离视点的距离排序
		if(CONFIG_ENABLE_SORT){
			Collections.sort(mRender.tg.alist);
		}
        //设置新的摄像机位置
        MatrixState.setCamera(cx,0,cz,0,0,0,0,1,0);
		return true;
	}
	
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {
		TreeGroup tg;
		Desert desert;
		TextureRect tr;//alpha测试用纹理矩形
		int treeId;
		int desertId;
		int maskTextureId;///系统分配的alpha测试纹理id
		@Override
		public void onDrawFrame(GL10 gl)
		{
			//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 100);
            //调用此方法产生摄像机观察矩阵
            MatrixState.setCamera(cx,0,cz,0,0,0,0f,1.0f,0.0f);
            MatrixState.pushMatrix();
            MatrixState.translate(0, -2, 0);
            desert.drawSelf(desertId);
            MatrixState.popMatrix();
            
            //开启混合 //设置混合因子  这是因为树木的纹理图片tree.png带有透明通道
            GLES30.glEnable(GLES30.GL_BLEND);
            GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);
            MatrixState.pushMatrix();
            MatrixState.translate(0, -2, 0);
            tg.drawSelf(treeId);
            MatrixState.popMatrix();
            //关闭混合
            GLES30.glDisable(GLES30.GL_BLEND);   
             
            //清除深度缓冲  				hhl 注意这里不管之前的深度 因为外框不属于主场景，不应该与主场景中的物体以同一个深度进行检测
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT);
			MatrixState.pushMatrix();
			//调用此方法计算产生正交矩阵		hhl 注意这里的是正交矩阵   hhl 由于TextureRect的顶点就是-4*UNIT_SIZE UNIT_SIZE=0.25 所以这里的近平面的矩形框是1
			MatrixState.setProjectOrtho(-1f, 1f, -1f, 1f, 1, 100);
			//调用此方法产生摄像机观察矩阵
			MatrixState.setCamera(
									0, 0, 3,
									0f, 0f, 0f,
									0f, 1.0f, 0.0f);
			tr.drawSelf(maskTextureId);//绘制alpha测试用矩形
			MatrixState.popMatrix();
		}
		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
			//设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 100);
            //调用此方法产生摄像机观察矩阵
            MatrixState.setCamera(cx,0,cz,0,0,0,0f,1.0f,0.0f);
		}
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config)
		{
			//设置屏幕背景色RGBA
            GLES30.glClearColor(1.0f,1.0f,1.0f,1.0f);
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            MatrixState.setInitStack();
            
            tg=new TreeGroup(MySurfaceView.this);
            tr = new TextureRect(MySurfaceView.this);

          	//初始化树顺序       hhl 这里有重新计算树的远近进行排序
			if(CONFIG_ENABLE_SORT) {
				Collections.sort(mRender.tg.alist);
			}
            desert=new Desert
            (
            	MySurfaceView.this,
            	new float[]
	            {
	          		0,0, 0,6, 6,6,
	          		6,6, 6,0, 0,0
	            } ,
	            30,
	            20
            ); 
            //初始化纹理
            treeId=initTexture(R.drawable.tree);
            desertId=initTexture(R.drawable.desert); 
			maskTextureId = initTexture(R.drawable.mask);
		}
    }

	public int initTexture(int drawableId)
	{

		int[] textures = new int[1];
		GLES30.glGenTextures(1, textures, 0);
		int textureId=textures[0];    
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_REPEAT);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_REPEAT);
        

        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try {
        	bitmapTmp = BitmapFactory.decodeStream(is);
        } finally {
            try {is.close();} catch(IOException e) {e.printStackTrace();}
        }

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmapTmp, 0);
        bitmapTmp.recycle();
        return textureId;
	}
}
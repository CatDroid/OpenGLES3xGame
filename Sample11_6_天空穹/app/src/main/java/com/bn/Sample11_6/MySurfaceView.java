package com.bn.Sample11_6;
import static com.bn.Sample11_6.Constant.*;
import static com.bn.Sample11_6.Sample11_6Activity.*;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import com.bn.Sample11_6.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;

public class MySurfaceView extends GLSurfaceView
{
	static float direction=0;//视线方向
    static float cx=0;//摄像机x坐标 
    static float cz=20;//摄像机z坐标
    
    static float tx=0;//观察目标点x坐标
    static float tz=0;//观察目标点z坐标   
    static final float DEGREE_SPAN=(float)(3.0/180.0f*Math.PI);//摄像机每次转动的角度
    //线程循环的标志位
    boolean flag=true;
    float x;
    float y;
    float Offset=20;
	SceneRenderer mRender;
	float preX;
	float preY;
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
								cx=cx-(float)Math.sin(direction)*1.0f;
								cz=cz-(float)Math.cos(direction)*1.0f;
							}
							else if(x>WIDTH/2&&x<WIDTH&&y>0&&y<HEIGHT/2)
							{//向后
								cx=cx+(float)Math.sin(direction)*1.0f;
								cz=cz+(float)Math.cos(direction)*1.0f;
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
		tx=(float)(cx-Math.sin(direction)*Offset);//观察目标点x坐标 
        tz=(float)(cz-Math.cos(direction)*Offset);//观察目标点z坐标
		return true;
	}
	
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {
		Mountion mountion;
		//山的纹理id
		int mountionId;
		int rockId;
		Sky sky;
		int skyId;
		@Override
		public void onDrawFrame(GL10 gl)
		{
			//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            //设置新的摄像机位置
            MatrixState.setCamera(cx,5,cz,tx,2,tz,0,1,0);
            MatrixState.pushMatrix();
            mountion.drawSelf(mountionId,rockId);
            MatrixState.popMatrix();
            
            MatrixState.pushMatrix();
			if(USING_SMALL_SKY_FALLOW_CAMERA){
				MatrixState.translate(cx, -6, cz);
			}else{
				MatrixState.translate(0, -2, 0); // hhl 天空穹 向下移动2个像素
			}

            sky.drawSelf(skyId);
            MatrixState.popMatrix();
		}
		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
			//设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 3000);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(cx,5,cz,tx,2,tz,0,1,0);
		}
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config)
		{
			//设置屏幕背景色RGBA
            GLES30.glClearColor(1.0f,1.0f,1.0f,1.0f);
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            MatrixState.setInitStack();
    		yArray=loadLandforms(MySurfaceView.this.getResources(), R.drawable.land);
           
            mountion=new Mountion(MySurfaceView.this,yArray,yArray.length-1,yArray[0].length-1);
            sky=new Sky(MySurfaceView.this);
            //初始化纹理
            skyId=initTexture(R.drawable.sky,false); // hhl:  天空的纹理没有使用MIPMAP
            mountionId=initTexture(R.drawable.grass,true);
            rockId=initTexture(R.drawable.rock,true);
		}  
    }
	//生成纹理Id的方法
	public int initTexture(int drawableId,boolean isMipmap)
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
		if(isMipmap)
		{//若标志位为true，则将采用方式设置为MipMap系列
			//设置MAG纹理采样方式
			GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);   
			//设置MIN纹理采用方式
			GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_NEAREST);
		}
		else
		{//若标志位为false，则将采用方式设置为普通采样系列
			//设置MAG纹理采样方式
			GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);   
			//设置MIN纹理采用方式
			GLES30.glTexParameteri ( GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
		}
		//ST方向纹理拉伸方式
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_REPEAT);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_REPEAT);		
        
        //通过输入流加载图片
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
        
        //实际加载纹理
        GLUtils.texImage2D
        (
        		GLES30.GL_TEXTURE_2D,   //纹理类型
        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  //纹理图像
        		0					  //纹理边框尺寸
        );   
        //自动生成Mipmap纹理
        if(isMipmap)
        {
        	GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
        }
        //释放纹理图
        bitmapTmp.recycle();
        //返回纹理ID
        return textureId;
	}
}
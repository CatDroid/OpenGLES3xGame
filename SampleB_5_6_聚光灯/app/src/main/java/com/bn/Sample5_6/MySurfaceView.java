package com.bn.Sample5_6;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import android.opengl.GLSurfaceView;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
 
@SuppressLint("NewApi")
class MySurfaceView extends GLSurfaceView
{
    //场景渲染器
    private SceneRenderer mRenderer;

     // 摄像机位置相关
    float cx=0;
    float cy=30;
    float cz=60;
    float cAngle=0;
    final float cR=60;
    
    // 灯光位置 和 目标向量
	float lx=50;
	float ly=40;
	float lz=30;
	float disx=0;  // 光源的目标点，跟摄像机九参数类似
	float disy=0;
	float disz=0;

    //灯光投影Up向量
    float ux=-3;
    float uy=2;
    float uz=0;
	float lAngle=0;
	final float lR=1;

	
	float tx=0;
	float ty=0;
	float tz=0;
	int angle=0;  

    float[] mMVPMatrixGY;       // 光源总变换矩阵

	int move_x = 0;             // 人体模型移动标志位
	int move_z = 0;             // 人体模型移动标志位

    private float ratio;
    private int width = 0;      // 屏幕的宽度
    private int height= 0;      // 屏幕的高度

	public static FloatBuffer dis; 
	
	public MySurfaceView(Context context)
    {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }
	
	//触摸事件回调方法
    @SuppressLint("ClickableViewAccessibility") @Override 
    public boolean onTouchEvent(MotionEvent e) 
    {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                // 屏幕左边 控制 x方向 (聚光灯在x轴直线移动，在屏幕上看平面 就是上下移动)
                // 屏幕右边 控制 z方向 (聚光灯在z轴直线移动，在屏幕上看平面 就是左右移动)
                if(x <  width*0.5f && y < height*0.5f)
                {
                    move_x=1;
                }
                else if(x < width*0.5f && y > height*0.5f)
                {
                    move_x=-1;
                }
                else if(x > width*0.5f&& y < height*0.5f)
                {
                    move_z=1;
                }
                else if(x > width*0.5f && y > height*0.5f)
                {
                    move_z=-1;
                }
            }
            break;
            case MotionEvent.ACTION_UP:
            {
                move_x=0;
                move_z=0;
            }
            break;
        }
        return true;
    }

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {  
    	//从指定的obj文件中加载对象
		LoadeObjectVertexLand lovo_pm;//平面
		LoadedObjectVertexNormal lovo_ch;//人体模型	
		//纹理Id
		//int tyTexId;
        int roate=90;

        public void onDrawFrame(GL10 gl)
        {
//        	//产生灯光位置的投影、摄像机矩阵
//            MatrixState.setCamera(lx,ly,lz,0,0f,0f,ux,uy,uz);
//            MatrixState.setProjectFrustum(-0.5f, 0.5f, -0.5f, 0.5f, 0.14f, 400);  
//            mMVPMatrixGY=MatrixState.getViewProjMatrix();


            // 清除深度缓冲与颜色缓冲
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

            // 摄像机9参数位置矩阵
            MatrixState.setCamera(cx, cy, cz, 0f, 0f, 0f, 0f, 1f, 0f);
            MatrixState.setProjectFrustum(-ratio, ratio, -1.0f, 1.0f, 2, 1000);

            // 聚光灯的位置
            MatrixState.setLightLocation(lx, ly, lz);

            // 更新聚光灯方向向量(光源指向表面点) 这里只更新目标的x和z
            float light[] = new float[3];
            light[0] = disx - lx;
            light[1] = disy - ly;
            light[2] = disz - lz;
            android.util.Log.d("TOM", Arrays.toString(light));
            ByteBuffer llbb = ByteBuffer.allocateDirect(3 * 4);
            llbb.order(ByteOrder.nativeOrder());
            dis = llbb.asFloatBuffer();
            dis.put(light);
            dis.position(0);

            // 绘制人体模型 和 平面阴影
            MatrixState.pushMatrix();
            MatrixState.translate(tx, 0, tz);
            MatrixState.rotate(-roate, 0, 1, 0);
            MatrixState.scale(2f, 2f, 2f);
            lovo_ch.drawSelf(0);
            lovo_ch.drawSelf(1);
            MatrixState.popMatrix();

            //绘制最下面的平面
            MatrixState.pushMatrix();
            MatrixState.scale(1f, 1f, 1f);
            lovo_pm.drawSelf();
            MatrixState.popMatrix();


        }


        public void onSurfaceChanged(GL10 gl, int width, int height) 
        {
        	MySurfaceView.this.width = width;
            MySurfaceView.this.height=height;
            MySurfaceView.this.ratio = (float) width / height;

        	GLES30.glViewport(0, 0, width, height); 
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
            lovo_ch=LoadUtil.loadFromFileVertexOnly("ver.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            lovo_pm=LoadUtil.loadFromFileVertexLand("pm.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            Move_Thread mt=new Move_Thread();
            mt.start();

            
        }
    }

    public int initTexture(int drawableId)//textureId
    {
        int[] textures = new int[1];
        GLES30.glGenTextures
                (
                        1,
                        textures,
                        0
                );
        int textureId = textures[0];
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try {
            bitmapTmp = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D,
                0,
                bitmapTmp,
                0
        );
        bitmapTmp.recycle();
        return textureId;
    }

    class Move_Thread extends Thread
    {
    	public void run()
    	{
    		while(true)
    		{
    			if(move_x==1)
    			{
    				disx+=1;
    			}
    			else if(move_x==-1)
    			{
    				disx-=1;
    			}
    			else if(move_z==1)
    			{
    				disz-=1;
    			}
    			else if(move_z==-1)
    			{
    				disz+=1;
    			}
    			try {
					sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    	}
    }
}


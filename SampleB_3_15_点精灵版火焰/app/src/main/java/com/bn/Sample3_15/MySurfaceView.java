package com.bn.Sample3_15;


import static com.bn.Sample3_15.ParticleDataConstant.walls;
import static com.bn.Sample3_15.Sample2_12Activity.HEIGHT;
import static com.bn.Sample3_15.Sample2_12Activity.WIDTH;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;

class MySurfaceView extends GLSurfaceView 
{
     private SceneRenderer mRenderer;//场景渲染器    
     
    List<ParticleSystem> mParticleSystems = new ArrayList<ParticleSystem>();

     WallsForwDraw wallsForDraw;    
     LoadedObjectVertexNormalTexture brazier;
     
     static float direction=0;//视线方向
    static float sCameraX = 0;     // 摄像机x坐标
    static float sCameraY = 18;    // 摄像机z坐标
    static float sCameraZ = 20;    // 摄像机z坐标

    final static float LOOK_AT_X = 0;          // 观察目标点x坐标 hhl从高处望向地面
    final static float LOOK_AT_Y = 5;          // 观察目标点y坐标
    final static float LOOK_AT_Z = 0;          // 观察目标点z坐标

    static float sUpX = -sCameraX;
    static float sUpY = Math.abs(
            ( sCameraX * LOOK_AT_X
            + sCameraZ * LOOK_AT_Z
            - sCameraX * sCameraX
            - sCameraZ * sCameraZ) / (LOOK_AT_Y - sCameraY));
    static float sUpZ = -sCameraZ;

//    static float sUpX = 0;  // hhl 即使不做上面的运算，效果也差不多，不清楚上述运算原理??
//    static float sUpY = 1;
//    static float sUpZ = 0;
     static final float DEGREE_SPAN=(float)(3.0/180.0f*Math.PI);//摄像机每次转动的角度
     
     float Offset=20;
     float x;
     float y;  
     
     int textureIdFire;//系统火焰分配的纹理id
     int textureIdbrazier;//系统火盆分配的纹理id
    int mFireCount;
   	 
     boolean flag=true;//线程循环的标志位
     
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES 3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		x=event.getX();
		y=event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                flag = true;
                new Thread() {
                    @Override
                    public void run() {
                        while (flag) {
                            if (x > WIDTH / 4 && x < 3 * WIDTH / 4 && y > 0 && y < HEIGHT / 2) {            // 向前
                                if (Math.abs(Offset - 0.5f) > 25 || Math.abs(Offset - 0.5f) < 15) {
                                    return;
                                }
                                Offset = Offset - 0.5f;
                            } else if (x > WIDTH / 4 && x < 3 * WIDTH / 4 && y > HEIGHT / 2 && y < HEIGHT) {// 向后
                                if (Math.abs(Offset + 0.5f) > 25 || Math.abs(Offset + 0.5f) < 15) {
                                    return;
                                }
                                Offset = Offset + 0.5f;
                            } else if (x < WIDTH / 4) { // 顺时针旋转
                                direction = direction - DEGREE_SPAN;
                            } else if (x > WIDTH / 4) { // 逆时针旋转
                                direction = direction + DEGREE_SPAN;
                            }
                            try {
                                Thread.sleep(100);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
                break;
            case MotionEvent.ACTION_UP:
                flag = false;
                break;
        }

        // 设置新的观察目标点XZ坐标
        sCameraX = (float) (Math.sin(direction) * Offset);//观察目标点x坐标
        sCameraZ = (float) (Math.cos(direction) * Offset);//观察目标点z坐标

        // 重新计算Up向量
        sUpX = -sCameraX;                                                           // 观察目标点x坐标
        sUpY = Math.abs((sCameraX * LOOK_AT_X + sCameraZ * LOOK_AT_Z - sCameraX * sCameraX - sCameraZ * sCameraZ) / (LOOK_AT_Y - sCameraY)); // 观察目标点y坐标
        sUpZ = -sCameraZ;                                                           // 观察目标点z坐标

        for (int i = 0; i < mFireCount; i++) {
            mParticleSystems.get(i).calculateBillboardDirection();  //  计算粒子的朝向
        }
        Collections.sort(this.mParticleSystems);                    // 根据粒子与摄像机的距离进行排序
        MatrixState.setCamera(sCameraX, sCameraY, sCameraZ, LOOK_AT_X, LOOK_AT_Y, LOOK_AT_Z, sUpX, sUpY, sUpZ);  // 重新设置摄像机的位置

//        android.util.Log.i("TOM",String.format("%f %f %f , %f %f %f , %f %f %f",
//                sCameraX, sCameraY, sCameraZ, LOOK_AT_X, LOOK_AT_Y, LOOK_AT_Z, sUpX, sUpY, sUpZ ));
        return true;
	}

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {   
		
		int countt=0;//计算帧速率的时间间隔次数--计算器
		long timeStart=System.nanoTime();//开始时间
        public void onDrawFrame(GL10 gl) 
        { 
        	if(countt==19)//每十次一计算帧速率
        	{
        		long timeEnd=System.nanoTime();//结束时间
        		
        		//计算帧速率
        		float ps=(float)(1000000000.0/((timeEnd-timeStart)/20));
        		System.out.println("pss="+ps);
        		countt=0;//计算器置0
        		timeStart=timeEnd;//起始时间置为结束时间
        	}
        	countt=(countt+1)%20;//更新计数器的值
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            MatrixState.pushMatrix();
            //绘制墙体
            wallsForDraw.drawSelf();
            //MatrixState.translate(0, 2.5f, 0);
            for (int i = 0; i < mFireCount; i++) {
                MatrixState.pushMatrix();
                MatrixState.translate(ParticleDataConstant.positionBrazierXZ[i][0], 0.5f/*-2f*/, ParticleDataConstant.positionBrazierXZ[i][1]);
                //若加载的物体部位空则绘制物体
                if (brazier != null) {
                    brazier.drawSelf(textureIdbrazier);
                }
                MatrixState.popMatrix();
            }
            //MatrixState.translate(0, 0.65f, 0);
            MatrixState.translate(0, 3.15f, 0);
            for (int i = 0; i < mFireCount; i++) {
                MatrixState.pushMatrix();
                mParticleSystems.get(i).drawSelf(textureIdFire);
                MatrixState.popMatrix();
            }
            MatrixState.popMatrix();
        }  
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-0.3f*ratio, 0.3f*ratio, -1*0.3f, 1*0.3f, 1, 100);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(sCameraX, sCameraY, sCameraZ, LOOK_AT_X, LOOK_AT_Y, LOOK_AT_Z, sUpX, sUpY, sUpZ);
            //初始化变换矩阵
       	    MatrixState.setInitStack();
       	    //初始化光源位置   
            MatrixState.setLightLocation(0, 15, 0);
        }
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {


            mFireCount = ParticleDataConstant.START_COLOR.length;// 4组绘制着，4种颜色

            ParticleForDraw pDraw  = new ParticleForDraw(MySurfaceView.this);
            for (int i = 0; i < mFireCount; i++) {// 创建粒子发射器 每个粒子系统有自己的粒子起始位置 和 粒子半径
                mParticleSystems.add( new ParticleSystem(pDraw,i)); // 创建粒子系统对象/4个粒子发射器
            }

            // 墙壁(6面)渲染程序 和 各个面墙壁的纹理
            wallsForDraw = new WallsForwDraw(MySurfaceView.this);
            for (int i = 0; i < walls.length; i++) {
                ParticleDataConstant.walls[i] = initTexture(R.drawable.wall0 + i);
            }

            // 加载火炬盘 每次会绘制count次 但在不同的位置
            brazier = LoadUtil.loadFromFile("brazier.obj", MySurfaceView.this.getResources(), MySurfaceView.this);

            // 初始化纹理
            textureIdbrazier = initTexture(R.drawable.brazier); // 火炬盘的纹理图
            textureIdFire = initTexture(R.drawable.fire);     // 粒子火焰的纹理图
            //textureIdFire = initTexture(R.drawable.stars2);     // 可以换成其他形状的粒子 alpha通道是星星形状

            GLES30.glClearColor(0.6f, 0.3f, 0.0f, 1.0f);    // 设置屏幕背景色RGBA
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);          // 打开深度检测
            GLES30.glDisable(GLES30.GL_CULL_FACE);          // 关闭背面剪裁

        }
    }

    public int initTexture(int resId) {

        int[] textures = new int[1];
        GLES30.glGenTextures(1, textures, 0);
        int textureId = textures[0];
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

        InputStream is = this.getResources().openRawResource(resId);
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

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmapTmp, 0);
        bitmapTmp.recycle();
        return textureId;
	}
}

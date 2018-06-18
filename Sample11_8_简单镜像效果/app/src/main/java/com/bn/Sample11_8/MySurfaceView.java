package com.bn.Sample11_8;

import java.io.IOException;
import java.io.InputStream;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import static com.bn.Sample11_8.Constant.*;

class MySurfaceView extends GLSurfaceView 
{
    private SceneRenderer mRenderer;//场景渲染器    
    int textureFloor;//系统分配的不透明地板纹理id
    int textureFloorBTM;//系统分配的半透明地板纹理id
    int textureBallId;//系统分配的篮球纹理id
	 
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {   
    	TextureRect texRect;//表示地板的纹理矩形
    	BallTextureByVertex btbv;//用于绘制的纹理球
    	BallForControl bfd;//完成物理计算球控制类
    	
		public void onDrawFrame(GL10 gl) 
		{ 
			//清除深度缓冲与颜色缓冲
//             GLES30.glDepthMask(false);
			GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
//            GLES30.glDisable(GLES30.GL_DEPTH_TEST);
//            GLES30.glDepthMask(true); // 开深度写入 -- 关闭了深度检测，深度也不能写入

            // hhl: 由于关闭了深度检测，所以这里的渲染顺序是，不透明地板，篮球镜像(覆盖了地板)，半透明地板(与篮球镜像混合)，篮球本身

            MatrixState.pushMatrix();
			MatrixState.translate(0, -2 , 0); // hhl: -2 相当于整个场景(地板和篮球都向下移动了) 所以FLOOR_Y为0也是没有影响的

			MatrixState.pushMatrix();
			MatrixState.translate(0, FLOOR_Y, 0);
			texRect.drawSelf(textureFloor);     // 绘制反射面地板（不透明）
		    MatrixState.popMatrix();

                                                // hhl: 镜像体整体，不用做任何移动(但是会加上之前整个场景往下移的-2)
		    bfd.drawSelfMirror(textureBallId);  // 绘制镜像体

            // hhl: 这里为了有真实感，再画一次，半透明的地板，目的是盖上镜像的篮球
            GLES30.glEnable(GLES30.GL_BLEND);   // 开启混合
            GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);// 设置混合因子
		    MatrixState.pushMatrix();
			MatrixState.translate(0, FLOOR_Y, 0);
			texRect.drawSelf(textureFloorBTM); // 绘制半透明地板
		    MatrixState.popMatrix();
		    GLES30.glDisable(GLES30.GL_BLEND);// 关闭混合

                                              // hhl: 镜像体整体，不用做任何移动(但是会加上之前整个场景往下移的-2)
		    bfd.drawSelf(textureBallId);      // 绘制实际物体
		    MatrixState.popMatrix();


//            GLES30.glEnable(GLES30.GL_DEPTH_TEST );
//            GLES30.glDepthMask(true);
//            MatrixState.pushMatrix();
//            MatrixState.translate(0, -4 , 2 ); // z = -2 如果之前有深度写入的话，这里打开深度检测后，应该片元被丢弃，看不到
//            texRect.drawSelf(textureFloor);
//            MatrixState.popMatrix();
//            GLES30.glDisable(GLES30.GL_DEPTH_TEST);
//            GLES30.glDepthMask(false);      // 关闭深度写入，会导致glClear(GL_DEPTH_BUFFER_BIT)没有作用 但是clear COLOR有作用，所以该深度的颜色是黑色的


		}  

        public void onSurfaceChanged(GL10 gl, int width, int height) 
        {
        	GLES30.glViewport(0, 0, width, height); // 设置视窗大小及位置
            float ratio = (float) width / height;   // 计算GLSurfaceView的宽高比
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 20);// 调用此方法计算产生透视投影矩阵
            MatrixState.setCamera( 0.0f,7.0f,7.0f,   0,0f,0,  0,1,0);         // 调用此方法产生摄像机9参数位置矩阵
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            GLES30.glClearColor(0.0f,0.0f,0.0f,1.0f);

            texRect=new TextureRect(MySurfaceView.this,4,2.568f);  

            btbv=new BallTextureByVertex(MySurfaceView.this,BALL_SCALE);//创建用于绘制的篮球对象
            bfd=new BallForControl(btbv,3f); // 创建用于完成篮球'自由下落与反弹物理计算'的BallForControl类对象

            GLES30.glDisable(GLES30.GL_DEPTH_TEST); // 关闭深度检测 开启深度检测之后，只有距离摄像机最近处才会被绘制
            //GLES30.glDepthFunc(GLES30.GL_ALWAYS); // 深度测试函数
            //GLES30.glDepthMask(false);             // 深度缓冲只读 不能写入深度

            GLES30.glEnable(GLES30.GL_CULL_FACE);   // 打开背面剪裁
            //GLES30.glCullFace(GLES30.GL_FRONT);
            //GLES30.glFrontFace(GLES30.GL_CCW);

            textureFloor=initTexture(R.drawable.mdb);
            textureFloorBTM=initTexture(R.drawable.mdbtm);
            textureBallId=initTexture(R.drawable.basketball);            

            MatrixState.setInitStack();//初始化变换矩阵
        }
    }
	
	public int initTexture(int drawableId)//textureId
	{
		int[] textures = new int[1];
		GLES30.glGenTextures(1, textures, 0);
		int textureId=textures[0];    
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);

        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try {bitmapTmp = BitmapFactory.decodeStream(is);}
        finally {
            try {is.close();}
            catch(IOException e) {e.printStackTrace();}
        }
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmapTmp, 0);
        bitmapTmp.recycle();
        return textureId;
	}
}

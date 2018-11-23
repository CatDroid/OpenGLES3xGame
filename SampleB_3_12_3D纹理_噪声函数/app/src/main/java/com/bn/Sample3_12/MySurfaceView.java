package com.bn.Sample3_12;
import java.nio.ByteBuffer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLES30;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.bn.Sample3_12.tex3d.Load3DTexUtil;
import com.bn.Sample3_12.tex3d.Tex3D;

import android.annotation.SuppressLint;
import android.content.Context;

class MySurfaceView extends GLSurfaceView 
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;// 角度缩放比例
    private SceneRenderer mRenderer;// 场景渲染器
    
    private float mPreviousY;       // 上次的触控位置Y坐标
    private float mPreviousX;       // 上次的触控位置X坐标
    
    int textureId;                  // 3D纹理
	
	public MySurfaceView(Context context) { 
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES 3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    } 
	
	//触摸事件回调方法
	@SuppressLint("ClickableViewAccessibility")
	@Override 
    public boolean onTouchEvent(MotionEvent e) 
    {
        float y = e.getY(); 
        float x = e.getX();
        switch (e.getAction()) 
        {
          case MotionEvent.ACTION_MOVE:
            float dy = y - mPreviousY;//计算触控笔Y位移
            float dx = x - mPreviousX;//计算触控笔X位移
            mRenderer.yAngle += dx * TOUCH_SCALE_FACTOR;//设置沿x轴旋转角度
            mRenderer.xAngle+= dy * TOUCH_SCALE_FACTOR;//设置沿z轴旋转角度
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {  
		float yAngle;//绕Y轴旋转的角度
    	float xAngle; //绕Z轴旋转的角度
    	//从指定的obj文件中加载对象
		LoadedObjectVertexNormal lovo;
    	
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

            //坐标系推远
            MatrixState.pushMatrix();
            MatrixState.translate(0, -0.2f, -3f);   
            //绕Y轴、Z轴旋转
            MatrixState.rotate(yAngle, 0, 1, 0);
            MatrixState.rotate(xAngle, 1, 0, 0);
            
            //若加载的物体部位空则绘制物体
            if(lovo!=null)
            {
            	lovo.drawSelf(textureId);
            }   
            MatrixState.popMatrix();                  
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) 
        {
            //设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio*0.3f, ratio*0.3f, -1*0.3f, 1*0.3f, 2, 100);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0,0,0,0f,0f,-1f,0f,1.0f,0.0f);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) 
        {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(1.0f,1.0f,1.0f,1.0f);    
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


            // 纹理数据
            // HHL: 3dNoise.bn3dtex 是只有4个倍频 而且每个倍频的幅度都一样 都是0~255 可以作为RGBA GL_UNSIGNED_BYTE
            // HHL: 尺寸是 64*64*64
    		Tex3D tex3D=Load3DTexUtil.load(MySurfaceView.this.getResources(), "3dNoise.bn3dtex");
            textureId=init3DTexture(tex3D.data,tex3D.width,tex3D.height,tex3D.depth);
        }
    }
  	public int init3DTexture(byte[] texData,int width,int height,int depth)//textureId
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
		GLES30.glBindTexture(GLES30.GL_TEXTURE_3D, textureId);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_3D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_LINEAR);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_3D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_3D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_REPEAT);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_3D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_REPEAT);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_3D, GLES30.GL_TEXTURE_WRAP_R,GLES30.GL_REPEAT);
        
		
		System.out.println(texData.length+"texData.length");		
		ByteBuffer texels = ByteBuffer.allocateDirect(texData.length); 
		texels.put(texData);
		texels.position(0);//设置缓冲区起始位置

        // 使用不可变纹理glTexStorage2D和glTexSubImage2D的结合
        // GLES30.glTexStorage2D();  然后 glTexSubImage2D
        // GLES30.glTexStorage3D();  然后 glTexSubImage3D
		GLES30.glTexImage3D
		(
				GLES30.GL_TEXTURE_3D,  
				0, 
				GLES30.GL_RGBA8, 
				width, 
				height, 
				depth, // 带有深度
				0, 
				GLES30.GL_RGBA, 
				GLES30.GL_UNSIGNED_BYTE, 
	            texels
	    );
	    
        return textureId;
	}
}

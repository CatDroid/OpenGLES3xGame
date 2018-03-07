package com.bn.Sample7_8;
import java.nio.ByteBuffer;
import android.opengl.GLSurfaceView;
import android.opengl.GLES30;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.content.Context;

class MySurfaceView extends GLSurfaceView
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
    private SceneRenderer mRenderer;//场景渲染器    
    
    private float mPreviousY;//上次的触控位置Y坐标 
    private float mPreviousX;//上次的触控位置X坐标
    
    int textureId;//系统分配的纹理id
	
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
    	float xAngle; //绕X轴旋转的角度
    	//楼梯绘制对象引用
		Stairs lovo;
        public void onDrawFrame(GL10 gl)
        {
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            //坐标系推远
            MatrixState.pushMatrix();
            MatrixState.translate(0, -0.2f, -3f);   
            //绕Y轴、X轴旋转
            MatrixState.rotate(yAngle, 0, 1, 0);
            MatrixState.rotate(xAngle, 1, 0, 0);
            lovo.drawSelf(textureId);
            MatrixState.popMatrix();                  
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
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
            GLES30.glClearColor(0.0f,0.0f,0.0f,1.0f);    
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //打开背面剪裁   
            GLES30.glEnable(GLES30.GL_CULL_FACE);
            
            //初始化变换矩阵
            MatrixState.setInitStack();
            MatrixState.setLightLocation(0, 100, 200);
            //创建楼梯绘制对象
            lovo=new Stairs(MySurfaceView.this);
            //加载纹理
            //纹理数据
    		byte[] texData= // hhl 2*2*2 = 8 个纹素  可以看成立体的像素
    		{
    			//3d 1
    			80,80,80,(byte)255, (byte)255,(byte)255,(byte)255,(byte)255,
    			(byte)255,(byte)255,(byte)255,(byte)255, 80,80,80,(byte)255,
    			//3d 2
    			(byte)255,(byte)255,(byte)255,(byte)255, 80,80,80,(byte)255,
    			80,80,80,(byte)255, (byte)255,(byte) 255,(byte)255,(byte)255,
    		}; // Mark 这个案例相当于 把 一个 正方体 按颜色分成 2*2*2部分 ，然后中间切开(z轴) 然后水平切4份(y轴) 另外纹理拉伸是REPEAT所以看到楼梯最外面刚好是相反颜色
    		textureId=init3DTexture(texData,2,2,2);
        }
    }
  	public int init3DTexture(byte[] texData,int width,int height,int depth)//加载3D纹理的方法
	{
		//生成纹理ID
		int[] textures = new int[1];
		GLES30.glGenTextures
		(
				1,          //产生的纹理id的数量
				textures,   //纹理id的数组
				0           //偏移量
		);    
		int textureId=textures[0];//获得纹理id

		GLES30.glBindTexture(GLES30.GL_TEXTURE_3D, textureId);//绑定纹理

		//设置MIN采样方式  MAG采样方式
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_3D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_3D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_NEAREST);


		//S轴为重复拉伸方式  T轴为重复拉伸方式   R轴为重复拉伸方式
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_3D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);// GL_REPEAT  Mark  效果不一样
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_3D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_3D, GLES30.GL_TEXTURE_WRAP_R,GLES30.GL_CLAMP_TO_EDGE);


		//创建纹理数据缓冲
		ByteBuffer texels = ByteBuffer.allocateDirect(texData.length);
		texels.put(texData);//向缓冲区放入纹理数据
		texels.position(0);//设置缓冲区起始位置
		
		GLES30.glTexImage3D//实际加载3D纹理的方法
		(
				GLES30.GL_TEXTURE_3D,  //纹理类型
				0, //纹理层次
				GLES30.GL_RGBA8, //纹理内部格式
				width, //纹理的宽度
				height, //纹理的高度
				depth, //纹理的深度                Mark  3D纹理glTexImage3D需要指定width/height/depth 纹素!!!
				0, //纹理边框尺寸
				GLES30.GL_RGBA, //纹理的格式       Mark  GLES30 继承自 GLES20
				GLES30.GL_UNSIGNED_BYTE, //纹理数据的类型
	            texels//纹理数据的缓冲
	    );
	    /*
	    * Mark
	    *   空间三维区域的位置指定一组颜色，这些纹理称为体纹理图案。
	    *   '体纹理'通过三维纹理空间(s, t, r)来指定。
	    *
	    *   一个三维空间在单位立方体内定义，其纹理坐标范围为0到1.0
	    *
	    *
	    *
	    *
	    * */
        return textureId;//返回3D纹理id
	}
}


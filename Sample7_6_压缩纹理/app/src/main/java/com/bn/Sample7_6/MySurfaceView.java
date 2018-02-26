package com.bn.Sample7_6;
import java.io.IOException;
import java.io.InputStream;
import android.opengl.ETC1Util;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.opengl.GLES30;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.bn.Sample7_6.R;

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
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }
	
	//触摸事件回调方法
    @SuppressLint("ClickableViewAccessibility")
	@Override 
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dy = y - mPreviousY;//计算触控笔Y位移
            float dx = x - mPreviousX;//计算触控笔X位移
            mRenderer.texRect.yAngle += dx * TOUCH_SCALE_FACTOR;//设置纹理矩形绕y轴旋转角度
            mRenderer.texRect.zAngle+= dy * TOUCH_SCALE_FACTOR;//设置第纹理矩形绕z轴旋转角度
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {   
    	Triangle texRect;//创建纹理三角形引用
    	
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            //绘制纹理三角形
            texRect.drawSelf(textureId);             
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 10);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(0,0,3,0f,0f,0f,0f,1.0f,0.0f);
            
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0.5f,0.5f,0.5f, 1.0f);  
            
            //创建三角形对象
            texRect=new Triangle(MySurfaceView.this);        
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //初始化纹理
            initTexture();
            //关闭背面剪裁   
            GLES30.glDisable(GLES30.GL_CULL_FACE);
        }
    }
	
	public void initTexture() //textureId
	{
		//生成纹理ID
		int[] textures = new int[1];
		GLES30.glGenTextures
		(
				1,          //产生的纹理id的数量
				textures,   //纹理id的数组
				0           //偏移量
		);    
		textureId=textures[0];    
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
        
     
        //通过输入流加载图片===============begin===================
        InputStream is = this.getResources().openRawResource(R.raw.s512x512_32bit);

        /*
        *  Mark.1 压缩纹理 ETC1格式是ES2.0标准 不支持alpha通道 ; ETC2压缩纹理格式 ES3.0标准 支持alpha通道
        *                 pkm文件有 ETC_PKM_HEADER_SIZE = 16 头部长度 包含了图片的宽高信息
        *                 见ETC1Util.java 和 ETC1.java , 内部用ETC1Texture表示 读取了16个字节头部 剩下的作为data
        *
        *
        *  Mark.2 压缩纹理 不用像bmp jpg那样要在CPU解码，再上传到GPU
        *
        *  Mark.3 压缩纹理 java层接口 ETC1Util.loadTexture
        *                 如果不支持 ETC1  那么内部会调用 ETC1.decodeImage 来解压 然后用 glTexImage2D
        *                 如果支持  就用OpenGL API glCompressedTexImage2D 来上传到CPU
        *                 compressTexture 支持把Buffer的rgb图片(e.g ByteBuffer)压缩成ETC1格式,并用ETC1Texture
        *                 writeTexture 支持把ETC1Texture打包到OutputStream(e.g 文件)
        *                 createTexture 支持从InputStream读取ETC1文件
        *
        *  Mark.4 sdk\platform-tools 下 etc1tool.exe 可以压缩编码成pkm文件 或者 解码成png图片
        *                etc1tool.exe xxx.png --encode
        *                etc1tool.exe xxx.pkm --decode
        *
        * */
        try
        {  
        	 ETC1Util.loadTexture//将纹理数据加载进纹理缓冲
        	 (
        			 GLES30.GL_TEXTURE_2D, //纹理类型
        			 0, //纹理层次
        			 0,//纹理边框尺寸
                     GLES30.GL_RGB,//色彩通道格式
                     GLES30.GL_UNSIGNED_BYTE, //每像素数据数
                     is//压缩纹理数据输入流
             );
        } 
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        finally 
        {
            try {is.close();} catch(IOException e){e.printStackTrace();}
        }
	}
}

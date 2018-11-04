package com.bn.Sample2_6;
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
    
    int textureId;//系统分配的纹理id

    // hhl 配置实用那个模型 false : zd.obj ; true : head.obj
    static public final boolean CONFIG_USING_HEAD_INSTEAD_OF_BOOM = false ;
	
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
            mRenderer.yAngle += dx * TOUCH_SCALE_FACTOR;//设置沿x轴旋转角度
            mRenderer.xAngle+= dy * TOUCH_SCALE_FACTOR;//设置沿z轴旋转角度
            requestRender();//重绘画面
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
		LoadedObjectVertexNormalTexture lovo;
    	
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

            MatrixState.pushMatrix();//保护现场
            MatrixState.translate(0, 0f, -15f);//执行平移
            if (CONFIG_USING_HEAD_INSTEAD_OF_BOOM){
                MatrixState.scale(35f, 35f, 35f);//执行缩放
            }else{
                MatrixState.scale(0.1f, 0.1f, 0.1f);//执行缩放
            }
            MatrixState.rotate(yAngle, 0, 1, 0);//绕y轴旋转yAngle度
            MatrixState.rotate(xAngle, 1, 0, 0);//绕x轴旋转xAngle度
            
            //若加载的物体部位空则绘制物体
            if(lovo!=null)
            {
            	lovo.drawSelf(textureId);
            }   
            MatrixState.popMatrix(); //恢复现场                 
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
        	GLES30.glViewport(0, 0, width, height);                     // 设置视口的大小及位置
            float ratio = (float) width / height;                       // 计算视口的宽高比
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);// 透视投影矩阵
            MatrixState.setCamera(0,0,0,0f,0f,-1f,0f,1.0f,0.0f);        // 摄像机矩阵
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) 
        {
            GLES30.glClearColor(1.0f,1.0f,1.0f,1.0f);   // 设置屏幕背景色RGBA
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);      // 打开深度检测
            GLES30.glEnable(GLES30.GL_CULL_FACE);       // 打开背面剪裁
            MatrixState.setInitStack();                 // 初始化变换矩阵
            MatrixState.setLightLocation(40, 10, 20);   // 初始化光源位置

            if (CONFIG_USING_HEAD_INSTEAD_OF_BOOM){
                lovo=LoadUtil.loadFromFile("head.obj",      // 加载要绘制的物体
                        MySurfaceView.this.getResources(),
                        MySurfaceView.this);
                textureId=initTexture(R.drawable.head);     // 加载纹理
            }else{
                lovo=LoadUtil.loadFromFile("zd.obj",      // 加载要绘制的物体
                        MySurfaceView.this.getResources(),
                        MySurfaceView.this);
                textureId=initTexture(R.drawable.zd_bg);     // 加载纹理
            }

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
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_REPEAT);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_REPEAT);

        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try {
        	bitmapTmp = BitmapFactory.decodeStream(is);
        } finally {
            try {is.close();} catch(IOException e) {e.printStackTrace();}
        }

	   	GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0,
                GLUtils.getInternalFormat(bitmapTmp),
                bitmapTmp,
                GLUtils.getType(bitmapTmp),
	     		0
	     );
	    bitmapTmp.recycle();
        return textureId;
	}
}

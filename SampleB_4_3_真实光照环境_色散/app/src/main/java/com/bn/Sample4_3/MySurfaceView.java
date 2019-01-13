package com.bn.Sample4_3;
import java.io.IOException;
import java.io.InputStream;
import android.opengl.GLSurfaceView;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import static com.bn.Sample4_3.Constant.*;

@TargetApi(Build.VERSION_CODES.CUPCAKE)
class MySurfaceView extends GLSurfaceView 
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
    private SceneRenderer mRenderer;//场景渲染器    
    
    private float mPreviousY;//上次的触控位置Y坐标
    private float mPreviousX;//上次的触控位置X坐标
    
    //摄像机的位置角度
    final float RCamera=90;//ball-90 ch-40
    float cx=0;
    float cy=2;
    float cz=RCamera;
    float cAngle=0;    

    int textureIdCM;//系统分配的Cube Map纹理
    int[] textureIdA=new int[6];//天空盒六面的纹理
	
	@SuppressLint("NewApi")
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
            cAngle+=dx * TOUCH_SCALE_FACTOR;
            cx=(float) (Math.sin(Math.toRadians(cAngle))*RCamera);
            cz=(float) (Math.cos(Math.toRadians(cAngle))*RCamera);
            cy+=dy/10.0f;
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(cx,cy,cz,0f,0f,0f,0f,1.0f,0.0f);
            //设置灯光位置
            MatrixState.setLightLocation(cx, cy+5, cz);
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }

	@SuppressLint("NewApi")
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {  
		float yAngle;//绕Y轴旋转的角度
    	float zAngle; //绕Z轴旋转的角度
    	//从指定的obj文件中加载对象
		LoadedObjectVertexNormalTexture lovo;
		TextureRect texRect;//纹理矩形
    	
		long olds;
    	long currs;
		
        public void onDrawFrame(GL10 gl) 
        { 
        	
        	currs=System.nanoTime();
			System.out.println(1000000000.0/(currs-olds)+"FPS");  
			olds=currs;
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

            MatrixState.copyMVMatrix();
            
            //坐标系推远
            MatrixState.pushMatrix();
            //绕Y轴、Z轴旋转
            MatrixState.rotate(yAngle, 0, 1, 0);
            MatrixState.rotate(zAngle, 1, 0, 0);
            
            //若加载的物体不为空则绘制物体
            if(lovo!=null)
            {
            	lovo.drawSelf(textureIdCM);
            }   
            MatrixState.popMatrix();   
            
            //天空盒六面调整值
            final float tzz=0.4f;
            
            //绘制后墙
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, -UNIT_SIZE+tzz);
            texRect.drawSelf(textureIdA[0]);
            MatrixState.popMatrix();  
            
            //绘制前墙
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, UNIT_SIZE-tzz);
            MatrixState.rotate(180, 0, 1, 0);
            texRect.drawSelf(textureIdA[5]);
            MatrixState.popMatrix();  
            
            //绘制左墙
            MatrixState.pushMatrix();
            MatrixState.translate(-UNIT_SIZE+tzz, 0, 0);
            MatrixState.rotate(90, 0, 1, 0);
            texRect.drawSelf(textureIdA[1]);
            MatrixState.popMatrix(); 
            
            //绘制右墙
            MatrixState.pushMatrix();
            MatrixState.translate(UNIT_SIZE-tzz, 0, 0);
            MatrixState.rotate(-90, 0, 1, 0);
            texRect.drawSelf(textureIdA[2]);
            MatrixState.popMatrix(); 
            
            //绘制下墙
            MatrixState.pushMatrix();
            MatrixState.translate(0, -UNIT_SIZE+tzz, 0);
            MatrixState.rotate(-90, 1, 0, 0);
            texRect.drawSelf(textureIdA[3]);
            MatrixState.popMatrix(); 
            
            //绘制上墙
            MatrixState.pushMatrix();
            MatrixState.translate(0, UNIT_SIZE-tzz, 0);
            MatrixState.rotate(90, 1, 0, 0);
            texRect.drawSelf(textureIdA[4]);
            MatrixState.popMatrix(); 
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 1000);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(cx,cy,cz,0f,0f,0f,0f,1.0f,0.0f);
            MatrixState.setLightLocation(cx, cy+5, cz);
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
            //初始化光源位置     
            MatrixState.setLightLocation(40, 10, 20);
            //加载要绘制的物体
            lovo=LoadUtil.loadFromFileVertexOnly("ball.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            //创建纹理矩形对对象 
            texRect=new TextureRect(MySurfaceView.this);   
            //加载纹理
            int[] cubeMapResourceIds = new int[]
            {
                    R.raw.skycubemap_right, R.raw.skycubemap_left, R.raw.skycubemap_up_cube,
                    R.raw.skycubemap_down_cube, R.raw.skycubemap_front, R.raw.skycubemap_back
            };
            textureIdCM=generateCubeMap(cubeMapResourceIds); 
                
            textureIdA[0]=initTexture(R.raw.skycubemap_back);
            textureIdA[1]=initTexture(R.raw.skycubemap_left);  
            textureIdA[2]=initTexture(R.raw.skycubemap_right);
            textureIdA[3]=initTexture(R.raw.skycubemap_down);
            textureIdA[4]=initTexture(R.raw.skycubemap_up);  
            textureIdA[5]=initTexture(R.raw.skycubemap_front);  
        }  
    }
  	@SuppressLint("NewApi")
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
        
        //实际加载纹理
        GLUtils.texImage2D
        (
        		GLES30.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  //纹理图像
        		0					  //纹理边框尺寸
        );
        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
        return textureId;
	}
  	
  	//加载立方图纹理
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public int generateCubeMap(int[] resourceIds) 
    {
        int[] ids = new int[1];
        GLES30.glGenTextures(1, ids, 0);
        int cubeMapTextureId = ids[0];
        
        GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP, cubeMapTextureId);     
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_CUBE_MAP,GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_CUBE_MAP,GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_REPEAT);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_REPEAT);

        for (int face = 0; face < 6; face++) 
        {
            InputStream is = getResources().openRawResource(resourceIds[face]);
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(is);
            } finally {
                try {
                    is.close();
                } catch(IOException e) {
                    Log.e("CubeMap", "Could not decode texture for face " + Integer.toString(face));
                }
            }
            GLUtils.texImage2D(GLES30.GL_TEXTURE_CUBE_MAP_POSITIVE_X + face, 0,bitmap, 0);
            bitmap.recycle();
        }
        return cubeMapTextureId;
    }
}

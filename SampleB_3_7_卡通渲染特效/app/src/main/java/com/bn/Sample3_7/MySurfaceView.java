package com.bn.Sample3_7;
import java.io.IOException;
import java.io.InputStream;
import android.opengl.GLSurfaceView;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

class MySurfaceView extends GLSurfaceView 
{
    private SceneRenderer mRenderer;//场景渲染器  	
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {
		//二维数组
		Object pmBase[]=new Object[2];
		ObjectToon pmJJ[]=new ObjectToon[2];
		int ObjectTexId[]=new int[2];//纹理图片id
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);            
            MatrixState.pushMatrix(); 
            MatrixState.translate(-6, 4.5f, 0); 
            pmBase[0].drawSelf(ObjectTexId[0]);//原物体水果
            MatrixState.popMatrix();    
            
            MatrixState.pushMatrix();  
            MatrixState.translate(6,4.5f, 0); 
            pmJJ[0].drawSelf(ObjectTexId[0]);//进行手绘的物体水果
            MatrixState.popMatrix(); 
            
            
            MatrixState.pushMatrix();  
            MatrixState.translate(-6,-5f, 0); 
            pmBase[1].drawSelf(ObjectTexId[1]);//原物体花
            MatrixState.popMatrix();
            
            MatrixState.pushMatrix();  
            MatrixState.translate(6,-5f, 0); 
            pmJJ[1].drawSelf(ObjectTexId[1]);//进行手绘的物体花
            MatrixState.popMatrix();
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //设置camera位置
            MatrixState.setCamera
            (
            		0,	//人眼位置的X
            		0, //人眼位置的Y
            		20, //人眼位置的Z
            		0, 	//人眼球看的点X
            		0,  //人眼球看的点Y
            		0,  //人眼球看的点Z
            		0, 	//up向量
            		1, 
            		0
            );
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);
        }
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
        	GLES30.glClearColor(1.0f,1.0f,1.0f,1.0f);    
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //打开背面剪裁   
            GLES30.glEnable(GLES30.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();  
            for(int i=0;i<2;i++)
            {
            	pmBase[i] = new Object(MySurfaceView.this,9, 9, 1, 1);
            	pmJJ[i] = new ObjectToon(MySurfaceView.this,9, 9, 1, 1);
            }
    		ObjectTexId[0]=initTexture(R.drawable.object1);//水果
    		ObjectTexId[1]=initTexture(R.drawable.object);//花
        }
    }

	//初始化纹理
	public int initTexture(int drawableId)
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
		//非Mipmap纹理采样过滤参数	
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
		
		//ST方向纹理拉伸方式
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
        //实际加载纹理,换成这个方法后，如果图片格式有问题，会抛出图片格式异常，不再会误显示其他异常
	   	GLUtils.texImage2D
	    (
	    		GLES30.GL_TEXTURE_2D, //纹理类型
	     		0, 
	     		GLUtils.getInternalFormat(bitmapTmp), 
	     		bitmapTmp, //纹理图像
	     		GLUtils.getType(bitmapTmp), 
	     		0 //纹理边框尺寸
	     );   
        //自动生成Mipmap纹理
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
        //释放纹理图
        bitmapTmp.recycle();
        //返回纹理ID
        return textureId;
	}	
}

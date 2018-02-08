package com.bn.Sample5_10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

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
    	Circle circle;//圆对象引用
    	
    	public void onDrawFrame(GL10 gl) 
    	{
    		GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT	//清除深度缓冲与颜色缓冲
    				| GLES30.GL_COLOR_BUFFER_BIT);
    		MatrixState.pushMatrix();							//保护现场
    		//绘制圆
    		MatrixState.pushMatrix();							//保护现场
    		MatrixState.translate(-1.2f, 0, 0);					//沿x负方向平移
    		//circle.drawSelf(0,24);							//绘制圆
            circle.drawSelf(0,30); // 10 * 3  分成10个三角形
    		MatrixState.popMatrix();							//恢复现场
    		//绘制半个圆
    		MatrixState.pushMatrix();							//保护现场
    		MatrixState.translate(1.2f, 0, 0);					//沿x正方向平移
    		//circle.drawSelf(6,12);							//绘制半个圆
            //circle.drawSelf(6,12); // 从第i个三角形开始  2*3  i*3  i from 0 ~
            circle.drawSelf(6,15);   // 分成10个三角形 就是每个36度  
            MatrixState.popMatrix();							//恢复现场
    		MatrixState.popMatrix();							//恢复现场
    	}  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视口的大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算视口的宽高比
            Constant.ratio = (float) width / height;
			// 调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-Constant.ratio, Constant.ratio, -1, 1, 20, 100);
			// 调用此方法产生摄像机矩阵
			MatrixState.setCamera(0, 8f, 30, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            
            //初始化变换矩阵
            MatrixState.setInitStack();
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0.5f,0.5f,0.5f, 1.0f);  
            //创建圆对象
            circle=new Circle(MySurfaceView.this);
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //打开背面剪裁   
            GLES30.glEnable(GLES30.GL_CULL_FACE);
        }
    }
}

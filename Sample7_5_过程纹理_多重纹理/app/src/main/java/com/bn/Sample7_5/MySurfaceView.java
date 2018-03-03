package com.bn.Sample7_5;
import java.io.IOException;
import java.io.InputStream;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.opengl.GLES30;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.bn.Sample7_5.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import static com.bn.Sample7_5.Constant.*;

@SuppressLint("ClickableViewAccessibility")
class MySurfaceView extends GLSurfaceView 
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
    private SceneRenderer mRenderer;//场景渲染器
    
    private float mPreviousX;//上次的触控位置X坐标
    private float mPreviousY;//上次的触控位置Y坐标
    
    int textureIdEarth;//系统分配的地球纹理id
    int textureIdEarthNight;//系统分配的地球夜晚纹理id
    int textureIdMoon;//系统分配的月球纹理id    

    float yAngle=0;//太阳灯光绕y轴旋转的角度
    float xAngle=0;//摄像机绕X轴旋转的角度
    
    float eAngle=0;//地球自转角度    
    float cAngle=0;//天球自转的角度
	
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0  支持的手机上会有异常 java.lang.IllegalArgumentException: eglChooseConfig failed
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }
    // Mark.1 this.setEGLContextClientVersion(3); 如果在不支持3.0的手机上运行 会出现异常 eglChooseConfig failed
	
	//触摸事件回调方法
    @Override 
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:

        	//  触控横向位移  太阳绕y轴旋转
            //  触控纵向位移  摄像机绕x轴旋转 -90～+90

//            float dx = x - mPreviousX;//计算触控笔X位移
//            yAngle += dx * TOUCH_SCALE_FACTOR;//将X位移折算成角度          // hhl 由于平面分辨率超过320 所以太阳会转到跟摄像头的对面
//            float sunx=(float)(Math.cos(Math.toRadians(yAngle))*100);
//            float sunz=-(float)(Math.sin(Math.toRadians(yAngle))*100);
//            MatrixState.setLightLocationSun(sunx,5,sunz);               // hhl 太阳总是在y=5平面上的 半径为100的 圆圈上

            //Log.d("TOM"," y = " + y + " x " + x ); // Mark.2 触摸屏坐标 左上角为为原点
                                                     // Mark.3 需求效果是:垂直/y方向往下拖,结果是从更上方看地球

            float dy = y - mPreviousY;          // 计算触控笔Y位移
            xAngle += dy * TOUCH_SCALE_FACTOR;  // 将Y位移折算成绕X轴旋转的角度
                                                                        // hhl 假设x/y的范围在0~320 映射成-90~90度 也就是屏幕上到下180度
            if(xAngle>90) {                     // Mark.4 触摸屏y方向移动 代表GL中绕x轴旋转
            	xAngle=90;
            }else if(xAngle<-90) {
            	xAngle=-90;                                             // hhl 望向y轴负方向为 右手螺旋为正 这样做跟屏幕 上面为正 下面为负对应
            }
            float cy=(float) (7.2*Math.sin(Math.toRadians(xAngle)));    // hhl 半径保证在7.2f
            float cz=(float) (7.2*Math.cos(Math.toRadians(xAngle)));
            float upy=(float) Math.cos(Math.toRadians(xAngle));
            float upz=-(float) Math.sin(Math.toRadians(xAngle));        // hhl 摄像头y轴正方向向量(单位向量) 垂直于摄像头位置到世界坐标系原点
            MatrixState.setCamera(  0, cy, cz,      // Mark.5 摄像头位置始终在世界坐标系的 YoZ平面的半径7.2f范围
                                    0, 0, 0,
                                    0, upy, upz);   // Mark.6 整个场景的变化通过更新摄像头 九参数摄像头坐标
        }
        mPreviousX = x;//记录触控笔位置
        mPreviousY = y;
        return true; 
    } 

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {   
    	Earth earth;//地球
    	Moon moon;//月球
    	Celestial cSmall;//小星星天球
    	Celestial cBig;//大星星天球
    	
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);   
            

            MatrixState.pushMatrix();//保护现场
            MatrixState.rotate(eAngle, 0, 1, 0);//地球自转
            earth.drawSelf(textureIdEarth,textureIdEarthNight);  //绘制地球
            MatrixState.translate(2f, 0, 0); //推坐标系到月球位置   // Mark.7 月球多了移位! 先自转 再移位
            MatrixState.rotate(eAngle, 0, 1, 0);//月球自转
            moon.drawSelf(textureIdMoon);//绘制月球
            MatrixState.popMatrix();//恢复现场

            MatrixState.pushMatrix(); //保护现场
            MatrixState.rotate(cAngle, 0, 1, 0);//星空天球旋转
            cSmall.drawSelf();//绘制小尺寸星星的天球
            cBig.drawSelf();//绘制大尺寸星星的天球

            MatrixState.popMatrix();//恢复现场
        }   

        public void onSurfaceChanged(GL10 gl, int width, int height) {

        	GLES30.glViewport(0, 0, width, height);         // 设置视窗大小及位置
            ratio= (float) width / height;//计算GLSurfaceView的宽高比
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1,   4f, 100);//调用此方法计算产生透视投影矩阵
            MatrixState.setCamera(  0,0,7.2f,       /*hhl:位于天球半径10.0f以内*/
                                    0f,0f,0f,
                                    0f,1.0f,0.0f);          // 调用此方法产生摄像机9参数位置矩阵

            GLES30.glEnable(GLES30.GL_CULL_FACE);           // 打开背面剪裁
            textureIdEarth=initTexture(R.drawable.earth);   // 初始化纹理
            textureIdEarthNight=initTexture(R.drawable.earthn);
            textureIdMoon=initTexture(R.drawable.moon);
            MatrixState.setLightLocationSun(100,5,0);       // 设置太阳灯光的初始位置
            

            new Thread()
            {
            	public void run()
            	{//启动一个线程定时旋转地球、月球
            		while(threadFlag)
            		{

            			eAngle=(eAngle+2)%360;//地球自转角度
            			cAngle=(cAngle+0.2f)%360;//天球自转角度
            			try {
							Thread.sleep(100);
						} catch (InterruptedException e) {				  			
							e.printStackTrace();
						}
            		}
            	}
            }.start();            
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0.0f,0.0f,0.0f, 1.0f);  
            //创建地球对象 
            earth=new Earth(MySurfaceView.this,2.0f);
            //创建月球对象 
            moon=new Moon(MySurfaceView.this,1.0f);

            //创建小星星天球对象  pointSize = 1
            cSmall=new Celestial(1,   0,1000,MySurfaceView.this);
            //创建大星星天球对象  pointSize = 2  两个Celestial分别产生的大小不一样的点
            cBig=new Celestial(2,     0,500,MySurfaceView.this);
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //初始化变换矩阵
            MatrixState.setInitStack();  
        }
    }
	
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
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
        

        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try 
        {
        	bitmapTmp = BitmapFactory.decodeStream(is);
        }finally {
            try{
                is.close();
            }catch(IOException e) {
                e.printStackTrace();
            }
        }

        GLUtils.texImage2D(
        		GLES30.GL_TEXTURE_2D,   //纹理类型
        		0, 				  	  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  //纹理图像
        		0					  //纹理边框尺寸
        );
        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
        
        return textureId;
	}
}


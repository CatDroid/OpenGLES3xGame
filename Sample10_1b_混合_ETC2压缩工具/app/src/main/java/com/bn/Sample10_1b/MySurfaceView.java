package com.bn.Sample10_1b;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.annotation.SuppressLint;
import android.content.Context;

class MySurfaceView extends GLSurfaceView 
{
    private SceneRenderer mRenderer;//场景渲染器  
	
	//矩形的位置
	static float rectX;
	static float rectY;
	static int rectState = KeyThread.Stop;
	static final float moveSpan = 0.1f;
	private KeyThread keyThread;
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
    public boolean onTouchEvent(MotionEvent e) 
    {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_DOWN:
        	if(x<Constant.SCREEN_WIDTH/3.0f) {//按下屏幕左面1/3向左移
        		rectState = KeyThread.left;
        	}
        	else if(x>Constant.SCREEN_WIDTH*2/3.0f){//按下屏幕右面2/3向右移
        		rectState = KeyThread.right;
        	}
        	else {
            	if(y<Constant.SCREEN_HEIGHT/2.0f) {   //按下屏幕上方向上移     		
            		rectState = KeyThread.up;
            	}
            	else {//按下屏幕下方向下移 
            		rectState = KeyThread.down;
            	}
        	}
        	break;
        case MotionEvent.ACTION_UP://抬起时停止移动
        	rectState = KeyThread.Stop;
        	break;
        }
        return true;
    }
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {
		int rectTexId;//纹理id
    	//从指定的obj文件中加载对象
		LoadedObjectVertexNormalFace pm;
		LoadedObjectVertexNormalFace cft;
		LoadedObjectVertexNormalAverage qt;
		LoadedObjectVertexNormalAverage yh;
		LoadedObjectVertexNormalAverage ch;
		TextureRect rect;
        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
           
            MatrixState.pushMatrix();
            MatrixState.pushMatrix();
            MatrixState.rotate(25, 1, 0, 0);       
            //若加载的物体部位空则绘制物体            
            pm.drawSelf();//平面
            
            //缩放物体
            MatrixState.pushMatrix();
            MatrixState.scale(1.5f, 1.5f, 1.5f);          
            //绘制物体 
            //绘制长方体
            MatrixState.pushMatrix();
            MatrixState.translate(-10f, 0f, 0);
            cft.drawSelf();
            MatrixState.popMatrix();   
            //绘制球体
            MatrixState.pushMatrix();
            MatrixState.translate(10f, 0f, 0);
            qt.drawSelf();
            MatrixState.popMatrix();  
            //绘制圆环
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, -10f);
            yh.drawSelf();
            MatrixState.popMatrix();  
            //绘制茶壶
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, 10f);
            ch.drawSelf();
            MatrixState.popMatrix();
            MatrixState.popMatrix(); 
            MatrixState.popMatrix(); 
              
            //开启混合
            GLES30.glEnable(GLES30.GL_BLEND);  
            //设置混合因子c
            GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA); 
            //绘制纹理矩形
            MatrixState.pushMatrix();
            MatrixState.translate(rectX, rectY, 25f);
            rect.drawSelf(rectTexId);
            MatrixState.popMatrix();
            //关闭混合
            GLES30.glDisable(GLES30.GL_BLEND);
            
            MatrixState.popMatrix();                  
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) 
        {
        	Constant.SCREEN_HEIGHT=height;
        	Constant.SCREEN_WIDTH=width;
        	
            //设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);
            //设置camera位置
            MatrixState.setCamera
            (
            		0,   //人眼位置的X
            		0, 	//人眼位置的Y
            		50,   //人眼位置的Z
            		0, 	//人眼球看的点X
            		0,   //人眼球看的点Y
            		0,   //人眼球看的点Z
            		0, 	//up位置
            		1, 
            		0
            );
            //初始化光源位置
            MatrixState.setLightLocation(100, 100, 100);
            keyThread = new KeyThread(MySurfaceView.this);
            keyThread.start();
        }
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0.3f,0.3f,0.3f,1.0f);    
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //打开背面剪裁   
            GLES30.glEnable(GLES30.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();         
            //纹理id
            try {
                rectTexId=BnETC2Util.initTextureETC2("pkm/lgq.pkm",MySurfaceView.this.getResources());
            } catch (Exception e) {
                e.printStackTrace();
                return ;
            }
            //加载要绘制的物体
            ch=LoadUtil.loadFromFileVertexOnlyAverage("obj/ch.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            Log.d("TOM","ch");
            pm=LoadUtil.loadFromFileVertexOnlyFace("obj/pm.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            Log.d("TOM","pm");
    		cft=LoadUtil.loadFromFileVertexOnlyFace("obj/cft.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            Log.d("TOM","cft");
    		qt=LoadUtil.loadFromFileVertexOnlyAverage("obj/qt.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            Log.d("TOM","qt");
    		yh=LoadUtil.loadFromFileVertexOnlyAverage("obj/yh.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            Log.d("TOM","yh");
    		rect = new TextureRect(MySurfaceView.this, 10, 10);
        }  
    }
	
	@Override
    public void onResume() 
	{
    	super.onResume();
    	KeyThread.flag = true;
    	keyThread = new KeyThread(MySurfaceView.this);
        keyThread.start();
    }
	@Override
	public void onPause() 
	{
		super.onPause();
		KeyThread.flag = false;
	}
}

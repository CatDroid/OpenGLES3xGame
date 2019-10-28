package com.bn.Sample5_2;
import android.opengl.GLSurfaceView;
import android.opengl.GLES30;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.annotation.SuppressLint;
import android.content.Context;

@SuppressLint("NewApi") class MySurfaceView extends GLSurfaceView 
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//角度缩放比例
    private SceneRenderer mRenderer;//场景渲染器    
    
    private float mPreviousY;//上次的触控位置Y坐标
    private float mPreviousX;//上次的触控位置X坐标

	// 关于摄像机的变量
	float cx=0;     // 摄像机x位置
	float cy=0;     // 摄像机y位置
	float cz=60;    // 摄像机z位置
	
	float tx=0;     // 目标点x位置
	float ty=0;     // 目标点y位置
	float tz=0;     // 目标点z位置


	private float currSightDis=60;//摄像机和目标的距离
    private float angdegElevation = 30;//仰角
    private float angdegAzimuth=180;//方位角
	
	// 关于灯光的变量
	float lx=0;//x位置
	float ly=0;//y位置
	float lz=0;//z位置
	float lightDis=100;
	float lightElevation=40;//灯光仰角
	public float lightAzimuth=180;//灯光的方位角	
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); // 设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	// 创建场景渲染器
        setRenderer(mRenderer);				// 设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);// 设置渲染模式为主动渲染
    }


	//触摸事件回调方法
    @SuppressLint("ClickableViewAccessibility") @Override 
    public boolean onTouchEvent(MotionEvent e) 
    {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dy = y - mPreviousY;//计算触控笔Y位移
            float dx = x - mPreviousX;//计算触控笔X位移
            angdegAzimuth += dx * TOUCH_SCALE_FACTOR;//设置沿x轴旋转角度
            angdegElevation += dy * TOUCH_SCALE_FACTOR;//设置沿z轴旋转角度
            //将仰角限制在5～90度范围内
            angdegElevation = Math.max(angdegElevation, 5);
            angdegElevation = Math.min(angdegElevation, 90);
            //设置摄像机的位置
            setCameraPostion();
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }


    // 设置摄像机位置的方法
	public void setCameraPostion() {

	    // 相对于目标点(tx,ty,tz)的半径为 currSigntDis 的球体

		double angradElevation = Math.toRadians(angdegElevation);   // 仰角（弧度）
		double angradAzimuth = Math.toRadians(angdegAzimuth);       // 方位角

		cx = (float) (tx - currSightDis * Math.cos(angradElevation)	* Math.sin(angradAzimuth));
		cy = (float) (ty + currSightDis * Math.sin(angradElevation));
		cz = (float) (tz - currSightDis * Math.cos(angradElevation) * Math.cos(angradAzimuth));
	}


	// 位置灯光位置的方法
	public void setLightPostion() {

		double angradElevation = Math.toRadians(lightElevation);    // 仰角（弧度）
		double angradAzimuth = Math.toRadians(lightAzimuth);        // 方位角

		lx = (float) (- lightDis * Math.cos(angradElevation) * Math.sin(angradAzimuth));
		ly = (float) (+ lightDis * Math.sin(angradElevation));
		lz = (float) (- lightDis * Math.cos(angradElevation) * Math.cos(angradAzimuth));
	}


	@SuppressLint("NewApi")
    private class SceneRenderer implements GLSurfaceView.Renderer
    {
    	//从指定的obj文件中加载对象
		LoadedObjectVertexNormalFace pm;
		LoadedObjectVertexNormalFace cft;
		LoadedObjectVertexNormalAverage qt;
		LoadedObjectVertexNormalAverage yh;
		LoadedObjectVertexNormalAverage ch;
    	
        public void onDrawFrame(GL10 gl) 
        { 
        	// 清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            // 设置camera位置
            MatrixState.setCamera
            (
            		cx,     // 人眼位置的X
            		cy,     // 人眼位置的Y
            		cz,     // 人眼位置的Z

            		tx, 	// 人眼球看的点X
            		ty,     // 人眼球看的点Y
            		tz,     // 人眼球看的点Z

            		0, 	// up位置
            		1, 
            		0
            );
            // 初始化光源位置
            MatrixState.setLightLocation(lx, ly, lz);                    
            // 若加载的物体部位空 则绘制物体

            // 绘制平面
            pm.drawSelf(0);

            // 绘制平面上各个物体的阴影
            GLES30.glDisable(GLES30.GL_CULL_FACE);
            drawObject(1);

            // 绘制物体本身 打开cull_face会导致茶杯有部分镂空情况:茶盖有缝,茶杯后面被面剔除了,结果看到平面
            GLES30.glEnable(GLES30.GL_CULL_FACE);
            drawObject(0);
           
        } 
        
        public void drawObject(int situ)//绘制平面上物体的方法   //根据参数的不同决定绘制阴影还是物体本身
        {
            // 绘制长方体
            MatrixState.pushMatrix();
            MatrixState.scale(1.5f, 1.5f, 1.5f);
            MatrixState.translate(-10f, 0f, 0);
            cft.drawSelf(situ);
            MatrixState.popMatrix();


            // 绘制球体
            MatrixState.pushMatrix();
            MatrixState.scale(1.5f, 1.5f, 1.5f);
            MatrixState.translate(10f, 0f, 0);
            qt.drawSelf(situ);
            MatrixState.popMatrix();


            // 绘制圆环
            MatrixState.pushMatrix();
            MatrixState.scale(1.5f, 1.5f, 1.5f);
            MatrixState.translate(0, 0, -10f);
            yh.drawSelf(situ);
            MatrixState.popMatrix();


            // 绘制茶壶
            MatrixState.pushMatrix();
            MatrixState.scale(1.5f, 1.5f, 1.5f);
            MatrixState.translate(0, 0, 10f);
            ch.drawSelf(situ);
            MatrixState.popMatrix(); 
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {

            // 设置视窗大小及位置
        	GLES30.glViewport(0, 0, width, height);

        	// 计算GLSurfaceView的宽高比
            float ratio = (float) width / height;

            // 调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);

            // 计算摄像机的位置
            setCameraPostion();

            // 计算灯光的位置
            setLightPostion();
            new Thread(){
            	@Override
            	public void run(){
            		while(true){
                		lightAzimuth +=1;
                		lightAzimuth %= 360;
                		//计算灯光的位置
                        setLightPostion();
                		try {
    						Thread.sleep(50);
    					} catch (InterruptedException e) {
    						e.printStackTrace();
    					}
            		}
            	}
            }.start();
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            // 设置屏幕背景色RGBA
            GLES30.glClearColor(0.3f,0.3f,0.3f,1.0f);

            // 打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);

            // 关闭背面剪裁
            //GLES30.glDisable(GLES30.GL_CULL_FACE);

            // 初始化变换矩阵
            MatrixState.setInitStack();

            // 加载要绘制的物体
            ch = LoadUtil.loadFromFileVertexOnlyAverage("ch.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            pm = LoadUtil.loadFromFileVertexOnlyFace("pm.obj", MySurfaceView.this.getResources(),MySurfaceView.this);;
    		cft = LoadUtil.loadFromFileVertexOnlyFace("cft.obj", MySurfaceView.this.getResources(),MySurfaceView.this);;
    		qt = LoadUtil.loadFromFileVertexOnlyAverage("qt.obj", MySurfaceView.this.getResources(),MySurfaceView.this);;
    		yh = LoadUtil.loadFromFileVertexOnlyAverage("yh.obj", MySurfaceView.this.getResources(),MySurfaceView.this);;
        }
    }
}

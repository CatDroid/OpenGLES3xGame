package com.bn.Sample2_3;
import static com.bn.Sample2_3.Constant.*;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
class GameSurfaceView extends GLSurfaceView
{
    private SceneRenderer mRenderer;//场景渲染器
    static Sample2_3_Activity activty;
    float mPreviousY;
    float mPreviousX;
    //-----纹理
    int tex_grassId;//系统分配的草地纹理id
    int tex_sandId;
    int tex_leavesId;//系统分配的灌木纹理id
    int tex_treejointId;//树干的纹理id
    int tex_waterId;//水面纹理Id
    int tex_skyId;//天空纹理id
    //-----物体对象组件
    TreesForControl treesForControl;//所有椰子树的控制类
    TreeLeaves treeLeaves[];//叶子模型
    TreeTrunk treeTrunk;//树干模型
    FloorRect floorLand;	//草地
    SeaWater water;//水面
    LandForm landForm;//灰度图山
    SkyBall skyBall;//天空球
    static float tx=CAMERA_X;//观察目标点x坐标  
    static float ty=CAMERA_HEIGHT;//观察目标点y坐标
    static float tz=CAMERA_Z;//观察目标点z坐标  
    static float cx=(float) (tx+Math.sin(Math.toRadians(camera_direction))*DISTANCE);//摄像机x坐标
    static float cy=CAMERA_HEIGHT;//摄像机y坐标
    static float cz=(float) (tz+Math.cos(Math.toRadians(camera_direction))*DISTANCE);//摄像机z坐标
	public GameSurfaceView(Context context) 
	{
        super(context);
        activty=(Sample2_3_Activity)context;
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
        this.setKeepScreenOn(true);
    }
	//触摸事件回调方法
    @Override 
    public boolean onTouchEvent(MotionEvent e) 
    {    	
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) 
        {
	        case MotionEvent.ACTION_DOWN://按下动作
	        break;
	        case MotionEvent.ACTION_UP://抬起动作
	        break;
	        case MotionEvent.ACTION_MOVE:
        	    float dy = y - mPreviousY;//计算触控笔Y位移
	            float dx = x - mPreviousX;//计算触控笔X位移 
	        	if(dy>5&&DISTANCE>600)//前进
	        	{
	        		DISTANCE-=20;
	        	}
	        	else if(dy<-5&&DISTANCE<4700)//后退
	        	{
	        		DISTANCE+=20;
	        	}
	        	if(dx<-10)//左转弯
	        	{
	        		camera_direction-=dx*0.1f;
	        	}
	        	else if(dx>10)//右转弯
	        	{
	        		camera_direction-=dx*0.1f;
	        	}
	        break;
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        //重新初始化摄像机的位置和方向
        cx=(float) (tx+Math.sin(Math.toRadians(camera_direction))*DISTANCE);//摄像机x坐标
        cy=CAMERA_HEIGHT;//摄像机y坐标
        cz=(float) (tz+Math.cos(Math.toRadians(camera_direction))*DISTANCE);//摄像机z坐标
        return true;
    }
	class SceneRenderer implements GLSurfaceView.Renderer 
    {   
		float oldTime=0;
        @SuppressLint("NewApi")
		public void onDrawFrame(GL10 gl) 
        { 
        	float currTime=System.nanoTime();
        	System.out.println("FPS  ==   "+1000000000.0/(currTime-oldTime));
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            MatrixState.setCamera(cx,cy,cz,tx,ty,tz,0f,1.0f,0.0f);
            //天空球旋转
			sky_rotation=(sky_rotation+0.03f)%360;
            //创建天空
            skyBall.drawSelf(tex_skyId,sky_rotation);

            //绘制水面
            drawWater();
			//绘制山脉
			drawLandForm();

            //绘制正常的椰子树
            treesForControl.drawSelf(tex_leavesId,tex_treejointId,bend_R,wind_direction);

			// skyBall.drawSelf(tex_skyId,sky_rotation);
			// hhl 不能把天空穹绘制最后，因为树叶透明的部分有做深度，最后绘制天空的时候，就会发现这里比较靠近摄像头，就不绘制
			// 		结果绘制树叶的时候，透明部分与黑色背景(当时还没有天空或者其他背景)混合成黑色，然后绘制天空时由于深度，又不能通过

            oldTime=currTime;
        }  
        @SuppressLint("NewApi")
		public void onSurfaceChanged(GL10 gl, int width, int height) 
        {
            //设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 3, 50000);
            //调用此方法产生摄像机9参数位置矩阵
            MatrixState.setCamera(cx,cy,cz,tx,ty,tz,0f,1.0f,0.0f);    
        }
        @SuppressLint("NewApi")
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
            MatrixState.setLightLocation(sunPosition[0], sunPosition[1],sunPosition[2]);
            //加载shader
            ShaderManager.loadCodeFromFile(GameSurfaceView.this.getResources());
            //编译shader
            ShaderManager.compileShader();
            initAllObject();//初始化纹理组件
            initAllTexture();//初始化纹理
            
            //创建一个线程，定时摆动树干
            new Thread()
            {
        		@Override
            	public void run()
            	{
        			while(flag_go)
        			{
        				//椰子树旋转
        				if(0!=wind)//如果风力为0级以上
        				{
        					//设置椰子树的左右摆动
            				wind_speed+=wind_force;//摆动的速度  
            				bend_R-=wind_speed;//摆动的幅度
            				if(bend_R>bend_R_max)//智力状态
            				{
            					wind_speed=wind_speed_init;
            					bend_R=bend_R_max;
            				}
        				}
        				try
        				{
        					Thread.sleep(50);
        				}
        				catch(Exception e)
        				{
        					e.printStackTrace();
        				}
        			}
            	}
            }.start();
        }
    }
	//绘制山地的方法
	public void drawLandForm()
	{
		for(int i=0;i<floor_array.length;i++) // hhl 目前只有一个山地
		{
			for(int j=0;j<floor_array[0].length;j++)
			{
				MatrixState.pushMatrix();
				MatrixState.translate(j*FLOOR_WIDTH,0,i*FLOOR_HEIGHT);
				if(1==floor_array[i][j])//绘制高山
				{
					landForm.drawSelf(tex_grassId,tex_sandId);
				}
				/*
				*  hhl 绘制山地的区域0表示海底1表示高山
				*  	{0,0,0},
				*  	{0,1,0},  hhl 代表在整个区域的中心绘制高山
				*  	{0,0,0}
				*  可以改成两个高山，不过相应的树木的位置要修改
				*  {0,0,1},
				*  {0,0,0},
				*  {1,0,0}
				* */
				MatrixState.popMatrix();
			}
		}
	}
	//绘制水面
	public void drawWater()
	{
		MatrixState.pushMatrix();
    	MatrixState.translate(0, 3f, 0);
		 //绘制水面
        water.drawSelf(tex_waterId);
        MatrixState.popMatrix();
	}
	//创建所有的物体组件对象
	public void initAllObject()
	{
		//创建树干
		treeTrunk=new TreeTrunk
		(
			ShaderManager.getTreeWaveShaderProgram(),
			bottom_radius,
			joint_height,
			joint_num,
			joint_available_num
		);
		//创建六片树叶  hhl 6棵数 上头的树叶都可以看成一个(类)，只是位置不一样(实例)；所有的叶子都可以看到一个，只是转角不一样(0,1,2,3,4,5,6)
		treeLeaves=new TreeLeaves[]
        {
			new TreeLeaves(ShaderManager.getLeavesShaderProgram(),leaves_width,leaves_height,leaves_offset,0),
			new TreeLeaves(ShaderManager.getLeavesShaderProgram(),leaves_width,leaves_height,leaves_offset,1),	
			new TreeLeaves(ShaderManager.getLeavesShaderProgram(),leaves_width,leaves_height,leaves_offset,2),
			new TreeLeaves(ShaderManager.getLeavesShaderProgram(),leaves_width,leaves_height,leaves_offset,3),
			new TreeLeaves(ShaderManager.getLeavesShaderProgram(),leaves_width,leaves_height,leaves_offset,4),
			new TreeLeaves(ShaderManager.getLeavesShaderProgram(),leaves_width,leaves_height,leaves_offset,5)
        };
		//创建树的控制类
		treesForControl=new TreesForControl(treeTrunk,treeLeaves);
	    //创建海底 hhl 这个例子没有使用
		floorLand=new FloorRect 
        (
        	ShaderManager.getTextureShaderProgram(),
            FLOOR_WIDTH,
            FLOOR_HEIGHT
        ); 
		//创建水面
		water=new SeaWater(ShaderManager.getWaterShaderProgram());
		//加载灰度图的山
		// hhl 使用一个灰度图片 作为山的高度，坐标的个数就是图片的分辨率
		// shader中固定了草地和土地的过渡带 	float height1=15.0;   float height2=25.0;
		initLand(getResources(),R.drawable.land);
		//创建灰度图山
		landForm=new LandForm(LAND_ARRAY,ShaderManager.getLandFormShaderProgram());//灰度图山
		//创建天空
		skyBall=new SkyBall
		(
			SKY_BALL_RADIUS,
			ShaderManager.getTextureShaderProgram(),
			SKY_BALL_RADIUS, 
			0, 
			SKY_BALL_RADIUS 
		);
	}
	//初始化所有的纹理图形
	public void initAllTexture()
	{
		Resources r=this.getResources();//获取资源
        tex_grassId=initTexture(r,R.drawable.caodi);//草地纹理
        tex_sandId=initTexture(r,R.drawable.sand);//沙滩纹理
        tex_leavesId=initTexture(r,R.drawable.coconut);//叶子纹理
        tex_treejointId=initTexture(r,R.raw.treetrunk);//树节点纹理
        tex_waterId=initTexture(r,R.drawable.water);//水面纹理
        tex_skyId=initTexture(r,R.drawable.sky);//天空纹理
	}
}

package com.bn.Sample3_15;//声明包


import static com.bn.Sample3_15.MySurfaceView.sCameraX;
import static com.bn.Sample3_15.MySurfaceView.sCameraZ;
import static com.bn.Sample3_15.ParticleDataConstant.*;
import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
public class ParticleSystem implements Comparable<ParticleSystem> 
{
	private float[] startColor;	// 粒子起始颜色数组
	private float[] endColor;	// 粒子终止颜色数组
	private int srcBlend;		// 源混合因子
	private int dstBlend;		// 目标混合因子
	private int blendFunc;		// 混合方式
	private float maxLifeSpan;	// 粒子最大生命期
	private float lifeSpanStep;	// 粒子生命期步进  		hhl 不用来计算位置/位移
	private int sleepSpan;		// 粒子更新线程休眠时间间隔	hhl maxLifeSpan/lifeSpanStep*sleepSpan=生命总时长
	private int groupCount;		// 每批喷发的粒子数量
	private float sx;			// 基础发射点x坐标
	private float sy;			// 基础发射点y坐标
	private float positionX;	// 绘制位置x坐标
	private float positionZ;	// 绘制位置z坐标
	private float xRange;		// 发射点x方向的变化范围
	private float yRange;		// 发射点y方向的变化范围
	//private float vx;			// 粒子发射的x方向速度
	private float vy;			// 粒子发射的y方向速度
	private float yAngle=0;		// 此粒子系统的旋转角度
	private ParticleForDraw fpfd;		// 粒子群的绘制者
	private boolean flag=true;			// 线程工作的标志位
	private float halfSize;				// 粒子半径
	private int maxNumOfGroup;			// 粒子分组的组数
	
	private float[] points;//粒子对应的所有顶点数据数组
	
	ParticleSystem(ParticleForDraw fpfd ,int index)
    {

    	this.positionX = ParticleDataConstant.positionFireXZ[index][0];	// 初始化此粒子系统的绘制位置x坐标
    	this.positionZ = ParticleDataConstant.positionFireXZ[index][1]; // 初始化此粒子系统的绘制位置y坐标

    	this.startColor	= START_COLOR[index];	// 初始化粒子起始颜色
    	this.endColor	= END_COLOR[index];	 	// 初始化粒子终止颜色

    	this.srcBlend	= SRC_BLEND[index];		// 初始化源混合因子
    	this.dstBlend	= DST_BLEND[index];		// 初始化目标混合因子
    	this.blendFunc	= BLEND_FUNC[index];	// 初始化混合方式

		this.halfSize	= RADIS[index]; 			// 初始化此粒子系统的粒子半径
    	this.maxLifeSpan= MAX_LIFE_SPAN[index];		// 初始化每个粒子的最大生命期
    	this.lifeSpanStep= LIFE_SPAN_STEP[index];	// 初始化每个粒子的生命步进
    	this.groupCount = GROUP_COUNT[index];		// 初始化每批喷发的粒子数
    	this.sleepSpan 	= THREAD_SLEEP[index];		// 初始化线程的休眠时间


    	this.sx = 0;					// 初始化此粒子系统的中心点x坐标
    	this.sy = 0;					// 初始化此粒子系统的中心点y坐标
    	this.xRange = X_RANGE[index];	// 初始粒子距离中心点x方向的最大距离
    	this.yRange = Y_RANGE[index];	// 初始粒子距离中心点y方向的最大距离
    	//this.vx = 0;					// 初始化粒子的x方向运动速度 x方向的速度由距离中心点距离来决定
    	this.vy = VY[index];			// 初始化粒子的y方向运动速度

    	
    	this.fpfd = fpfd;				// 初始化粒子群的绘制者
    	
    	this.points=initPoints( MAX_COUNT_OF_PARTICLE[index] );//初始化粒子所对应的所有顶点数据数组
    	initVertexData(points);//调用初始化顶点坐标与纹理坐标数据的方法

		maxNumOfGroup = (points.length/4/groupCount) ;
    	startUpdateThread();
    }
    
	private FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
	private int vCount = 0;   //顶点个数

	// 初始化顶点坐标  由于使用点精灵 不用传入纹理坐标
	private void initVertexData(float[] points) {

		vCount = points.length / 4; // 每个顶点有x,y,z,w 4个成分

		ByteBuffer vbb = ByteBuffer.allocateDirect(points.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(points);
		mVertexBuffer.position(0);

	}


	/**
	 * 初始化所有粒子对应的顶点
	 * @param zcount 该物理系统的粒子总数目  粒子为点精灵  只有一个订单  每个顶点4个值
	 */
    public float[] initPoints(int zcount)
    {
    	float[] points=new float[zcount*4];
    	for(int i=0 ; i<zcount ; i++)
    	{
			resetStatus(points,i);

    	}

    	for(int j=0;j<groupCount;j++)	// 循环遍历第一批的粒子
        {
    		points[4*j+3]=lifeSpanStep;	// 设置粒子生命期，不为10时，表示粒子处于活跃状态
        }
    	
		return points;					// 返回所有粒子顶点属性数据
    }
    
	/**
	 * 重置粒子的状态/重新设置该粒子参数
	 * @param i 粒子索引,哪一个粒子
	 */
	private void resetStatus(float[] points , int i ){
		float px=(float) (sx+xRange*(Math.random()*2-1.0f));// 计算粒子位置x坐标 // 在中心(sx,sy)附近范围
		float py=(float) (sy+yRange*(Math.random()*2-1.0f));// 计算粒子位置y坐标
		float vx=(sx-px)/150;	// 计算粒子x方向的速度
		points[i*4]=px;			// 将粒子位置的x坐标存入points数组中
		points[i*4+1]=py;		// 将粒子位置的y坐标存入points数组中
		points[i*4+2]=vx;		// 将粒子x方向的速度存入points数组中
		points[i*4+3]=10.0f;	// 将粒子的当前生命期存入points数组中----为10时，粒子处于没有被激活状态，不为10时，粒子处于活跃状态
	}

	private void startUpdateThread(){
		new Thread() // 创建粒子的更新线程
		{
			public void run()//重写run方法
			{
				while(flag)
				{
					update();//调用update方法更新粒子状态
					try
					{
						Thread.sleep(sleepSpan);//休眠一定的时间
					} catch (InterruptedException e)
					{
						e.printStackTrace();//打印异常信息
					}
				}
			}
		}.start();//启动线程
	}
	
	void drawSelf(int texId)
    {
    	//关闭深度检测
        GLES30.glDisable(GLES30.GL_DEPTH_TEST);
    	//开启混合
        GLES30.glEnable(GLES30.GL_BLEND);  
        //设置混合方式
         GLES30.glBlendEquation(blendFunc);
        //设置混合因子
        GLES30.glBlendFunc(srcBlend,dstBlend); 
        
    	MatrixState.translate(positionX, 1, positionZ);
		MatrixState.rotate(yAngle, 0, 1, 0); // 点精灵还是需要标记板技术，因为点精灵的粒子在物体坐标系上是在一个平面XOZ上
		
		MatrixState.pushMatrix();//保护现场
		synchronized(lock){ // 加锁--防止在将顶点坐标数据送入渲染管线时，更新顶点坐标数据
			fpfd.drawSelf(texId,startColor,endColor,maxLifeSpan,halfSize,mVertexBuffer,null/*没有纹理坐标*/,vCount);//绘制粒子群
		}
		MatrixState.popMatrix();//恢复现场
    	
    	//开启深度检测
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
    	//关闭混合
        GLES30.glDisable(GLES30.GL_BLEND);  
    }
    
	//更新顶点坐标数据缓冲的方法
	private void updatVertexData(float[] points) {
		mVertexBuffer.clear();//清空顶点坐标数据缓冲
		mVertexBuffer.put(points);//向缓冲区中放入顶点坐标数据
		mVertexBuffer.position(0);//设置缓冲区起始位置
	}

	private int mCurrentGroupIdx = 1;	// 激活粒子的位置计算器
	private void update()				// 更新粒子状态的方法
    {	
		if( mCurrentGroupIdx >= maxNumOfGroup ){ // 计算器超过激活粒子位置时
			mCurrentGroupIdx =0;				 // 重新计数
		}
    	
    	//查看生命期以及计算下一位置
    	for(int i=0;i<points.length/4;i++)
    	{//循环遍历所有粒子
    		if(points[i*4+3]!=10.0f)//当前为活跃粒子时
    		{
    			points[i*4+3]+=lifeSpanStep;//计算当前生命期
        		if(points[i*4+3]>this.maxLifeSpan)//当前生命期大于最大生命期时---重新设置该粒子参数
        		{
					resetStatus(points,i);
        		}else//当前生命期小于最大生命期时----计算粒子的下一位置坐标
        		{
        			 points[i*4]+=points[i*4+2];//计算粒子位置的x坐标
                     points[i*4+1]+=vy;//计算粒子位置的y坐标
        		}
    		}
    	}
    	
    	for(int i=0;i<groupCount;i++)
    	{//循环发射一批激活计数器所指定位置的粒子
    		if( points[groupCount*mCurrentGroupIdx*4+ 4*i +3] == 10.0f)//如果粒子处于未激活状态时
    		{
    			points[groupCount*mCurrentGroupIdx*4+ 4*i +3] = lifeSpanStep;//激活粒子--设置粒子当前的生命期
    		}
    	}
    	
		synchronized(lock) {			// 加锁--防止在更新顶点坐标数据时，将顶点坐标数据送入渲染管线
			updatVertexData(points);	// 更新顶点坐标数据缓冲的方法
		}
		mCurrentGroupIdx++;				// 下次激活的粒子组
    }


	public void calculateBillboardDirection()	// 根据摄像机位置计算火焰朝向
	{
		// 由于每个粒子系统都在不同位置，不是在世界坐标系中心，所有不能用MySurfaceView.direction
		// 所以要根据每个火炬在世界坐标系中的位置，与摄像头的位置 得到角度
		float xspan = positionX - sCameraX;
		float zspan = positionZ - sCameraZ;
		if(zspan<=0) {
			yAngle=(float)Math.toDegrees(Math.atan(xspan/zspan));	
		} else {
			yAngle=180+(float)Math.toDegrees(Math.atan(xspan/zspan));
		}
	}


	@Override
	public int compareTo(ParticleSystem another) {// 重写的比较两个火焰离摄像机距离的方法

		float xs=positionX- sCameraX;
		float zs=positionZ- sCameraZ;
		
		float xo=another.positionX- sCameraX;
		float zo=another.positionZ- sCameraZ;
		
		float disA = xs*xs + zs*zs ;
		float disB = xo*xo + zo*zo;
		return ((disA-disB)==0)?0:((disA-disB)>0)?-1:1;  
	}

}

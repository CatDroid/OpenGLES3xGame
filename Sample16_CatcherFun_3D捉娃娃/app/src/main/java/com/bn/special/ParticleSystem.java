package com.bn.special;

import java.util.*;
import com.bn.MatrixState.MatrixState3D;
import android.opengl.GLES30;
import static com.bn.special.ParticleDataConstant.*;
import static com.bn.constant.SourceConstant.*;
public class ParticleSystem implements Comparable<ParticleSystem> 
{
	//用于存放所有的粒子
	public ArrayList<ParticleSingle> alFsp=new ArrayList<ParticleSingle>();
	//-用于存放需要删除的粒子
	ArrayList<ParticleSingle> alFspForDel=new ArrayList<ParticleSingle>();
	//用于转存所有的粒子，每次都要情况/
	public ArrayList<ParticleSingle> alFspForDraw=new ArrayList<ParticleSingle>();
	//用于绘制的所有粒子
	public ArrayList<ParticleSingle> alFspForDrawTemp=new ArrayList<ParticleSingle>();
	//资源锁
	Object lock=new Object();
	//起始颜色
	public float[] startColor;
	//终止颜色
	public float[] endColor;
	//源混合因子
	public int srcBlend;
	//目标混合因子
	public int dstBlend;
	//混合方式
	public int blendFunc;
	//粒子最大生命期
	public float maxLifeSpan;
	//粒子生命期步进
	public float lifeSpanStep;
	//粒子更新线程休眠时间间隔
	public int sleepSpan;
	//每次喷发的例子数量
	public int groupCount;
	//基础发射点
	public float sx;
	public float sy;
	//绘制位置
	float positionX;
	float positionY;
	float positionZ;
	//发射点变化范围
	public float xRange;
	public float yRange;
	//粒子发射的速度
	public float vx;
	public float vy;
	//旋转角度
	float yAngle=0;
	//绘制者
	ParticleForDraw fpfd;
	//工作标志位
	boolean flag=true;
    public ParticleSystem(float positionx,float positiony,float positionz,ParticleForDraw fpfd)
    {
    	this.positionX=positionx;
        this.positionY=positiony;
    	this.positionZ=positionz;
    	this.startColor=START_COLOR[CURR_INDEX];
    	this.endColor=END_COLOR[CURR_INDEX];
    	this.srcBlend=SRC_BLEND[CURR_INDEX]; 
    	this.dstBlend=DST_BLEND[CURR_INDEX];
    	this.blendFunc=BLEND_FUNC[CURR_INDEX];
    	this.maxLifeSpan=MAX_LIFE_SPAN[CURR_INDEX];
    	this.lifeSpanStep=LIFE_SPAN_STEP[CURR_INDEX];
    	this.groupCount=GROUP_COUNT[CURR_INDEX];
    	this.sleepSpan=THREAD_SLEEP[CURR_INDEX];
    	this.sx=0;
    	this.sy=0;
    	this.xRange=X_RANGE[CURR_INDEX];
    	this.yRange=Y_RANGE[CURR_INDEX];
    	this.vx=0;
    	this.vy=VY[CURR_INDEX];
    	this.fpfd=fpfd;
    	
    	new Thread()
    	{
    		public void run()
    		{
    			while(flag)
    			{
    				update();
    				try 
    				{
						Thread.sleep(sleepSpan);
					} catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
    			}
    		}
    	}.start();
    }
    
    public void drawSelf()
    {
    	//关闭深度检测
        GLES30.glDisable(GLES30.GL_DEPTH_TEST);
    	//开启混合
        GLES30.glEnable(GLES30.GL_BLEND);  
        //设置混合方式
         GLES30.glBlendEquation(blendFunc);
        //设置混合因子
        GLES30.glBlendFunc(srcBlend,dstBlend); 
        
        //因为每次进行绘制粒子的个数已经对象是不断变化的，所以需要不断地更新
    	alFspForDrawTemp.clear();
    	synchronized(lock)
    	{
    	   //加锁的目的是为了保证添加和情况不同时进行，也就是保证了每次add都会有对象
    	   for(int i=0;i<alFspForDraw.size();i++)
    		{
    			alFspForDrawTemp.add(alFspForDraw.get(i));
    		}
    	}
    	MatrixState3D.translate(positionX, positionY, positionZ);
    	calculateBillboardDirection();
		MatrixState3D.rotate(yAngle, 0, 1, 0);
    	for(ParticleSingle fsp:alFspForDrawTemp)
    	{
    		fsp.drawSelf(startColor,endColor,maxLifeSpan);
    	}
    	//开启深度检测
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
    	//关闭混合
        GLES30.glDisable(GLES30.GL_BLEND);  
    }
    
    public void update()
    {
		//喷发新粒子
    	for(int i=0;i<groupCount;i++)
    	{
    		if(SpecialBZ==5)//绘制刷新后出现的粒子系统
    		{
        		//在中心附近产生产生粒子的位置------**/
        		float px=(float) (sx+xRange*(Math.random()*2-1.0f));
                float py=(float) (sy+yRange*(Math.random()*2-1.0f));
    			//随机产生粒子的方位角及仰角
    			double elevation=Math.random()*Math.PI/12+Math.PI*2/12;//仰角
    			double direction=Math.random()*Math.PI*2;//方位角
    			//计算出粒子在XYZ轴方向的速度分量
    			float vy=(float)(2f*Math.sin(elevation));	
    			float vx=(float)(2f*Math.cos(elevation)*Math.cos(direction));	
    			float vz=(float)(2f*Math.cos(elevation)*Math.sin(direction));
    			ParticleSingle fsp=new ParticleSingle(px,py,vx,vy,vz,fpfd);
    			alFsp.add(fsp);
    		}
    		if(SpecialBZ==2)//抓到娃娃后的特效
    		{
        		//在中心附近产生产生粒子的位置------**/
        		float px=(float) (sx+xRange*(Math.random()*2-1.0f));
                float py=(float) (sy+yRange*(Math.random()*2-1.0f));
    			//随机产生粒子的方位角及仰角
    			double elevation=Math.random()*Math.PI/12+Math.PI*2/12;//仰角
    			double direction=Math.random()*Math.PI*2;//方位角
    			//计算出粒子在XYZ轴方向的速度分量
    			float vy=(float)(2f*Math.sin(elevation));	
    			float vx=(float)(2f*Math.cos(elevation)*Math.cos(direction));	
    			float vz=(float)(2f*Math.cos(elevation)*Math.sin(direction));
    			ParticleSingle fsp=new ParticleSingle(px,py,vx,vy,vz,fpfd);
    			alFsp.add(fsp);
    		}
//    		else{
//        		//在中心附近产生产生粒子的位置------**/
//        		float px=(float) (sx+xRange*(Math.random()*2-1.0f));
//                float py=(float) (sy+yRange*(Math.random()*2-1.0f));
//                float vx=(sx-px)/150;
//                //x方向的速度很小,所以就产生了拉长的火焰粒子
//                ParticleSingle fsp=new ParticleSingle(px,py,vx,vy,fpfd);
//                alFsp.add(fsp);
//    		}
    	}   	
    	
    	//清空缓冲的粒子列表，此列表主要存储需要删除的粒子
    	alFspForDel.clear();
    	for(ParticleSingle fsp:alFsp)
    	{
    		//对每个粒子执行运动操作
    		fsp.go(lifeSpanStep);
    		//果粒子已经存在的时间已经足够了，就把它添加到需要删除的粒子列表
    		if(fsp.lifeSpan>this.maxLifeSpan)
    		{
    			alFspForDel.add(fsp);
    		}
    	}
    	
    	//删除过期粒子
    	for(ParticleSingle fsp:alFspForDel)
    	{
    		alFsp.remove(fsp);
    	}    
    	//alFsp列表中存放了所有的粒子对象，其他的列表为其服务，他可以添加粒子，同时也可以删除某些过期的粒子对象
    	//更新绘制列表 
    	synchronized(lock)
    	{
    		alFspForDraw.clear();
    		for(int i=0;i<alFsp.size();i++)
    		{
    			alFspForDraw.add(alFsp.get(i));
    		}
    	}
    }
	public void calculateBillboardDirection()
	{
		//根据摄像机位置计算火焰朝向
		float xspan=positionX-EYE_X;
		float zspan=positionZ-EYE_Z;
		
		if(zspan<=0)
		{
			yAngle=(float)Math.toDegrees(Math.atan(xspan/zspan));	
		}
		else
		{
			yAngle=180+(float)Math.toDegrees(Math.atan(xspan/zspan));
		}
	}
	public int compareTo(ParticleSystem another) {
		//重写的比较两个火焰离摄像机距离的方法
		float xs=positionX-EYE_X;
		float zs=positionZ-EYE_Z;
		
		float xo=another.positionX-EYE_X;
		float zo=another.positionZ-EYE_Z;
		
		float disA=(float)(xs*xs+zs*zs);
		float disB=(float)(xo*xo+zo*zo);
		return ((disA-disB)==0)?0:((disA-disB)>0)?-1:1;  
	}

}

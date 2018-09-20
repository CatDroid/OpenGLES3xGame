package com.bn.special;

import android.util.Log;

import com.bn.MatrixState.MatrixState3D;
import com.bn.constant.SourceConstant;
public class ParticleSingle 
{    
    public float x;
    public float y;
    public float vx;
    public float vy;
    public float lifeSpan;
    
    ParticleForDraw fpfd;
    
    public float vx1;
    public float vy1;
    public float vz1;
    public float x1;
    public float y1;
    public float z1;
    
    public ParticleSingle(float x,float y,float vx,float vy,ParticleForDraw fpfd)
    {
    	this.x=x;
    	this.y=y;
    	this.vx=vx;
    	this.vy=vy;
    	this.fpfd=fpfd;
    }
    public ParticleSingle(float x,float y,float vx,float vy,float vz,ParticleForDraw fpfd)
    {
    	this.vx1=vx;
    	this.vy1=vy;
    	this.vz1=vz;
    	this.x1=x;
    	this.y1=y;
    	this.z1=0;
    	this.fpfd=fpfd;
    }


    // 在ParticleSystem线程更新 不收渲染帧率影响
    public void go(float lifeSpanStep)
    {
    	// 粒子进行移动的方法，同时岁数增大的方法
    	if(SourceConstant.SpecialBZ==5) // 刷新按钮按下的粒子系统
    	{
    		x1 = x1 + vx1*lifeSpan/6.0f; 			 // hhl x1 = x1 + (t/6) * vx  跟时间成正比线性关系 恒定速度
     		y1 = y1 + 0.5f*1*lifeSpan*lifeSpan*2.0f; // hhl y1 = y1 + 0.5*2*t^2   跟时间平方成正比	 加速度是2m/s 不考虑每次的初始速度
    		z1 = z1 + vz1*lifeSpan/6.0f;			 // hhl z1 = z1 + (t/6) * vz  跟时间成正比线性关系 恒定速度
    	}
    	if(SourceConstant.SpecialBZ==2)
    	{
    		x1 = x1 + vx1*lifeSpan*0.02f;
    		y1 = y1 - 0.5f*1*lifeSpan*lifeSpan;		 // hhl y1 =  y1 - 0.5 * 1 * t^2  方向向下 加速度是1m/s 没有考虑每次的初始速度
    		z1 = z1 + vz1*lifeSpan*0.2f;			 // hhl 速度公式 vt=v0+at 位移公式 S = (v0) * t + (1/2) * a * (t^2)
    	}
//    	else{
//        	x=x+vx;
//        	y=y+vy;
//    	}
    	lifeSpan+=lifeSpanStep;
    }
    
    public void drawSelf(float[] startColor,float[] endColor,float maxLifeSpan){
    	
    	MatrixState3D.pushMatrix();//保护现场
    	if(SourceConstant.SpecialBZ==5){ // 刷新按钮
    		MatrixState3D.translate(x1, y1, z1);
    	}
    	if(SourceConstant.SpecialBZ==2)  // 抓娃娃成功(刷新也可能导致娃娃刚好落到收纳箱中而成功捉娃娃)
    	{
    		MatrixState3D.translate(x1, y1, z1);
    	}
//    	else{
//    		MatrixState3D.translate(x, y, 0);
//    	}

    	float sj=(maxLifeSpan-lifeSpan)/maxLifeSpan;

		/* hhl
		 * 衰减因子在逐渐的变小，最后变为0  剩下的生命时间占比
		 * 不同的粒子有不同的 生命步进时间 和 最大生命时间
		 */
		Log.e("TOM","this " + this + " sj " + sj + " " + lifeSpan + " " + maxLifeSpan);
		if(sj < 0.0f){
			// hhl 由于ParticleSystem update负责新增和回收 与 drawSelf是不同的线程 所以会出现衰减到负数
			//Log.e("TOM","life span is negative");
		}else{
			fpfd.drawSelf(sj,startColor,endColor);//绘制单个粒子
		}


		/*
		 * 一个粒子的颜色 :
		 *
		 * color = (1- r当前/r整个) * sj
		 *             |             |
		 *             |			 | 剩余生命时间长度
		 *             | 越接近中心颜色越深
		 *
		 * 整个粒子物体的颜色和形状(通过一张图的alpha通道来限制形状)
		 *
		 * color = color * texture2D(position).a
		 *
		 */
		MatrixState3D.popMatrix();//恢复现场
    }
}

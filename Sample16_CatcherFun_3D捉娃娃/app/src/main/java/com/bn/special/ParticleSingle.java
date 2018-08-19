package com.bn.special;

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
    public void go(float lifeSpanStep)
    {
    	//粒子进行移动的方法，同时岁数增大的方法
    	if(SourceConstant.SpecialBZ==5)
    	{//刷新按钮按下的粒子系统
    		x1=x1+vx1*lifeSpan/6.0f;
    		y1=y1+0.5f*1*lifeSpan*lifeSpan*2.0f;
    		z1=z1+vz1*lifeSpan/6.0f;
    	}
    	if(SourceConstant.SpecialBZ==2)
    	{
    		x1=x1+vx1*lifeSpan*0.02f;
    		y1=y1-0.5f*1*lifeSpan*lifeSpan;
    		z1=z1+vz1*lifeSpan*0.2f;
    	}
//    	else{
//        	x=x+vx;
//        	y=y+vy;
//    	}
    	lifeSpan+=lifeSpanStep;
    }
    
    public void drawSelf(float[] startColor,float[] endColor,float maxLifeSpan){
    	
    	MatrixState3D.pushMatrix();//保护现场
    	if(SourceConstant.SpecialBZ==5){
    		MatrixState3D.translate(x1, y1, z1);
    	}
    	if(SourceConstant.SpecialBZ==2)
    	{
    		MatrixState3D.translate(x1, y1, z1);
    	}
//    	else{
//    		MatrixState3D.translate(x, y, 0);
//    	}
    	float sj=(maxLifeSpan-lifeSpan)/maxLifeSpan;//衰减因子在逐渐的变小，最后变为0
    	fpfd.drawSelf(sj,startColor,endColor);//绘制单个粒子   	
    	MatrixState3D.popMatrix();//恢复现场
    }
}

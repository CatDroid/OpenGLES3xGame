package com.bn.Sample5_14;
//立方体
public class Cube 
{
	//用于绘制各个面的颜色矩形
	ColorRect cr;
	//半边长
	float unitSize;
	
	public Cube(MySurfaceView mv,float unitSize,float[] color)
	{
		//创建用于绘制各个面的颜色矩形
		cr=new ColorRect(mv,unitSize,color);
		//记录半边长
		this.unitSize=unitSize;
	}
	
	public void drawSelf()
	{
		//总绘制思想：通过把一个颜色矩形旋转移位到立方体每个面的位置
		//绘制立方体的每个面		
		//保护现场
		MatrixState.pushMatrix();
		
		//绘制前小面
		MatrixState.pushMatrix();
		MatrixState.translate(0, 0, unitSize);
		cr.drawSelf();		
		MatrixState.popMatrix();
		
		//绘制后小面
		MatrixState.pushMatrix();		
		MatrixState.translate(0, 0, -unitSize);
		MatrixState.rotate(180, 0, 1, 0);
		cr.drawSelf();		
		MatrixState.popMatrix();
		
		//绘制上大面
		MatrixState.pushMatrix();	
		MatrixState.translate(0,unitSize,0);
		MatrixState.rotate(-90, 1, 0, 0);
		cr.drawSelf();
		MatrixState.popMatrix();
		
		//绘制下大面
		MatrixState.pushMatrix();	
		MatrixState.translate(0,-unitSize,0);
		MatrixState.rotate(90, 1, 0, 0);
		cr.drawSelf();
		MatrixState.popMatrix();
		
		//绘制左大面
		MatrixState.pushMatrix();	
		MatrixState.translate(unitSize,0,0);
		MatrixState.rotate(-90, 1, 0, 0);
		MatrixState.rotate(90, 0, 1, 0);
		cr.drawSelf();
		MatrixState.popMatrix();
		
		//绘制右大面
		MatrixState.pushMatrix();				
		MatrixState.translate(-unitSize,0,0);
		MatrixState.rotate(90, 1, 0, 0);
		MatrixState.rotate(-90, 0, 1, 0);
		cr.drawSelf();
		MatrixState.popMatrix();
		
		//恢复现场
		MatrixState.popMatrix();
	}
	

}

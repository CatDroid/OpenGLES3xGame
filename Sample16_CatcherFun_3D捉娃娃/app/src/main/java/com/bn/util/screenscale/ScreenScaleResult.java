package com.bn.util.screenscale;

enum ScreenOrien
{
	HP,  //表示横屏的枚举值
	SP   //表示竖屏的枚举值
}
//缩放计算的结果
public class ScreenScaleResult
{
	public int lucX;//左上角X坐标
	public int lucY;//左上角y坐标
	public float ratio;//缩放比例
	ScreenOrien so;//横竖屏情况	
	
	public ScreenScaleResult(int lucX,int lucY,float ratio,ScreenOrien so)
	{
		this.lucX=lucX;
		this.lucY=lucY;
		this.ratio=ratio;
		this.so=so;
	}
	
	public String toString()
	{
		return "lucX="+lucX+", lucY="+lucY+", ratio="+ratio+", "+so;
	}
}
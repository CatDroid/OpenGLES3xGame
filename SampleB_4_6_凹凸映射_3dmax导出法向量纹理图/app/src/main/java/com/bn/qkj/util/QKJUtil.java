package com.bn.qkj.util;

public class QKJUtil 
{
	//计算切空间,返回值为切向量
	public static double[] calQKJ
	(
		double p0x,//三角形面第一个点的X坐标
		double p0y,//三角形面第一个点的Y坐标
		double p0z,//三角形面第一个点的Z坐标
		double p1x,//三角形面第二个点的X坐标
		double p1y,//三角形面第二个点的Y坐标
		double p1z,//三角形面第二个点的Z坐标			
		double p2x,//三角形面第三个点的X坐标
		double p2y,//三角形面第三个点的Y坐标
		double p2z,//三角形面第三个点的Z坐标	
		double p0s,//三角形面第一个点的S纹理坐标
		double p0t,//三角形面第一个点的T纹理坐标
		double p1s,//三角形面第二个点的S纹理坐标
		double p1t,//三角形面第二个点的T纹理坐标	
		double p2s,//三角形面第三个点的S纹理坐标
		double p2t//三角形面第三个点的T纹理坐标			
	)
	{
		//每个变量都是基于纹理坐标中的s轴的
		//解出X分量 
		double a0=p1s-p0s;//顶点1与顶点0纹理坐标s差值
		double b0=p1t-p0t;//顶点1与顶点0纹理坐标t差值
		double c0=p0x-p1x;//顶点1与顶点0位置坐标x差值
		
		double a1=p2s-p0s;//顶点2与顶点0纹理坐标s差值
		double b1=p2t-p0t;//顶点2与顶点0纹理坐标t差值
		double c1=p0x-p2x;	//顶点2与顶点0位置坐标x差值
		
		double[] TBX=EYYCFCUtil.solveEquation(a0,b0,c0,a1,b1,c1);//进行仿射变换获得x分量
		
		//解出Y分量 
		a0=p1s-p0s;//顶点1与顶点0纹理坐标s差值
		b0=p1t-p0t;//顶点1与顶点0纹理坐标t差值
		c0=p0y-p1y;//顶点1与顶点0位置坐标y差值
		
		a1=p2s-p0s;//顶点2与顶点0纹理坐标s差值
		b1=p2t-p0t;//顶点2与顶点0纹理坐标t差值
		c1=p0y-p2y;	//顶点2与顶点0位置坐标y差值
		
		double[] TBY=EYYCFCUtil.solveEquation(a0,b0,c0,a1,b1,c1);//进行仿射变换获得y分量
		
		//解出Z分量 
		a0=p1s-p0s;//顶点1与顶点0纹理坐标s差值
		b0=p1t-p0t;//顶点1与顶点0纹理坐标t差值
		c0=p0z-p1z;//顶点1与顶点0位置坐标z差值
		
		a1=p2s-p0s;//顶点2与顶点0纹理坐标s差值
		b1=p2t-p0t;//顶点2与顶点0纹理坐标t差值
		c1=p0z-p2z;	//顶点2与顶点0位置坐标z差值
		
		double[] TBZ=EYYCFCUtil.solveEquation(a0,b0,c0,a1,b1,c1);//进行仿射变换获得z分量
		
		return new double[]{TBX[0],TBY[0],TBZ[0]};//返回该三角形面的切向量
	}
	
	public static void main(String args[])
	{
		double[] r=calQKJ(5,5,-5, 0,0,0, 10,0,0, 0.5,0, 0,1, 1,1);
		System.out.println(r[0]+","+r[1]+","+r[2]);
	}
}

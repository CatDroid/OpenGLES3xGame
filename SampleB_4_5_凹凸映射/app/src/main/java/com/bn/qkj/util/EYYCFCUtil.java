package com.bn.qkj.util;

//解二元一次方程的工具类
public class EYYCFCUtil 
{
	public static double[] solveEquation
	(//解二元一次方程的方法
		//方程 a0*x+b0*y+c0=0的三个系数
		double a0,							//方程1的x系数
		double b0, 							//方程1的y系数
		double c0, 							//方程1的常数
		//方程 a1*x+b1*y+c1=0的三个系数
		double a1, 							//方程2的x系数
		double b1,		 					//方程2的x系数
		double c1							//方程2的常数
	){
		double x=(c1*b0-c0*b1)/(a0*b1-a1*b0);		//计算得出的x值
		double y=(-a0*x-c0)/b0; 					//计算得出的y值
		return new double[]{x,y};					//返回计算结果
	}
	
	public static void main(String args[])
	{
		double[] r=solveEquation(2,4,7,3,4,5);
		System.out.println(r[0]+"|"+r[1]);
	}
}

package com.bn.bezier;//包声明

import java.util.ArrayList;//相关类的引入

public class BezierUtil 
{
   static ArrayList<BNPosition> al=new ArrayList<BNPosition>();	//控制点的列表
   
   public static ArrayList<BNPosition> getBezierData(float span)
   {//生成贝塞尔曲线上点序列的方法
	   ArrayList<BNPosition> result=new ArrayList<BNPosition>();//存放结果点序列的列表
	   
	   int n=al.size()-1;	//得到控制点线段数
	   
	   if(n<1)	//线段数少于1，无贝塞尔曲线
	   {
		   return result;//返回空列表
	   }
	   
	   int steps=(int) (1.0f/span);	//计算总分段数
	   long[] jiechengNA=new long[n+1];	//声明一个长度为n+1的阶乘数组
	   
	   for(int i=0;i<=n;i++){	//求0到n的阶乘
		   jiechengNA[i]=jiecheng(i);//调用jiecheng方法计算i的阶乘
	   }
	   
	   for(int i=0;i<=steps;i++)
	   {//分段进行循环
		   float t=i*span;
		   if(t>1)		//t的值必须在0～1
		   {
			   t=1;
		   }
		   float xf=0;//贝塞尔曲线上点的x坐标
		   float yf=0;//贝塞尔曲线上点的y坐标
		   
		   float[] tka=new float[n+1];//新建一个长度为n+1的数组
		   float[] otka=new float[n+1];//新建一个长度为n+1的数组
		   for(int j=0;j<=n;j++)
		   {
			   tka[j]=(float) Math.pow(t, j); //计算t的j次幂
			   otka[j]=(float) Math.pow(1-t, j); //计算1-t的j次幂
		   }
		   
		   for(int k=0;k<=n;k++)
		   {//循环n+1次计算贝塞尔曲线上各个点的坐标
			   float xs=(jiechengNA[n]/(jiechengNA[k]*jiechengNA[n-k]))*tka[k]*otka[n-k];
			   xf=xf+al.get(k).x*xs;
			   yf=yf+al.get(k).y*xs;
		   }
		   result.add(new BNPosition(xf,yf));//将得到的点存入结果列表
	   }
	   
	   return result;//返回贝塞尔曲线上点的列表
   }
   
 //求阶乘的方法
   public  static long jiecheng(int n){
	   long result=1;	//声明一个long型的变量
	   if(n==0)			//0的阶乘为1
	   {
		   return 1;
	   }
	   
	   for(int i=2;i<=n;i++){	//求大于等于2的数的阶乘
		   result=result*i;
	   }
	   
	   return result;	//返回阶乘的结果值
   }
}

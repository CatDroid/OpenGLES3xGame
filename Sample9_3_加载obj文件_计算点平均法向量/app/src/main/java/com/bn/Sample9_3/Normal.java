package com.bn.Sample9_3;//声明包

import java.util.Set;//引入相关类
//表示法向量的类，此类的一个对象表示一个法向量
public class Normal 
{
   public static final float DIFF=0.0000001f;//判断两个法向量是否相同的阈值
   //法向量在X、Y、Z轴上的分量
   float nx;
   float ny;
   float nz;
   
   public Normal(float nx,float ny,float nz)
   {
	   //对法向量的X、Y、Z分量进行初始化
	   this.nx=nx;
	   this.ny=ny;
	   this.nz=nz;
   }
   
   @Override 
   public boolean equals(Object o)
   {
	   if(o instanceof  Normal)
	   {//若两个法向量X、Y、Z 3个分量的差都小于指定的阈值则认为这两个法向量相等
		   Normal tn=(Normal)o;
		   if(Math.abs(nx-tn.nx)<DIFF&&
			  Math.abs(ny-tn.ny)<DIFF&&
			  Math.abs(ny-tn.ny)<DIFF
             )
		   {
			   //android.util.Log.i("TOM","equal");
			   return true;
		   }
		   else
		   {
			   return false;
		   }
	   }
	   else
	   {
		   return false;
	   }
   }
   
   //由于要用到HashSet，因此一定要重写hashCode方法
   @Override
   public int hashCode()
   {
	   return 1;
   }
   
   //求法向量平均值的工具方法
   public static float[] getAverage(Set<Normal> sn)
   {
	   float[] result=new float[3];
	   //android.util.Log.i("TOM","Set Size = " + sn.size()); // 3~6个都有可能
	   for(Normal n:sn)//把集合中所有的法向量X、Y、Z分量各自求和
	   {
		   result[0]+=n.nx;
		   result[1]+=n.ny;
		   result[2]+=n.nz;
	   }
	   return LoadUtil.vectorNormal(result); // 规格化
   }
}

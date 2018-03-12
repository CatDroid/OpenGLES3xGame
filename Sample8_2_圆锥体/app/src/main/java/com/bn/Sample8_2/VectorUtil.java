package com.bn.Sample8_2;

//计算三角形法向量的工具类
public class VectorUtil {
	
	public static float[] calConeNormal
	(//计算圆锥面指定棱顶点法向量的方法
			float x0,float y0,float z0,//A，中心点(底面圆的圆心)
			float x1,float y1,float z1,//B，底面圆上的某一点
			float x2,float y2,float z2 //C，圆锥中心最高点
	)
	{
		float[] a={x1-x0, y1-y0, z1-z0};//向量AB
		float[] b={x2-x0, y2-y0, z2-z0};//向量AC
		float[] c={x2-x1, y2-y1, z2-z1};//向量BC
		float[] k=crossTwoVectors(a,b);//先求平面ABC的法向量k
		
		float[] d=crossTwoVectors(c,k);//将c和k做叉乘，得出所求向量d
		return normalizeVector(d);//返回规格化后的法向量
	}
	//向量规格化的方法
	public static float[] normalizeVector(float [] vec){//向量规格化的方法
		float mod=module(vec);//求向量的模
		return new float[]{vec[0]/mod, vec[1]/mod, vec[2]/mod};//返回规格化后的向量
	}
	
	public static float module(float [] vec){//求向量的模的方法
		return (float) Math.sqrt(vec[0]*vec[0]+vec[1]*vec[1]+vec[2]*vec[2]);
	}
	
	public static float[] crossTwoVectors(//求两个向量叉积的方法
			float[] a,
			float[] b)
	{
		float x=a[1]*b[2]-a[2]*b[1];//向量叉积的X分量
		float y=a[2]*b[0]-a[0]*b[2];//向量叉积的Y分量
		float z=a[0]*b[1]-a[1]*b[0];//向量叉积的Z分量
		return new float[]{x, y, z};//返回叉积向量
	}
}

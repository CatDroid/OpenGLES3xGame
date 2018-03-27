package com.bn.zqt.util;

//向量计算方法的封装类
public class VectorUtil 
{
	//求两个向量的叉积
	public static float[] getCrossProduct(float x1,float y1,float z1,float x2,float y2,float z2){		
		//求出两个矢量叉积矢量在XYZ轴的分量ABC
        float A=y1*z2-y2*z1;
        float B=z1*x2-z2*x1;
        float C=x1*y2-x2*y1;
		return new float[]{A,B,C};
	}
	
	//向量规格化 
	public static float[] vectorNormal(float[] vector){
		//求向量的模
		float module=(float)Math.sqrt(vector[0]*vector[0]+vector[1]*vector[1]+vector[2]*vector[2]);
		return new float[]{vector[0]/module,vector[1]/module,vector[2]/module};
	}
	//求两个向量的点积
	public static float dotProduct(float[] vec1,float[] vec2){
	    	return
			vec1[0]*vec2[0]+
			vec1[1]*vec2[1]+
			vec1[2]*vec2[2];
		
	}   
	
	//求向量的模
	public static float mould(float[] vec){
		return (float)Math.sqrt(vec[0]*vec[0]+vec[1]*vec[1]+vec[2]*vec[2]);
	}
	
	//求两个向量的夹角
	public static float angle(float[] vec1,float[] vec2){
		//先求点积
		float dp=dotProduct(vec1,vec2);
		//再求两个向量的模
		float m1=mould(vec1);
		float m2=mould(vec2);
		
		float acos=dp/(m1*m2);
		
		//为了避免计算误差带来的问题
		if(acos>1)	{
			acos=1;
		}
		else if(acos<-1){
			acos=-1;
		}
		return (float)Math.acos(acos);
	}
}

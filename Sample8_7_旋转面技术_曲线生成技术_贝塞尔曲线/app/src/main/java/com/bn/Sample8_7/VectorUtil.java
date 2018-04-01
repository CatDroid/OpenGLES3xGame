package com.bn.Sample8_7;

import java.util.ArrayList;

//三维坐标系中法向量的工具类
public class VectorUtil {

	//根据原纹理坐标和索引，计算卷绕后的纹理的方法
	public static float[] calTextures(
			ArrayList<Float> alST,//原纹理坐标列表（未卷绕）
			ArrayList<Integer> alTexIndex//组织成面的纹理坐标的索引值列表（按逆时针卷绕）
			)
	{
		float[] textures=new float[alTexIndex.size()*2];
		//生成顶点的数组
		int stCount=0;
		for(int i:alTexIndex){
			textures[stCount++]=alST.get(2*i);
			textures[stCount++]=alST.get(2*i+1);
		}
		return textures;
	}
	
	public static float[] calVertices(
			ArrayList<Float> alv,//原顶点列表（未卷绕）
			ArrayList<Integer> alFaceIndex//组织成面的顶点的索引值列表（按逆时针卷绕）
			)
	{
		float[] vertices=new float[alFaceIndex.size()*3];
		//生成顶点的数组
		int vCount=0;
		for(int i:alFaceIndex){
			vertices[vCount++]=alv.get(3*i);
			vertices[vCount++]=alv.get(3*i+1);
			vertices[vCount++]=alv.get(3*i+2);
		}
		return vertices;
	}
	
	
}

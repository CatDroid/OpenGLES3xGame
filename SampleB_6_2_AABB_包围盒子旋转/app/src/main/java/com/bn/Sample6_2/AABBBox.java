package com.bn.Sample6_2;
import android.opengl.Matrix;
public class AABBBox 
{
	float minX;//x轴最小位置
	float maxX;//x轴最大位置
	float minY;//y轴最小位置
	float maxY;//y轴最大位置
	float minZ;//z轴最小位置
	float maxZ;//z轴最大位置
	
	public AABBBox(float[] vertices)
	{	
		float[] data=findMinAndMax(vertices);
		minX=data[0];maxX=data[1];
		minY=data[2];maxY=data[3];
		minZ=data[4];maxZ=data[5];
	}
	
	public AABBBox(float minX,float maxX,float minY,float maxY,float minZ,float maxZ)
	{
		this.minX=minX;
		this.maxX=maxX;
		this.minY=minY;
		this.maxY=maxY;
		this.minZ=minZ;
		this.maxZ=maxZ;
	}
	
	public void init(float[] data)
	{
		data[0]=Float.POSITIVE_INFINITY;
		data[1]=Float.NEGATIVE_INFINITY;
		data[2]=Float.POSITIVE_INFINITY;
		data[3]=Float.NEGATIVE_INFINITY;
		data[4]=Float.POSITIVE_INFINITY;
		data[5]=Float.NEGATIVE_INFINITY;	
	}
	
	public float[] findMinAndMax(float[] vertices)
	{
		float[] result=new float[6];
		init(result);
		for(int i=0;i<vertices.length/3;i++)
		{
			//判断X轴的最小和最大位置
			if(vertices[i*3]<result[0])
			{
				result[0]=vertices[i*3];
			}
			if(vertices[i*3]>result[1])
			{
				result[1]=vertices[i*3];
			}
			//判断Y轴的最小和最大位置
			if(vertices[i*3+1]<result[2])
			{
				result[2]=vertices[i*3+1];
			}
			if(vertices[i*3+1]>result[3])
			{
				result[3]=vertices[i*3+1];
			}
			//判断Z轴的最小和最大位置
			if(vertices[i*3+2]<result[4])
			{
				result[4]=vertices[i*3+2];
			}
			if(vertices[i*3+2]>result[5])
			{
				result[5]=vertices[i*3+2];
			}
		}
		return result;
	}
	
	public AABBBox getCurrAABBBox(Vector3f currPosition,Orientation currOrientation)
	{
		//先计算旋转后的包围盒
		Vector3f[] va=
		{
			new  Vector3f(minX,minY,minZ),
			new  Vector3f(minX,maxY,minZ),
			new  Vector3f(maxX,minY,minZ),
			new  Vector3f(maxX,maxY,minZ),
			new  Vector3f(minX,minY,maxZ),
			new  Vector3f(minX,maxY,maxZ),
			new  Vector3f(maxX,minY,maxZ),
			new  Vector3f(maxX,maxY,maxZ),  
		};
	    float[] boxVertices=new float[24];
	    int count=0;
		for(int i=0;i<va.length;i++)
		{
			float[] result=new float[4];
			float[] dot=new float[]{va[i].x,va[i].y,va[i].z,1};			
			Matrix.multiplyMV(result, 0, currOrientation.orientationData,0,dot, 0);
			boxVertices[count++]=result[0];
			boxVertices[count++]=result[1];
			boxVertices[count++]=result[2];
		}
		
		float[] data=findMinAndMax(boxVertices);
		// 再计算移动后的包围盒
		AABBBox result=new AABBBox
		(			
			data[0]+currPosition.x,
			data[1]+currPosition.x,
			data[2]+currPosition.y,
			data[3]+currPosition.y,
			data[4]+currPosition.z,
			data[5]+currPosition.z
		);
		return result;
	}
}

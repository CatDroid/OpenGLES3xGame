package com.bn.Sample5_8;

public class Vector3
{

	float x;
	float y;
	float z;
	
	public Vector3()
	{
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public Vector3(float x, float y, float z)
	{

		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void set(Vector3 vec)
	{
		this.x=vec.x;
		this.y=vec.y;
		this.z=vec.z;
	}

	public void set(Point3 p)
	{
		this.x=p.x;
		this.y=p.y;
		this.z=p.z;
	}

	public void set(float vec[])
	{
		this.x=vec[0];
		this.y=vec[1];
		this.z=vec[2];
	}

	public void set(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// 与常量相乘 本身并不修改 只是
	public Vector3 multiConst(float constant)
	{
		return new Vector3(this.x*constant,this.y*constant,this.z*constant);
	}
	
	// 点积
	public static float dot(Vector3 v1,Vector3 v2){
		
		return v1.x*v2.x + v1.y*v2.y + v1.z*v2.z;
	}
	
	// 点积
	public static float dot(Point3 p1,Vector3 v2)
	{
		return p1.x*v2.x + p1.y*v2.y + p1.z*v2.z;
	}
	
	// 点积
	public static float dot(Point3 p1,Point3 p2)
	{
		return p1.x*p2.x + p1.y*p2.y + p1.z*p2.z;
	}
	
	// 加法
	public Vector3 add(Vector3 v)
	{
		return new Vector3(this.x+v.x,this.y+v.y,this.z+v.z);
	}
	
	public String toString()
	{
		return "vector:["+this.x+","+this.y+","+this.z+"]";
	}

	/*
	 * 将向量转换成齐次坐标表示的方法
	 *
	 * 齐次坐标：
	 * 若增加第四个分量1，以表示该四元组为点
	 * 若增加第四个分量0，以表示该四元组为向量
	 */
	public float[] toQici4()
	{
		return new float[]{this.x,this.y,this.z,0};
	}
		
}

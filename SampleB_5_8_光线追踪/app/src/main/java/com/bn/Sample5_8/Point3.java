package com.bn.Sample5_8;

public class Point3 {

	float x;
	float y;
	float z;

	public Point3()
	{
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public Point3(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Point3(Point3 data)
	{
		this.x = data.x;
		this.y = data.y;
		this.z = data.z;
	}

	public Point3(float[] data)
	{
		this.x=data[0];
		this.y=data[1];
		this.z=data[2];
	}

	public void set(Point3 p)
	{
		this.x=p.x;
		this.y=p.y;
		this.z=p.z;
	}

	public void set(Vector3 vec)
	{
		this.x=vec.x;
		this.y=vec.y;
		this.z=vec.z;
	}

	public void set(float p[])
	{
		this.x=p[0];
		this.y=p[1];
		this.z=p[2];
	}
	
	//将一个点按向量vec移动，得到另一个点的方法
	public Point3 addVec(Vector3 vec)
	{
		return new Point3(this.x+vec.x,this.y+vec.y,this.z+vec.z);
	}

	//点和点相减得到一个向量
	public Vector3 minus(Point3 p)
	{
		return new Vector3(this.x-p.x,this.y-p.y,this.z-p.z);
	}

	//点和点相减得到一个向量
	public Vector3 minus(Vector3 vec)
	{
		return new Vector3(this.x-vec.x,this.y-vec.y,this.z-vec.z);
	}

	@Override
	public String toString()
	{
		return "Point:("+this.x+","+this.y+","+this.z+")";
	}
	
	
	/*
	 * 齐次坐标：
	 * 若增加第四个分量1，以表示该四元组为点
	 * 若增加第四个分量0，以表示该四元组为向量
	 */
	//将点转换成齐次坐标表示的方法
	public float[] toQici4()
	{
		return new float[]{this.x,this.y,this.z,1};
	}
}

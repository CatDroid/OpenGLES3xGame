package com.bn.Sample6_2;

public class Vector3f
{
	float x;
	float y;
	float z;
	
	public Vector3f(float x,float y,float z)
	{
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public void add(Vector3f temp)
	{
		this.x+=temp.x;
		this.y+=temp.y;
		this.z+=temp.z;
	}
}

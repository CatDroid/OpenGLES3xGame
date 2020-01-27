package com.bn.Sample5_8;

public class Color3f {
	float red;
	float green;
	float blue;
	
	public Color3f()
	{

	}

	public Color3f(float red, float green, float blue)
	{
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public Color3f(float[] clr)
	{
		this.red = clr[0];
		this.green = clr[1];
		this.blue = clr[2];
	}

	public void set(Color3f clr)
	{
		this.red = clr.red;
		this.green = clr.green;
		this.blue = clr.blue;
	}

	@Override
	public String toString()
	{
		return "Color3:("+this.red+","+this.green+","+this.blue+")";
	}
}

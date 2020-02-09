package com.bn.Sample6_2;
import android.opengl.Matrix;
public class Orientation //姿态类
{
	float[] orientationData=new float[16];
	
	public Orientation(float angle,float zx,float zy,float zz)
	{
		Matrix.setRotateM(orientationData, 0, angle, zx, zy, zz);
	}
}

package com.bn.Sample4_8;

import android.opengl.Matrix;

public class CameraUtil 
{
	private static final float[] LIGHT_POSITION={0.98f,11.27f,-27.6f,1};
	
	private static final float[] UP_INIT={0,1,0,1};
	private static final float[] TARGRT_INIT={0,0,-1,1};
	
	private static final float cx=0;
	private static final float cy=0;
	private static final float cz=0;
	
	private static float tx=0;
	private static float ty=0;
	private static float tz=0;
	
	private static float upx=0;
	private static float upy=0;
	private static float upz=0;	
	
	private static float direction=0;
	private static float yj=0;
	
	private static void calCamera()
	{
		float[] mat=new float[16];
		Matrix.setIdentityM(mat, 0);
		Matrix.rotateM(mat, 0, direction, 0, 1, 0);
		Matrix.rotateM(mat, 0, yj, 1, 0, 0);
		float[] upResult=new float[4];
		Matrix.multiplyMV(upResult, 0, mat, 0, UP_INIT, 0);
		upx=upResult[0];
		upy=upResult[1];
		upz=upResult[2];
		float[] targetResult=new float[4];
		Matrix.multiplyMV(targetResult, 0, mat, 0, TARGRT_INIT, 0);
		tx=targetResult[0];
		ty=targetResult[1];
		tz=targetResult[2];					
	}
	public static void init3DCamera()
	{
		calCamera();
		flush3DCamera();
	}
	
	public static void flush3DCamera()
	{
		MatrixState.setCamera(cx, cy, cz, tx, ty, tz, upx, upy, upz);	
	}


	public static float[] calLightScreen(float ratio)
	{

		//  PVM * 光源位置 = 光源在“标准设备空间”(-1,1)的位置 但是没有做透视除法
		//  这里只有 VP矩阵 没有M矩阵 所以 LIGHT_POSITION 给出的是世界坐标系中的坐标
		//  一般光源给出的是 世界坐标系 中的坐标 所以只用用 MVP中的VP
		float[] mat=MatrixState.getFinalMatrix();
		float[] lightResult = new float[4];
		Matrix.multiplyMV(lightResult, 0, mat, 0, LIGHT_POSITION, 0);

		// 透视除法之后 z/w, lightResult[2]=z, z轴超出1.0 标准设备空间
		if(Math.abs(lightResult[2]/lightResult[3]) > 1.0)
		{
			// 返回 超出屏幕空间 的 坐标点
			// -ratio ~ radio,
			// -1 ~ 1
			return new float[]{ ratio*2,  2};
		}
		else
		{
			// x 从 -1,1 转换到 -radio,radio
			// y 从 -1,1 转换到 -1,1
			return new float[]{ ratio*lightResult[0]/lightResult[3], lightResult[1]/lightResult[3]};
		}
		
	}
	public static void changeDirection(float delta)
	{
		direction=direction+delta;
		if(direction<0)
		{
			direction=direction+360;
		}
		if(direction>360)
		{
			direction=direction-360;
		}
		calCamera();
	}
	public static void changeYj(float delta) 
	{
		yj=yj+delta;
		if(yj>88)
		{
			yj=88;
		}
		if(yj<-88)
		{
			yj=-88;
		}		
		calCamera();		
	}	
}

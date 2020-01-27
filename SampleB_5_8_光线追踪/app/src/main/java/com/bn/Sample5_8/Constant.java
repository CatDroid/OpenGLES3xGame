package com.bn.Sample5_8;//声明包

//常量类 
public class Constant {
	//关于屏幕尺寸的常量
	public static final float SCREEN_WIDTH = 800;//屏幕的宽度
	public static final float SCREEN_HEIGHT = 480;//屏幕的高度
	//关于渲染时的常量
	public static final float blockSize = 8f;//基本块的尺寸
	public static final float W = SCREEN_WIDTH / 2.0f;//屏幕半宽
	public static final float H = SCREEN_HEIGHT / 2.0f;//屏幕半高
	public static final float ratio = W / H;//屏幕宽高比
	public static final float nRows = SCREEN_HEIGHT;//屏幕像素总行数
	public static final float nCols = SCREEN_WIDTH;//屏幕像素总列数

	// 真实3D世界中近平面的量
	public static final float N_3D = 24;		// 近平面到摄像机的距离
	public static final float W_3D = ratio;		// 近平面半宽
	public static final float H_3D = 1.0f;		// 近平面半高

	// 真实世界中各物体的量
	public static final float R = 0.6f;//球的半径
	public static final float CENTER_DIS = 0.7f;//球心与世界坐标系y轴的距离
	public static final float PLANE_WIDTH = 3.5f;//平面宽度
	public static final float PLANE_HEIGHT = 4f;//平面长度
	
	public static final float[] BALL1_COLOR = {0.8f,0.2f,0.2f};	//球1的颜色
	public static final float[] BALL2_COLOR = {0.2f,0.2f,0.8f};//球2的颜色
	public static final float[] PLANE_COLOR = {0.2f,0.8f,0.2f};//平面的颜色
	//关于摄像机的参数
	public static final float CAM_X = 15;//摄像机位置x坐标
	public static final float CAM_Y = 7;//摄像机位置y坐标
	public static final float CAM_Z = 32;//摄像机位置z坐标


	// 光源的参数
	public static final float LIGHT_X = 100;// 光源位置x坐标
	public static final float LIGHT_Y = 80;	// 光源位置y坐标
	public static final float LIGHT_Z = 0;	// 光源位置z坐标


	// 计算阴影时用的极小的正数
	public static final float MNIMUM = 0.00001f;
}
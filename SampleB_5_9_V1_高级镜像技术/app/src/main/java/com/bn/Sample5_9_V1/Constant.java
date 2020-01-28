package com.bn.Sample5_9_V1;


public class Constant  
{

	public static final String TAG = "HHL" ;

	public static final boolean CONFIG_USING_CUSTOM_FBO = true ;


	public static float r = 40;			// 摄像机到目标点的距离，即摄像机旋转的半径
	public static float ANGLE_MIN = -55;// 摄像机旋转的角度范围的最小值
	public static float ANGLE_MAX = 55; // 摄像机旋转的角度范围的最大值
	
	public static final float UNIT_SIZE = 15f;
	public static int SCREEN_WIDTH;		// 屏幕宽度
	public static int SCREEN_HEIGHT;	// 屏幕高度


	// 设置投影矩阵的参数
	public static  float left=0;
	public static float right=0;
	public static float bottom=0;
	public static float top=0;
	public static float near=0;
	public static float far=0;
	public static float ratio =0;
	
	// 注意start======主摄像与镜像摄像机的目标点和up向量是一致的
	// 摄像机目标点的坐标
	final public static float targetX = 0;
	final public static float targetY = 18;	 // 目标在镜面内
	final public static float targetZ = -45; // 天空盒的尺寸是90*90*90
	
	// 摄像机的up向量
	public static float upX = 0;
	public static float upY = 1;
	public static float upZ = 0;
	// 注意end======主摄像与镜像摄像机的目标点和up向量是一致的
	
	// 主摄像机的观察者坐标
	public static float mainCameraX = 0;
	public static float mainCameraY = 0;
	public static float mainCameraZ = 0;
	
	// 镜像摄像机的观察者坐标
	public static float mirrorCameraX = 0;
	public static float mirrorCameraY = 0;
	public static float mirrorCameraZ = 0;


	public static void calculateMainAndMirrorCamera(float angle)
	{		
		// 计算主摄像机观察者的坐标  围绕的中心点是 (0,taretY, targetZ)   轴心线是 x=0,z=targetZ, y始终是targetY
		mainCameraX = (float) (r*Math.sin(Math.toRadians(angle))) + 0 ;
		mainCameraY = targetY;
		mainCameraZ = (float) (r*Math.cos(Math.toRadians(angle))) + targetZ;
		
		// 计算镜像摄像机观察者的坐标
		mirrorCameraX = mainCameraX;						//	镜像摄像机的x坐标与主摄像的z坐标一致
		mirrorCameraY = mainCameraY;
		mirrorCameraZ = targetZ + (targetZ - mainCameraZ);	//	根据对称关系计算镜像摄像机的z坐标

		// 关于 z=targetZ (XOY平面)
	}
	
	// 初始化投影矩阵的参数
	public static void initProject(float factor)
	{
		left 	= -ratio*factor;
		right 	= ratio*factor;
		bottom 	= -1*factor;
		top  	= 1*factor;
		near 	= 1*factor;
	    far		= 500;
	}
}
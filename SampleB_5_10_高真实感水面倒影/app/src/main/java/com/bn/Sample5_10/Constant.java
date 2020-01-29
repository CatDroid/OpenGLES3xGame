package com.bn.Sample5_10;


public class Constant  
{

	final public static boolean CONFIG_SHOW_REFLECTION = false ; // 把虚拟镜像摄像机显示在平面上

	final public static float RECT_WIDTH = 2f * 63;		// 横向长度总跨度

	public static boolean threadFlag=true;//水面换帧线程工作标志位	  
	public static float r=60;	//摄像机到目标点的距离，即摄像机旋转的半径
	
	
	public static float XANGLE_MIN=-55;//摄像机左右旋转的角度范围的最小值
	public static float XANGLE_MAX=55;//摄像机左右旋转的角度范围的最大值
	public static float YANGLE_MIN=15;//摄像机上下旋转的角度范围的最小值
	public static float YANGLE_MAX=90;//摄像机上下旋转的角度范围的最大值
	
	public static final float UNIT_SIZE=0.6f;	
	public static int SCREEN_WIDTH;//屏幕宽度
	public static  int SCREEN_HEIGHT;//屏幕高度   

	//设置投影矩阵的参数
	public static  float left=0;
	public static float right=0;
	public static float bottom=0;
	public static float top=0;
	public static float near=0;
	public static float far=0;
	public static float ratio =0;
	
	//注意start======主摄像与镜像摄像机的目标点和up向量是一致的
	//摄像机目标点的坐标
	public static float targetX=0;
	public static float targetY=0;
	public static float targetZ=0;
	
	//摄像机的up向量
	public static float upX=0;
	public static float upY=1;
	public static float upZ=0;	
	//注意end======主摄像与镜像摄像机的目标点和up向量是一致的
	
	// 主摄像机的观察者坐标
	public static float mainCameraX=0;
	public static float mainCameraY=0;
	public static float mainCameraZ=0;
	
	// 镜像摄像机的观察者坐标
	public static float mirrorCameraX=0;
	public static float mirrorCameraY=0;
	public static float mirrorCameraZ=0;	
	
	public static void calculateMainAndMirrorCamera(float xAngle,float yAngle)
	{
		mainCameraX = (float) (r*Math.cos(Math.toRadians(yAngle))*Math.sin(Math.toRadians(xAngle)));
		mainCameraY = (float) (r*Math.sin(Math.toRadians(yAngle)));
		mainCameraZ = (float) (r*Math.cos(Math.toRadians(yAngle))*Math.cos(Math.toRadians(xAngle)));

		mirrorCameraX = mainCameraX;							// 镜像摄像机的x坐标与主摄像的z坐标一致
		mirrorCameraY = targetY + (targetY -mainCameraY); 		// 关于XOZ平面镜像
		mirrorCameraZ = mainCameraZ;							// 根据对称关系计算镜像摄像机的z坐标
	}
	//初始化投影矩阵的参数
	public static void initProject(float factor)
	{
		left=-ratio*factor*0.5f;
		right=ratio*factor*0.5f;
		bottom=-1*factor*0.5f;
		top=1*factor*0.5f;
		near=1*factor;
	    far=500;
	}

	final public static float waveFrequency1 = 0.19f;							// 1号波波频
	final public static float waveFrequency2 = 0.09f;							// 2号波波频
	final public static float waveFrequency3 = 0.01f;							// 3号波波频  3号 周期长 频率小 幅度大
	
	
	final public  static float waveAmplitude1=0.126f;							// 1号波振幅
	final public  static float waveAmplitude2=0.21f;							// 2号波振幅
	final public  static float waveAmplitude3=0.35f;							// 3号波振幅
	
	
	public static float wave1PositionX=0;										// 1号波的位置
	public static float wave1PositionY=0;
	public static float wave1PositionZ=0;
	
	public static float wave2PositionX=-200;									// 2号波的位置
	public static float wave2PositionY=0;
	public static float wave2PositionZ=-200;
	
	public static float wave3PositionX=300;										// 3号波的位置
	public static float wave3PositionY=0;
	public static float wave3PositionZ=300;
	
	
	public static final Object slock = new Object();//锁资源
}
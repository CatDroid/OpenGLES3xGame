package com.bn.constant;
import com.bn.catcherFun.MainActivity;
import com.bn.util.screenscale.ScreenScaleResult;

public class Constant 
{
	static MainActivity mainActivity;
    //=======屏幕自适应数据=======start=======================================================//
	public static float SCREEN_WIDTH_STANDARD = 1080;//720;//屏幕标准宽度	
	public static float SCREEN_HEIGHT_STANDARD = 1920;//1280;//屏幕标准高度
	public static float RATIO = SCREEN_WIDTH_STANDARD/SCREEN_HEIGHT_STANDARD;//屏幕标准比例--即透视投影的比例
	public static ScreenScaleResult ssr;//屏幕自适应对象
	//=======屏幕自适应数据=======end=========================================================//
	
	
	public static float fromPixSizeToNearSize(float size)
	{
		return size*2/SCREEN_HEIGHT_STANDARD;
		/*
		*   hhl 归一化 都以高为长边  除以  SCREEN_HEIGHT_STANDARD/2 ( 把SCREEN_HEIGHT_STANDARD/2 归成 1 )
		*   ? = size  / (SCREEN_HEIGHT_STANDARD/2)
		* */
	}

	/** 屏幕x坐标到视口x坐标 和 屏幕y坐标到视口y坐标  视口坐标就是要归一化
	 *
	 *
	 * 	1. 原点从左上角 改成 中心  只需要 -WIDTH/2 或者 -HEIGHT/2
	 * 	2. 归一化
	 * 			因为 setProjectOrtho 正交变换 设置为 -radio,radio,-1,1
	 * 			就是 width/height,width/height,height/height,height/height
	 * 			所以 这里除以 HEIGHT/2 作为归一化
	 *
 	 */
	public static float fromScreenXToNearX(float x) {
		return (x-SCREEN_WIDTH_STANDARD/2)/(SCREEN_HEIGHT_STANDARD/2);
	}

	public static float fromScreenYToNearY(float y) {
		return -(y-SCREEN_HEIGHT_STANDARD/2)/(SCREEN_HEIGHT_STANDARD/2);
	}

	// 物理屏幕坐标 --> 缩放后的屏幕坐标 -->  1080x1920标准屏幕的坐标
	//实际屏幕x坐标到标准屏幕x坐标
	public static float fromRealScreenXToStandardScreenX(float rx)
	{
		return (rx-ssr.lucX)/ssr.ratio;
	}
	//实际屏幕y坐标到标准屏幕y坐标
	public static float fromRealScreenYToStandardScreenY(float ry)
	{
		return (ry-ssr.lucY)/ssr.ratio;
	}
	//从标准屏幕到实际屏幕x坐标
	public static float fromStandardScreenXToRealScreenX(float tx)
	{
		return tx*ssr.ratio+ssr.lucX;
	}
	//从标准屏幕到实际屏幕y坐标
	public static float fromStandardScreenYToRealScreenY(float ty)
	{
		return ty*ssr.ratio+ssr.lucY;
	}
	public static float fromStandardScreenSizeToRealScreenSize(float size)
	{
		return size*ssr.ratio;
	}
}

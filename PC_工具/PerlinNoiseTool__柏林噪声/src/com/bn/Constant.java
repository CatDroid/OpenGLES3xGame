package com.bn;

import java.awt.image.BufferedImage;

public class Constant 
{
	public static float BASE_AMPLITUDE=80;  //1D绘图用基础振幅
	public static float X_ARRANGE=800;	  //1D绘图用基础宽度
	
	//是否增强对比度标志
	public static boolean ZQDBD_FLAG=true;
	
	public static int BP=2;//倍频
	public static int PLS=5;//频率数量
	public static int COUNT=16;//一级倍频段数
	public static double X_SPAN=1.0;//一级倍频X步进
	public static int X_CURR=0;//求噪声的起始X
	public static int Y_CURR=0;//求噪声的起始Y
	public static int Z_CURR=0;//求噪声的起始Z
	
	
	public static boolean RESTART_FROM_BEGIN = false ; // HHL 如果每级别倍频都是从 X_CURR=0开始,这样每次运行的结果都是一样,生成的地形图不会改变 
	public static boolean CONFIG_ROW_COLUMN = false ;  // HHL 对于二维柏林噪声 控制点从(0,0),(0,1),(0,2)....(1,0)(1,1)(1,2)等 一行行来的控制点
	
	public static BufferedImage[] D3Each;
}

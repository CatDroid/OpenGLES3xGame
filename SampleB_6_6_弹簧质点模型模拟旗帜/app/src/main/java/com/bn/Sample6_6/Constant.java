package com.bn.Sample6_6;

import java.nio.FloatBuffer;

public class Constant 
{
	static FloatBuffer mVertexBufferForFlag;//顶点缓冲引用
	final static Object lockA = new Object();//锁对象A
	final static Object lockB = new Object();//锁对象B

	final static int NUMROWS = 7;	// 旗面网格行数
	final static int NUMCOLS = 10;	// 旗面网格列数

	final static int NUMVERTICES = (NUMROWS + 1) * (NUMCOLS + 1); // 顶点数目 行和列的顶点都会比格子数目多一个

									// 弹簧数目
	final static int NUMSPTINGS = NUMROWS *(NUMCOLS + 1) // 竖向弹簧数目
									+
								  (NUMROWS + 1)*NUMCOLS  // 横向弹簧数目
									+
								  2 * NUMROWS * NUMCOLS ;// 左斜/右斜弹簧数目


	final static float GRAVITY = -0.7f;					// 重力加速度  y轴向上为正 不使用9.8m/s^2   ?????
	final static float DRAGCOEFFICIENT = 0.01f;			// 空气阻力      ?????
	// F = 0.5 * Cd * ρ * A * V^2
	// 常见物体的Cd值 垂直平面 = 1    ρ 空气密度 25度=1.185kg/m3  A=  红旗的面积是  0.75f * 1.0f = 0.75 m^2   系数?=0.444375‬??

	final static float KRESTITUTION = 0.3f; 			// 反弹系数
	final static float FRICTIONFACTOR = 0.9f; 			// 摩擦系数 能量损失系数 衰减系数

	final static float COLLISIONTOLERANCE = -6.6f;		// 地面位置

	final static float FLAGPOLERADIUS = 0.04f;			// 旗杆半径 (ground.obj 包含一个圆柱旗杆 直径是0.08个像素单位)
	final static float FLAGPOLERADIUS_SQUARE = FLAGPOLERADIUS*FLAGPOLERADIUS ;


	final static float SPRING_TENSION_CONSTANT = 500.f;	// 构造弹簧参数  弹簧横着或者竖着时候的弹簧系数 ?????
	final static float SPRING_SHEAR_CONSTANT = 300.f;	// 剪力弹簧参数  弹簧斜着时候的弹簧参数


	final static float SPRING_DAMPING_CONSTANT = 2.f;	// 弹簧阻尼

	static boolean isC = false;					// 是否开启碰撞检测标志

	static float WindForce = 2.0f;				// 风力  ????? 2N


}

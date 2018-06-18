package com.bn.Sample11_8;

import static com.bn.Sample11_8.Constant.*;
import android.opengl.GLES30;
import android.util.Log;

//存储球运动过程中物理信息的对象所属类
public class BallForControl   
{	
	public static final float TIME_SPAN=0.05f;//物理模拟的单位时间间隔
	public static final float G=0.8f;//重力加速度
	 
	BallTextureByVertex btv;// 用于绘制的篮球的纹理球
	float startY;		// 每轮起始点位置
	float timeLive=0;	// 此周期存活时长
	float currentY=0;	// 当前Y位置
	float vy=0;			// 每轮初始y轴方向速度
	
	public BallForControl(BallTextureByVertex btv,float startYIn)
	{
		// 初始化速度与起始位置
		this.btv=btv;
		this.startY=startYIn;	// 3f
		currentY=startYIn;
		new Thread()
		{//开启一个线程运动篮球
			public void run()
			{
				while(true) // hhl 上抛运动的物理规律 0.8f为恢复系数，代表每次篮球碰撞地面后还能保存的动能占碰撞前的比例
				{
					// hhl 理论依据  s = v0*t + 0.5gt²
					//		最终位移  s = s0 + v0*t + 0.5gt²
					// 		每次碰到平面时，重置v0=计算损耗后 t=0  s0=0  g=-9.8(向上为正)
					//

					//此轮运动时间增加
					timeLive+=TIME_SPAN;
					//根据此轮起始Y坐标、此轮运动时间、此轮起始速度计算当前位置
					float tempCurrY=  startY   -   0.5f*G*timeLive*timeLive   +  vy*timeLive;

					if(tempCurrY<=FLOOR_Y)
					{// 若当前位置低于地面则碰到地面反弹
						// 反弹后起始高度为0
						startY=FLOOR_Y;		
						//反弹后起始速度
						float vTesty= vy*0.8f;   // hhl 不考虑空气阻力 上抛时候初始速度等于下降触碰前速度
						vy=-(vy-G*timeLive)*0.8f;// hhl 考虑第一次下落时候 初始速度是0，所以这里按照时间来计算
						Log.d("Test",String.format("vTestY %f vy %f",vTesty, vy));
						//反弹后此轮运动时间清0
						timeLive=0;
						//若速度小于阈值则停止运动
						if(vy<0.35f)
						{
							currentY=FLOOR_Y;
							break;
						}
					}
					else {//若没有碰到地面则正常运动
						currentY=tempCurrY;
					}
					
					try {Thread.sleep(20);}
					catch(Exception e) {e.printStackTrace();}
				}
			}
		}.start();
	}
	
	public void drawSelf(int texId)// 绘制物体自己
	{
		MatrixState.pushMatrix();
		MatrixState.translate(0, UNIT_SIZE*BALL_SCALE+currentY, 0);
		btv.drawSelf(texId);					
		MatrixState.popMatrix();
	}
	
	public void drawSelfMirror(int texId)//绘制 镜像体
	{
		GLES30.glFrontFace(GLES30.GL_CW);	//	hhl: 顺时针卷绕
		MatrixState.pushMatrix();	
		MatrixState.scale(1, -1, 1);		//  上下倒转
		MatrixState.translate(0, UNIT_SIZE*BALL_SCALE+currentY-2*FLOOR_Y, 0); // 当前按物理规律计算出来的位置 加上 球的半径
		btv.drawSelf(texId);		
		MatrixState.popMatrix();
		GLES30.glFrontFace(GLES30.GL_CCW);	//	hhl: 逆时针卷绕
	}
}

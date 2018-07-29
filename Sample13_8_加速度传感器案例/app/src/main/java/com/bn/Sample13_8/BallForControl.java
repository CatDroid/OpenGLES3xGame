package com.bn.Sample13_8;

import static com.bn.Sample13_8.Constant.*;
import android.opengl.Matrix;
import android.util.Log;

//用于控制的球 
public class BallForControl {
	MySurfaceView mv;// MySurfaceView类的引用
	Ball ball;//用于绘制的球(采用的是第8章的线框几何球)
	

	private final Object mRealTimeOffsetLock = new Object();
	private float mRealTimeXOffset =0;	//球实时位置坐标
	private float mRealTimeZOffset =0;


	private float[] selfRotateMatrix;//自带旋转矩阵
	
	public BallForControl(MySurfaceView mv,float scale,float aHalf,int n)
	{
		this.mv=mv;
		ball=new Ball(mv,scale,aHalf,n);//创建用于绘制的几何球对象
		//初始化自带旋转矩阵
		selfRotateMatrix=new float[16];
		//初始时旋转一定的度数
		Matrix.setRotateM(selfRotateMatrix, 0,
				10, // 旋转角度
				0, 1, 0);
	}
	
	public void drawSelf()
	{
		float realXOffset ;
		float realZOffset ;
		synchronized (mRealTimeOffsetLock){
			realXOffset = mRealTimeXOffset;
			realZOffset = mRealTimeZOffset;
		}

		MatrixState.pushMatrix();
		MatrixState.translate(realXOffset, 1.2f,realZOffset );	// 移动到指定位置
		MatrixState.matrix(selfRotateMatrix);					// 加上记录姿态的旋转矩阵
		ball.drawSelf();	// 绘制球  							// hhl: 自身旋转 + 位移
		MatrixState.popMatrix();
	}
	
	//球根据手机姿态移动
	public void go(){

		float tempSPANX;	//球移动距离的临时变量
		float tempSPANZ;
		float tempLength;	//球前进的距离临时变量
		float tempAngle;	//球旋转的角度

		synchronized (SPAN_LOCK){		// Constant.SPANX 是在传感器的回调线程 go()是在BallGoThread线程
			tempSPANX=Constant.SPANX;	// 球移动距离的临时变量赋值
			tempSPANZ=Constant.SPANZ;
		}

		float tempX;	//球位置的临时变量 hhl 用于计算是否超出边界
		float tempZ;

		tempX= mRealTimeXOffset +tempSPANX;//根据传感器计算结果，改变当前球的位置
		tempZ= mRealTimeZOffset +tempSPANZ;

		if( (tempZ<-ZBOUNDARY)||(tempZ>ZBOUNDARY)) {
			tempSPANZ=0;//如果与上下两条边发生碰撞 z轴方向 步进为0
		}

		if((tempX<-XBOUNDARY)|| (tempX>XBOUNDARY)) {
			tempSPANX=0;//如果与左右两条边发生碰撞 x轴方向 步进为0
		}
		

		// 球当前的位置实时发生变化 hhl: 在原来的位置(mRealTimeXOffset, mRealTimeZOffset) + 方向向量*0.08
		synchronized (mRealTimeOffsetLock){
			mRealTimeXOffset +=tempSPANX; // hhl 当超过边界的时候为0 不超过的时候 每次都是方向向量*0.08
			mRealTimeZOffset +=tempSPANZ;
		}


		//*****************旋转 begin************************
		// 球的旋转轴
		float rotateX;
		float rotateY;
		float rotateZ;

		// 前进的方向向量为(Constant.SPANX,0,Constant.SPANZ) or (tempSPANX,0,tempSPANZ)
		// 那么旋转轴为
		// hhl  '两个垂直向量点积为零'
		// hhl  (tempSPANX,0,tempSPANZ)*(rotateX,0,rotateZ) = 0  = >
		//		tempSPANX * rotateX + tempSPANZ * rotateZ = 0 ;
		// 		rotateX = tempSPANZ , rotateZ = -tempSPANX;
		rotateX=tempSPANZ;
		rotateY=0;
		rotateZ=-tempSPANX;
		//计算球前进的距离  hhl 前进的距离 就是 圆上的弧度
		tempLength=(float) Math.sqrt(tempSPANX*tempSPANX+tempSPANZ*tempSPANZ);
		//计算前进的角度值  hhl 设半径为r，弧长为I，圆心角为α， 则α=I/r（单位：弧度）
		tempAngle=(float) Math.toDegrees(tempLength/Constant.BALLR);

		//改变球的旋转矩阵
		//旋转时要求角度不为0且轴不能全为0
		if(Math.abs(tempAngle)!=0&&(Math.abs(rotateZ)!=0||Math.abs(rotateX)!=0))
		{

			float[] newMatrix=new float[16];
			Matrix.setRotateM(newMatrix, 0, tempAngle, rotateX, rotateY, rotateZ);
			// hhl 旋转轴是 (rotateX,rotateY,rotateZ)

			float[] resultMatrix=new float[16];// hhl 将新旋转矩阵 右乘 原来的旋转矩阵
			Matrix.multiplyMM(resultMatrix, 0, newMatrix, 0, selfRotateMatrix,0);
			selfRotateMatrix=resultMatrix;// 得到最终旋转矩阵
		}else{
			//Log.e(TAG,"Angle is Zero");
		}
		
		//************************旋转 end************************
	}	
}

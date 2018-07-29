package com.bn.Sample13_8;

import android.util.Log;

import static com.bn.Sample13_8.Constant.TAG;

/*
 * 控制球运动的线程
 */
public class BallGoThread extends Thread {
	BallForControl ballForControl;//声明AllBalls的引用
	int timeSpan=12;
	private boolean flag=false;//循环标志位
	
	public BallGoThread(BallForControl ballForControl)//构造器
	{
		this.ballForControl=ballForControl;//成员变量赋值
	}
	
	@Override
	public void run()//重写run方法
	{
		while(flag)//while循环
		{	
			ballForControl.go();//调用使所有球运动的方法
			//Log.d(TAG,"Thread go " + Thread.currentThread().getId());
			try{
				Thread.sleep(timeSpan);//一段时间后再运动
			}
			catch(Exception e){
				e.printStackTrace();//打印异常
			}
		}
		Log.w(TAG,"Thread Exit " + Thread.currentThread().getId() );
	}
	public void setFlag(boolean flag) {
		Log.w(TAG,"Thread Flag  " + flag );
		this.flag = flag;
	}
}

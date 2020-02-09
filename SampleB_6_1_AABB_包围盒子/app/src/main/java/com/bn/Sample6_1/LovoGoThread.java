package com.bn.Sample6_1;

import java.util.ArrayList;

public class LovoGoThread extends Thread{

	ArrayList<RigidBody> al;// 控制列表

	boolean flag = true;	// 线程控制标志位
	
	public LovoGoThread(ArrayList<RigidBody> al)
	{
		this.al = al;
	}

	public void run()
	{
		while (flag)
		{

			int size = al.size();

			for(int i=0;i<size;i++)
			{
				al.get(i).go(al); // 运动,计算新的位置,检查碰撞,更新速度
			}

			try {
				sleep(20);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}

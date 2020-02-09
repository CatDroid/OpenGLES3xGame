package com.bn.Sample6_2;

import java.util.ArrayList;

public class LovoGoThread extends Thread{

	ArrayList<RigidBody> al;//控制列表
	boolean flag=true;//线程控制标志位
	
	public LovoGoThread(ArrayList<RigidBody> al)
	{
		this.al=al;
	}

	public void run()
	{
		while(flag)
		{
			int size=al.size();
			for(int i=0;i<size;i++)
			{
				RigidBody ct=al.get(i);
				ct.go(al);
			}
			try
			{
				sleep(10);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}

package com.bn.thread;

import static com.bn.constant.SourceConstant.*;

public class Angle2DThread extends Thread{

    private  boolean isStop = false ;

	public Angle2DThread() {
		super("Angle2DThread");
	}

	public void run() 
	{
        boolean isST = false; // 递增或者递减  保证Angle2D角度范围在3~-3   3D可爱抓娃娃 标题 只是在左右摆动
		while(!isStop)
		{//循环定时移动炮弹
			try{
				if(!isST){
					Angle2D+=1;
					if(Angle2D>2){
						isST=true;
					}
				}
				if(isST){
					Angle2D-=1;
					if(Angle2D<-2){
						isST=false;
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			try
			{
				Thread.sleep(60);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}	
	}

	public void quitSync(){ // 避免开启不保留活动或者部分后台会surfaceDestroy手机 会导致线程过多,旋转越来越快

        isStop = true ;
        try {
            this.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package com.bn.Sample1_4;
public class UpdateThread extends Thread{
	
	MySurfaceView mv;
    int count=0;
    boolean isBallCube=true;//物体的状态，初始状态为球    
	public UpdateThread(MySurfaceView mv)
	{
		this.mv=mv;
	}

	public void run()
	{
		while(true)
		{
			//获取顶点坐标数据
			mv.mBallAndCube.calVertices(count,isBallCube);			
			try{
	            count++;
	            if(count%mv.mBallAndCube.span==0)
	            {
	            	count=0;
	            	isBallCube=!isBallCube;	          
	            }
	            Thread.sleep(40);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

}

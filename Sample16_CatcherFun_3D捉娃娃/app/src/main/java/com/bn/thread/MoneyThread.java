package com.bn.thread;
import com.bn.view.GameView;
import static com.bn.constant.SourceConstant.*;
public class MoneyThread extends Thread //监听键盘状态的线程
{
	GameView gv; 
	long currtime;
	long pretime;
	public MoneyThread(GameView mv)
	{
		this.gv=mv;
		pretime=System.currentTimeMillis();
        
	}
	public void run() 
	{
					
			try 
			{        			
					while(true)
					{
						currtime=System.currentTimeMillis();
						if(moneycount==20){
							pretime=currtime;
						}else if(moneycount<20&&(currtime-pretime>3000*60))
						{
							moneycount++;
							pretime=currtime;
						}
						if(moneycount>=1)
						{
							gv.ismoneyout=false;
						}
						Thread.sleep(20);
					}	
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
}
	
	

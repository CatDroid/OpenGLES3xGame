package com.bn.thread;

import javax.vecmath.Vector3f;

import com.bn.addRigidBody.Claw;
import com.bn.util.SliderHelper;
import com.bn.view.GameView;
import com.bulletphysics.linearmath.Transform;
public class KeyThread extends Thread //监听键盘状态的线程
{
	GameView gv; 
	public KeyThread(GameView mv)
	{
		this.gv=mv;
	}
	public void run() 
	{
		if(gv.isGrab)
		{        
			
			gv.isGrabOver=false;
			try 
			{		            			
    				gv.sliderhelper.slideUD(-1f);//下降
    			
					while(true)
					{
						if(Claw.bodyg[0].getMotionState().getWorldTransform(new Transform()).origin.y<2f)
						{
							break;
						}
						Thread.sleep(20);
					}
					gv.claw.motorFlag=false;
    				gv.claw.changeMotor();//闭合
    				
    				while(true)
    				{
    					if(Claw.hingeConstraint[1].getHingeAngle()<=-0.7f)
    					{
    						break;
    					}
    					Thread.sleep(20);
    				}
    				Thread.sleep(2000);
    				
    				gv.sliderhelper.slideUD(0.5f);//上升
    				while(true)
					{
						if(Claw.bodyg[0].getMotionState().getWorldTransform(new Transform()).origin.y>5.0f)
						{
							break;
						}
						Thread.sleep(20);
					}   
    				while(true)
					{
    					if(gv.xAngle<2&&gv.xAngle>-2)
    					{
    						gv.xAngle=0;
    						break;
    					}
    					GameView.calculateMainAndMirrorCamera(gv.xAngle);
    					
    					if(gv.xAngle<0)
    					{
    						gv.xAngle=gv.xAngle+180.0f/320;
    						
    					
    					}else if(gv.xAngle>0)
    					{
    						gv.xAngle=gv.xAngle-180.0f/320;
    						
    						
    					}
    					
						Thread.sleep(20);
					}
    				
    				while(true)
					{  
    					
    					gv.claw.moveBy(new Vector3f(0.015f,0,0));
						if(SliderHelper.cubeBody.getMotionState().getWorldTransform(new Transform()).origin.x>1.2f)
						{
							break;
						}
						Thread.sleep(20);
					}
    				while(true)
					{	
						gv.claw.moveBy(new Vector3f(0,0,0.015f));
						if(SliderHelper.cubeBody.getMotionState().getWorldTransform(new Transform()).origin.z>14.8f)
						{
							break;
						}
						Thread.sleep(20);
					}
    				Thread.sleep(2000);
    				gv.claw.motorFlag=true;
    				gv.claw.changeMotor();  
    				
    				
    				Thread.sleep(5000);
       				gv.isGrabOver=true;
    		    	if(gv.isGrabOver){
    		    		GameView.isdsMoney=false;
    		    	}
    				if(gv.isGrabOver)
    				{
    					gv.reData();
    					
    				}
    			
    				
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		
	}
	
	
		
}

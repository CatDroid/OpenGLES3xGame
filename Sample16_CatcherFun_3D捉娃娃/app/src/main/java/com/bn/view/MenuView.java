package com.bn.view;

import java.util.ArrayList;
import java.util.List;
import com.bn.MatrixState.MatrixState2D;
import com.bn.catcherFun.MainActivity;
import com.bn.catcherFun.MySurfaceView;
import com.bn.constant.Constant;
import com.bn.hand.R;
import com.bn.object.BN2DObject;
import com.bn.util.manager.ShaderManager;
import com.bn.util.manager.TextureManager;
import android.view.MotionEvent;
import static com.bn.constant.SourceConstant.*;
public class MenuView{
	MySurfaceView mv;
    public List<BN2DObject> menulist=new ArrayList<BN2DObject>();//存放BNObject对象
	float PreviousX;
	float PreviousY;
	int isSetYY=0;//背景音乐触控计数
	int isSetYX=0;
	public MenuView(MySurfaceView mv)
	{
		this.mv=mv;
		
		initView();
		
	}
	public void initView() 
	{		
		menulist.add(0,new BN2DObject(530, 910,700, 1400, TextureManager.getTextures("set.png"), 
				ShaderManager.getShader(2)));	
	    menulist.add(1,new BN2DObject(630, 750,120, 80, TextureManager.getTextures("off.png"), 
				ShaderManager.getShader(2)));
	   menulist.add(2,new BN2DObject(630, 970,120, 80, TextureManager.getTextures("off.png"), 
				ShaderManager.getShader(2)));
	}
	public boolean onTouchEvent(MotionEvent e) 
	{
		float x=Constant.fromRealScreenXToStandardScreenX(e.getX());//获取触控点的坐标
		float y=Constant.fromRealScreenYToStandardScreenY(e.getY());
		switch(e.getAction())
    	{
		
	    	case  MotionEvent.ACTION_MOVE:
				break;
	    	case MotionEvent.ACTION_UP:
	    		
	    		break;
	    	case MotionEvent.ACTION_DOWN:
	    		if(x>backyouxi_left&&x<backyouxi_right&&y>backyouxi_top&&y<backyouxi_bottom&&!isCollection)
	    		{
	    			if(!effictOff){
	    				MainActivity.sound.playMusic(SOUND_Click,0);
	    			}
	    			if(isSet)
	    			{
	    				 isSet=false;
	    				 mv.mainView.reSetData();
	    				 mv.currView=mv.mainView;
	    			}else
	    			{
		    			 mv.gameView.isMenu=false;
		    			 mv.currView=mv.gameView;
		    			 mv.gameView.reData();
		    			
	    			}
	    			
	    		}
	    		if(x>backmenu_left&&x<backmenu_right&&y>backmenu_top&&y<backmenu_bottom&&!isCollection)
	    		{
	    		  mv.gameView.isMenu=false;
	    		  isSet=false;
	    		  mv.mainView.reSetData();
	    		  mv.currView=mv.mainView;
	    		  
	    		  if(!effictOff){
	    			  MainActivity.sound.playMusic(SOUND_Click,0);
	    		  }
	    		  if(!isBGMusic){
		    		  if(!musicOff){
		    			  MainActivity.sound.playBackGroundMusic(mv.activity, R.raw.nogame);
		    		  }
	    		  }
	    		}
	    		if(x>yinxiao_left&&x<yinxiao_right&&y>yinxiao_top&&y<yinxiao_bottom)
	    		{
	    			if(!effictOff){
	    				MainActivity.sound.playMusic(SOUND_Click,0);
	    			}
	    			//这是背景音乐的触控
	    			yinxiaoIsOn=!yinxiaoIsOn;
	    			if(isSetYY%2==0){
	    				isBGMusic=true;
					      if(MainActivity.sound.mp!=null){
							     MainActivity.sound.mp.pause();
							     MainActivity.sound.mp=null;
						   }
	    			}else if(isSetYY%2==1){//创建音乐
	    				if(isSet){//如果为true则是在菜单界面，播放菜单界面音乐
	    					isBGMusic=false;
	    					if(!musicOff){
	    					   MainActivity.sound.playBackGroundMusic(mv.activity, R.raw.nogame);
	    					}	
	    				}
	    				if(mv.gameView.isMenu){//如果为false
	    					isBGMusic=false;
	    					if(!musicOff){
	    					  MainActivity.sound.playBackGroundMusic(mv.activity, R.raw.game);
	    					}
	    				}
					}
					isSetYY++;
					isSetYY=isSetYY%20;
	    		}
	    		
	    		if(x>yinyue_left&&x<yinyue_right&&y>yinyue_top&&y<yinyue_bottom)
	    		{
	    			yinyueIsOn=!yinyueIsOn;
	    			if(!effictOff){
	    				MainActivity.sound.playMusic(SOUND_Click,0);
	    			}
	    			if(isSetYX%2==0){
	    				effictOff=true;
	    			}else if(isSetYX%2==1){
	    				effictOff=false;
	    			}
	    			isSetYX++;
	    			isSetYX=isSetYX%20;
	    		}
	    		if(x>collection_left&&x<collection_right&&y>collection_top&&y<collection_bottom)
	    		{//奖品收藏按钮
	    			 if(!effictOff){
	    			 	 MainActivity.sound.playMusic(SOUND_Click,0);
	    			 }
	    			 isCollection=true;
	    			 mv.currView=mv.collectionview;
	    		
	    		}
	    		break;
    	}
		PreviousX=x;
		PreviousY=y;
		return true;
	}
	
	
	public void drawView() {
		MatrixState2D.pushMatrix();//保护现场  
	     menulist.get(0).drawSelf();      
   	if(!yinxiaoIsOn)
   	{
   		menulist.get(1).drawSelf();
   	}
   	if(!yinyueIsOn)
   	{
   		menulist.get(2).drawSelf();
   	}
	    MatrixState2D.popMatrix();//恢复现场
	}


}

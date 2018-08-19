package com.bn.view;

import javax.microedition.khronos.opengles.GL10;

import com.bn.catcherFun.MainActivity;
import com.bn.catcherFun.MySurfaceView;
import com.bn.constant.Constant;
import com.bn.object.BN2DObject;
import com.bn.util.manager.ShaderManager;
import android.opengl.GLES30;
import android.view.MotionEvent;
import static com.bn.constant.SourceConstant.*;
public class GameHelpView extends BNAbstractView{
	MySurfaceView mv;
	float PreviousX;
	float PreviousY;
	float wzx;
	boolean isadd=false;
	float dx=0;
	float dance=0;
	public GameHelpView(MySurfaceView mv)
	{
		this.mv=mv;
		initView();
	}
	@Override
	public void initView() 
	{
		YXJXView_Button.add(new BN2DObject(Pagex[0], Pagey, PageSizex, 
				PageSizey,Page0Id,ShaderManager.getShader(2)));//0
		YXJXView_Button.add(new BN2DObject(Pagex[1], Pagey, PageSizex, 
				PageSizey,Page1Id,ShaderManager.getShader(2)));//1
		YXJXView_Button.add(new BN2DObject(Pagex[2], Pagey, PageSizex, 
				PageSizey,Page2Id,ShaderManager.getShader(2)));//2
		YXJXView_Button.add(new BN2DObject(Pagex[3], Pagey, PageSizex, 
				PageSizey,Page3Id,ShaderManager.getShader(2)));//3
		YXJXView_Button.add(new BN2DObject(Pagex[4], Pagey, PageSizex, 
				PageSizey,Page4Id,ShaderManager.getShader(2)));//4
		
		YXJXView_Button.add(new BN2DObject(YXJXBackx, YXJXBacky, YXJXBackSizex, 
				YXJXBackSizey,AllbackId,ShaderManager.getShader(2)));//5
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) 
	{
		float x=Constant.fromRealScreenXToStandardScreenX(e.getX());//获取触控点的坐标
		float y=Constant.fromRealScreenYToStandardScreenY(e.getY());
		switch(e.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				 wzx=x;
				 if(x>YXJXBack_TOUCH_LEFT_x&&x<YXJXBack_TOUCH_RIGHT_x&&
							y>YXJXBack_TOUCH_TOP_y&&y<YXJXBack_TOUCH_BOTTOM_y){
					 mv.mainView.reSetData();
					 mv.currView=mv.mainView;
					 isYXJXTouch=false;
					 if(!effictOff){
				    		MainActivity.sound.playMusic(SOUND_Back,0);
				     }
				 }
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				dx=x-wzx;//用于计算摁下抬起的左右滑动的走向
				break;
		}
		PreviousX=x;
		PreviousY=y;
		return true;
	}
    public void cgangeHD()//这是帮助界面中图片移动的方法
    {
        if(dance<54){
			if(dx<0){//左滑	
	    		if(Pagex[4]-20f>540){
	    				for(int i=0;i<5;i++){
	    					YXJXView_Button.get(i).setX(Pagex[i]-20);
	    					Pagex[i]=Pagex[i]-20;
	    				}
	    		}
				
	    	}
	    	if(dx>0){
	    		if(Pagex[0]+20f<540){            	
	    			for(int i=0;i<5;i++){
						YXJXView_Button.get(i).setX(Pagex[i]+20);
						Pagex[i]=Pagex[i]+20;
					}
	    		}
	    	}
	    	dance++;
        }else{
        	dx=0;
        	dance=0;
        }
        
        
    }
	@Override
	public void drawView(GL10 gl) 
	{
		//设置屏幕背景色RGBA
        GLES30.glClearColor(0.0f,0.0f,0.0f, 1.0f);
		GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
		GLES30.glDisable(GLES30.GL_DEPTH_TEST); 
		for(int i=0;i<5;i++){
			if(dx!=0){
				cgangeHD();
			}
			YXJXView_Button.get(i).drawSelf();
		}
		YXJXView_Button.get(5).drawSelf();
		GLES30.glEnable(GLES30.GL_DEPTH_TEST); 
	}

}

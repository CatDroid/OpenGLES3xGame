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
public class GameAboutView extends BNAbstractView{
	MySurfaceView mv;
	public GameAboutView(MySurfaceView mv)
	{
		this.mv=mv;
		initView();
	}

	@Override
	public void initView() 
	{
		GameAboutView_Button.add(new BN2DObject(GameAboutTextx, GameAboutTexty, GameAboutText_SIZEx, 
				GameAboutText_SIZEy,GameAboutTextId,ShaderManager.getShader(2)));//这是游戏关于界面按钮按下10
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) 
	{
		float x=Constant.fromRealScreenXToStandardScreenX(e.getX());//获取触控点的坐标
		float y=Constant.fromRealScreenYToStandardScreenY(e.getY());
		switch(e.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				 if(x>YXJXBack_TOUCH_LEFT_x&&x<YXJXBack_TOUCH_RIGHT_x&&
							y>YXJXBack_TOUCH_TOP_y&&y<YXJXBack_TOUCH_BOTTOM_y){
					 if(!effictOff){
				    	 MainActivity.sound.playMusic(SOUND_Back,0);
				     }
					 mv.mainView.reSetData();
					 mv.currView=mv.mainView;
					 
				 }
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:

				break;
		}
		return true;
	}

	@Override
	public void drawView(GL10 gl) 
	{
		//设置屏幕背景色RGBA
        GLES30.glClearColor(0.0f,0.0f,0.0f, 1.0f);
		GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
		GLES30.glDisable(GLES30.GL_DEPTH_TEST); 
		GameAboutView_Button.get(0).drawSelf();
		MainView_Button.get(0).drawSelf();
		YXJXView_Button.get(5).drawSelf();
		GameAboutView_Button.get(0).drawSelf();
		GLES30.glEnable(GLES30.GL_DEPTH_TEST); 
	}

}

package com.bn.view;

import javax.microedition.khronos.opengles.GL10;

import com.bn.catcherFun.MainActivity;
import com.bn.catcherFun.MySurfaceView;
import com.bn.constant.Constant;
import com.bn.hand.R;
import com.bn.object.BN2DObject;
import com.bn.thread.Angle2DThread;
import com.bn.util.DrawNumber;
import com.bn.util.manager.ShaderManager;
import com.bn.util.manager.TextureManager;
import android.opengl.GLES30;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.Toast;
import static com.bn.constant.SourceConstant.*;
public class MainView extends BNAbstractView{

	MySurfaceView mv;
	DrawNumber score;
	float PreviousX;
	float PreviousY;
	boolean isStartGm=false;//这是开始游戏图片按钮标志位
	boolean isYXJX=false;//这是游戏教学图片按钮标志位
	boolean SCJP=false;//这是奖品收藏按钮的标志位
	boolean GameAbout=false;//这是游戏关于按钮的标志位
	boolean GameSD=false;//这是游戏设定按钮的标志位
	boolean GameScore=false;
	boolean quitgame=false;
	public static boolean isMainView=false;
	public static boolean Exit = false;
	private static boolean isExit = false;
	int dance=0;
	public MainView(MySurfaceView mv)
	{
		this.mv=mv;
		initView();
	}

	@Override
	public void initView() 
	{
		MainView_Button.add(new BN2DObject(MainView_BG_x, MainView_BG_y, MainView_BG_SIZE_x, 
				MainView_BG_SIZE_y,MainView_BGId,ShaderManager.getShader(2)));//这是背景图片0
		MainView_Button.add(new BN2DObject(StartGame_x, StartGame_y, StartGame_SIZE_x, 
				StartGame_SIZE_y,MainView_SGMId,ShaderManager.getShader(2)));//这是开始游戏图片1
		MainView_Button.add(new BN2DObject(StartGame_x, StartGame_y, StartGame_SIZE_x, 
				StartGame_SIZE_y,MainView_SGMDId,ShaderManager.getShader(2)));//这是开始游戏按下图片2
		MainView_Button.add(new BN2DObject(YXJX_x, YXJX_y, YXJX_SIZE_x, 
				YXJX_SIZE_y,MainView_YXJXId,ShaderManager.getShader(2)));//这是游戏教学图片3
		MainView_Button.add(new BN2DObject(YXJX_x, YXJX_y, YXJX_SIZE_x, 
				YXJX_SIZE_y,MainView_YXJXDId,ShaderManager.getShader(2)));//这是游戏教学按下图片4
		
		MainView_Button.add(new BN2DObject(MainViewSCx, MainViewSCy, MainViewSCSIZEx, 
				MainViewSCSIZEy,MainViewSCId,ShaderManager.getShader(2)));//这是奖品收藏按钮5
		MainView_Button.add(new BN2DObject(MainViewSCx, MainViewSCy, MainViewSCSIZEx, 
				MainViewSCSIZEy,MainViewSCDownId,ShaderManager.getShader(2)));//这是奖品收藏按钮按下6
		
		MainView_Button.add(new BN2DObject(GameAboutx, GameAbouty, GameAbout_SIZEx, 
				GameAbout_SIZEy,GameAboutId,ShaderManager.getShader(2)));//这是游戏关于界面按钮7
		MainView_Button.add(new BN2DObject(GameAboutx, GameAbouty, GameAbout_SIZEx, 
				GameAbout_SIZEy,GameAboutDownId,ShaderManager.getShader(2)));//这是游戏关于界面按钮按下8
		
		MainView_Button.add(new BN2DObject(GameSDx, GameSDy, GameSD_SIZEx, 
				GameSD_SIZEy,GameSDId,ShaderManager.getShader(2)));//这是游戏关于界面按钮9
		MainView_Button.add(new BN2DObject(GameSDx, GameSDy, GameSD_SIZEx, 
				GameSD_SIZEy,GameSDDownId,ShaderManager.getShader(2)));//这是游戏关于界面按钮按下10
		
		MainView_Button.add(new BN2DObject(GameScorex, GameScorey, GameScore_SIZEx, 
				GameScore_SIZEy,TextureManager.getTextures("button_score.png"),ShaderManager.getShader(2)));//这是游戏得分界面按钮11
		MainView_Button.add(new BN2DObject(GameScorex, GameScorey, GameScore_SIZEx, 
				GameScore_SIZEy,TextureManager.getTextures("button_score_Down.png"),ShaderManager.getShader(2)));//这是游戏得分界面按钮按下12
		
		MainView_Button.add(new BN2DObject(Gamequitx, Gamequity, Gamequit_SIZEx, 
				Gamequit_SIZEy,TextureManager.getTextures("button_quit.png"),ShaderManager.getShader(2)));//这是游戏退出界面按钮13
		MainView_Button.add(new BN2DObject(Gamequitx, Gamequity, Gamequit_SIZEx, 
				Gamequit_SIZEy,TextureManager.getTextures("button_quit_Down.png"),ShaderManager.getShader(2)));//这是游戏退出界面按钮按下14
	
		
		if(BackText!=null){
			Angle2DThread ad=new Angle2DThread();
			ad.start();
		}

		if(!isBGMusic){
			//创建音乐
			if(!musicOff){
				MainActivity.sound.playBackGroundMusic(mv.activity, R.raw.nogame);
			}
		}
		 score=new DrawNumber(mv);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) 
	{
		if(isSet)//这是游戏设定按钮的标志位
		{
			 return mv.menuview.onTouchEvent(e);
		}else{
			float x=Constant.fromRealScreenXToStandardScreenX(e.getX());//获取触控点的坐标
			float y=Constant.fromRealScreenYToStandardScreenY(e.getY());
			switch(e.getAction())
	    	{
	    	case  MotionEvent.ACTION_MOVE:
				break;
	    	case MotionEvent.ACTION_UP:
	    		if(x>StartGame_TOUCH_LEFT_x&&x<StartGame_TOUCH_RIGHT_x&&
						y>StartGame_TOUCH_TOP_y&&y<StartGame_TOUCH_BOTTOM_y&&!isSet&&isStartGm)
	    		{
	    			isStartGm=false;
	    			mv.currView=mv.gameView;
	    			mv.gameView.reData();
	    			//创建音乐
	    			if(!isBGMusic){
		    			if(!musicOff){
		    				MainActivity.sound.playBackGroundMusic(mv.activity, R.raw.game);
		    			}
	    			}
	    		}else if(x>YXJX_TOUCH_LEFT_x&&x<YXJX_TOUCH_RIGHT_x&&
						y>YXJX_TOUCH_TOP_y&&y<YXJX_TOUCH_BOTTOM_y&&!isSet&&isYXJX)
	    		{
	    			isYXJX=false;
	    			isYXJXTouch=true;
	    			mv.currView=mv.YXJXView;
	    		}else if(x>MainViewSC_TOUCH_LEFT_x&&x<MainViewSC_TOUCH_RIGHT_x&&//这是收藏奖品按钮的监听
						y>MainViewSC_TOUCH_TOP_y&&y<MainViewSC_TOUCH_BOTTOM_y&&!isSet&&SCJP)
	    		{
	    			SCJP=false;
	    			mv.currView=mv.collectionview;
	    		}else if(x>GameAbout_TOUCH_LEFT_x&&x<GameAbout_TOUCH_RIGHT_x&&//这是游戏关于按钮的监听
						y>GameAbout_TOUCH_TOP_y&&y<GameAbout_TOUCH_BOTTOM_y&&!isSet&&GameAbout)
	    		{
	    			GameAbout=false;
	    			mv.currView=mv.GameAboutView;
	    		}else if(x>GameSD_TOUCH_LEFT_x&&x<GameSD_TOUCH_RIGHT_x&&//这是游戏设定按钮的监听
						y>GameSD_TOUCH_TOP_y&&y<GameSD_TOUCH_BOTTOM_y&&!isSet&&GameSD)
	    		{
	    			GameSD=false;
	    			isSet=true;
	    			
	    		}else if(x>GameScore_TOUCH_LEFT_x&&x<GameScore_TOUCH_RIGHT_x&&
						y>GameScore_TOUCH_TOP_y&&y<GameScore_TOUCH_BOTTOM_y&&!isSet&&GameScore)
	    		{
	    			
	    			GameScore=false;
	    			mv.currView=mv.ScoreView;
	    			
	    		}else if(x>Gamequit_TOUCH_LEFT_x&&x<Gamequit_TOUCH_RIGHT_x&&
						y>Gamequit_TOUCH_TOP_y&&y<Gamequit_TOUCH_BOTTOM_y&&!isSet&&quitgame)
	    		{
	    			
	    			quitgame=false;
	    			exit();
	    			
	    		}
	    		break;
	    	case MotionEvent.ACTION_DOWN:
	    		if(x>StartGame_TOUCH_LEFT_x&&x<StartGame_TOUCH_RIGHT_x&&
						y>StartGame_TOUCH_TOP_y&&y<StartGame_TOUCH_BOTTOM_y&&!isSet)
	    		{
	    			isStartGm=true;
	    			if(!effictOff){
	    				MainActivity.sound.playMusic(SOUND_Click,0);
	    			}
	    		}else if(x>YXJX_TOUCH_LEFT_x&&x<YXJX_TOUCH_RIGHT_x&&
						y>YXJX_TOUCH_TOP_y&&y<YXJX_TOUCH_BOTTOM_y&&!isSet)
	    		{
	    			isYXJX=true;
	    			if(!effictOff){
	    				MainActivity.sound.playMusic(SOUND_Click,0);
	    			}
	    		}else if(x>MainViewSC_TOUCH_LEFT_x&&x<MainViewSC_TOUCH_RIGHT_x&&
						y>MainViewSC_TOUCH_TOP_y&&y<MainViewSC_TOUCH_BOTTOM_y&&!isSet)
	    		{
	    			SCJP=true;
	    			if(!effictOff){
	    				MainActivity.sound.playMusic(SOUND_Click,0);
	    			}
	    		}else if(x>GameAbout_TOUCH_LEFT_x&&x<GameAbout_TOUCH_RIGHT_x&&
						y>GameAbout_TOUCH_TOP_y&&y<GameAbout_TOUCH_BOTTOM_y&&!isSet)
	    		{
	    			GameAbout=true;
	    			if(!effictOff){
	    				MainActivity.sound.playMusic(SOUND_Click,0);
	    			}
	    		}else if(x>GameSD_TOUCH_LEFT_x&&x<GameSD_TOUCH_RIGHT_x&&
						y>GameSD_TOUCH_TOP_y&&y<GameSD_TOUCH_BOTTOM_y&&!isSet)
	    		{
	    			GameSD=true;
	    			if(!effictOff){
	    				MainActivity.sound.playMusic(SOUND_Click,0);
	    			}
	    		}else if(x>GameScore_TOUCH_LEFT_x&&x<GameScore_TOUCH_RIGHT_x&&
						y>GameScore_TOUCH_TOP_y&&y<GameScore_TOUCH_BOTTOM_y&&!isSet)
	    		{
	    			GameScore=true;
	    			if(!effictOff){
	    				MainActivity.sound.playMusic(SOUND_Click,0);
	    			}
	    		}else if(x>Gamequit_TOUCH_LEFT_x&&x<Gamequit_TOUCH_RIGHT_x&&
						y>Gamequit_TOUCH_TOP_y&&y<Gamequit_TOUCH_BOTTOM_y&&!isSet)
	    		{
	    			quitgame=true;
	    			
	    		}
	    		break;
	    	}
			PreviousX=x;
			PreviousY=y;
			return true;
		}
	}
	private void exit()
	{
		if (isExit == false) 
		{
			isExit = true; // 准备退出
			Toast.makeText(mv.getContext(),"再按一次退出游戏", Toast.LENGTH_SHORT).show();
			new Handler().postDelayed(new Runnable()
			{
				public void run()
				{
					isExit = false;
					isBGMusic=true;
					effictOff=true;
				}
			}, 2500);
		}else
		{
			android.os.Process.killProcess(android.os.Process.myPid()); 
		}
	}

	public void drawMoney()
	{
		initdatax=1450-dance*20;
		initdatay=1210;
		
		score.drawnumber(moneycount);
	}
	@Override
	public void drawView(GL10 gl) 
	{
		GLES30.glDisable(GLES30.GL_DEPTH_TEST); 
		MainView_Button.get(0).drawSelf();
		
		if(isStartGm){
			MainView_Button.get(2).setX(StartGame_x+400-dance*20);
			MainView_Button.get(2).drawSelf();
		}else{
			MainView_Button.get(1).setX(StartGame_x+400-dance*20);
			MainView_Button.get(1).drawSelf();
		}
		if(isYXJX){
			MainView_Button.get(4).setX(StartGame_x+400-dance*20);
			MainView_Button.get(4).drawSelf();
		}else{
			MainView_Button.get(3).setX(StartGame_x+400-dance*20);
			MainView_Button.get(3).drawSelf();
		}
		if(!SCJP){//这是奖品收藏按钮的绘制
			MainView_Button.get(5).setX(MainViewSCx+MainViewSCSIZEx-dance*20+6);
			MainView_Button.get(5).drawSelf();
		}else{
			MainView_Button.get(6).setX(MainViewSCx+MainViewSCSIZEx-dance*20+6);
			MainView_Button.get(6).drawSelf();
		}
		if(!GameSD){//这是游戏设定按钮的绘制
			MainView_Button.get(9).setX(GameSDx+GameSD_SIZEx-dance*20-14);
			MainView_Button.get(9).drawSelf();
		}else{
			MainView_Button.get(10).setX(GameSDx+GameSD_SIZEx-dance*20-14);
			MainView_Button.get(10).drawSelf();
		}
		
		
		if(!GameAbout){//这是游戏关于按钮的绘制
			MainView_Button.get(7).setX(GameAboutx-GameAbout_SIZEx+dance*20+12);
			MainView_Button.get(7).drawSelf();
		}else{
			MainView_Button.get(8).setX(GameAboutx-GameAbout_SIZEx+dance*20+12);
			MainView_Button.get(8).drawSelf();
		}
		
		if(!GameScore){//这是游戏defen按钮的绘制
			MainView_Button.get(11).setX(GameScorex-GameScore_SIZEx+dance*20+15);
			MainView_Button.get(11).drawSelf();
		}else{
			MainView_Button.get(12).setX(GameScorex-GameScore_SIZEx+dance*20+15);
			MainView_Button.get(12).drawSelf();
		}
		if(!quitgame){//这是游戏tuichu按钮的绘制
			MainView_Button.get(13).setX(Gamequitx-Gamequit_SIZEx+dance*20+14);
			MainView_Button.get(13).drawSelf();
		}else{
			MainView_Button.get(14).setX(Gamequitx-Gamequit_SIZEx+dance*20+14);
			MainView_Button.get(14).drawSelf();
		}
		BackText.drawSelf();
		isMainView=true;

		drawMoney();
		
		isMainView=false;
		if(isSet)
		{
			mv.menuview.drawView();
		}
	    GLES30.glEnable(GLES30.GL_BLEND);//打开混合
//	    //设置混合因子
     	GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA,GLES30.GL_ONE_MINUS_SRC_ALPHA);
     	for(int i=0;i<cpng.size();i++){
     		cpng.get(i).drawSelf();
     	}
	    GLES30.glDisable(GLES30.GL_BLEND);
	    dance++;
	    if(dance>20){
	    	dance=20;
	    }
		GLES30.glEnable(GLES30.GL_DEPTH_TEST); 
	}
	public void reSetData()
	{
		dance=0;
	}

}

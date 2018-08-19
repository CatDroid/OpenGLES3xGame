package com.bn.view;

import static com.bn.constant.SourceConstant.*;
import java.util.ArrayList;
import java.util.List;
import javax.microedition.khronos.opengles.GL10;

import com.bn.catcherFun.MySurfaceView;
import com.bn.object.BN2DObject;
import com.bn.special.SpecialUtil;
import com.bn.thread.SwitchThread;
import com.bn.util.CLpng;
import com.bn.util.LoadUtil;
import com.bn.util.manager.ShaderManager;
import com.bn.util.manager.TextureManager;
import android.opengl.GLES30;
import android.view.MotionEvent;
public class LoadView extends BNAbstractView{

	List<BN2DObject> load=new ArrayList<BN2DObject>();//存放BNObject对象
	List<BN2DObject> loadjm=new ArrayList<BN2DObject>();//存放BNObject对象
	MySurfaceView  mv;
	int initIndex=0;
	int index=0;
	public LoadView(MySurfaceView mv)
	{
		this.mv=mv;
		initView();
	}
	@Override
	public void initView() 
	{
		TextureManager.loadingTexture(this.mv, 0, 92);//加载图片资源
		for(int i=1;i<=5;i++)
		{
			for(int j=1;j<=5;j++)
			{
				load.add(new BN2DObject(540,1580,400,400,i,j,5,5,TextureManager.getTextures("load.png"),
						ShaderManager.getShader(2)));
			}
		}
		loadjm.add(new BN2DObject(540,1680,900,20,TextureManager.getTextures("lu1.png"),
				ShaderManager.getShader(2)));
		loadjm.add(new BN2DObject(540,1680,900,20,TextureManager.getTextures("lu.png"),
				ShaderManager.getShader(1)));
		BackText=new BN2DObject(BackTextx,BackTexty,BackText_Sizex,BackText_Sizey,TextureManager.getTextures("backText.png"),
				ShaderManager.getShader(2),2);
		
		cpng.add(new CLpng(340,460,500,500,TextureManager.getTextures("stars2.png"),ShaderManager.getShader(5)));
		cpng.add(new CLpng(340,660,500,500,TextureManager.getTextures("stars2.png"),ShaderManager.getShader(5)));
		cpng.add(new CLpng(780,460,500,500,TextureManager.getTextures("stars2.png"),ShaderManager.getShader(5)));
		cpng.add(new CLpng(780,660,500,500,TextureManager.getTextures("stars2.png"),ShaderManager.getShader(5)));
		
		if(cpng!=null){
			SwitchThread swtd=new SwitchThread();
			swtd.start();
		}

		
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) 
	{
		return false;
	}
	public void initBNView(int index)
	{
		switch(index)
		{
			case 0:
				break;
			case 2:bodyForDraws[0] = LoadUtil.loadFromFile("a.obj", mv.activity.getResources(), this.mv,ShaderManager.getShader(0));
				break;
		    case 5:bodyForDraws[1] = LoadUtil.loadFromFile("a.obj", mv.activity.getResources(), this.mv,ShaderManager.getShader(0));
			       bodyForDraws[2] = LoadUtil.loadFromFile("a.obj", mv.activity.getResources(), this.mv,ShaderManager.getShader(0));
			    break;	
			case 8:bodyForDraws[3] = LoadUtil.loadFromFile("a.obj", mv.activity.getResources(), this.mv,ShaderManager.getShader(0));
				   bodyForDraws[4] = LoadUtil.loadFromFile("gan.obj", mv.activity.getResources(), this.mv,ShaderManager.getShader(0));
				   bodyForDraws[5] = LoadUtil.loadFromFile("dun.obj", mv.activity.getResources(), this.mv,ShaderManager.getShader(0));
				   break;
			case 10:dollbox=LoadUtil.loadFromFile("dollbox.obj", mv.activity.getResources(), this.mv,ShaderManager.getShader(0));
					hole=LoadUtil.loadFromFile("hole.obj", mv.activity.getResources(), this.mv,ShaderManager.getShader(0));
					babe=LoadUtil.loadFromFile("babe.obj", mv.activity.getResources(), this.mv,ShaderManager.getShader(0));
					hb=LoadUtil.loadFromFile("HB.obj", mv.activity.getResources(), this.mv,ShaderManager.getShader(3));
				break;
			case 14: holebox=LoadUtil.loadFromFile("holebox.obj", mv.activity.getResources(), this.mv,ShaderManager.getShader(0));
					 niu=LoadUtil.loadFromFile("niu.obj", mv.activity.getResources(), this.mv,ShaderManager.getShader(0));
					 tvmodle=LoadUtil.loadFromFile("tv.obj", mv.activity.getResources(), this.mv,ShaderManager.getShader(0));
					 ParrotMd=LoadUtil.loadFromFile("parrot.obj", mv.activity.getResources(), this.mv,ShaderManager.getShader(0));
					 break;
			case 17:doll0=LoadUtil.loadFromFile("doll0.obj", mv.activity.getResources(), this.mv,ShaderManager.getShader(0));
					doll1=LoadUtil.loadFromFile("doll1.obj", mv.activity.getResources(), this.mv,ShaderManager.getShader(0));
					doll2=LoadUtil.loadFromFile("doll2.obj", mv.activity.getResources(), this.mv,ShaderManager.getShader(0));
					ganbox=LoadUtil.loadFromFile("ganbox.obj", mv.activity.getResources(), this.mv,ShaderManager.getShader(0));
					break;
			case 20:floorTextureId=TextureManager.getTextures("floor.jpg");
					clawId=TextureManager.getTextures("claw.png");
					ganId=TextureManager.getTextures("gan.png");
					dunId=TextureManager.getTextures("dunId.png");
					doll0Id=TextureManager.getTextures("doll0.png");
					doll1Id=TextureManager.getTextures("doll1.png");
				break;
			case 25:doll2Id=TextureManager.getTextures("doll2.png");
					holeId=TextureManager.getTextures("hole.png");
					dollboxId=TextureManager.getTextures("dollbox.png");
					babeId=TextureManager.getTextures("babe.png");
					holeboxId=TextureManager.getTextures("holebox.png");
					ganboxId=TextureManager.getTextures("ganbox.png");
				break;
			case 28:niuId=TextureManager.getTextures("niu.png");
					tvId=TextureManager.getTextures("tv.png");
					RigidBodyId=TextureManager.getTextures("f6.png");
				break;
			case 31:
				    MainView_BGId=TextureManager.getTextures("MainView_Background.png");
				    MainView_SGMId=TextureManager.getTextures("Button_Start.png");
				    MainView_SGMDId=TextureManager.getTextures("Button_StartDown.png");
				    MainView_YXJXId=TextureManager.getTextures("Button_Tutorail.png");
				    MainView_YXJXDId=TextureManager.getTextures("Button_TutorailDown.png");
				    
				break;
			case 34:
				    Box1Id=TextureManager.getTextures("Box1.png");
				    Box2Id=TextureManager.getTextures("Box2.png");
					MGstartId=TextureManager.getTextures("MainGame_start.png");
					MGstartDownId=TextureManager.getTextures("MainGame_startDown.png");
					MoneyBoxId=TextureManager.getTextures("Tex_Money.png");
					MoneyId=TextureManager.getTextures("Tex_Money.png");
				break;
		    case 36:
		    	    HBId=TextureManager.getTextures("HB.png");
				    shuaxinId=TextureManager.getTextures("shuaxin.png");
				    shuaxinDownId=TextureManager.getTextures("shuaxin_Down.png");
				    CameraId=TextureManager.getTextures("camera.png");
				    Camera=LoadUtil.loadFromFile("camera.obj", mv.activity.getResources(), this.mv,ShaderManager.getShader(0));
				    robotId=TextureManager.getTextures("robot.png");
				    RobotMD=LoadUtil.loadFromFile("robot.obj", mv.activity.getResources(), this.mv,ShaderManager.getShader(0));
				    CarId=TextureManager.getTextures("car.png");
				    CarMD=LoadUtil.loadFromFile("car.obj", mv.activity.getResources(), this.mv,ShaderManager.getShader(0));
				    parrotId=TextureManager.getTextures("parrot.png");
//				    spngId=TextureManager.getTextures("stars2.png");
				    jb=LoadUtil.loadFromFile("MyGoldCoin.obj", mv.activity.getResources(), this.mv,ShaderManager.getShader(0));
			        jbId=TextureManager.getTextures("jb.png");
				    break;
		    case 38:
			    	Page0Id=TextureManager.getTextures("page0.png");
			    	Page1Id=TextureManager.getTextures("page1.png");
			    	Page2Id=TextureManager.getTextures("page2.png");
			    	Page3Id=TextureManager.getTextures("page3.png");
			    	Page4Id=TextureManager.getTextures("page4.png");
			    	AllbackId=TextureManager.getTextures("button_back.png");
			    	GameAboutTextId=TextureManager.getTextures("aboutText.png");
			    	GameAboutId=TextureManager.getTextures("Game_About.png");
			    	
				    MainViewSCId=TextureManager.getTextures("config_collections.png");
				    MainViewSCDownId=TextureManager.getTextures("config_collectionsDown.png");
				    
				    GameAboutId=TextureManager.getTextures("Game_About.png");
				    GameAboutDownId=TextureManager.getTextures("Game_AboutDown.png");
				    
				    GameSDId=TextureManager.getTextures("Button_Config.png");
				    GameSDDownId=TextureManager.getTextures("Button_ConfigDown.png");
		    	break;
			    
			case 39:
				    Special=new SpecialUtil();
				    mv.collectionview=new CollectionView(this.mv);
				    mv.gameView=new GameView(this.mv);
				    mv.menuview=new MenuView(this.mv);
					mv.mainView=new MainView(this.mv);
					mv.YXJXView=new GameHelpView(this.mv);
					mv.ScoreView=new ScoreView(this.mv);
					mv.GameAboutView=new GameAboutView(this.mv);
				
				break;	
			case 40:
				mv.isInitOver = true;
				mv.currView=mv.mainView;
				reSetData();
				break;
		
		}
	}
	@Override
	public void drawView(GL10 gl) 
	{
		GLES30.glDisable(GLES30.GL_DEPTH_TEST); 
		for(int j=0;j<loadjm.size()-1;j++){
			loadjm.get(j).drawSelf();
		}

		BackText.drawSelf();
		if(!mv.isInitOver)
		{
			if(index>=load.size())
			{
				index=0;
			}//initIndex*2
			load.get(index).setX(90+initIndex*20f);
			load.get(index).drawSelf();
			loadPosition=90+initIndex*20f;
			loadjm.get(1).drawSelf();
			index++;			
			
			//初始化界面资源
			initBNView(initIndex);
			if(initIndex<41)
			{
				initIndex++;//图片索引加1
			}
		}
	    GLES30.glEnable(GLES30.GL_BLEND);//打开混合
//	    //设置混合因子
     	GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA,GLES30.GL_ONE_MINUS_SRC_ALPHA);
     	for(int i=0;i<cpng.size();i++){
     		cpng.get(i).drawSelf();
     	}
	    GLES30.glDisable(GLES30.GL_BLEND);
		GLES30.glEnable(GLES30.GL_DEPTH_TEST); 
	}
	public void reSetData()
	{
		this.initIndex=0;
		this.index=0;
		mv.isInitOver = false;
	}

}

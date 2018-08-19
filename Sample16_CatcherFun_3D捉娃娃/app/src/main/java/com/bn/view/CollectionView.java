package com.bn.view;

import java.util.ArrayList;
import java.util.List;
import javax.microedition.khronos.opengles.GL10;
import com.bn.MatrixState.MatrixState2D;
import com.bn.MatrixState.MatrixState3D;
import com.bn.catcherFun.MainActivity;
import com.bn.catcherFun.MySurfaceView;
import com.bn.constant.Constant;
import com.bn.object.BN2DObject;
import com.bn.object.LoadedObjectVertexNormalTexture;
import com.bn.util.DrawLine;
import com.bn.util.DrawNumber;
import com.bn.util.manager.ShaderManager;
import com.bn.util.manager.TextureManager;
import android.opengl.GLES30;
import android.view.MotionEvent;
import static com.bn.constant.SourceConstant.*;
public class CollectionView extends BNAbstractView{
	MySurfaceView mv;//场景管理器引用
	SellView saleview;//出售界面引用
	DrawNumber score;//绘制分数对象引用
	DrawLine drawline;//绘制黄色亮线引用
	float PreviousX;//上一次触控位置X坐标
	float PreviousY;//上一次触控位置Y坐标
	static float angle=-90;//娃娃起始旋转角度
	static int dt=1;//娃娃旋转速度
	public static float ObjX=-5.5f;//娃娃起始位置X坐标
	public static float ObjY=11f;//娃娃起始位置Y坐标
	
	public static List<BN2DObject> numberlist=new ArrayList<BN2DObject>();//存放数字对象
	
	public static List<LoadedObjectVertexNormalTexture> dollobj=new ArrayList<LoadedObjectVertexNormalTexture>();//存放娃娃obi对象
	public static List<Integer> textureId=new ArrayList<Integer>();//娃娃纹理ID集合
	public static List<BN2DObject> backgroundlist=new ArrayList<BN2DObject>();//收藏界面背景图结合
	public static List<Float> objscale=new ArrayList<Float>();//娃娃调整大小比例集合
	public static float[][] numberlocationdata=new float[9][2];//娃娃数量数字绘制位置集合
	public static List<String> numberaward=new ArrayList<String>();//娃娃是否连成线标志集合
	int spanx=380;//绘制数字图片X间隔
	int spany=500;//绘制数字图片Y间隔
	public  static boolean[] isOntouch=new boolean[9];//是否触碰到娃娃标志位集合
	public static boolean islock=true;//中间娃娃是否处于被锁状态标志位
	public static boolean isSale=false;//是否需要出售标志位
	public CollectionView(MySurfaceView mv)
	{
		this.mv=mv;
		initView();//初始化界面的方法
		
	}
	public void initView() 
	{		
		initbackgroundlist();//加载界面背景图的方法
		initnumberlist();//加载数字图片的方法
		initnumberLocationData();//初始化数字位置的方法
		initDoll();//初始化娃娃相关参数的方法
	
		score=new DrawNumber(mv);//创建绘制数字对象
		drawline=new DrawLine(mv);//创建绘制黄色亮线的对象
		saleview=new SellView(mv);	//创建出售界面对象
		
	}
	public void initnumberLocationData()
	{
        for(int i=0;i<9;i++)//循环计算娃娃数量数字的位置
        { 
        	numberlocationdata[i][0]=160+(i%3)*spanx;//X坐标
        	numberlocationdata[i][1]=600+(i/3)*spany;//Y坐标
        }
		
	}
	public void initbackgroundlist()//加载界面背景图的方法
	{
		backgroundlist.add(0,new BN2DObject(540, 960, 1080, 
				1920, TextureManager.getTextures("background.png"), 
				ShaderManager.getShader(2)));
		backgroundlist.add(1,new BN2DObject(390, 460, 420, 
				110, TextureManager.getTextures("hengtiao.png"), 
				ShaderManager.getShader(2)));
		backgroundlist.add(2,new BN2DObject(775, 460, 420, 
				110, TextureManager.getTextures("hengtiao.png"), 
				ShaderManager.getShader(2)));
		backgroundlist.add(3,new BN2DObject(395, 970,420, 
				110, TextureManager.getTextures("hengtiao.png"), 
				ShaderManager.getShader(2)));
		backgroundlist.add(4,new BN2DObject(780, 970, 420, 
				110, TextureManager.getTextures("hengtiao.png"), 
				ShaderManager.getShader(2)));
		backgroundlist.add(5,new BN2DObject(390,1465, 420, 
				110, TextureManager.getTextures("hengtiao.png"), 
				ShaderManager.getShader(2)));
		backgroundlist.add(6,new BN2DObject(780,1465, 420, 
				110, TextureManager.getTextures("hengtiao.png"), 
				ShaderManager.getShader(2)));
		backgroundlist.add(7,new BN2DObject(195,700, 120, 
				500, TextureManager.getTextures("shutiao.png"), 
				ShaderManager.getShader(2)));
		backgroundlist.add(8,new BN2DObject(560,715, 120, 
				500, TextureManager.getTextures("shutiao.png"), 
				ShaderManager.getShader(2)));
		backgroundlist.add(9,new BN2DObject(935,705, 120, 
				500, TextureManager.getTextures("shutiao.png"), 
				ShaderManager.getShader(2)));
		backgroundlist.add(10,new BN2DObject(195,1195, 120, 
				500, TextureManager.getTextures("shutiao.png"), 
				ShaderManager.getShader(2)));
		backgroundlist.add(11,new BN2DObject(555,1195, 120, 
				500, TextureManager.getTextures("shutiao.png"), 
				ShaderManager.getShader(2)));
		backgroundlist.add(12,new BN2DObject(935,1195, 120, 
				500, TextureManager.getTextures("shutiao.png"), 
				ShaderManager.getShader(2)));
		
		backgroundlist.add(13,new BN2DObject(100,200, 150, 
				150, TextureManager.getTextures("Tex_Money.png"), 
				ShaderManager.getShader(2)));
		
		backgroundlist.add(14,new BN2DObject(132,1750,150, 
				150, TextureManager.getTextures("back.png"), 
				ShaderManager.getShader(2)));
		backgroundlist.add(15,new BN2DObject(560,900,180, 
				200, TextureManager.getTextures("lock.png"), 
				ShaderManager.getShader(2)));
	}
	public void initnumberlist()//加载数字图片的方法
	{
		numberlist.add(0,new BN2DObject(0, 0,80, 80, TextureManager.getTextures("0.png"), 
				ShaderManager.getShader(2)));
		numberlist.add(1,new BN2DObject(0, 0,80, 80, TextureManager.getTextures("1.png"), 
				ShaderManager.getShader(2)));
		numberlist.add(2,new BN2DObject(0, 0,80, 80, TextureManager.getTextures("2.png"), 
				ShaderManager.getShader(2)));
		numberlist.add(3,new BN2DObject(0, 0,80, 80, TextureManager.getTextures("3.png"), 
				ShaderManager.getShader(2)));
		numberlist.add(4,new BN2DObject(0, 0,80, 80, TextureManager.getTextures("4.png"), 
				ShaderManager.getShader(2)));
		numberlist.add(5,new BN2DObject(0, 0,80, 80, TextureManager.getTextures("5.png"), 
				ShaderManager.getShader(2)));
		numberlist.add(6,new BN2DObject(0, 0,80, 80, TextureManager.getTextures("6.png"), 
				ShaderManager.getShader(2)));
		numberlist.add(7,new BN2DObject(0, 0,80, 80, TextureManager.getTextures("7.png"), 
				ShaderManager.getShader(2)));
		numberlist.add(8,new BN2DObject(0,0,80, 80, TextureManager.getTextures("8.png"), 
				ShaderManager.getShader(2)));
		numberlist.add(9,new BN2DObject(0, 0,80, 80, TextureManager.getTextures("9.png"), 
				ShaderManager.getShader(2)));
		numberlist.add(10,new BN2DObject(0, 0,60, 60, TextureManager.getTextures("x.png"), 
				ShaderManager.getShader(2)));
		numberlist.add(11,new BN2DObject(0, 0,80, 80, TextureManager.getTextures("%.png"), 
				ShaderManager.getShader(2)));
		
	}
	public void drawcount()//根据娃娃数量位置绘制娃娃数量
	{
		for(int i=0;i<dollcount.length;i++)
		{
			initdatax=numberlocationdata[i][0];
			initdatay=numberlocationdata[i][1];
		    score.drawnumber(dollcount[i]);
		}
		initdatax=200;
		initdatay=200;
		score.drawnumber(moneycount);
	}
	public void initDoll()//初始化娃娃的相关参数
	{
		dollobj.add(0,niu);textureId.add(niuId);objscale.add(2f);numberaward.add("012");
		dollobj.add(1,doll0);textureId.add(doll0Id);objscale.add(2f);numberaward.add("345");
		dollobj.add(2,doll2);textureId.add(doll2Id);objscale.add(2f);numberaward.add("678");
		dollobj.add(3,ParrotMd);textureId.add(parrotId);objscale.add(8f);numberaward.add("036");
		dollobj.add(4,RobotMD);textureId.add(robotId);objscale.add(9f);numberaward.add("147");
		dollobj.add(5,CarMD);textureId.add(CarId);objscale.add(8f);numberaward.add("258");
		dollobj.add(6,tvmodle);textureId.add(tvId);objscale.add(2f);
		dollobj.add(7,doll1);textureId.add(doll1Id);objscale.add(2f);
		dollobj.add(8,Camera);textureId.add(CameraId);objscale.add(7.5f);
		
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
	    	    if(x>back_left&&x<back_right&&y>back_top&&y<back_bottom&&!isSale)
		     	{//点击返回按钮
	    			 if(!effictOff){
	    				 MainActivity.sound.playMusic(SOUND_Back,0);//播放背景音乐
	    			 }
		    		  if(isCollection)
		    		  {
		     			isCollection=false;
		     			mv.currView=mv.gameView;//返回游戏界面
		     			mv.gameView.isMenu=false;
		     			mv.gameView.reData();
		    		  }else
		    		  {
		    			  mv.mainView.reSetData();
		    			  mv.currView=mv.mainView;//返回主界面
		    		  }
		    		   
		    		  if(isSet)
		    		  {
		    			  isSet=false;
		    			  mv.currView=mv.mainView;
		    		  }
		     	}
	    	    for(int i=0;i<9;i++)
	    	    {
	    	    	if(x>numberlocationdata[i][0]-90&&x<numberlocationdata[i][0]+140
	    	    			&&y>numberlocationdata[i][1]-300&&y<numberlocationdata[i][1]-60&&!isSale)
	    	    	{//点击娃娃进入出售界面
	    	    		if(i==4&&islock)
	    	    		{
	    	    			break;
	    	    		}else
	    	    		{
		    	    		isOntouch[i]=true;
		    	    		isSale=true;
	    	    		}
	    	    		break;
	    	    	}
	    	    }
	    	   
	    		break;
    	}
		PreviousX=x;
		PreviousY=y;
		 
			 if(isSale)
			 {
				 return saleview.onTouchEvent(e);
			 }
		
		return true;
	}
	public static void CalculateAward()//计算奖励
	{
		
		
		List<String> removeAward=new ArrayList<String>();
		for(String a:numberaward)
		{
		  if(dollcount[a.charAt(0)-'0']!=0&&dollcount[a.charAt(1)-'0']!=0&&dollcount[a.charAt(2)-'0']!=0)
		  {
			moneycount=moneycount+3;
			removeAward.add(a);
		  }
		}
		for(int i=0;i<removeAward.size();i++)
		{
			
			numberaward.remove(removeAward.get(i));
		}
		if(numberaward.size()==2)
		{
		  
			  islock=false;
		}
		
	}
	public void drawdollObjAndCount()//绘制娃娃及其数量的方法
	{
		angle=angle+dt;
		if(angle>0)
		{
			dt=-1;
		}else if(angle<-180)
		{
			dt=1;
		}
       
		for(int i=0;i<dollcount.length;i++)
		{		    
	          if(dollcount[i]!=0&&!isOntouch[i])
	          { 
	        	    MatrixState3D.pushMatrix();
	        	    if(i==4)
	  	  			{
	        	    	MatrixState3D.translate(ObjX+(i%3)*5.8f, ObjY-(i/3)*6.5f, 0);
	  	  			}else if(i==2)
	  	  			{
	  	  			 MatrixState3D.translate(ObjX+(i%3)*5.8f, 12, 0);
	  	  			}else
	  	  			{
	  	  			   MatrixState3D.translate(ObjX+(i%3)*5.8f, ObjY-(i/3)*7.8f, 0);
	  	  			}
	  	  			MatrixState3D.rotate(angle, 0, 1, 0);
	  	  			MatrixState3D.scale(objscale.get(i),objscale.get(i),objscale.get(i));
	  	  			dollobj.get(i).drawSelf(textureId.get(i));
	  	  			MatrixState3D.popMatrix();
	          }else if(!isOntouch[i])
	          {
	        	   MatrixState3D.pushMatrix();
	        	   if(i==4)
	  	  			{
	        	    	MatrixState3D.translate(ObjX+(i%3)*5.8f, ObjY-(i/3)*6.5f, 0);
	  	  			}else if(i==2)
	  	  			{
	  	  			 MatrixState3D.translate(ObjX+(i%3)*5.8f, 12, 0);
	  	  			}else
	  	  			{
	  	  			   MatrixState3D.translate(ObjX+(i%3)*5.8f, ObjY-(i/3)*7.8f, 0);
	  	  			}
	  	  			MatrixState3D.rotate(angle, 0, 1, 0);
	  	  		    MatrixState3D.scale(objscale.get(i),objscale.get(i),objscale.get(i));
	  	  			dollobj.get(i).drawSelf(0);
	  	  			MatrixState3D.popMatrix();
	          }
	     }
		
	}
	@Override
	public void drawView(GL10 gl) {
		GLES30.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);   
		
		  MatrixState3D.setCamera( 
	         		0,   //人眼位置的X
	         		4, 	//人眼位置的Y
	         		22,   //人眼位置的Z
	         		0, 	//人眼球看的点X
	         		4,   //人眼球看的点Y
	         		14,   //人眼球看的点Z
	         		0, 
	         		1, 
	         		0);
	
		GLES30.glDisable(GLES30.GL_DEPTH_TEST); 		
		backgroundlist.get(0).drawSelf();	
		backgroundlist.get(13).drawSelf();	
		backgroundlist.get(14).drawSelf();
		drawline.drawSelf();
		drawcount();
		
		GLES30.glEnable(GLES30.GL_DEPTH_TEST); 
		drawdollObjAndCount();
		GLES30.glDisable(GLES30.GL_DEPTH_TEST); 	
		if(islock)
		{
		 MatrixState2D.pushMatrix();
		 MatrixState2D.rotate(-20, 0,0, 1);
		 backgroundlist.get(15).drawSelf();
		 MatrixState2D.popMatrix();
		}
		GLES30.glEnable(GLES30.GL_DEPTH_TEST); 
		 if(isSale)
	     {
	    	 saleview.drawView(); 
	     }
	}


}

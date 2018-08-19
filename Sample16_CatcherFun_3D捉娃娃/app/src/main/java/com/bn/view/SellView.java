package com.bn.view;

import java.util.ArrayList;
import java.util.List;
import com.bn.MatrixState.MatrixState3D;
import com.bn.catcherFun.MainActivity;
import com.bn.catcherFun.MySurfaceView;
import com.bn.constant.Constant;
import com.bn.object.BN2DObject;
import com.bn.util.DrawNumber;
import com.bn.util.manager.ShaderManager;
import com.bn.util.manager.TextureManager;
import android.opengl.GLES30;
import android.view.MotionEvent;
import static com.bn.constant.SourceConstant.*;
public class SellView{
	MySurfaceView mv;
    DrawNumber score;
	float PreviousX;
	float PreviousY;
	float angle=0;
	int count;
	public int z=10;
	public int y=4;
	
	public static List<BN2DObject> background=new ArrayList<BN2DObject>();//存放BNObject对象
	public static List<BN2DObject> xinglist=new ArrayList<BN2DObject>();//存放BNObject对象
	public static List<Integer> jiazhilist=new ArrayList<Integer>();//存放BNObject对象
	public static List<Integer> xingcountlist=new ArrayList<Integer>();//存放BNObject对象
	public boolean isSell=false;
	public SellView(MySurfaceView mv)
	{
		this.mv=mv;
		
		initView();
	}
	public void initView() 
	{		
		background.add(0,new BN2DObject(540, 960, 1080, 
				1920, TextureManager.getTextures("salebackground.png"), 
				ShaderManager.getShader(2)));	
		background.add(1,new BN2DObject(132,1750,180, 
				180, TextureManager.getTextures("back.png"), 
				ShaderManager.getShader(2)));
		background.add(2,new BN2DObject(100,200, 150, 
				150, TextureManager.getTextures("Tex_Money.png"), 
				ShaderManager.getShader(2)));
//		background.add(3,new BN2DObject(480,1280, 150, 
//				150, TextureManager.getTextures("Tex_Money.png"), 
//				ShaderManager.getShader(2)));
		background.add(3,new BN2DObject(560,1500, 300, 
				200, TextureManager.getTextures("sell.png"), 
				ShaderManager.getShader(2)));
	
		xinglist.add(0,new BN2DObject(320,1100, 150, 
				150, TextureManager.getTextures("xing1.png"), 
				ShaderManager.getShader(2)));
		xinglist.add(1,new BN2DObject(500,1100, 150, 
				150, TextureManager.getTextures("xing1.png"), 
				ShaderManager.getShader(2)));
		xinglist.add(2,new BN2DObject(680,1100, 150, 
				150, TextureManager.getTextures("xing1.png"), 
				ShaderManager.getShader(2)));
		xinglist.add(3,new BN2DObject(860,1100, 150, 
				150, TextureManager.getTextures("xing1.png"), 
				ShaderManager.getShader(2)));
		xinglist.add(4,new BN2DObject(320,1100, 150, 
				150, TextureManager.getTextures("xing2.png"), 
				ShaderManager.getShader(2)));
		xinglist.add(5,new BN2DObject(500,1100, 150, 
				150, TextureManager.getTextures("xing2.png"), 
				ShaderManager.getShader(2)));
		xinglist.add(6,new BN2DObject(680,1100, 150, 
				150, TextureManager.getTextures("xing2.png"), 
				ShaderManager.getShader(2)));
		xinglist.add(7,new BN2DObject(860,1100, 150, 
				150, TextureManager.getTextures("xing2.png"), 
				ShaderManager.getShader(2)));
	
		score=new DrawNumber(mv);
		initlist();
	
		
	}
	public void initlist()
	{
		jiazhilist.add(1);
		jiazhilist.add(3);
		jiazhilist.add(2);
		jiazhilist.add(1);
		jiazhilist.add(4);
		jiazhilist.add(2);
		jiazhilist.add(2);
		jiazhilist.add(3);
		jiazhilist.add(1);
		
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
	    		if(x>sell_left&&x<sell_right&&y>sell_top&&y<sell_bottom)
	    		 {
		    		background.remove(3);
		    		background.add(3,new BN2DObject(560,1500, 300, 
		    				200, TextureManager.getTextures("sell.png"), 
		    				ShaderManager.getShader(2)));
		    	 }
	    		break;
	    	case MotionEvent.ACTION_DOWN:
	    		 if(x>sell_left&&x<sell_right&&y>sell_top&&y<sell_bottom)
	    		 {
		    		 if(!effictOff){
		    			MainActivity.sound.playMusic(SOUND_Click,0);
		    		 }
		    		  background.remove(3);
		    		  background.add(3,new BN2DObject(560,1500, 300, 
		    				200, TextureManager.getTextures("sell_down.png"), 
		    				ShaderManager.getShader(2)));
		    		  isSell=true;
		    			sell();
		    		  isSell=false;
	    		 }
	    	 if(x>back_left&&x<back_right&&y>back_top&&y<back_bottom)
	    	  {
    			 if(!effictOff){
    				 MainActivity.sound.playMusic(SOUND_Back,0);
    			 }
	    		 for(int i=0;i<9;i++)
		    	    {
		    	    	if(CollectionView.isOntouch[i])
		    	    	{
		    	    		CollectionView.isOntouch[i]=false;
		    	    		CollectionView.isSale=false;
		    	    		break;
		    	    	}
		    	    }
	    		
	    	  }
	    		
	    		break;
    	}
		PreviousX=x;
		PreviousY=y;
		return true;
	}

	public void drawdollObjAndCount()
	{
		angle=angle+2;
		for(int i=0;i<dollcount.length;i++)
		{	
			if(i==4||i==2)
			{
				y=6;
			}else
			{
				y=4;
			}
	     if(dollcount[i]!=0&&CollectionView.isOntouch[i])
       	  {
       		
       		    MatrixState3D.pushMatrix();
 	  			MatrixState3D.translate(0,y, z);
 	  			if(i==2||i==4)
 	  			{
 	  				MatrixState3D.rotate(angle, 0.17436f, 0.98480f,0f);
 	  			}else
 	  			{
 	  				MatrixState3D.rotate(angle, 0, 1, 0);
 	  			}
 	  			
	  			MatrixState3D.scale(CollectionView.objscale.get(i),CollectionView.objscale.get(i),CollectionView.objscale.get(i));
 	  			CollectionView.dollobj.get(i).drawSelf(CollectionView.textureId.get(i));
 	  			MatrixState3D.popMatrix();
 	  			
 	  			count=dollcount[i];
       		 
       	  }else if(CollectionView.isOntouch[i])
       	  {
       		    MatrixState3D.pushMatrix();
	  			MatrixState3D.translate(0,y,z);
	  			MatrixState3D.rotate(angle, 0, 1, 0);
	  			MatrixState3D.scale(CollectionView.objscale.get(i),CollectionView.objscale.get(i),CollectionView.objscale.get(i));
	  			CollectionView.dollobj.get(i).drawSelf(0);
	  			MatrixState3D.popMatrix();
	  			count=dollcount[i];
	  	   }
		}
		  MatrixState3D.pushMatrix();
			MatrixState3D.translate(0f,1.3f,z);
			MatrixState3D.rotate(angle, 0, 1, 0);
			MatrixState3D.scale(0.015f,0.015f,0.015f);
		    jb.drawSelf(jbId);
			MatrixState3D.popMatrix();
		
	}
	public void sell()
	{
		for(int i=0;i<dollcount.length;i++)
		{ 
			if(isSell&&dollcount[i]>1&&CollectionView.isOntouch[i])
			{
				moneycount=moneycount+jiazhilist.get(i);
				
				dollcount[i]=dollcount[i]-1;
				System.out.println("dollcount[i]:   "+dollcount[i]);
			}
			
		}
	}
	public void drawxing()
	{
		for(int i=0;i<4;i++)
		{
			xinglist.get(i).drawSelf();
		}
		for(int i=0;i<dollcount.length;i++)
		{ 
			if(CollectionView.isOntouch[i])
			{
				initdatax=620;
				initdatay=1280;
				score.drawnumber(jiazhilist.get(i));
				for(int j=0;j<jiazhilist.get(i);j++)
				{
					xinglist.get(j+4).drawSelf();
				}
			}
			
		}
		
	}
	public  void drawcount(int dollcount)
	{
		
		initdatax=900;
		initdatay=1700;
		score.drawnumber(dollcount);
		
		initdatax=200;
		initdatay=200;
		score.drawnumber(moneycount);
	}
	public void drawView() 
	{
		
		GLES30.glDisable(GLES30.GL_DEPTH_TEST); 	
		for(int i=0;i<background.size();i++)
		{
		 background.get(i).drawSelf();
		}
		drawxing();
		drawcount(count);
		GLES30.glEnable(GLES30.GL_DEPTH_TEST); 
		drawdollObjAndCount();
	}

}

package com.bn.view;

import java.util.ArrayList;
import java.util.List;
import com.bn.MatrixState.MatrixState2D;
import com.bn.MatrixState.MatrixState3D;
import com.bn.catcherFun.MySurfaceView;
import com.bn.constant.Constant;
import com.bn.object.BN2DObject;
import com.bn.util.manager.ShaderManager;
import com.bn.util.manager.TextureManager;
import android.view.MotionEvent;
public class CatchSucceedView{
	MySurfaceView mv;
    public List<BN2DObject> background=new ArrayList<BN2DObject>();//存放BNObject对象
	float PreviousX;
	float PreviousY;
	float angle=0;
	public CatchSucceedView(MySurfaceView mv)
	{
		this.mv=mv;
		initView();
		
	}
	public void initView() 
	{	
		
		background.add(0,new BN2DObject(540, 960, 1080, 
				1920, TextureManager.getTextures("salebackground.png"), 
				ShaderManager.getShader(5)));
		background.add(0,new BN2DObject(580, 660, 350, 
				200, TextureManager.getTextures("catchbackground.png"), 
				ShaderManager.getShader(2)));
		background.add(0,new BN2DObject(540, 1060, 1000, 
				1000, TextureManager.getTextures("showscore.png"), 
				ShaderManager.getShader(5),3));
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
	    		mv.gameView.isSuccess=false;
	    		angle=0;
	    		break;
    	}
		PreviousX=x;
		PreviousY=y;
		return true;
	}
	public void drawxing(int biaohao)
	{
		for(int i=0;i<4;i++)
		{
			SellView.xinglist.get(i).drawSelf(320+i*150,1500);
				
		}
		for(int j=0;j<SellView.jiazhilist.get(biaohao);j++)
		 {
			SellView.xinglist.get(j+4).drawSelf(320+j*150,1500);
		 }
	}
	public  void drawView(int biaohao) {
		//GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);   
		
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
		angle=angle+5;		
		MatrixState2D.pushMatrix();//保护现场  
		for(int j=0;j<background.size();j++){
			background.get(j).drawSelf();
		}
	    drawxing( biaohao);
		
	    MatrixState2D.popMatrix();//恢复现场
	
	    MatrixState3D.pushMatrix();
	   
	    MatrixState3D.translate(0,2.0f,5);
	    MatrixState3D.rotate(angle, 0, 1, 0);
	    //MatrixState3D.scale(2, 2, 2);
	    MatrixState3D.scale(CollectionView.objscale.get(biaohao),CollectionView.objscale.get(biaohao),CollectionView.objscale.get(biaohao));
	    CollectionView.dollobj.get(biaohao).drawSelf(CollectionView.textureId.get(biaohao));
	   // niu.drawSelf(niuId);
	    MatrixState3D.popMatrix();
	}


}

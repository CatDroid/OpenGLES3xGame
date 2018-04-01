package com.bn.Sample8_7;

/*
 * 绘制塔的类
 */
public class Tower {
	float scale;//塔的大小
	boolean texFlag;//是否绘制纹理的标志位
	
	TowerPart1 tower1;//塔的第一部分
	TowerPart2 tower2;//塔中圆柱部分
	TowerPart3 tower3;//
	
    float xAngle=0;//绕x轴旋转的角度
    float yAngle=0;//绕y轴旋转的角度
    float zAngle=0;//绕z轴旋转的角度
	
	
	Tower(MySurfaceView mv,float scale, int nCol ,int nRow){
		this.scale=scale;//大小赋值
		//创建对象
		tower1=new TowerPart1(mv,0.4f*scale,nCol,nRow);//穹顶的第一部分
		tower2=new TowerPart2(mv,0.35f*scale,nCol,nRow);//穹顶的第二部分
		tower3=new TowerPart3(mv,0.4f*scale,nCol,nRow);//穹顶的第二部分

	}
	public void drawSelf(int texId)
	{
   	 	MatrixState.rotate(xAngle, 1, 0, 0);
   	 	MatrixState.rotate(yAngle, 0, 1, 0);
   	 	MatrixState.rotate(zAngle, 0, 0, 1);
		
		//塔的第一部分——穹顶
		MatrixState.pushMatrix();
		MatrixState.translate(0f, 3.0f*scale, 0f);
        tower1.drawSelf(texId);//穹顶的第一部分
        MatrixState.popMatrix();
        
        //**************塔的第二部分——四根圆柱****************************************
        //四根圆柱
		MatrixState.pushMatrix();
		MatrixState.translate(0.62f*scale, 2.15f*scale, 0.62f*scale); // hhl 0.62 2.15 0.62 都是本来要移动的距离 由于缩放了 所以移动的距离也要同时缩放
        tower2.drawSelf(texId);//穹顶的第一部分
        MatrixState.popMatrix();
        
		MatrixState.pushMatrix();
		MatrixState.translate(-0.62f*scale, 2.15f*scale, 0.62f*scale);
        tower2.drawSelf(texId);//穹顶的第一部分
        MatrixState.popMatrix();
        
		MatrixState.pushMatrix();
		MatrixState.translate(0.62f*scale, 2.15f*scale, -0.62f*scale);
        tower2.drawSelf(texId);//穹顶的第一部分
        MatrixState.popMatrix();
        
		MatrixState.pushMatrix();
		MatrixState.translate(-0.62f*scale, 2.15f*scale, -0.62f*scale);
        tower2.drawSelf(texId);//穹顶的第一部分
        MatrixState.popMatrix();
      //**************塔的第二部分****************************************
        
    	//塔的第一部分
		MatrixState.pushMatrix();
		MatrixState.translate(0f, 0.7f*scale, 0f);
        tower3.drawSelf(texId);
        MatrixState.popMatrix();
        
		MatrixState.pushMatrix();
		MatrixState.translate(0f, -1.4f*scale, 0f);
        tower3.drawSelf(texId);
        MatrixState.popMatrix();
        
		MatrixState.pushMatrix();
		MatrixState.translate(0f, -3.55f*scale, 0f);
        tower3.drawSelf(texId);
        MatrixState.popMatrix();
	}

}

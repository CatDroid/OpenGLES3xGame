package com.bn.Sample8_7;

/*
 * 绘制穹顶的类
 */

public class Top {
	float scale;//穹顶的大小
	
	TopPart1 top1;//穹顶的第一部分
	TopPart2 top2;//穹顶的第二部分
	TopPart3 top3;//穹顶的第三部分
	TopPart4 top4;//穹顶的第四部分
	
	
    float xAngle=0;//绕x轴旋转的角度
    float yAngle=0;//绕y轴旋转的角度
    float zAngle=0;//绕z轴旋转的角度
	
	
	Top(MySurfaceView mv,float scale, int nCol ,int nRow)
	{
		this.scale=scale;//茶壶大小赋值
		
		//创建对象
		top1=new TopPart1(mv,0.4f*scale,nCol,nRow);//穹顶的第一部分
		top2=new TopPart2(mv,0.4f*scale,nCol,nRow);//穹顶的第二部分
		top3=new TopPart3(mv,0.8f*scale,nCol,nRow);//穹顶的第三部分
		top4=new TopPart4(mv,0.8f*scale,nCol,nRow);//穹顶的第四部分

	}
	public void drawSelf(int texId)
	{
		
   	 	MatrixState.rotate(xAngle, 1, 0, 0);
   	 	MatrixState.rotate(yAngle, 0, 1, 0);
   	 	MatrixState.rotate(zAngle, 0, 0, 1);
		
		
		//穹顶的第一部分
		MatrixState.pushMatrix();
		MatrixState.translate(0f, 4.0f*scale, 0f);
        top1.drawSelf(texId);//穹顶的第一部分
        MatrixState.popMatrix();
		//穹顶的第二部分
    	MatrixState.pushMatrix();
    	MatrixState.translate(0f, 3.7f*scale, 0f);
        top2.drawSelf(texId);//穹顶的第二部分
        MatrixState.popMatrix();
		//穹顶的第三部分
    	MatrixState.pushMatrix();
    	MatrixState.translate(0f, 0f*scale, 0f);
        top3.drawSelf(texId);
        MatrixState.popMatrix();
		//穹顶的第四部分
    	MatrixState.pushMatrix();
    	MatrixState.translate(0f, -1.9f*scale, 0f);
        top4.drawSelf(texId);
        MatrixState.popMatrix();
	}

}

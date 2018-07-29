package com.bn.Sample13_8;

public class CubeGroup {
	
	MySurfaceView mv;
	
	TextureRect textureRect;//底部立方体
	Cube sideCube1;//左右侧立方体
	Cube sideCube2;//前后侧立方体
	float size;//尺寸
	float a;
	float b;
	float c;
	float width;
	public CubeGroup(MySurfaceView mv,
			float scale, 		//比例
			float a, 			//	矩形平面长度
			float b, 			//墙的高度
			float c ,			//	矩形平面宽度
			float width			//墙的厚度
		){
		//创建各个组成部分的对象
		textureRect = new TextureRect(mv,scale,a,c);//底部的长方形 长和宽是 scale*a scale*c
		sideCube1 = new Cube(mv, scale, new float[]{c, 		   b, width});	// 长方体 width是高度/厚度都一样
		sideCube2 = new Cube(mv, scale, new float[]{a-2*width, b, width});	// 长方体 宽度都是b 但是长度有两个是c 两个是a-2*width
		// 初始化完成后再改变各量的值
		size = scale;
		a *= size; 
		b *= size;
		c *= size;
		width *= size;
		//初始化成员变量的值
		this.a = a;
		this.b = b;
		this.c = c;
		this.width = width;
	}
	public void drawSelf(int floorTexId,int wallTexId){
		//底部
        MatrixState.pushMatrix();
        MatrixState.rotate(-90, 1, 0, 0);
        textureRect.drawSelf(floorTexId);
        MatrixState.popMatrix();
		//左右侧
        MatrixState.pushMatrix();
        MatrixState.translate(-(a - width)/2,b/2, 0);
        MatrixState.rotate(90, 0, 1, 0);
        sideCube1.drawSelf(wallTexId);
        MatrixState.popMatrix();
        
        MatrixState.pushMatrix();
        MatrixState.translate((a - width)/2,b/2, 0);
        MatrixState.rotate(90, 0, 1, 0);
        sideCube1.drawSelf(wallTexId);
        MatrixState.popMatrix();
		//前后侧
        MatrixState.pushMatrix();
        MatrixState.translate(0,b/2, (c - width)/2);
        sideCube2.drawSelf(wallTexId);
        MatrixState.popMatrix();
        
        MatrixState.pushMatrix();
        MatrixState.translate(0,b/2, -(c - width)/2);
        sideCube2.drawSelf(wallTexId);
        MatrixState.popMatrix();
	}
}

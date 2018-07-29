package com.bn.Sample13_8;

class Cube
{
	MySurfaceView mv;
	TextureRect[] rect=new TextureRect[3]; // hhl: 只需要3个 每个对面的长方形都一样的

    private float a;	//立方体的长
    private float b;	//立方体的高
    private float c;	//立方体的宽（厚度）

    Cube(MySurfaceView mv,float scale,float[] abc)
	{
		a=abc[0];
		b=abc[1];
		c=abc[2];
		rect[0]=new TextureRect(mv,scale,a,b);
		rect[1]=new TextureRect(mv,scale,c,b);
		rect[2]=new TextureRect(mv,scale,a,c);

		// 初始化完成后再改变各量的值 hhl: 上面的TextureRect会把顶点scale e.g scale*a scale*b
		a*=scale;
		b*=scale;
		c*=scale;
	}

    void drawSelf(int ballTexId)
	{
        //前面
        MatrixState.pushMatrix();
        MatrixState.translate(0, 0, c/2);
		rect[0].drawSelf(ballTexId);
        MatrixState.popMatrix();
		//后面
        MatrixState.pushMatrix();
        MatrixState.translate(0, 0, -c/2);
		MatrixState.rotate(180.0f, 0, 1, 0);
		rect[0].drawSelf(ballTexId);
        MatrixState.popMatrix();
		//右面
        MatrixState.pushMatrix();
        MatrixState.translate(a/2, 0, 0);
		MatrixState.rotate(90.0f, 0, 1, 0);
		rect[1].drawSelf(ballTexId);
        MatrixState.popMatrix();
		//左面
        MatrixState.pushMatrix();
        MatrixState.translate(-a/2, 0, 0);
		MatrixState.rotate(-90.0f, 0, 1, 0);
		rect[1].drawSelf(ballTexId);
        MatrixState.popMatrix();
		//下面
        MatrixState.pushMatrix();
        MatrixState.translate(0, -b/2, 0);
		MatrixState.rotate(90.0f, 1, 0, 0);
		rect[2].drawSelf(ballTexId);
        MatrixState.popMatrix();
		//上面
        MatrixState.pushMatrix();
        MatrixState.translate(0, b/2, 0);
		MatrixState.rotate(-90.0f, 1, 0, 0);
		rect[2].drawSelf(ballTexId);
        MatrixState.popMatrix();
	}
}

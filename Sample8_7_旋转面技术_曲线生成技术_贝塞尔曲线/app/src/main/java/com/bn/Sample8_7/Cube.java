package com.bn.Sample8_7;

public class Cube 
{
	Texture[] rect=new Texture[6];
	float xAngle=0;//绕x轴旋转的角度
    float yAngle=0;//绕y轴旋转的角度
    float zAngle=0;//绕z轴旋转的角度
    float a;
    float b;
    float c;
    float scale;//尺寸

	boolean texFlag;//是否绘制纹理的标志位
	public Cube(MySurfaceView mv,float scale,float[] abc)
	{
		a=abc[0];
		b=abc[1];
		c=abc[2];
		rect[0]=new Texture(mv,scale,a,b,1,1);
		rect[1]=new Texture(mv,scale,a,b,1,1);
		rect[2]=new Texture(mv,scale,c,b,1,1);
		rect[3]=new Texture(mv,scale,c,b,1,1);
		rect[4]=new Texture(mv,scale,a,c,1,1);
		rect[5]=new Texture(mv,scale,a,c,1,1);
		// 初始化完成后再改变各量的值

		a*=scale;
		b*=scale;
		c*=scale;
	}
	public void drawSelf(int texId)
	{
		MatrixState.rotate(xAngle, 1, 0, 0);
		MatrixState.rotate(yAngle, 0, 1, 0);
		MatrixState.rotate(zAngle, 0, 0, 1);
        //前面
		MatrixState.pushMatrix();
		MatrixState.translate(0, 0, c/2);
		rect[0].drawSelf(texId);
        MatrixState.popMatrix();
		//后面
		MatrixState.pushMatrix();
		MatrixState.translate(0, 0, -c/2);
		MatrixState.rotate(180.0f, 0, 1, 0);
		rect[1].drawSelf(texId);
        MatrixState.popMatrix();
		//右面
		MatrixState.pushMatrix();
		MatrixState.translate(a/2, 0, 0);
		MatrixState.rotate(90.0f, 0, 1, 0);
		rect[2].drawSelf(texId);
        MatrixState.popMatrix();
		//左面
		MatrixState.pushMatrix();
		MatrixState.translate(-a/2, 0, 0);
		MatrixState.rotate(-90.0f, 0, 1, 0);
		rect[3].drawSelf(texId);
        MatrixState.popMatrix();
		//下面
		MatrixState.pushMatrix();
		MatrixState.translate(0, -b/2, 0);
		MatrixState.rotate(90.0f, 1, 0, 0);
		rect[4].drawSelf(texId);
        MatrixState.popMatrix();
		//上面
		MatrixState.pushMatrix();
		MatrixState.translate(0, b/2, 0);
		MatrixState.rotate(-90.0f, 1, 0, 0);
		rect[5].drawSelf(texId);
        MatrixState.popMatrix();
	}
}

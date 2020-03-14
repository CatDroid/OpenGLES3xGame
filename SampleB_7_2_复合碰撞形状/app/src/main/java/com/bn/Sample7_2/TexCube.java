package com.bn.Sample7_2;
import com.bulletphysics.dynamics.RigidBody;
public class TexCube 
{
	TextureRect tr;//用于绘制各个面的纹理矩形
	float halfSize;//立方体的半边长
	RigidBody body;//对应的刚体对象
	int mProgram;
	MySurfaceView mv;
	public TexCube(float halfSize,int mProgram)
	{		
		tr=new TextureRect(halfSize);//创建纹理矩形
		this.mProgram=mProgram;//保存着色器程序引用
		this.halfSize=halfSize;	//保存半长
	}
	public void drawSelf(int texId)
	{
		tr.intShader(mv, mProgram);//纹理矩形初始化着色器
		MatrixState.pushMatrix();//保护现场
		MatrixState.pushMatrix();//保护现场
	    MatrixState.translate(0, halfSize, 0);//执行平移
	    MatrixState.rotate(-90, 1, 0, 0);//执行旋转
	    tr.drawSelf( texId);//绘制上面
		MatrixState.popMatrix();//恢复现场
		MatrixState.pushMatrix();//保护现场
	    MatrixState.translate(0, -halfSize, 0);//执行平移
	    MatrixState.rotate(90, 1, 0, 0);//执行旋转
	    tr.drawSelf( texId);//绘制下面
		MatrixState.popMatrix();//恢复现场
		MatrixState.pushMatrix();//保护现场
	    MatrixState.translate(-halfSize, 0, 0);//执行平移
	    MatrixState.rotate(-90, 0, 1, 0);//执行旋转
	    tr.drawSelf( texId);//绘制左面
		MatrixState.popMatrix();//恢复现场
		MatrixState.pushMatrix();//保护现场
	    MatrixState.translate(halfSize, 0, 0);//执行平移
	    MatrixState.rotate(90, 0, 1, 0);//执行旋转
	    tr.drawSelf( texId);//绘制右面
		MatrixState.popMatrix();//恢复现场
		MatrixState.pushMatrix();//保护现场
		MatrixState.translate(0, 0, halfSize);//执行平移
	    tr.drawSelf(texId);//绘制前面
		MatrixState.popMatrix();//恢复现场
		MatrixState.pushMatrix();//保护现场
		MatrixState.translate(0, 0, -halfSize);//执行平移
		MatrixState.rotate(180, 0, 1, 0);//执行旋转
	    tr.drawSelf( texId);//绘制后面
		MatrixState.popMatrix();//恢复现场
		MatrixState.popMatrix();//恢复现场
	}
}

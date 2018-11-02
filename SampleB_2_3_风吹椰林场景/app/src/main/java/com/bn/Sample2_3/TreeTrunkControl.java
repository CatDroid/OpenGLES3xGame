package com.bn.Sample2_3;
//该类为树干的控制类
public class TreeTrunkControl 
{
	static int num=0;
	//标志
	int flag=0;
	//树的位置
	float positionX;
	float positionY;
	float positionZ;
	//树干的模型
	TreeTrunk treeTrunk;
	public TreeTrunkControl(float positionX,float positionY,float positionZ,TreeTrunk treeTrunk)
	{
		flag=num++;
		this.positionX=positionX;
		this.positionY=positionY;
		this.positionZ=positionZ;
		this.treeTrunk=treeTrunk;
	}
	public void drawSelf(int tex_treejointId,float bend_R,float wind_direction)
	{
		MatrixState.pushMatrix();
		MatrixState.translate(positionX, positionY, positionZ);//移动到指定的位置
		treeTrunk.drawSelf(tex_treejointId, bend_R, wind_direction);
		MatrixState.popMatrix();
	}
}

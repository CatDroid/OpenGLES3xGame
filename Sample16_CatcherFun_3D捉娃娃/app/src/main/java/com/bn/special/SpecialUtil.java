package com.bn.special;

import java.util.ArrayList;
import java.util.List;
import com.bn.MatrixState.MatrixState3D;
import com.bn.util.manager.ShaderManager;
import com.bn.util.manager.TextureManager;
import static com.bn.constant.SourceConstant.*;
public class SpecialUtil
{
	List<ParticleSystem> fps=new ArrayList<ParticleSystem>();//放粒子系统的列表
	ParticleForDraw[] fpfd;//雪花绘制的对象
	int count=0;
	int index=0;
	int index2=0;
	public SpecialUtil()
	{
		initSpecial();
	}
	public void initSpecial()
	{
		//总共有六种粒子系统
		count=ParticleDataConstant.START_COLOR.length;
		fpfd=new ParticleForDraw[count];//6组绘制着，6种颜色
		for(int i=0;i<count;i++)
		{
			ParticleDataConstant.CURR_INDEX=i;
			if(i==0)//火焰0
			{
				fpfd[i]=new ParticleForDraw(ParticleDataConstant.RADIS[ParticleDataConstant.CURR_INDEX],
						ShaderManager.getShader(4),TextureManager.getTextures("fire.png"));
				//创建对象,将雪花的初始位置传给构造器
				fps.add(new ParticleSystem(0,0,0,fpfd[i]));
			}else if(i>=1&&i<=3)//碰撞1
			{
				fpfd[i]=new ParticleForDraw(ParticleDataConstant.RADIS[ParticleDataConstant.CURR_INDEX],
						ShaderManager.getShader(4),TextureManager.getTextures("stars.png"));
				//创建对象,将雪花的初始位置传给构造器
				fps.add(new ParticleSystem(0,0,0,fpfd[i]));
			}else if(i>3)//烟花2
			{
				fpfd[i]=new ParticleForDraw(ParticleDataConstant.RADIS[ParticleDataConstant.CURR_INDEX],
						ShaderManager.getShader(4),TextureManager.getTextures("stars2.png"));
				//创建对象,将雪花的初始位置传给构造器
				fps.add(new ParticleSystem(0,0,0,fpfd[i]));
			}
		}
	}
	
	public void drawSpecial(int i)
	{ 
		if(i==5){
			//刷新功能的粒子系统
			MatrixState3D.pushMatrix();
			fps.get(5).positionX=0.0f;
			fps.get(5).positionY=0.0f;
			fps.get(5).positionZ=10.5f;
			SpecialBZ=5;
			fps.get(5).drawSelf();
			MatrixState3D.popMatrix();
		}else if(i==2){
			MatrixState3D.pushMatrix();
			fps.get(2).positionX=0.0f;
			fps.get(2).positionY=1.5f;
			fps.get(2).positionZ=10.5f;
			SpecialBZ=2;
			fps.get(2).drawSelf();
			MatrixState3D.popMatrix();
		}
	}
}

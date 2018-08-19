package com.bn.util;

import com.bn.MatrixState.MatrixState2D;
import com.bn.catcherFun.MySurfaceView;
import com.bn.object.BN2DObject;
import com.bn.view.CollectionView;
import com.bn.view.MainView;
import com.bn.view.ScoreView;

import static com.bn.constant.SourceConstant.*;
public class DrawNumber 
{
	MySurfaceView mv;
	BN2DObject[] numberReds=new BN2DObject[12];//创建数字数组
	public DrawNumber(MySurfaceView mv)
	{
		this.mv=mv;		
		//生成0-9十个数字的纹理矩形
		for(int i=0;i<12;i++)
		{
			numberReds[i]=CollectionView.numberlist.get(i);
		}
	}
	
	public void drawScore(int count)//绘制分数的方法
	{
		String str=count+"";//将int数值转化为字符串
		for(int i=0;i<str.length();i++)
		{
			char c=str.charAt(i);//获取字符串指定字符
			  MatrixState2D.pushMatrix();//保护现场
			  numberReds[c-'0'].drawSelf(initdatax+i*50-str.length()*20,initdatay);	//绘制指定数字
			  if(ScoreView.isPrecent)//若完成度标志位为true
			  {
				  numberReds[11].drawSelf(initdatax+str.length()*50-str.length()*20,initdatay);	//绘制百分号
				  ScoreView.isPrecent=false;//标志置反
			  }
			  MatrixState2D.popMatrix();//恢复现场
		}
	}
	
	public void drawnumber(int count)
	{					
			
		String str=count+"";
		for(int i=0;i<str.length();i++)
		{
			char c=str.charAt(i);
			  MatrixState2D.pushMatrix();
			 
			  if(MainView.isMainView)
			  {
				  MatrixState2D.scale(0.6f, 0.6f, 0.6f);
			  }
			  numberReds[10].drawSelf(initdatax,initdatay);	
			  numberReds[c-'0'].drawSelf(initdatax+(i+1)*50,initdatay);	
			  MatrixState2D.popMatrix();
		}
		
	}
}

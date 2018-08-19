package com.bn.util;
import com.bn.catcherFun.MySurfaceView;
import com.bn.object.BN2DObject;
import com.bn.view.CollectionView;
import static com.bn.constant.SourceConstant.*;
//表示移动步数的类
public class DrawLine 
{
	MySurfaceView mv;
	BN2DObject[] line=new BN2DObject[12];
	int[][] dollnumber=new int[12][2]; 
	public DrawLine(MySurfaceView mv)
	{
		this.mv=mv;		
		for(int i=0;i<12;i++)
		{
			line[i]=CollectionView.backgroundlist.get(i+1);
		}
		dollnumber[0][0]=0;dollnumber[0][1]=1;
		dollnumber[1][0]=1;dollnumber[1][1]=2;
		dollnumber[2][0]=3;dollnumber[2][1]=4;
		dollnumber[3][0]=4;dollnumber[3][1]=5;
		dollnumber[4][0]=6;dollnumber[4][1]=7;
		dollnumber[5][0]=7;dollnumber[5][1]=8;
		
		dollnumber[6][0]=0;dollnumber[6][1]=3;
		dollnumber[7][0]=1;dollnumber[7][1]=4;
		dollnumber[8][0]=2;dollnumber[8][1]=5;
		dollnumber[9][0]=3;dollnumber[9][1]=6;
		dollnumber[10][0]=4;dollnumber[10][1]=7;
		dollnumber[11][0]=5;dollnumber[11][1]=8;
	}
	
	public void drawSelf()
	{		
		
			for(int i=0;i<12;i++)
			{
				if(dollcount[dollnumber[i][0]]!=0&&dollcount[dollnumber[i][1]]!=0)
				{
					line[i].drawSelf();
				}
			}
	}
}

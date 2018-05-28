package com.bn;

import java.util.ArrayList;
import java.util.Collections;

public class LiZiChenJiUtil 
{
	//产生沉积数据
	@SuppressWarnings("unchecked")
	public static void genCJ
	(//产生沉积数据的方法
		float[][] result,  	//记录地形高度数据的数组
		int cx,				//粒子掉落的中心点X坐标
		int cy,				//粒子掉落的中心点Y坐标
		int count,			//总粒子数
		int span,			//搜索步进
		int gdyzIn,			//高度阈值范围
		boolean ssfxjh,		//搜索方向是否均衡
		boolean sfSmms,		//是否采用山脉模式
		int[][] smwz 		//山脉位置数组
	)
	{	
		@SuppressWarnings("rawtypes")
		ArrayList[] knPosition=new ArrayList[span];	//创建可能的搜索位置列表1	
		for(int k=1;k<=span;k++)//生成可能的搜索位置数据
		{
			knPosition[k-1]=new ArrayList<int[]>();//创建步长范围为k时所有可能搜索位置的列表
			int powerSpan=k*k;//求出允许搜索范围值的平方
			for(int i=-k;i<=k;i++)//在-k~+k的行列范围内循环查看
			{
				for(int j=-k;j<=k;j++)
				{
					if(i==0&&j==0) continue;
					if(i*i+j*j<=powerSpan)//若此位置距离平方小于允许搜索范围的平方
					{
						knPosition[k-1].add(new int[]{i,j});//记录此位置进可能搜索位置列表
					}
				}
			}
		}						
		
		//获取地形的列数与行数
		//对应灰度图的宽度与高度
		int width=result.length;
		int height=result[0].length;
		
		//循环沉积制定数量的粒子
		for(int i=0;i<count;i++)
		{
			//粒子当前行列
			int currX=cx;
			int currY=cy;	
			//若采用山脉模式
			if(sfSmms)
			{
				currX=smwz[i%smwz.length][0];
				currY=smwz[i%smwz.length][1];
			}			
			
			//循环探查周围位置的高度，直至找到此粒子最终沉积的位置
			zong:while(true)
			{
				//取出目标位置的高度
				float currHeight=result[currX][currY];	
				//随机在范围内得出此次搜索的步长
				int currSpan=(int)Math.ceil(span*Math.random());
				//随机在范围内得出此次搜索的高度阈值
				int gdyz=(int)(Math.ceil((gdyzIn/2.0)*Math.random()+(gdyzIn/2.0)));
				//根据此次搜索的步长获取可能的搜索位置列表
				ArrayList<int[]> knwz=(ArrayList<int[]>)(knPosition[currSpan-1]);
				//若搜索方向均衡则对搜索位置打乱
				if(ssfxjh)
				{
					Collections.shuffle(knwz);
				}			
				//对可能的位置进行搜索
				for(int[] wz:knwz)
				{
					int j=wz[0];
					int k=wz[1];				
					//若可能的位置超出范围则放弃此可能位置
					if(currX+j<0||currX+j>=width||currY+k<0||currY+k>=height)
					{
						continue;
					}			
					//获取此可能位置的高度
					float tempHeight=result[currX+j][currY+k];
					//若当前位置的高度与可能位置的高度差大于高度阈值
					if(currHeight-tempHeight>gdyz)
					{
						//将当前位置移动到此可能的位置
						currX=currX+j;
						currY=currY+k;
						//从新位置开始重新搜索
						continue zong;
					}
				}
				//若找到了此粒子最终沉积的位置则退出搜索循环
				break zong;
			}			
			//在此粒子沉积的位置将高度值加一
			result[currX][currY]+=1;			
		}		
	}
}

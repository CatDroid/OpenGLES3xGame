package com.bn;

import java.util.ArrayList;
import java.util.Collections;

public class LiZiChenJiUtil {
	
	//产生沉积数据
	@SuppressWarnings("unchecked")
	public static void genCJ
	(//产生沉积数据的方法
			float[][] result,  	//记录地形高度数据的数组
			int cx,				//粒子掉落的中心点X坐标
			int cy,				//粒子掉落的中心点Y坐标
			int count,			//总粒子数
			int span,			//搜索步进  最小为1
			int gdyzIn,			//高度阈值范围
			boolean ssfxjh,		//搜索方向是否均衡
			boolean sfSmms,		//是否采用山脉模式
			int[][] smwz 		//山脉位置数组
	)
	{
		if(span == 0) {
			throw new RuntimeException("span is zero");
		}
		
		@SuppressWarnings("rawtypes")
		ArrayList[] knPosition = new ArrayList[span];	// 创建可能的搜索位置列表1	
		for(int k = 1 ; k <= span ; k++) 				// 生成可能的搜索位置数据  对应每一种可能的步长
		{
			knPosition[ k-1 ]=new ArrayList<int[]>();	// 创建步长范围为k时所有可能搜索位置的列表
			
			int powerSpan = k*k;						// 求出允许搜索范围值的平方  对应这个步长 可以搜索的范围就是k*k 步进作为半径的圆形区域 
			for(int i = -k ; i <= k ; i++ )				// 在-k~+k的行列范围内循环查看
			{
				for(int j = -k ; j <= k ; j++ )
				{
					if( i==0 && j==0 ) continue;		// 释放位置不计入 搜索位置 
					
					if( i*i + j*j <= powerSpan)				// 若此位置距离平方小于允许搜索范围的平方
					{
						knPosition[k-1].add(new int[]{i,j});// 记录此位置进可能搜索位置列表
					}
				}
			}
		}
		

		//获取地形的列数与行数
		//对应灰度图的宽度与高度
		int width = result.length;
		int height= result[0].length;
		
		System.out.println("span = " + span + " width = " + width + " height = " + height  ); 

		//循环沉积制定数量的粒子
		for (int i = 0; i < count; i++) { // 每个释放的粒子 

			//粒子当前行列 如果不是山脉模式 那么就从固定的点 释放粒子 
			int currX=cx;
			int currY=cy;	
			//若采用山脉模式  那么就采用均匀释放粒子到路径上   smwz二维数组 存放的是 山脉路径    
			if(sfSmms)
			{
				currX=smwz[ i % smwz.length][0];
				currY=smwz[ i % smwz.length][1];
			}

			//循环探查周围位置的高度，直至找到此粒子最终沉积的位置
			while(true)
			{
				//取出目标位置的高度， 释放点的位置
				float currHeight = result[currX][currY];
	
				//随机在范围内得出此次搜索的步长   span*[0.0~1.0)   span=5.0   currSpan=0.0~5.0 
				int currSpan = (int) Math.ceil(span * Math.random()); 
				
				// System.out.println("Math.ceil(0) = " + Math.ceil(0)); // = 0 
	
				//随机在范围内得出此次搜索的高度阈值
				int gdyz = (int) Math.ceil(gdyzIn / 2.0D * Math.random() + gdyzIn / 2.0D);
	
				// 取出这个搜索步长  对应的搜索区域  
				ArrayList<int[]> knwz = knPosition[(currSpan - 1)];
	
				//若搜索方向均衡则对搜索位置打乱  
				if (ssfxjh) {
					Collections.shuffle(knwz); // 使用默认随机源对列表进行置换，所有置换发生的可能性都是大致相等的
				}
	
				boolean found = false ;// 记录是否找到更低的位置  
				for (int[] wz : knwz) {// 遍历  每个可能都搜索位置 
					int j = wz[0];
					int k = wz[1];
	
					int search_x = currX + j;
					int search_y = currY + k; 
					
					// 若可能的位置超出范围则放弃此可能位置
					if ((search_x >= 0) && (search_x < width) && (search_y >= 0) && (search_y < height)) {
	
						//获取此可能位置的高度
						float tempHeight = result[search_x][search_y];
	
						//若当前位置的高度  高过  可能位置 ，  并大过  高度阈值
						if (currHeight - tempHeight > gdyz) {
							currX = search_x;  	// 发现比当前位置更低的位置，就调到更低的位置 
							currY = search_y ;
							found = true ;		//	找到更加底的位置，从新位置开始重新搜索
							break; 
						}
					}
				}
				
				if(found) { // 如果粒子找搜索范围内找到新的最低点 那么在这个新的最低点 再开始搜索
					continue ; 
				}else { 	// 如果跑到来这里， 说明周围已经没有更加底的位置了，  此粒子最终沉积的位置则退出搜索循环
					break  ;	
				}
			}
			
			result[currX][currY] += 1.0F;//在此粒子沉积的位置将高度值加一
		}
	}
}

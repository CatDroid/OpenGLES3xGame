package com.bn.Sample8_7;

import java.util.ArrayList;


// Android 绘制N阶Bezier曲线(动画显示)
// https://www.2cto.com/kf/201606/520268.html
// 数据点：确定曲线的起始和结束位置 控制点：确定曲线的弯曲程度
// 2阶只能画一个凹或凸线 越高阶曲线形状更多
// 应用情景: QQ小红点拖拽效果、阅读软件的翻书效果、平滑折线图的制作


// BezierEx\bin>java  com.bn.bezier.BezierExMain  运行贝塞尔曲线工具

public class BezierUtil 
{
   static ArrayList<BNPosition> al=new ArrayList<BNPosition>();
   
   public static ArrayList<BNPosition> getBezierData(float span)
   {
	   ArrayList<BNPosition> result=new ArrayList<BNPosition>();
	   
	   int n=al.size()-1;
	   
	   if(n<1)
	   {
		   return result;
	   }
	   
	   int steps=(int) (1.0f/span);
	   long[] jiechengNA=new long[n+1];
	   
	   for(int i=0;i<=n;i++)// hhl  查找表法!!!  公式中的 k!  L!  (L-k)! 都可以用这个  不同的t都可以使用
	   {
		   jiechengNA[i]=jiecheng(i); // 0 ~ L 的阶乘
	   }
	   
	   for(int i=0;i<=steps;i++)
	   {
		   float t=i*span;
		   if(t>1)
		   {
			   t=1;
		   }
		   float xf=0;
		   float yf=0;
		   
		   float[] tka=new float[n+1];
		   float[] otka=new float[n+1];
		   for(int j=0;j<=n;j++)// hhl 查找表法!!! 公式中 (1-t)^L-k 和 t^k  但是针对每个t都要算一次
		   {
			   tka[j]=(float) Math.pow(t, j); 
			   otka[j]=(float) Math.pow(1-t, j); 
		   }
		   
		   for(int k=0;k<=n;k++)
		   {
			   float xs=(jiechengNA[n]/(jiechengNA[k]*jiechengNA[n-k]))*tka[k]*otka[n-k];
			   xf=xf+al.get(k).x*xs;
			   yf=yf+al.get(k).y*xs;
		   }
		   result.add(new BNPosition(xf,yf));
	   }
	   
	   return result;
   }
   
   //求阶乘
   private static long jiecheng(int n)
   {
	   long result=1;
	   if(n==0)
	   {
		   return 1;
	   }
	   
	   for(int i=2;i<=n;i++)
	   {
		   result=result*i;
	   }
	   
	   return result;
   }
}

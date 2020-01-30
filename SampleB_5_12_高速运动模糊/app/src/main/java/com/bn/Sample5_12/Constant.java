package com.bn.Sample5_12;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class Constant
{
	public static boolean threadFlag=true;//水面换帧线程工作标志位	  
	
	public static float[][] yArray; 
	public static final float LAND_HIGH_ADJUST=3f;//陆地的高度调整值
	public static final float LAND_HIGHEST=40f;//陆地最大高差  
	
	public static final int SAMPLENUMBER=2;
	public static final float SPAN=2.0f;
    
    static int GEN_TEX_WIDTH=1024;
    static int GEN_TEX_HEIGHT=1024;
	//从灰度图片中加载陆地上每个顶点的高度
	public static float[][] loadLandforms(Resources resources,int index)
	{
		Bitmap bt=BitmapFactory.decodeResource(resources, index);
		int colsPlusOne=bt.getWidth(); 
		int rowsPlusOne=bt.getHeight(); 
		float[][] result=new float[rowsPlusOne][colsPlusOne];
		for(int i=0;i<rowsPlusOne;i++)
		{
			for(int j=0;j<colsPlusOne;j++)
			{
				int color=bt.getPixel(j,i);
				int r=Color.red(color);
				int g=Color.green(color); 
				int b=Color.blue(color);
				int h=(r+g+b)/3;
				result[i][j]=h*LAND_HIGHEST/255-LAND_HIGH_ADJUST;  
			}
		}
		return result;
	}
}
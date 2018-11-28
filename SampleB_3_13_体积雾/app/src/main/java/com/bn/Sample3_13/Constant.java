package com.bn.Sample3_13;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class Constant
{
	public static float[][] yArray;
	public static final float LAND_HIGH_ADJUST=2f;	// 陆地的高度调整值
	public static final float LAND_HIGHEST=60f;		// 陆地最大高差
	public static final float CAMERA_Y=25;			// 摄像机Y坐标
	public static float GLASS_HIGH_END = 0 ; 		// 草地的最高高度 (因为地面是从-LAND_HIGH_ADJUST开始的)
	public static float GLASS_ROCK_HIGH = 50 ; 		// 草地和石头的过渡带高度

	public static float UNIT_SIZE = 3.0f;			// 灰度图地形图每个像素对应的实际单位长度
	public static float TJ_GOG_SLAB_Y = 8f;			// 体积雾的高度

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
				result[i][j]=h*LAND_HIGHEST/255-LAND_HIGH_ADJUST;  // 地面从-LAND_HIGH_ADJUST开始 落差是LAND_HIGHEST
			}
		}
		return result;
	}
}
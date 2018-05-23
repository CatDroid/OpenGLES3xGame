package com.bn.Sample11_2;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class Constant
{
	public final static String TAG = "TOM";
	public enum RENDER_TYPE {
		One_Texture ,
		Procedural_Texture ,
		MipMap_Texture,
	}
	public static final RENDER_TYPE CONFIG_TEXTRUE = RENDER_TYPE.MipMap_Texture;
	public static final boolean USING_MY_MIPMAP = true;


	public static float[][] yArray;//存储地形顶点高度的数组
	public static final float LAND_HIGH_ADJUST=-2f;//陆地的高度调整值
	public static final float LAND_HIGHEST=80f;//陆地最大高差
	public static final float END_OF_GRASS = 40;// 完全草地的最高高度
	public static final float BETWEEN_GRASS_AND_ROCK = 20; // 草地和石头过渡带
	public static final int ROCK_R_ID = R.raw.rock ;// R.raw.rock_500_256; //;
	public static final int GRASS_R_ID =R.raw.grass; // R.raw.grass_250_250; //

	//从灰度图片中加载陆地上每个顶点的高度
	public static float[][] loadLandforms(Resources resources,int index)
	{
		Bitmap bt=BitmapFactory.decodeResource(resources, index);//导入灰度图
		int colsPlusOne=bt.getWidth(); //获得存储地形顶点高度数组的列数
		int rowsPlusOne=bt.getHeight(); //获得存储地形顶点高度数组的行数
		float[][] result=new float[rowsPlusOne][colsPlusOne];//创建存储地形顶点高度的数组
		for(int i=0;i<rowsPlusOne;i++)//对灰度图像素行遍历
		{
			for(int j=0;j<colsPlusOne;j++)//对灰度图像素列遍历
			{
				int color=bt.getPixel(j,i);//获得指定行列处像素的颜色
				//获得该像素红、绿、蓝三个色彩通道的值
				int r=Color.red(color);
				int g=Color.green(color); 
				int b=Color.blue(color);
				int h=(r+g+b)/3;//3个色彩通道求平均
				result[i][j]=h*LAND_HIGHEST/255+LAND_HIGH_ADJUST;  //按公式计算顶点海拔
			}
		}
		return result;//返回存储地形顶点高度的数组
	}
}
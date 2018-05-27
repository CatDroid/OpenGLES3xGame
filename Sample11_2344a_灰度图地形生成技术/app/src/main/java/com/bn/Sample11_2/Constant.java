package com.bn.Sample11_2;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Constant
{
	public final static String TAG = "TOM";
	public enum RENDER_TYPE {
		One_Texture ,
		Procedural_Texture , // 过渡纹理
		MipMap_Texture,		 // 使用MIPMAP纹理
		Using_Texture_In_VertexShader , // 在顶点着色器中使用纹理
	}
	public static final RENDER_TYPE CONFIG_TEXTRUE = RENDER_TYPE.Using_Texture_In_VertexShader;
	public static final boolean USING_MY_MIPMAP = true;


	public static float[][] yArray;//存储地形顶点高度的数组
	public static final float LAND_HIGH_ADJUST= 2f;//陆地的高度调整值
	public static final float LAND_HIGHEST=60f;//陆地最大高差
	public static final float END_OF_GRASS = 0 ;// 完全草地的最高高度
	public static final float BETWEEN_GRASS_AND_ROCK = 50; // 草地和石头过渡带
	public static final int ROCK_R_ID = R.raw.rock ;// R.raw.rock_500_256; //;
	public static final int GRASS_R_ID =R.raw.grass; // R.raw.grass_250_250; //
	public static final int LAND_ID = R.raw.land;


	private static Bitmap decodeResource(Resources resources, int id) {
		TypedValue value = new TypedValue();
		resources.openRawResource(id, value);
		/**
		 * raw  TypedValue.density = 0
		 * hdpi-drawable TypedValue.density = 240
		 * mdpi-drawable TypedValue.density = 160
		 * ldpi-drawable TypedValue.density = 120
		 * drawable TypedValue.density = 0
		 *
		 * 如果 opts.inDensity = 0 或者没有 opts
		 * 如果有传入TypedValue 且 TypedValue.density = 0 那么 inDenstiy 会设置为160
		 *
		 * 如果 opts.inTargetDensity = 0 或者没有opts
		 * 那么会用屏幕的dpi作为输出的dpi,即 inTargetDensity = getDisplayMetrics().densityDpi
		 *
		 * final int density = opts.inDensity;
		 * final int targetDensity = opts.inTargetDensity;
		 * float scale = targetDensity / (float)density;
		 *
		 * 荣耀V10 海思麒麟970 	dpi 480
		 * 小米5   高通晓龙820		dpi 480
		 *
		 * 由于默认情况下 inDensity = 160 , 而在上述的两款机器上 inTargetDensity = 480
		 * 所以解码的图片会放大了3倍 land.png本来是64*64 就会放到到192*192
		 *
		 */
		Log.w(TAG, "value.density: " + value.density + " densityDpi " + resources.getDisplayMetrics().densityDpi );
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inTargetDensity = value.density; // 如果放在hdpi mdpi ldpi目录 设置这个 解码出来就不会缩放 否则缩放到屏幕的densityDpi
		// 解决decodeResouce会根据屏幕dpi缩放解码图片问题
		// https://blog.csdn.net/walid1992/article/details/50074521
		// 如果外面设置了 inTargetDensity 和 inDensity  内部就会根据给定的
		// 否则 inTargetDensity 使用传入的 TypedValue
		//	   inDensity 使用 getDisplayMetrics().densityDpi
		return BitmapFactory.decodeResource(resources, id, opts);
	}

	//从灰度图片中加载陆地上每个顶点的高度
	public static float[][] loadLandforms(Resources resources,int index)
	{
		//Bitmap bt = decodeResource(resources,index);
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

//		int bytes = bt.getByteCount();
//		ByteBuffer buf = ByteBuffer.allocate(bytes);
//		bt.copyPixelsToBuffer(buf);
//		buf.flip();
//
//		try {
//			FileChannel os = new FileOutputStream(new File(Environment.getExternalStorageDirectory()+"/temp5.rgba")).getChannel();
//			os.write(buf);
//			os.close();
//			Log.d(TAG,"save file id = " + Integer.toHexString( index) );
//			Log.d(TAG,"save file " + bytes + " colsPlusOne = " + colsPlusOne + " rowsPlusOne = " + rowsPlusOne );
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		return result;//返回存储地形顶点高度的数组
	}
}
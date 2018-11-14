package com.bn;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExportUtil3DTexture 
{
	//将整数变为四字节数组，索引大的字节为高位
		public static byte[] fromIntToBytes(int k)
		{
			byte[] buff = new byte[4];
			buff[0]=(byte)(k&0x000000FF);
			buff[1]=(byte)((k&0x0000FF00)>>>8);
			buff[2]=(byte)((k&0x00FF0000)>>>16);
			buff[3]=(byte)((k&0xFF000000)>>>24);
			
			return buff;
		}
	
    //导出统一灰度用
	public static void Export(int[][][] data) throws IOException
	{
		//二进制输出
		FileOutputStream fout=new FileOutputStream("TexData3D/3dNoise.bn3dtex");
		int width=data.length;
		int height=data[0].length;
		int depth=data[0][0].length;
		
		//写出三个维度尺寸
		fout.write(fromIntToBytes(width));
		fout.write(fromIntToBytes(height));
		fout.write(fromIntToBytes(depth));
		
		//写出三个维度的数据
		for(int i=0;i<width;i++)
		{
			for(int j=0;j<height;j++)
			{
				for(int k=0;k<depth;k++)
				{ 
					int gray=data[i][j][k];
					byte[] temp={(byte)gray,(byte)gray,(byte)gray,(byte)255};
					fout.write(temp);
				}
			}
		}
		
		fout.close();
	}
	
    //导出RGBA各自一个倍频用
	public static void Export_BPFL(int[][][][] data) throws IOException
	{
		//二进制输出
		FileOutputStream fout=new FileOutputStream("TexData3D/3dNoise.bn3dtex");
		int width=data.length;
		int height=data[0].length;
		int depth=data[0][0].length;
		
		//写出三个维度尺寸
		fout.write(fromIntToBytes(width));
		fout.write(fromIntToBytes(height));
		fout.write(fromIntToBytes(depth));
		
		//写出三个维度的数据
		for(int i=0;i<width;i++)
		{
			for(int j=0;j<height;j++)
			{
				for(int k=0;k<depth;k++)
				{ 
					int red=data[i][j][k][0];
					int green=data[i][j][k][1];
					int blue=data[i][j][k][2];
					int alpha=data[i][j][k][3];
							
					byte[] temp={(byte)red,(byte)green,(byte)blue,(byte)alpha};
					fout.write(temp);
				}
			}
		}
		
		fout.close();
	}
}

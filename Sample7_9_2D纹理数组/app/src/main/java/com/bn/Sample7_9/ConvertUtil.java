package com.bn.Sample7_9;

import java.io.IOException;
import java.io.InputStream;
import java.nio.*;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ConvertUtil 
{
	public static ByteBuffer convertPicsToBuffer(Resources res,int[] resIds,int width,int height)
	{
		//每幅图数据字节数
		int perPicByteCount=width*height*4;
		//计算一组图片对应缓冲的大小，即总缓存
		ByteBuffer buf=ByteBuffer.allocateDirect(perPicByteCount*resIds.length);
		//遍历每一幅图
		for(int i=0;i<resIds.length;i++)
		{
			int id=resIds[i];//获取每一幅图的id
			//通过输入流将图片加载到内存===============begin===================
			InputStream is = res.openRawResource(id);//建立指向纹理图的流
			Bitmap bitmapTmp;//加载后的图片对象引用
	        try 
	        {
	        	bitmapTmp = BitmapFactory.decodeStream(is);	//从流中加载图片内容        	
	        } 
	        finally 
	        {
	            try {is.close();}catch(IOException e){e.printStackTrace();}
	        }
	        //通过输入流加载图片===============end=====================  
	        buf.position(i*perPicByteCount);//设置缓冲区起始位置
	        bitmapTmp.copyPixelsToBuffer(buf);//将图像的像素拷贝到总缓存	        
	        bitmapTmp.recycle();//加载成功后释放图片
		}

		return buf;		//返回加载了数据后的缓冲
	}
}

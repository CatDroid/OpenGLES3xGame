package com.bn.Sample10_1b;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.opengl.ETC1Util;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.os.SystemClock;
import android.util.Log;

import static android.opengl.GLES20.GL_NO_ERROR;

public class BnETC2Util 
{
	public static final int PKM_HEADER_SIZE=16;
	public static final int PKM_HEADER_WIDTH_OFFSET=12;
	public static final int PKM_HEADER_HEIGHT_OFFSET=14;
	
	public static byte[] loadDataFromAssets(String fname, Resources r)
	{
		byte[] data=null;
		InputStream in=null;
		try
		{
			in = r.getAssets().open(fname);
	    	int ch=0;
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    while((ch=in.read())!=-1)
		    {
		      	baos.write(ch);
		    }      
		    data=baos.toByteArray();
		    baos.close();
		    in.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return data;
	}


	public static ByteBuffer loadDataFromAssetsByteBuffer(String fname, Resources r)
	{
		byte[] data=null;
		ByteBuffer output = null;
		try {
			AssetFileDescriptor fd =  r.getAssets().openFd(fname);
			int file_size = (int)fd.getLength();
			output = ByteBuffer.allocateDirect(file_size);

//			FileChannel fc = new FileInputStream(fd.getFileDescriptor()).getChannel(); // 这种方式读取错误 即使设置了 aaptOptions.noCompress 'pkm'
			FileChannel fc = fd.createInputStream().getChannel();
			fc.read(output);
			output.flip();

			fd.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return output;
		/*

			r.getAssets().openFd 抛出异常:
			java.io.FileNotFoundException: This file can not be opened as a file descriptor; it is probably compressed


		  	assert目录 默认不压缩下列文件
			static const char* kNoCompressExt[] = {
				".jpg", ".jpeg", ".png", ".gif",
				".wav", ".mp2", ".mp3", ".ogg", ".aac",
				".mpg", ".mpeg", ".mid", ".midi", ".smf", ".jet",
				".rtttl", ".imy", ".xmf", ".mp4", ".m4a",
				".m4v", ".3gp", ".3gpp", ".3g2", ".3gpp2",
				".amr", ".awb", ".wma", ".wmv"
			};
		  */
	}
	
    public static int initTextureETC2(String pkmName,Resources r) throws Exception //textureId
	{
		//生成纹理ID
		int[] textures = new int[1];
		GLES30.glGenTextures
		(
				1,          //产生的纹理id的数量
				textures,   //纹理id的数组
				0           //偏移量
		);    
		int textureId=textures[0];    
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);

//		long before = System.nanoTime();
//        byte[] data=loadDataFromAssets(pkmName,r);
//		Log.d("TOM","cost = " + (System.nanoTime() - before));// 测试三次耗时 26652604  21844271  21884375  平均 20+ms
//        ByteBuffer buffer = ByteBuffer.allocateDirect(data.length).order(ByteOrder.LITTLE_ENDIAN);
//        buffer.put(data).position(PKM_HEADER_SIZE); // 跳过头部16个字节
//
//        ByteBuffer header = ByteBuffer.allocateDirect(PKM_HEADER_SIZE).order(ByteOrder.BIG_ENDIAN);// 头部按大端处理!!
//        header.put(data, 0, PKM_HEADER_SIZE).position(0);
//
//        int width = header.getShort(PKM_HEADER_WIDTH_OFFSET);// 从头部取出宽高  hl.he 一定要大端
//        int height = header.getShort(PKM_HEADER_HEIGHT_OFFSET);
//
//		GLES30.glCompressedTexImage2D
//		(
//				GLES30.GL_TEXTURE_2D,
//				0,
//				GLES30.GL_COMPRESSED_RGBA8_ETC2_EAC,  // ETC1使用的是 ETC1_RGB8_OES
//				width,
//				height,
//				0,
//				data.length - PKM_HEADER_SIZE,
//				buffer
//		);

		long before = System.nanoTime();
		ByteBuffer bfile = loadDataFromAssetsByteBuffer(pkmName,r);
		Log.d("TOM","cost = " + (System.nanoTime() - before));// 628645  1023437 654688 平均耗时 ~1ms
		if(bfile == null){
			Log.e("TOM","Exception!!!!!!!!!!! loadDataFromAssetsByteBuffer");
			throw new Exception("Fail to load pkg texture");
		}
		Log.d("TOM","ByteBuffer =  " + bfile.remaining() + " " + bfile.position() );
		Log.d("TOM",String.format("%c %c %c %c %c %c" , bfile.get(0),bfile.get(1) ,bfile.get(2) ,bfile.get(3),bfile.get(4) ,bfile.get(5)));
		bfile.position(0);
		bfile.order(ByteOrder.BIG_ENDIAN);

		int width = bfile.getShort(PKM_HEADER_WIDTH_OFFSET);
		int height = bfile.getShort(PKM_HEADER_HEIGHT_OFFSET);
		bfile.position(PKM_HEADER_SIZE);
		bfile.order(ByteOrder.LITTLE_ENDIAN);
		Log.d("TOM","width = " + width + " height = " + height);
		// 记得转换大小端!!! width = 0 height = -28161

		GLES30.glCompressedTexImage2D
		(
			GLES30.GL_TEXTURE_2D,
			0,
			GLES30.GL_COMPRESSED_RGBA8_ETC2_EAC,  // ETC1使用的是 ETC1_RGB8_OES
			width,
			height,
			0,
			bfile.remaining(),
			bfile
		);


//		 ETC1Util.loadTexture()

		if( GLES20.glGetError() != GL_NO_ERROR){
			Log.e("TOM","Exception!!!!!!!!!!! GL_NO_ERROR");
			throw new Exception("Fail to glCompressedTexImage2D");
		}
		return textureId;
	}
}

package com.bn.Sample6_7;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.bn.util.MatrixState;
import com.bn.util.ShaderManager;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import static com.bn.Sample6_7.Constant.*;


// 布料仿真--质点弹簧系统

public class Cloth
{

	private FloatBuffer mTextureBuffer;
	private FloatBuffer mColorBuffer;//顶点着色数据缓冲

	private int mProgram;// 自定义渲染管线程序id
	private int muMVPMatrixHandle;// 总变换矩阵引用id
	private int maPositionHandle; // 顶点位置属性引用id
	private int maTexCoorHandle; // 顶点纹理坐标属性引用id


	int texId;
	
	public Cloth(GLSurfaceView gsv)
	{
		initVertexData();
		initShader(gsv);
	}
	
	// 初始化顶点坐标与着色数据的方法
	private void initVertexData() 
	{

    	float texCoords[] = generateTexCoor(NUMCOLS, NUMROWS);
        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        mTextureBuffer= tbb.asFloatBuffer();
        mTextureBuffer.put(texCoords);
        mTextureBuffer.position(0);
	}
	
	// 初始化着色器
	public void initShader(GLSurfaceView gsv)
	{
		// 加载顶点着色器的脚本内容
		String mVertexShader = ShaderManager.loadFromAssetsFile("vertex.glsl",gsv.getResources());
		// 加载片元着色器的脚本内容
		String mFragmentShader = ShaderManager.loadFromAssetsFile("frag.glsl",gsv.getResources());
		// 基于顶点着色器与片元着色器创建程序
		mProgram = ShaderManager.createProgram(mVertexShader, mFragmentShader);

		// 获取程序中顶点位置属性引用id
		maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
		// 获取程序中顶点纹理坐标属性引用id
		maTexCoorHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoor");
		// 获取程序中总变换矩阵引用id
		muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
	}
	
	public void drawSelf(FloatBuffer fb,int texId)
	{
		
		if (fb == null)
		{
			return;
		}
		
		// 制定使用某套shader程序
		GLES30.glUseProgram(mProgram);
		// 将最终变换矩阵传入shader程序
		GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,MatrixState.getFinalMatrix(), 0);

		// 顶点属性--顶点坐标
		GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT,false, 3 * 4, fb);
		GLES30.glEnableVertexAttribArray(maPositionHandle);

		// 顶点属性--纹理坐标
		GLES30.glVertexAttribPointer(maTexCoorHandle, 2, GLES30.GL_FLOAT,false, 2 * 4, mTextureBuffer);
		GLES30.glEnableVertexAttribArray(maTexCoorHandle);

		// 绑定纹理
		GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);

		// 绘制纹理矩形
		GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, fb.capacity()/3);
		//GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, fb.capacity()/3);
	}

	//自动切分纹理产生纹理数组的方法
    public float[] generateTexCoor(int bw,int bh)
    {
    	float[] result=new float[bw*bh*6*2]; 
    	float sizew=1.0f/bw;//列数
    	float sizeh=1.0f/bh;//行数
    	int c=0;
    	for(int i=0;i<bh;i++)
    	{
    		for(int j=0;j<bw;j++)
    		{
    			//每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
    			float s=j*sizew;
    			float t=i*sizeh;
    			
    			result[c++]=s;
    			result[c++]=t;
    			
    			result[c++]=s;
    			result[c++]=t+sizeh;
    			
    			result[c++]=s+sizew;
    			result[c++]=t;
    			
    			
    			result[c++]=s+sizew;
    			result[c++]=t;
    			
    			result[c++]=s;
    			result[c++]=t+sizeh;
    			
    			result[c++]=s+sizew;
    			result[c++]=t+sizeh;    			
    		}
    	}
    	return result;
    }
}

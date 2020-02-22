package com.bn.Sample6_6;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.bn.util.MatrixState;
import com.bn.util.ShaderManager;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import static com.bn.Sample6_6.Constant.*;

public class TextureRect {
	FloatBuffer mTextureBuffer;
	
	int mProgram;// 自定义渲染管线程序id
	int muMVPMatrixHandle;// 总变换矩阵引用id
	int maPositionHandle; // 顶点位置属性引用id
	int maTexCoorHandle; // 顶点纹理坐标属性引用id
    int maColorHandle; //顶点颜色属性引用id 
	String mVertexShader;// 顶点着色器
	String mFragmentShader;// 片元着色器

	int texId;
	
	public TextureRect(GLSurfaceView gsv)
	{
		// 初始化顶点坐标与着色数据
		initVertexData();
		// 初始化shader
		initShader(gsv);
	}
	
	// 初始化顶点坐标与着色数据的方法
	private void initVertexData() 
	{
		final int cols = NUMCOLS;
		final int rows = NUMROWS;        
        //顶点纹理数据的初始化================begin============================
    	//自动生成纹理数组，20列15行
    	float textures[]=generateTexCoor(cols,rows);
        
        //创建顶点纹理数据缓冲
        ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length*4);
        tbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTextureBuffer= tbb.asFloatBuffer();//转换为Float型缓冲
        mTextureBuffer.put(textures);//向缓冲区中放入顶点着色数据
        mTextureBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点纹理数据的初始化================end============================
	}
	
	// 初始化着色器
	public void initShader(GLSurfaceView gsv) {
		// 加载顶点着色器的脚本内容
		mVertexShader = ShaderManager.loadFromAssetsFile("vertex.glsl",gsv.getResources());
		// 加载片元着色器的脚本内容
		mFragmentShader = ShaderManager.loadFromAssetsFile("frag.glsl",gsv.getResources());
		// 基于顶点着色器与片元着色器创建程序
		mProgram = ShaderManager.createProgram(mVertexShader, mFragmentShader);
		// 获取程序中顶点位置属性引用id
		maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
		// 获取程序中顶点纹理坐标属性引用id
		maTexCoorHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中顶点颜色属性引用id  
        maColorHandle= GLES30.glGetAttribLocation(mProgram, "aColor");
		// 获取程序中总变换矩阵引用id
		muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
	}
	
	public void drawSelf(FloatBuffer fb,int texId) {
		
		if(fb==null) return;
		
		// 制定使用某套shader程序
		GLES30.glUseProgram(mProgram);
		// 将最终变换矩阵传入shader程序
		GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,MatrixState.getFinalMatrix(), 0);
		// 为画笔指定顶点位置数据
		GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT,false, 3 * 4, fb);
		// 为画笔指定顶点纹理坐标数据
		GLES30.glVertexAttribPointer(maTexCoorHandle, 2, GLES30.GL_FLOAT,false, 2 * 4, mTextureBuffer);		
		// 允许顶点位置数据数组
		GLES30.glEnableVertexAttribArray(maPositionHandle);
		GLES30.glEnableVertexAttribArray(maTexCoorHandle); 
		// 绑定纹理
		GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
		// 绘制纹理矩形
		GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, fb.capacity()/3);
	}

	//自动切分纹理产生纹理数组的方法
    public float[] generateTexCoor(int bw,int bh)
    {
    	float[] result = new float[bw*bh*6*2];
    	float sizew = 1.0f / bw;    // 列数
    	float sizeh = 1.0f / bh;    // 行数
    	int c = 0;
    	for(int i = 0; i < bh ; i++ )
    	{
    		for(int j = 0; j < bw ; j++)
    		{
    			// 每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
    			float s = j * sizew;
    			float t = i * sizeh;
    			
    			result[c++]=s;
    			result[c++]=1.0f - t;
    			
    			result[c++]=s;
    			result[c++]=1.0f - (t + sizeh);
    			
    			result[c++]=s+sizew;
    			result[c++]=1.0f - t;
    			
    			
    			result[c++]=s+sizew;
    			result[c++]=1.0f - t;
    			
    			result[c++]=s;
    			result[c++]=1.0f - (t + sizeh);
    			
    			result[c++]=s+sizew;
    			result[c++]=1.0f - (t + sizeh);
    		}
    	}
    	return result;
    }
}

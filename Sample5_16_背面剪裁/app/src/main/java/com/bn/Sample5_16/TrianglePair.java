package com.bn.Sample5_16;
import java.nio.ByteBuffer;

import static com.bn.Sample5_16.Constant.*;

import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES30;

//三角形对
public class TrianglePair {
	int mProgram;// 自定义渲染管线着色器程序id
	int muMVPMatrixHandle;// 总变换矩阵引用
	int maPositionHandle; // 顶点位置属性引用
	int maColorHandle; // 顶点颜色属性引用
	String mVertexShader;// 顶点着色器
	String mFragmentShader;// 片元着色器

	FloatBuffer mVertexBuffer;// 顶点坐标数据缓冲
	FloatBuffer mColorBuffer;// 顶点着色数据缓冲
	int vCount = 0;

	public TrianglePair(MySurfaceView mv) {
		// 初始化顶点坐标与着色数据
		initVertexData();
		// 初始化shader
		initShader(mv);
	}

	// 初始化顶点坐标与着色数据的方法
	public void initVertexData() {
		// 顶点坐标数据的初始化================begin============================
		vCount = 6;//顶点数量
		float vertices[] = new float[] {
				-8 * UNIT_SIZE, 10 * UNIT_SIZE, 0,//左面三角形的第1个顶点
				-2 * UNIT_SIZE, 2 * UNIT_SIZE, 0, //左面三角形的第2个顶点
				-8 * UNIT_SIZE, 2 * UNIT_SIZE, 0,//左面三角形的第3个顶点

				8 * UNIT_SIZE, 2 * UNIT_SIZE, 0, //右面三角形的第1个顶点
				8 * UNIT_SIZE, 10 * UNIT_SIZE, 0, //右面三角形的第2个顶点
				2 * UNIT_SIZE, 10 * UNIT_SIZE, 0 //右面三角形的第2个顶点
		};

		// 创建顶点坐标数据缓冲
		// vertices.length*4是因为一个整数四个字节
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());// 设置字节顺序
		mVertexBuffer = vbb.asFloatBuffer();// 转换为Float型缓冲
		mVertexBuffer.put(vertices);// 向缓冲区中放入顶点坐标数据
		mVertexBuffer.position(0);// 设置缓冲区起始位置
		// 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
		// 转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
		// 顶点坐标数据的初始化================end============================

		// 顶点着色数据的初始化================begin============================
		float colors[] = new float[]// 顶点颜色值数组，每个顶点4个色彩值RGBA
		{ 
				1, 1, 1, 0, 
				0, 0, 1, 0, 
				0, 0, 1, 0, 
				1, 1, 1, 0, 
				0, 1, 0, 0, 
				0, 1, 0, 0 
		};
		// 创建顶点着色数据缓冲
		ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
		cbb.order(ByteOrder.nativeOrder());// 设置字节顺序
		mColorBuffer = cbb.asFloatBuffer();// 转换为Float型缓冲
		mColorBuffer.put(colors);// 向缓冲区中放入顶点着色数据
		mColorBuffer.position(0);// 设置缓冲区起始位置
		// 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
		// 转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
		// 顶点着色数据的初始化================end============================

	}

	// 初始化着色器
	public void initShader(MySurfaceView mv) {
		// 加载顶点着色器的脚本内容
		mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh",
				mv.getResources());
		// 加载片元着色器的脚本内容
		mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh",
				mv.getResources());
		// 基于顶点着色器与片元着色器创建程序
		mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
		// 获取程序中顶点位置属性引用
		maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
		// 获取程序中顶点颜色属性引用
		maColorHandle = GLES30.glGetAttribLocation(mProgram, "aColor");
		// 获取程序中总变换矩阵引用
		muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
	}

	public void drawSelf() {
		// 指定使用某套着色器程序
		GLES30.glUseProgram(mProgram);
		// 将最终变换矩阵传入渲染管线
		GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
				MatrixState.getFinalMatrix(), 0);
		// 将顶点位置数据送入渲染管线
		GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT,
				false, 3 * 4, mVertexBuffer);
		// 将顶点颜色数据送入渲染管线
		GLES30.glVertexAttribPointer(maColorHandle, 4, GLES30.GL_FLOAT, false,
				4 * 4, mColorBuffer);
		// 启用顶点位置数据数组
		GLES30.glEnableVertexAttribArray(maPositionHandle);
		// 启用顶点颜色数据数组
		GLES30.glEnableVertexAttribArray(maColorHandle);
		
		GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);
	}
}

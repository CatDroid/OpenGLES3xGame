package com.bn.Sample7_7;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES30;

import javax.microedition.khronos.opengles.GL10;

public class Points {
	int mProgram;// 自定义渲染管线着色器程序id
	int muMVPMatrixHandle;// 总变换矩阵引用
	int maPositionHandle; // 顶点位置属性引用
	String mVertexShader;// 顶点着色器代码脚本
	String mFragmentShader;// 片元着色器代码脚本

	FloatBuffer mVertexBuffer;// 顶点坐标数据缓冲
	int vCount = 0;

	public Points(MySurfaceView mv) 
	{
		//调用初始化顶点数据的方法
		initVertexData();
		//调用初始化着色器的方法
		initShader(mv);
	}

	// 初始化顶点数据的方法
	public void initVertexData() 
	{

		float vertices[] = new float[] {

				0, Constant.UNIT_SIZE*2, 0,
				Constant.UNIT_SIZE, Constant.UNIT_SIZE/2, 0,
				-Constant.UNIT_SIZE/3, Constant.UNIT_SIZE, 0,
				-Constant.UNIT_SIZE*0.4f, -Constant.UNIT_SIZE*0.4f, 0,
				-Constant.UNIT_SIZE, -Constant.UNIT_SIZE, 0,
				Constant.UNIT_SIZE*0.2f, -Constant.UNIT_SIZE*0.7f, 0,
				Constant.UNIT_SIZE/2, -Constant.UNIT_SIZE*3/2, 0,
				-Constant.UNIT_SIZE*4/5, -Constant.UNIT_SIZE*3/2, 0,

				0, 0, 0.5f, // hhl 调整点的深度，使这个点在前方    管线流程:  深度测试 --> 颜色混合  如果靠前的先渲染 背后的渲染时候就会在深度测试被抛弃 靠前的在渲染时候已经跟当前framebuffer的值混合alpha

		};

		vCount = vertices.length / 3 ;

		// 创建顶点坐标数据缓冲
		// vertices.length*4是因为一个整数四个字节
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());// 设置字节顺序
		mVertexBuffer = vbb.asFloatBuffer();// 转换为Float型缓冲
		mVertexBuffer.put(vertices);// 向缓冲区中放入顶点坐标数据
		mVertexBuffer.position(0);// 设置缓冲区起始位置
		// 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
		// 转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题

	}

	// 初始化着色器
	public void initShader(MySurfaceView mv) {


		mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh",
				mv.getResources());
		mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh",
				mv.getResources());
		mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);


		maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
		muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");

	}
	public void drawSelf(int texId) 
	{

		GLES30.glUseProgram(mProgram);						// 指定使用某套着色器程序

		GLES30.glEnable(GLES30.GL_BLEND);					// 启用混合
		GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA); // 设置下要使用的混合方法


		/*
			glBlendFunc(sfactor, dfactor);
			sfactor 及 dfactor 分别代表源和目标颜色在混合时所占比重的枚举常量。

			glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
			是比较典型的半透明效果，
			如果源色 alpha 为0，则取目标色，
			如果源色alpha为1，则取源色，
			否则视源色的alpha大小各取一部分。
			源色的alpha越大，则源色取的越多，最终结果源色的表现更强；
			源色的alpha越小，则目标色“透过”的越多。


			此外在一般的渲染过程中，都会把有半透明效果的渲染放到后边，
			先把不透明的部分在深度测试启用的情况下渲染完，
			再关闭深度测试写入(glDepthMask(false))，并渲染半透明的部分。
			这样就不会出现  由于半透明且离镜头近的面  被先渲染时   污染深度缓冲了 ?????


		* */

		GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
				MatrixState.getFinalMatrix(), 0);			// 将最终变换矩阵传入渲染管线


		GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT,
				false, 3 * 4, mVertexBuffer);				// 将顶点位置数据传入渲染管线
		GLES30.glEnableVertexAttribArray(maPositionHandle);	// 启用顶点位置数据数组





        GLES30.glEnable(GLES30.GL_TEXTURE_2D);  //开启纹理
		GLES30.glActiveTexture(GLES30.GL_TEXTURE0);	//激活纹理
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);//绑定纹理



		GLES30.glDrawArrays(GLES30.GL_POINTS, 0, vCount);
	}
}

package com.bn.Sample5_10;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES30;

//颜色圆
public class Circle {
	int mProgram;// 自定义渲染管线着色器程序id
	int muMVPMatrixHandle;// 总变换矩阵引用
	int maPositionHandle; // 顶点位置属性引用
	int maColorHandle; // 顶点颜色属性引用
	String mVertexShader;// 顶点着色器代码脚本
	String mFragmentShader;// 片元着色器代码脚本

	FloatBuffer mVertexBuffer;// 顶点坐标数据缓冲
	FloatBuffer mColorBuffer;// 顶点着色数据缓冲
	private ByteBuffer mIndexBuffer;// 顶点索引数据缓冲
	int vCount = 0;//顶点数量
	int iCount = 0;//索引数量

	public Circle(MySurfaceView mv) {//构造器
		// 初始化顶点坐标与着色数据
		initVertexData();
		// 初始化shader
		initShader(mv);
	}

	// 初始化顶点坐标与着色数据的方法
	public void initVertexData() {
		// 顶点坐标数据的初始化================begin============================
		int n = 10; // hhl . 改为化成10个三角形
		vCount = n + 2;

		float angdegSpan = 360.0f / n;
		float[] vertices = new float[vCount * 3];//顶点坐标数据
		// 坐标数据初始化
		int count = 0;
		vertices[count++] = 0;
		vertices[count++] = 0;
		vertices[count++] = 0;

		for (float angdeg = 0; Math.ceil(angdeg) <= 360; angdeg += angdegSpan) {
			double angrad = Math.toRadians(angdeg);// 当前弧度
			// 当前点
			vertices[count++] = (float) (-Constant.UNIT_SIZE * Math.sin(angrad));// 顶点坐标
			vertices[count++] = (float) (Constant.UNIT_SIZE * Math.cos(angrad));
			vertices[count++] = 0;
		} // Mark.6 这里的角度 angdeg 定义的是 与y轴正方向的夹角 以右手螺旋为正
		//
		// 这就就是之前 使用 GL_TRIANGLE_FANS 产生的 所有外角  的坐标


		// 创建顶点坐标数据缓冲
		// vertices.length*4是因为一个整数四个字节
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());// 设置字节顺序
		mVertexBuffer = vbb.asFloatBuffer();// 转换为Float型缓冲
		mVertexBuffer.put(vertices);// 向缓冲区中放入顶点坐标数据
		mVertexBuffer.position(0);// 设置缓冲区起始位置
		// Mark.5 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
		// 					转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题

		// 顶点坐标数据的初始化================end============================
		
		// 三角形构造索引数据初始化==========begin==========================
		byte indices[] ={
				0,1,2,
				0,2,3,
				0,3,4,
				0,4,5,   // Mark.4 后面绘制方式  是   GL_TRIANGLES 而不是之前的 GL_TRIANGLE_FANS  所以会有重复顶点/索引
				0,5,6,	 // 每个索引都对应着 一个x y z 顶点坐标
				0,6,7,
				0,7,8,
				0,8,9,
				0,9,10, //  Mark.5 范围会超过 glDrawRangeElements 但还是会绘制出来
				0,10,1

		};
		iCount = indices.length; // 总共 3*8 = 24 个顶点

		// 创建三角形构造索引数据缓冲
		mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
		mIndexBuffer.put(indices);// 向缓冲区中放入三角形构造索引数据
		mIndexBuffer.position(0);// 设置缓冲区起始位置
		// 三角形构造索引数据初始化==========end==============================
		
		// 顶点着色数据的初始化================begin============================
		// 顶点颜色值数组，每个顶点4个色彩值RGBA
		count = 0;
        float colors[]=new float[vCount*4];
        colors[count++] = 1; 
        colors[count++] = 1; 
        colors[count++] = 1; 
        colors[count++] = 0;
        for(int i=4; i<colors.length; i+=4){
        	colors[count++] = 0; 
        	colors[count++] = 1; 
        	colors[count++] = 0; 
        	colors[count++] = 0;
        }
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
		// 获取程序中顶点位置属性引用id
		maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
		// 获取程序中顶点颜色属性引用id
		maColorHandle = GLES30.glGetAttribLocation(mProgram, "aColor");
		// 获取程序中总变换矩阵引用id
		muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
	}

	public void drawSelf(int start,int count) {
		//指定使用某套着色器程序
		GLES30.glUseProgram(mProgram);
		// 将最终变换矩阵传入渲染管线
		GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
				MatrixState.getFinalMatrix(), 0);


		//将顶点位置数据送入渲染管线
		GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, mVertexBuffer);
		//将顶点颜色数据送入渲染管线
		GLES30.glVertexAttribPointer(maColorHandle, 4, GLES30.GL_FLOAT, false, 4 * 4, mColorBuffer);
		//启用顶点位置数据数组
		GLES30.glEnableVertexAttribArray(maPositionHandle);
		//启用顶点颜色数据数组
		GLES30.glEnableVertexAttribArray(maColorHandle);


		// Mark.1 使用 glDraw[Range]Elements 需要 两个buffer : 顶点坐标buffer(跟glDrawArrays一样通过glVertexAttribPointer传入) + 索引buffer(glDraw传入)

		
		mIndexBuffer.position(start);//顶点构建索引数据缓冲的起始位置
		//用glDrawRangeElements方法绘制物体
		GLES30.glDrawRangeElements
		(
				GLES30.GL_TRIANGLES,	//绘制方式	      Mark.2 之前画圆形 是用 GL_TRIANGLE_FAN  使用GL_TRIANGLES绘制方法可以使用索引法来解决大量重复顶点问题
				0, 						//最小顶点索引值
				8, 						//最大顶点索引值	  Mark.3 这是 glDrawRangeElements 新增加的 告诉渲染管线需绘制的物体 对应的 顶点索引范围
				count, 					//索引数量		  Mark.3.1 使得管线在绘制物体时 可以首先了解绘制物体对应的顶点索引范围 并有机会将 该索引范围内的顶点数据优先加载到高速内存 使绘制工作执行更加高效
				GLES30.GL_UNSIGNED_BYTE, //数据类型		  Mark.3.2 范围只是影响高速缓存 并不会导致绘制效果的不一样 所有indexbuffer给定的顶点索引都会用上 并不是过滤掉!!
				mIndexBuffer
		);

		// 绘制图形
//		GLES30.glDrawElements(
//				GLES30.GL_TRIANGLES,
//				// 没有索引的最大值和最小值
//				iCount,
//				GLES30.GL_UNSIGNED_BYTE,
//				mIndexBuffer);//用索引法绘制图形

	}
}

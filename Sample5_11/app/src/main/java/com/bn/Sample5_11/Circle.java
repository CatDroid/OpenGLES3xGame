package com.bn.Sample5_11;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES30;

//颜色圆
public class Circle {
	int mProgram;// 自定义渲染管线着色器程序id
	int muMVPMatrixHandle;// 总变换矩阵引用
	String mVertexShader;// 顶点着色器代码脚本
	String mFragmentShader;// 片元着色器代码脚本

	FloatBuffer mVertexBuffer;// 顶点坐标数据缓冲
	FloatBuffer mColorBuffer;// 顶点着色数据缓冲
	private ByteBuffer mIndexBuffer;// 顶点索引数据缓冲
	int vCount = 0;
	int iCount = 0;

	public Circle(MySurfaceView mv) {
		// 初始化顶点坐标与着色数据
		initVertexData();
		// 初始化shader
		initShader(mv);
	}

	// 初始化顶点坐标与着色数据的方法
	public void initVertexData() {
		// 顶点坐标数据的初始化================begin============================
		int n = 10;
		vCount = n + 2;

		float angdegSpan = 360.0f / n;
		float[] vertices = new float[vCount * 3];// 顶点坐标数据
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
		}
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
		
		// 三角形构造索引数据初始化==========begin==========================
		iCount = vCount;
		byte indices[] = new byte[iCount];
		for(int i=0; i<iCount; i++){
			indices[i] = (byte) i;
		}

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

//	private int mPosIdx = 1 ;
//	private int mColorIdx = 2 ;

	private int mPosIdx = 1 ;
	private int mColorIdx = 2 ;

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
		// 获取程序中总变换矩阵引用id
		muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");


		// Mark.1 glBindAttribLocation 只要program编译连接之后 就可以在Java代码设置 绑定 顶点属性变量 对应的 位置/编号/引用号/
		// Mark.3 java中的 glBindAttribLocation 必须跟 shader中的 layout限定符的location一样
		// Mark.4 glEnableVertexAttribArray glVertexAttribPointer 要用 glBindAttribLocation 给定的
		// Mark.5 glBindAttriLocation的作用是 可以切换Program!! 两个Program中相识的两个顶点属性变量都用 同一个引用号

		// layout (location = 1) in vec3 aPosition;  //顶点位置   两个顶点的属性:顶点位置 颜色(这里没有纹理坐标)
		// layout (location = 2) in vec4 aColor;     //顶点颜色


		int posIdx = GLES30.glGetAttribLocation(mProgram ,"aPosition" );
		int colorIdx = GLES30.glGetAttribLocation(mProgram ,"aColor" );
		android.util.Log.w("TOM","before " + posIdx + " " + colorIdx);

		//把顶点位置属性变量索引与顶点着色器中的变量名进行绑定
		 GLES30.glBindAttribLocation(mProgram, mPosIdx, "aPosition");
		//把顶点颜色属性变量索引与顶点着色器中的变量名进行绑定
	     GLES30.glBindAttribLocation(mProgram, mColorIdx , "aColor");

		posIdx = GLES30.glGetAttribLocation(mProgram ,"aPosition" );
		colorIdx = GLES30.glGetAttribLocation(mProgram ,"aColor" ); // Mark.5 glGetAttribLocation是shader中layout限定符的location
		android.util.Log.w("TOM","after  " + posIdx + " " + colorIdx);
	}

	public void drawSelf() {
		// 指定使用某套着色器程序
		GLES30.glUseProgram(mProgram);
		// 将最终变换矩阵传入渲染管线
		GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
				MatrixState.getFinalMatrix(), 0);
		//将顶点位置数据送入渲染管线
		GLES30.glVertexAttribPointer(mPosIdx, 3, GLES30.GL_FLOAT,
				false, 3 * 4, mVertexBuffer);
		//将顶点颜色数据送入渲染管线
		GLES30.glVertexAttribPointer(mColorIdx, 4, GLES30.GL_FLOAT, false,
				4 * 4, mColorBuffer);
		//启用顶点位置数据数组
        GLES30.glEnableVertexAttribArray(mPosIdx);  // Mark.3 区别 glEnableVertexAttribArray glVertexAttribPointer是glBindAttribLocation给定的 不是通过glGetAttribLocation获取而已!!
      //启用顶点颜色数据数组
        GLES30.glEnableVertexAttribArray(mColorIdx);
        
		// 绘制图形
		GLES30.glDrawElements(GLES30.GL_TRIANGLE_FAN, iCount,
				GLES30.GL_UNSIGNED_BYTE, mIndexBuffer);//用索引法绘制图形
	}
}

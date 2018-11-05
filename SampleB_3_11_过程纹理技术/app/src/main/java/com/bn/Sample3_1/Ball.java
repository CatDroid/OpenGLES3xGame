package com.bn.Sample3_1;

import java.nio.ByteBuffer;

import static com.bn.Sample3_1.Constant.*;

import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import android.opengl.GLES30;

//球
public class Ball {
	int mProgram;//自定义渲染管线着色器程序id
	int muMVPMatrixHandle;//总变换矩阵引用
	int maPositionHandle; //顶点位置属性引用
	int maLongLatHandle; //顶点经纬度属性引用
	String mVertexShader;//顶点着色器代码脚本
	String mFragmentShader;//片元着色器代码脚本

	FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mLongLatBuffer;//顶点经纬度数据缓冲
	int vCount = 0;
	float yAngle = 0;//绕y轴旋转的角度
	float xAngle = 0;//绕x轴旋转的角度
	float zAngle = 0;//绕z轴旋转的角度
	float r = 0.8f;
	public Ball(MySurfaceView mv) {
		//初始化顶点数据
		initVertexData();
		//初始化着色器
		initShader(mv);
	}

	//初始化顶点数据的方法
	public void initVertexData() {
		//顶点坐标数据的初始化================begin============================
		ArrayList<Float> alVertix = new ArrayList<Float>();//存放顶点坐标的ArrayList
    	ArrayList<Float> alLongLat=new ArrayList<Float>();//存放顶点经纬度的ArrayList
		final int angleSpan = 10;//将球进行单位切分的角度
		for (int vAngle = -90; vAngle < 90; vAngle = vAngle + angleSpan)//垂直方向angleSpan度一份
		{
			for (int hAngle = 0; hAngle <= 360; hAngle = hAngle + angleSpan)//水平方向angleSpan度一份
			{//纵向横向各到一个角度后计算对应的此点在球面上的坐标    	
        		float x0=(float)(r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle))*Math.cos(Math.toRadians(hAngle)));
        		float y0=(float)(r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle))*Math.sin(Math.toRadians(hAngle)));
        		float z0=(float)(r*UNIT_SIZE*Math.sin(Math.toRadians(vAngle)));        		
        		float long0=hAngle; float lat0=vAngle;
        		
        		float x1=(float)(r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle))*Math.cos(Math.toRadians(hAngle+angleSpan)));
        		float y1=(float)(r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle))*Math.sin(Math.toRadians(hAngle+angleSpan)));
        		float z1=(float)(r*UNIT_SIZE*Math.sin(Math.toRadians(vAngle)));
        		float long1=hAngle+angleSpan; float lat1=vAngle;
        		
        		float x2=(float)(r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle+angleSpan))*Math.cos(Math.toRadians(hAngle+angleSpan)));
        		float y2=(float)(r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle+angleSpan))*Math.sin(Math.toRadians(hAngle+angleSpan)));
        		float z2=(float)(r*UNIT_SIZE*Math.sin(Math.toRadians(vAngle+angleSpan)));
        		float long2=hAngle+angleSpan; float lat2=vAngle+angleSpan;
        		
        		float x3=(float)(r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle+angleSpan))*Math.cos(Math.toRadians(hAngle)));
        		float y3=(float)(r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle+angleSpan))*Math.sin(Math.toRadians(hAngle)));
        		float z3=(float)(r*UNIT_SIZE*Math.sin(Math.toRadians(vAngle+angleSpan)));
        		float long3=hAngle; float lat3=vAngle+angleSpan;
        		
        		//将计算出来的XYZ坐标加入存放顶点坐标的ArrayList        		
        		alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);  
        		alVertix.add(x3);alVertix.add(y3);alVertix.add(z3);
        		alVertix.add(x0);alVertix.add(y0);alVertix.add(z0);
        		      		
        		alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);
        		alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
        		alVertix.add(x3);alVertix.add(y3);alVertix.add(z3);
        		
        		//将计算出来的顶点经纬度加入存放顶点经纬度的ArrayList        		
        		alLongLat.add(long1);alLongLat.add(lat1);
        		alLongLat.add(long3);alLongLat.add(lat3);
        		alLongLat.add(long0);alLongLat.add(lat0);
        		
        		alLongLat.add(long1);alLongLat.add(lat1);
        		alLongLat.add(long2);alLongLat.add(lat2);
        		alLongLat.add(long3);alLongLat.add(lat3);
        	}
		}
		vCount = alVertix.size() / 3;//顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标


		float vertices[] = new float[vCount * 3];
		for (int i = 0; i < alVertix.size(); i++) {
			vertices[i] = alVertix.get(i);
		}
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);


        float[] longlat=new float[alLongLat.size()];
        for(int i=0;i<alLongLat.size();i++) {
        	longlat[i]=alLongLat.get(i);
        }
        ByteBuffer llbb = ByteBuffer.allocateDirect(longlat.length*4);
        llbb.order(ByteOrder.nativeOrder());
        mLongLatBuffer=llbb.asFloatBuffer();
        mLongLatBuffer.put(longlat);
        mLongLatBuffer.position(0);        
	}

	//初始化着色器
	public void initShader(MySurfaceView mv) {
		//加载顶点着色器的脚本内容
		mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
		//加载片元着色器的脚本内容
		mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
		//基于顶点着色器与片元着色器创建程序
		mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
		//获取程序中顶点位置属性引用
		maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
		//获取程序中总变换矩阵引用
		muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
		//获取程序中顶点经纬度属性引用
        maLongLatHandle=GLES30.glGetAttribLocation(mProgram, "aLongLat");
	}

	public void drawSelf() {
		
    	MatrixState.rotate(xAngle, 1, 0, 0);//绕X轴转动
    	MatrixState.rotate(yAngle, 0, 1, 0);//绕Y轴转动
    	MatrixState.rotate(zAngle, 0, 0, 1);//绕Z轴转动

		//指定使用某套着色器程序
		GLES30.glUseProgram(mProgram);

		//将最终变换矩阵传入渲染管线
		GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
		  
        //将顶点经纬度数据传入渲染管线
        GLES30.glVertexAttribPointer(maLongLatHandle, 2, GLES30.GL_FLOAT, false, 2*4, mLongLatBuffer);
		//将顶点位置数据传入渲染管线
		GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, mVertexBuffer);
		//启用顶点位置、顶点经纬度数据数组
		GLES30.glEnableVertexAttribArray(maPositionHandle);
		GLES30.glEnableVertexAttribArray(maLongLatHandle);


		//绘制球		
		GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);
	}
}

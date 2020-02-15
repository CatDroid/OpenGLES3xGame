package com.bn.Sample6_4;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES30;

//用户绘制单个粒子的类
public class GrainForDraw 
{
	private FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	private FloatBuffer   mColorBuffer;//顶点颜色数据缓冲
    float scale;//星星尺寸
    
    String mVertexShader;//顶点着色器    	 
    String mFragmentShader;//片元着色器
    
    int mProgram;//自定义渲染管线程序id 
    int muMVPMatrixHandle;//总变换矩阵引用id
    int maPositionHandle; //顶点位置属性引用id  
    int uPointSizeHandle;//顶点尺寸参数引用
    int uColorHandle;//顶点颜色参数引用
    
    public GrainForDraw(float scale,float red,float green,float blue,MySurfaceView mv)
    {
    	this.scale=scale;
    	initVertexData(red,green,blue);
    	initShader(mv);
    }
    
    //初始化顶点坐标的方法
    public void initVertexData(float red,float green,float blue)
    {    	  	
    	//顶点坐标数据的初始化   
        float vertices[]={0,0,0};		
        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个Float四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为int型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        
        //顶点坐标数据的初始化
        float colors[]={red,green,blue};		
        //创建顶点坐标数据缓冲
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mColorBuffer = cbb.asFloatBuffer();//转换为int型缓冲
        mColorBuffer.put(colors);//向缓冲区中放入顶点坐标数据
        mColorBuffer.position(0);//设置缓冲区起始位置
    }

    //初始化shader
    public void initShader(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容       
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex_xk.glsl", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_xk.glsl", mv.getResources());
        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");        
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix"); 
        //获取顶点尺寸参数引用
        uPointSizeHandle = GLES30.glGetUniformLocation(mProgram, "uPointSize"); 
        //获取顶点颜色参数引用
        uColorHandle = GLES30.glGetUniformLocation(mProgram, "uColor"); 
    }
    
    public void drawSelf()
    {  
    	//指定使用某套shader程序
   	    GLES30.glUseProgram(mProgram);
        //将最终变换矩阵传入shader程序
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);  
        //将顶点尺寸传入Shader程序
        GLES30.glUniform1f(uPointSizeHandle, scale);  
        //将顶点颜色传入Shader程序
        GLES30.glUniform3fv(uColorHandle, 1,mColorBuffer); 
        //为画笔指定顶点位置数据    
        GLES30.glVertexAttribPointer        
        (
        		maPositionHandle,   
        		3, 
        		GLES30.GL_FLOAT, 
        		false,
                3*4, 
                mVertexBuffer   
        );   
        //允许顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maPositionHandle);         
        //绘制星星点    
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, 1); 
    }
}
package com.bn.Sample5_3_V1;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES30;

//加载后的物体——携带顶点信息，自动计算面平均法向量
public class LoadedObjectVertexNormal//加载后的物体
{	
	int mProgram;//自定义渲染管线着色器程序id  
    int muMVPMatrixHandle;//总变换矩阵引用
    int muMMatrixHandle;//位置、旋转变换矩阵
    int maPositionHandle; //顶点位置属性引用  
    int maNormalHandle; //顶点法向量属性引用  
    int maLightLocationHandle;//光源位置属性引用  
    int muMVPMatrixGYHandle;//光源总变换矩阵引用 
    int maCameraHandle; //摄像机位置属性引用 
    String mVertexShader;//顶点着色器代码脚本    	 
    String mFragmentShader;//片元着色器代码脚本    
    
    int mProgramForShadow;//自定义着色器程序id（距离纹理用）  
    int muMVPMatrixHandleForShadow;//总变换矩阵引用（距离纹理用）
    int muMMatrixHandleForShadow;//位置、旋转、缩放变换矩阵引用（距离纹理用）
    int maPositionHandleForShadow; //顶点位置属性引用（距离纹理用）  
    int maLightLocationHandleForShadow;//光源位置属性引用（距离纹理用）
    String mVertexShaderForShadow;//顶点着色器    	 
    String mFragmentShaderForShadow;//片元着色器    
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲  
	FloatBuffer   mNormalBuffer;//顶点法向量数据缓冲
    int vCount=0;     
    
    public LoadedObjectVertexNormal(MySurfaceView mv,float[] vertices,float[] normals)
    {    	   	
    	initVertexData(vertices,normals);//初始化顶点数据	       
    	initShaderForShadow(mv);//初始化绘制距离纹理的着色器
    }
    
    //初始化顶点数据  
    public void initVertexData(float[] vertices,float[] normals)
    {
    	//顶点坐标数据的初始化================begin============================
    	vCount=vertices.length/3;   
		
        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点坐标数据的初始化================end============================
        
        //顶点法向量数据的初始化================begin============================  
        ByteBuffer cbb = ByteBuffer.allocateDirect(normals.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mNormalBuffer.put(normals);//向缓冲区中放入顶点法向量数据
        mNormalBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点着色数据的初始化================end============================
    } 
    
    //初始化绘制距离纹理的着色器
    public void initShaderForShadow(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容
        mVertexShaderForShadow=ShaderUtil.loadFromAssetsFile("vertex_shadow.sh", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShaderForShadow=ShaderUtil.loadFromAssetsFile("frag_shadow.sh", mv.getResources());  
        //基于顶点着色器与片元着色器创建程序
        mProgramForShadow = ShaderUtil.createProgram(mVertexShaderForShadow, mFragmentShaderForShadow);
        //获取程序中顶点位置属性引用  
        maPositionHandleForShadow = GLES30.glGetAttribLocation(mProgramForShadow, "aPosition");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandleForShadow = GLES30.glGetUniformLocation(mProgramForShadow, "uMVPMatrix");  
        //获取位置、旋转变换矩阵引用
        muMMatrixHandleForShadow = GLES30.glGetUniformLocation(mProgramForShadow, "uMMatrix"); 
        //获取程序中光源位置引用
        maLightLocationHandleForShadow=GLES30.glGetUniformLocation(mProgramForShadow, "uLightLocation");
    }
    
    //绘制物体的方法（距离纹理用）
    public void drawSelfForShadow()
    {        
   	 	//指定使用某套着色器程序
   	 	GLES30.glUseProgram(mProgramForShadow);
        //将最终变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixHandleForShadow, 1, false, MatrixState.getFinalMatrix(), 0);      
        //将基本变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMMatrixHandleForShadow, 1, false, MatrixState.getMMatrix(), 0);   
        //将光源位置传入渲染管线   
        GLES30.glUniform3fv(maLightLocationHandleForShadow, 1, MatrixState.lightPositionFB);
        
        //将顶点位置数据传入渲染管线
        GLES30.glVertexAttribPointer  
        (
        		maPositionHandleForShadow,   
        		3, 
        		GLES30.GL_FLOAT, 
        		false,
               3*4,   
               mVertexBuffer
        );       
        //启用顶点位置数据
        GLES30.glEnableVertexAttribArray(maPositionHandleForShadow);  
        //绘制加载的物体
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
    }
}

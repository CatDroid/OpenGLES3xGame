package com.bn.Sample5_7;//声明包
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES30;

//加载后的物体——携带顶点信息，平均法向量
public class LoadedObjectVertexNormal
{	
	int mProgram1;//自定义渲染管线着色器程序id  

    int muMVPMatrixHandle1;//总变换矩阵引用
    int muMMatrixHandle1;//位置、旋转变换矩阵
    int maPositionHandle1; //顶点位置属性引用 
    int maNormalHandle1; //顶点法向量属性引用 
    int maLightLocationHandle1;//光源位置属性引用 
    int muSkyColorHandle1;
    int muGroundColorHandle1;
    
    String mVertexShader1;//顶点着色器    	 
    String mFragmentShader1;//片元着色器    
	
	FloatBuffer mVertexBuffer;//顶点坐标数据缓冲  
	FloatBuffer mNormalBuffer;//顶点法向量数据缓冲

    int vCount=0;     
    
    public LoadedObjectVertexNormal(MySurfaceView mv,float[] vertices,float[] normals)
    {    	
    	//初始化顶点坐标与着色数据
    	initVertexData(vertices,normals);
    	//初始化shader        
    	intShader(mv);
    }
    
    //初始化顶点坐标与着色数据的方法
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

    //初始化shader
    public void intShader(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容
        mVertexShader1=ShaderUtil.loadFromAssetsFile("vertex_ball.glsl", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader1=ShaderUtil.loadFromAssetsFile("frag_ball.glsl", mv.getResources());
        //基于顶点着色器与片元着色器创建程序
        mProgram1 = ShaderUtil.createProgram(mVertexShader1, mFragmentShader1);
       
        //获取程序中顶点位置属性引用 
        maPositionHandle1 = GLES30.glGetAttribLocation(mProgram1, "aPosition");
        //获取程序中顶点颜色属性引用 
        maNormalHandle1= GLES30.glGetAttribLocation(mProgram1, "aNormal");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle1 = GLES30.glGetUniformLocation(mProgram1, "uMVPMatrix");  
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle1 = GLES30.glGetUniformLocation(mProgram1, "uMMatrix"); 
        //获取程序中光源位置引用
        maLightLocationHandle1=GLES30.glGetUniformLocation(mProgram1, "uLightLocation");
        
        muSkyColorHandle1=GLES30.glGetUniformLocation(mProgram1, "uSkyColor");
        muGroundColorHandle1=GLES30.glGetUniformLocation(mProgram1, "uGroundColor");
    } 
    
    public void drawSelf()
    {   

		GLES30.glUseProgram(mProgram1);

		GLES30.glUniformMatrix4fv(muMVPMatrixHandle1, 1, false, MatrixState.getFinalMatrix(), 0);
		GLES30.glUniformMatrix4fv(muMMatrixHandle1, 1, false, MatrixState.getMMatrix(), 0);
		GLES30.glUniform3fv(maLightLocationHandle1, 1, MatrixState.lightPositionFB);

		//
		GLES30.glUniform3f(muSkyColorHandle1,1f,1f,1f);         // 将天空颜色传入渲染管线
		GLES30.glUniform3f(muGroundColorHandle1,0.1f,0.1f,0.1f);// 将大地颜色传入渲染管线

		GLES30.glVertexAttribPointer(maPositionHandle1, 3, GLES30.GL_FLOAT, false, 3*4, mVertexBuffer);
		GLES30.glVertexAttribPointer( maNormalHandle1,3, GLES30.GL_FLOAT,false,3*4, mNormalBuffer);

		GLES30.glEnableVertexAttribArray(maPositionHandle1);  
		GLES30.glEnableVertexAttribArray(maNormalHandle1);  

		GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);
    }
}

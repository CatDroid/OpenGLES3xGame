package com.bn.Sample4_8;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES30;

//纹理矩形
public class DrawFlare 
{	
	int mProgram;           // 自定义渲染管线着色器程序id
    int muMVPMatrixHandle;  // 总变换矩阵引用
    int maPositionHandle;   // 顶点位置属性引用
    int maTexCoorHandle;    // 顶点纹理坐标属性引用
    int mColorHandle;
    String mVertexShader;   // 顶点着色器
    String mFragmentShader; // 片元着色器
	
	FloatBuffer   mVertexBuffer;    // 顶点坐标数据缓冲
	FloatBuffer   mTexCoorBuffer;   // 顶点纹理坐标数据缓冲
    int vCount = 0;
    float UNIT_SIZE = 1.0f;
    
    public DrawFlare(MySurfaceView mv)
    {    	

    	initVertexData();   // 初始化顶点坐标与着色数据
    	intShader(mv);      // 初始化shader
    }
    

    public void initVertexData()
    {

        vCount=6;
       
        float vertices[]=new float[]
        {
        	-UNIT_SIZE,UNIT_SIZE,0,
        	-UNIT_SIZE,-UNIT_SIZE,0,
        	UNIT_SIZE,-UNIT_SIZE,0,
        	  
        	UNIT_SIZE,-UNIT_SIZE,0,
        	UNIT_SIZE,UNIT_SIZE,0,
        	-UNIT_SIZE,UNIT_SIZE,0
        };

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置



        float texCoor[]=new float[]
        {
        		1,0, 1,1, 0,1,
        		0,1, 0,0, 1,0        		
        };
        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
        cbb.order(ByteOrder.nativeOrder());
        mTexCoorBuffer = cbb.asFloatBuffer();
        mTexCoorBuffer.put(texCoor);
        mTexCoorBuffer.position(0);


    }

    //初始化shader
    public void intShader(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());  
        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用 
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用 
        maTexCoorHandle= GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");  
        
        mColorHandle=GLES30.glGetUniformLocation(mProgram, "color");
    }
    
    public void drawSelf(int texId,float[] color)
    {        
    	 //制定使用某套着色器程序
    	 GLES30.glUseProgram(mProgram); 
         //将最终变换矩阵传入着色器程序
         GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
         
         GLES30.glUniform4f(mColorHandle,color[0],color[1],color[2],color[3]); 
         //将顶点位置数据传入渲染管线
         GLES30.glVertexAttribPointer  
         (
         		maPositionHandle,   
         		3, 
         		GLES30.GL_FLOAT, 
         		false,
                3*4,   
                mVertexBuffer
         );       
         //将顶点纹理坐标数据传入渲染管线
         GLES30.glVertexAttribPointer  
         (
        		maTexCoorHandle, 
         		2, 
         		GLES30.GL_FLOAT, 
         		false,
                2*4,   
                mTexCoorBuffer
         );   
         //启用顶点位置、纹理坐标数据
         GLES30.glEnableVertexAttribArray(maPositionHandle);  
         GLES30.glEnableVertexAttribArray(maTexCoorHandle);  
         
         //绑定纹理
         GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
         GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
         
         //绘制纹理矩形
         GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
    }
}

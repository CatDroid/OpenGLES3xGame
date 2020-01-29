package com.bn.Sample5_11;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.annotation.SuppressLint;
import android.opengl.GLES30;

//纹理三角形
@SuppressLint("NewApi") 
public class TextureRect 
{
	int mProgram;//自定义渲染管线程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用
    int maTexCoorHandle; //顶点纹理坐标属性引用
    
    int textureOneHandle;
    int textureTwoHandle;
    int textureThreeHandle;
    int textureFourHandle;
    int textureFiveHandle;
    
    String mVertexShader;//顶点着色器代码脚本
    String mFragmentShader;//片元着色器代码脚本
	
	FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer mTexCoorBuffer;//顶点纹理坐标数据缓冲
    int vCount=0;
    
    public TextureRect(MySurfaceView mv,float ratio)
    {    	
    	//初始化顶点数据方法
    	initVertexData(ratio);
    	//初始化着色器方法
    	initShader(mv);
    }
    
    //初始化顶点数据方法
    public void initVertexData(float ratio)
    {

        vCount=6;
        final float UNIT_SIZE=1.0f;
        float vertices[]=new float[]
        {
        		-ratio*UNIT_SIZE,   UNIT_SIZE,  0,
        		-ratio*UNIT_SIZE,   -UNIT_SIZE, 0,
        		ratio*UNIT_SIZE,    -UNIT_SIZE, 0,
        		
        		ratio*UNIT_SIZE,    -UNIT_SIZE, 0,
        		ratio*UNIT_SIZE,    UNIT_SIZE,  0,
        		-ratio*UNIT_SIZE,   UNIT_SIZE,  0
        };
		

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        float texCoor[]=new float[]
        {    
        		0,      1,
                0,      0,
                1,      0,
        		1,      0,
                1,      1,
                0,      1
        };        

        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
        cbb.order(ByteOrder.nativeOrder());
        mTexCoorBuffer = cbb.asFloatBuffer();
        mTexCoorBuffer.put(texCoor);
        mTexCoorBuffer.position(0);
    }

    //初始化着色器方法
    public void initShader(MySurfaceView mv)
    {
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex_tex.glsl", mv.getResources());
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_tex.glsl", mv.getResources());
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);


        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        maTexCoorHandle= GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        
        textureOneHandle=GLES30.glGetUniformLocation(mProgram, "sTextureOne"); 
        textureTwoHandle=GLES30.glGetUniformLocation(mProgram, "sTextureTwo"); 
        textureThreeHandle=GLES30.glGetUniformLocation(mProgram, "sTextureThree"); 
        textureFourHandle=GLES30.glGetUniformLocation(mProgram, "sTextureFour"); 
        textureFiveHandle=GLES30.glGetUniformLocation(mProgram, "sTextureFive"); 


        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");  
    }
    
    public void drawSelf(int texId1,int texId2,int texId3,int texId4,int texId5)
    {        
    	 //指定使用某套着色程序
    	 GLES30.glUseProgram(mProgram);
    	 MatrixState.pushMatrix();
         //设置沿Z轴正向位移1
         //MatrixState.translate(0, 0, 1); // 这个删除没有问题
         //将最终变换矩阵传入渲染管线
         GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
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
         //启用顶点位置数据数组
         GLES30.glEnableVertexAttribArray(maPositionHandle);
         //启用顶点纹理数据数组
         GLES30.glEnableVertexAttribArray(maTexCoorHandle);  
         
         //绑定纹理
         GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
         GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId1);
         GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
         GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId2);
         GLES30.glActiveTexture(GLES30.GL_TEXTURE2);
         GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId3);
         GLES30.glActiveTexture(GLES30.GL_TEXTURE3);
         GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId4);
         GLES30.glActiveTexture(GLES30.GL_TEXTURE4);
         GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId5);
         
         GLES30.glUniform1i(textureOneHandle, 0);
         GLES30.glUniform1i(textureTwoHandle, 1);
         GLES30.glUniform1i(textureThreeHandle, 2);
         GLES30.glUniform1i(textureFourHandle, 3);
         GLES30.glUniform1i(textureFiveHandle, 4);
         
         //绘制纹理矩形
         GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
         MatrixState.popMatrix();
    }
}

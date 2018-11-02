package com.bn.Sample2_3;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES30;
/*
 * 用于绘制海底
 */
public class FloorRect 
{	
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用  
    int maTexCoorHandle; //顶点纹理坐标属性引用  
    String mVertexShader;//顶点着色器    	 
    String mFragmentShader;//片元着色器
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲
    int vCount=0;   
    float texSize=5;
    public FloorRect(int mProgram,float width,float height)
    {    	
    	this.mProgram=mProgram;
    	//初始化顶点数据
    	initVertexData(width,height);
    	//初始化着色器        
    	initShader();
    }
    //初始化顶点数据的方法
    public void initVertexData(float width,float height)
    {
        vCount=6;
        float vertices[]=new float[]
        {
    		0,0,0,
        	0,0,height,
        	width,0,height,
        	
        	width,0,height,
        	width,0,0,
        	0,0,0
        };
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //顶点纹理坐标数据
        float []texCoor=new float[] // hhl 物体是矩形，纹理坐标设置为超过1.0 而且纹理的拉伸方式是REPEAT的话,就可以重复
        {
          		0,0, 0,texSize, texSize,texSize,
          		texSize,texSize, texSize,0, 0,0        
        };
        //创建顶点纹理坐标数据缓冲
        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTexCoorBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mTexCoorBuffer.put(texCoor);//向缓冲区中放入顶点着色数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
    }
    //初始化着色器
    public void initShader()
    {
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用  
        maTexCoorHandle= GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");  
    }
    public void drawSelf(int texId)
    {        
    	 //指定使用某套shader程序
    	 GLES30.glUseProgram(mProgram);
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
         //启用顶点位置、纹理坐标数据数组
         GLES30.glEnableVertexAttribArray(maPositionHandle);  
         GLES30.glEnableVertexAttribArray(maTexCoorHandle);  
         //绑定纹理
         GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
         GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
         //绘制纹理矩形
         GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
    }
}

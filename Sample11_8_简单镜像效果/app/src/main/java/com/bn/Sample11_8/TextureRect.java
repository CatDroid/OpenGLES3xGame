package com.bn.Sample11_8;
import static com.bn.Sample11_8.Constant.CONFIG_DRAW_DEPTH;
import static com.bn.Sample11_8.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES30;

//表示地板的纹理矩形
public class TextureRect 
{	
	int mProgram;//自定义渲染管线程序id
    int muMVPMatrixHandle;//总变换矩阵引用id
    int maPositionHandle; //顶点位置属性引用id  
    int maTexCoorHandle; //顶点纹理坐标属性引用id  
    String mVertexShader;//顶点着色器    	 
    String mFragmentShader;//片元着色器
    static float[] mMMatrix = new float[16];//具体物体的移动旋转矩阵
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲
    int vCount=0;   
    
    public TextureRect(MySurfaceView mv,float width,float height)
    {    	
    	//初始化顶点坐标与着色数据
    	initVertexData(width,height);
    	//初始化shader        
    	initShader(mv);
    }
    
    //初始化顶点坐标与着色数据的方法
    public void initVertexData(float width,float height)
    {
    	//顶点坐标数据的初始化
        vCount=6;
        final float UNIT_SIZE=1.0f;
        float vertices[]=new float[]
        {
        	-width*UNIT_SIZE,0,-height*UNIT_SIZE,
        	-width*UNIT_SIZE,0,height*UNIT_SIZE,     
        	width*UNIT_SIZE,0,height*UNIT_SIZE,
        	
        	width*UNIT_SIZE,0,height*UNIT_SIZE,
        	width*UNIT_SIZE,0,-height*UNIT_SIZE,     
        	-width*UNIT_SIZE,0,-height*UNIT_SIZE,
        };
		
        //创建顶点坐标数据缓冲
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices).position(0);
        
        //顶点纹理坐标数据的初始化
        float texCoor[]=new float[]//顶点颜色值数组，每个顶点4个色彩值RGBA
        {
            0,      0,
            0,      0.642f,         // hhl 纹理图片是512x512的但是 实际只有512*328有实际图像(??考虑对齐??)
            1,      0.642f,
            1,      0.642f,
            1,      0,
            0,      0
        };       
        //创建顶点纹理坐标数据缓冲
        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
        cbb.order(ByteOrder.nativeOrder());
        mTexCoorBuffer = cbb.asFloatBuffer();
        mTexCoorBuffer.put(texCoor).position(0);
    }

    //初始化shader
    public void initShader(MySurfaceView mv)
    {

        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex_tex.sh", mv.getResources());
        if(CONFIG_DRAW_DEPTH){
            mFragmentShader=ShaderUtil.loadFromAssetsFile("drag_depth_frag_tex.sh", mv.getResources());
        }else{
            mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_tex.sh", mv.getResources());
        }
        mProgram = createProgram(mVertexShader, mFragmentShader);


        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        maTexCoorHandle= GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");  
    }
    
    public void drawSelf(int texId)
    {        

    	 GLES30.glUseProgram(mProgram);
         GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
         GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3*4, mVertexBuffer);
         GLES30.glVertexAttribPointer(maTexCoorHandle, 2, GLES30.GL_FLOAT, false, 2*4, mTexCoorBuffer);

         //允许顶点位置、纹理坐标数据数组
         GLES30.glEnableVertexAttribArray(maPositionHandle);  
         GLES30.glEnableVertexAttribArray(maTexCoorHandle);  
         
         //绑定纹理
         GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
         GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
         
         //绘制纹理矩形
         GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
    }
}

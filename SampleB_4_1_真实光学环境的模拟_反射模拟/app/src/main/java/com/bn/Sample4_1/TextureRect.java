package com.bn.Sample4_1;
import static com.bn.Sample4_1.Constant.*;
import static com.bn.Sample4_1.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.annotation.SuppressLint;
import android.opengl.GLES30;

//纹理矩形
public class TextureRect 
{	
	private int mProgram;           //自定义渲染管线着色器程序id
    private int muMVPMatrixHandle;  //总变换矩阵引用
    private int maPositionHandle;   //顶点位置属性引用
    private int maTexCoorHandle;    //顶点纹理坐标属性引用


    private FloatBuffer mVertexBuffer;      //顶点坐标数据缓冲
    private FloatBuffer mTexCoorBuffer;     //顶点纹理坐标数据缓冲
    private FloatBuffer mTexCoorBufferNotFlip; //顶点纹理坐标数据缓冲
    private int vCount=0;
    
    public TextureRect(MySurfaceView mv)
    {
    	initVertexData();
    	intShader(mv);
    }
    

    private void initVertexData()
    {

        vCount=6;
       
        float vertices[]=new float[]
        {
        	-UNIT_SIZE, UNIT_SIZE,  0,
        	-UNIT_SIZE, -UNIT_SIZE, 0,
        	UNIT_SIZE,  -UNIT_SIZE, 0,
        	  
        	UNIT_SIZE,  -UNIT_SIZE, 0,
        	UNIT_SIZE,  UNIT_SIZE,  0,
        	-UNIT_SIZE, UNIT_SIZE,  0
        };
		

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder()); // 设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();// 转换为Float型缓冲
        mVertexBuffer.put(vertices);        // 向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);          // 设置缓冲区起始位置
        // 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        // 转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        // 顶点纹理坐标数据的初始化================end============================

        {
            float texCoor[]=new float[]
                    {
                            1,0, 1,1, 0,1,
                            0,1, 0,0, 1,0
                    };

            //创建顶点纹理坐标数据缓冲
            ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
            cbb.order(ByteOrder.nativeOrder());
            mTexCoorBuffer = cbb.asFloatBuffer();
            mTexCoorBuffer.put(texCoor);
            mTexCoorBuffer.position(0);
        }
        {
            float texCoor2[]=new float[]
                    {
                            0,1, 0,0, 1,0,
                            1,0, 1,1, 0,1
                    };

            //创建顶点纹理坐标数据缓冲
            ByteBuffer cbb2 = ByteBuffer.allocateDirect(texCoor2.length*4);
            cbb2.order(ByteOrder.nativeOrder());
            mTexCoorBufferNotFlip = cbb2.asFloatBuffer();
            mTexCoorBufferNotFlip.put(texCoor2);
            mTexCoorBufferNotFlip.position(0);
        }




    }


    @SuppressLint("NewApi")
	public void intShader(MySurfaceView mv)
    {
        String mVertexShader=ShaderUtil.loadFromAssetsFile("vertex_tex.sh", mv.getResources());
        String mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_tex.sh", mv.getResources());
        mProgram = createProgram(mVertexShader, mFragmentShader);

        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        maTexCoorHandle= GLES30.glGetAttribLocation(mProgram, "aTexCoor");

        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");  
    }

    public void drawSelfWithFlip(int texId , boolean horizonFlip){

        GLES30.glUseProgram(mProgram);

        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);

        GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3*4, mVertexBuffer);
        if(horizonFlip){
            GLES30.glVertexAttribPointer(maTexCoorHandle, 2, GLES30.GL_FLOAT, false, 2*4, mTexCoorBuffer);
        }else{
            GLES30.glVertexAttribPointer(maTexCoorHandle, 2, GLES30.GL_FLOAT, false, 2*4, mTexCoorBufferNotFlip);
        }

        GLES30.glEnableVertexAttribArray(maPositionHandle);
        GLES30.glEnableVertexAttribArray(maTexCoorHandle);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);
    }
    
    @SuppressLint("NewApi")
	public void drawSelf(int texId)
    {
        drawSelfWithFlip(texId,true);
    }
}

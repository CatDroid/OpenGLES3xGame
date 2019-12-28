package com.bn.Sample5_3_V2;

import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;



//纹理矩形
public class TextureRect 
{	
	private int mProgram;
    private int muMVPMatrixHandle;
    private int muUsingTextureDepth;
    private int maPositionHandle;
    private int maTexCoorHandle;
    private String mVertexShader;
    private String mFragmentShader;
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTexCoorBuffer;
    private int vCount = 0;
    
    public TextureRect(MySurfaceView mv)
    {
    	initVertexData();
    	intShader(mv);
    }
    

    private void initVertexData()
    {
        vCount = 6;
        final float UNIT_SIZE = 0.15f;
        float vertices[] = new float[]
        {
        	-4*UNIT_SIZE,   4*UNIT_SIZE,    0,
        	-4*UNIT_SIZE,   -4*UNIT_SIZE,   0,
        	4*UNIT_SIZE,    -4*UNIT_SIZE,   0,
        	
        	4*UNIT_SIZE,    -4*UNIT_SIZE,   0,
        	4*UNIT_SIZE,    4*UNIT_SIZE,    0,
        	-4*UNIT_SIZE,   4*UNIT_SIZE,    0
        };
		

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        float texCoor[]=new float[]
        {
        		0,       1,
                0,       0,
                1.0f,    0,
                1.0f,    0,
                1.0f,    1,
                0,       1
        };        

        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
        cbb.order(ByteOrder.nativeOrder());
        mTexCoorBuffer = cbb.asFloatBuffer();
        mTexCoorBuffer.put(texCoor);
        mTexCoorBuffer.position(0);

    }


    private void intShader(MySurfaceView mv)
    {

        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex_display_shadow.glsl", mv.getResources());
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag_display_shadow.glsl", mv.getResources());
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);

        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        maTexCoorHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoor");

        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        muUsingTextureDepth = GLES30.glGetUniformLocation(mProgram, "usingTextureDepth");
    }
    
    public void drawSelf(int texId)
    {
        GLES30.glUseProgram(mProgram);

        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);

        GLES30.glUniform1f(muUsingTextureDepth, 0.0f);

        GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3*4, mVertexBuffer);
        GLES30.glVertexAttribPointer(maTexCoorHandle, 2, GLES30.GL_FLOAT, false, 2*4, mTexCoorBuffer);

        GLES30.glEnableVertexAttribArray(maPositionHandle);
        GLES30.glEnableVertexAttribArray(maTexCoorHandle);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);
    }
}

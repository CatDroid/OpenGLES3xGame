package com.bn.Sample12_3;

import android.opengl.GLES20;
import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Hanlon on 2018/12/13.
 */

public class SimpleDrawer {

    private String vertex =
            "attribute vec4 aPosition;\n" +
            "attribute vec2 aTextureCoord;\n" +
            "varying vec2 vTextureCoord;\n" +
            "\n" +
            "void main() {\n" +
            "  gl_Position = aPosition;\n" +
            "  vTextureCoord = aTextureCoord;\n" +
            "}";

    private String frag =
            "precision mediump float;\n" +
            "\n" +
            "varying vec2 vTextureCoord;\n" +
            "uniform sampler2D sTexture;\n" +
            "\n" +

            "\n" +
            "void main() {\n" +
            "  vec4 color = texture2D(sTexture, vTextureCoord);\n" +
            "  gl_FragColor = color;\n" +
            "}";

    public SimpleDrawer(){

    }

    private int mProgram = 0 ;
    private int maPosition = 0;
    private int maTexCoor = 0 ;
    private int muSampler = 0 ;
    FloatBuffer mVertexBuffer;
    FloatBuffer mTexCoorBuffer ;
    private int mWidth = 0 ;
    private int mHeight = 0 ;
    public void init(int width , int height){

        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        int fragmentShader =  GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);


        GLES20.glShaderSource(vertexShader,vertex);
        GLES20.glShaderSource(fragmentShader,frag);


        GLES20.glCompileShader(vertexShader);;
        GLES20.glCompileShader(fragmentShader);


        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

        assert ( GLES30.glGetError() != GLES30.GL_NO_ERROR );


        float vertexCoords[] = {
                -1.0f,  1.0f, 0.0f,   // top left
                -1.0f, -1.0f, 0.0f,   // bottom left
                1.0f, -1.0f, 0.0f,   // bottom right
                1.0f,  1.0f, 0.0f }; // top right


        ByteBuffer bb = ByteBuffer.allocateDirect(4*vertexCoords.length);
        bb.order(ByteOrder.nativeOrder());

        mVertexBuffer = bb.asFloatBuffer();
        mVertexBuffer.put(vertexCoords);
        mVertexBuffer.position(0);

        maPosition = GLES20.glGetAttribLocation(mProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(maPosition);
        GLES20.glVertexAttribPointer(maPosition, 3, GLES20.GL_FLOAT, false, 3*4, mVertexBuffer);


        assert ( GLES30.glGetError() != GLES30.GL_NO_ERROR );


        float texCoords[] = {       // 原点在左下
                0f,     1f,         // top left
                0f,     0f,         // bottom left
                1f,     0f,        // bottom right
                1f,     1f,  };    // top right


        bb = ByteBuffer.allocateDirect(4*texCoords.length);
        bb.order(ByteOrder.nativeOrder());

        mTexCoorBuffer = bb.asFloatBuffer();
        mTexCoorBuffer.put(texCoords);
        mTexCoorBuffer.position(0);

        maTexCoor = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        GLES20.glEnableVertexAttribArray(maTexCoor);
        GLES20.glVertexAttribPointer(maTexCoor, 2, GLES20.GL_FLOAT, false, 2*4, mTexCoorBuffer);

        assert ( GLES30.glGetError() != GLES30.GL_NO_ERROR );

        muSampler = GLES20.glGetUniformLocation(mProgram, "sTexture");

    }




    public void draw(int srcTexture){

        GLES30.glUseProgram(mProgram);
        GLES30.glClearColor(0.0f,1.0f,0.0f,1.0f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        boolean on = GLES30.glIsEnabled(GLES30.GL_CULL_FACE);
        boolean onDepth = GLES30.glIsEnabled(GLES30.GL_DEPTH_TEST);
        boolean onBlend = GLES30.glIsEnabled(GLES30.GL_BLEND);
        boolean onStencil = GLES30.glIsEnabled(GLES30.GL_STENCIL);
        GLES30.glDisable(GLES30.GL_CULL_FACE);
        GLES30.glDisable(GLES30.GL_DEPTH_TEST);
        GLES30.glDisable(GLES30.GL_BLEND);
        GLES30.glDisable(GLES30.GL_STENCIL_TEST);


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,srcTexture);
        GLES20.glUniform1i(muSampler, 0);

        GLES20.glEnableVertexAttribArray(maPosition);
        GLES20.glVertexAttribPointer(maPosition, 3, GLES20.GL_FLOAT, false, 3*4, mVertexBuffer);

        GLES20.glEnableVertexAttribArray(maTexCoor);
        GLES20.glVertexAttribPointer(maTexCoor, 2, GLES20.GL_FLOAT, false, 2*4, mTexCoorBuffer);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP,0,4);

        GLES20.glDisableVertexAttribArray(maPosition);
        GLES20.glDisableVertexAttribArray(maTexCoor);
        if(on)GLES30.glEnable(GLES30.GL_CULL_FACE);
        if(onDepth) GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        if(onBlend) GLES30.glEnable(GLES30.GL_BLEND);
        if(onStencil) GLES30.glEnable(GLES30.GL_STENCIL_TEST);

    }


}

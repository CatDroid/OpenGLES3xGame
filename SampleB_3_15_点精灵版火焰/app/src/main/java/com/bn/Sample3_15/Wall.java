package com.bn.Sample3_15;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES30;

import static com.bn.Sample3_15.ShaderUtil.createProgram;


public class Wall {

    private int mProgram;//自定义渲染管线程序id

    private int muMVPMatrixHandle;  //总变换矩阵引用id
    private int muMMatrixHandle;    //位置、旋转变换矩阵
    private int maLightLocationHandle;  // 光源位置属性引用
    private int maCameraHandle;         // 摄像机位置属性引用
    private int maNormalHandle;         // 顶点法向量属性引用
    private int maPositionHandle;       // 顶点位置属性引用id
    private int maTexCoorHandle;        // 顶点纹理坐标属性引用id


    private FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    private FloatBuffer mNormalBuffer;//顶点法向量数据缓冲
    private FloatBuffer mTexCoorBuffer;//顶点纹理坐标数据缓冲

    private int vCount = 0;     // hhl 顶点的数目 glDrawArrays要给定顶点的数目 glDrawElements需要给定索引数目和索引的buffer


    Wall(MySurfaceView mv, float wallsLength) {


        // 初始化顶点坐标与着色数据  // hhl  立方体屋子以1为单位长度
        initVertexData(wallsLength);
        //初始化着色器
        initShader(mv);
    }


    private void initVertexData(float wallsLength) {

        vCount = 6;
        float vertices[] = new float[]{
              -wallsLength, 0, -wallsLength,     wallsLength, 0, wallsLength,
              -wallsLength, 0, wallsLength,      -wallsLength, 0, -wallsLength,
              wallsLength, 0, -wallsLength,      wallsLength, 0, wallsLength,};
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);


        float normalVertex[] = new float[]{// 法向量 用来计算光照的 都沿y轴正方向
             0,1,0,   0,1,0,
             0,1,0,   0,1,0,
             0,1,0,   0,1,0 };
        ByteBuffer nbb = ByteBuffer.allocateDirect(normalVertex.length * 4);
        nbb.order(ByteOrder.nativeOrder());
        mNormalBuffer = nbb.asFloatBuffer();
        mNormalBuffer.put(normalVertex);
        mNormalBuffer.position(0);


        float texCoor[] = new float[]{
            0, 0, 1, 1,
            0, 1, 0, 0,
            1, 0, 1, 1};
        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        mTexCoorBuffer = cbb.asFloatBuffer();
        mTexCoorBuffer.put(texCoor);
        mTexCoorBuffer.position(0);
    }


    private void initShader(MySurfaceView mv) {

        //加载顶点着色器的脚本内容
        String mVertexShader = ShaderUtil.loadFromAssetsFile("vertex_brazier.sh", mv.getResources());
        //加载片元着色器的脚本内容
        String mFragmentShader = ShaderUtil.loadFromAssetsFile("frag_brazier.sh", mv.getResources());
        //基于顶点着色器与片元着色器创建程序
        mProgram = createProgram(mVertexShader, mFragmentShader);


        //获取程序中顶点颜色属性引用  
        maNormalHandle = GLES30.glGetAttribLocation(mProgram, "aNormal");
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中光源位置引用
        maLightLocationHandle = GLES30.glGetUniformLocation(mProgram, "uLightLocation");

        //获取程序中顶点纹理坐标属性引用id  
        maTexCoorHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取程序中摄像机位置引用
        maCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera");
    }

    void drawSelf(int texId) {

        GLES30.glUseProgram(mProgram);

        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        GLES30.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
        // 两个等效
        //GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
        GLES30.glUniform3f(maCameraHandle,MatrixState.cx, MatrixState.cy, MatrixState.cz);

        GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        GLES30.glVertexAttribPointer(maNormalHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, mNormalBuffer);
        GLES30.glVertexAttribPointer(maTexCoorHandle, 2, GLES30.GL_FLOAT, false, 2 * 4, mTexCoorBuffer);
        GLES30.glEnableVertexAttribArray(maPositionHandle);
        GLES30.glEnableVertexAttribArray(maNormalHandle);
        GLES30.glEnableVertexAttribArray(maTexCoorHandle);


        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);


        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);
    }
}

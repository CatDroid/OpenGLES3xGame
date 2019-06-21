package com.bn.Sample4_5;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES30;

//加载后的物体——仅携带顶点信息，颜色随机
public class LoadedObjectVertexNormalTexture {
    int mProgram;           // 自定义渲染管线着色器程序id
    int muMVPMatrixHandle;  // 总变换矩阵引用
    int muMMatrixHandle;    // 位置、旋转变换矩阵
    int muLightLocationHandle;  // 光源位置属性引用
    int muCameraHandle;         // 摄像机位置属性引用
    int maPositionHandle;       // 顶点位置属性引用
    int maNormalHandle;         // 顶点法向量属性引用
    int maTangentHandle;        // 顶点切向量属性引用

    int maTexCoorHandle;        // 顶点纹理坐标属性引用


    FloatBuffer mVertexBuffer;  // 顶点坐标数据缓冲
    FloatBuffer mNormalBuffer;  // 顶点法向量数据缓冲
    FloatBuffer mTangentBuffer; // 顶点切向量数据缓冲
    FloatBuffer mTexCoorBuffer; // 顶点纹理坐标数据缓冲
    int vCount = 0;

    int uTexHandle;             // 外观纹理
    int uNormalTexHandle;       // 法线纹理

    public LoadedObjectVertexNormalTexture(MySurfaceView mv, float[] vertices, float[] normals, float[] texCoors, float[] tangent) {//带有凹凸贴图的物体
        initVertexData(vertices, normals, texCoors, tangent);
        initShader(mv);
    }

    public LoadedObjectVertexNormalTexture(MySurfaceView mv, float[] vertices, float[] normals, float[] texCoors) {//普通物体
        initVertexDataN(vertices, normals, texCoors);
        initShaderN(mv);
    }

    // 初始化普通物体顶点数据的方法
    private void initVertexDataN(float[] vertices, float[] normals, float texCoors[]) {

        vCount = vertices.length / 3;

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        ByteBuffer cbb = ByteBuffer.allocateDirect(normals.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        mNormalBuffer = cbb.asFloatBuffer();
        mNormalBuffer.put(normals);
        mNormalBuffer.position(0);

        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoors.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        mTexCoorBuffer = tbb.asFloatBuffer();
        mTexCoorBuffer.put(texCoors);
        mTexCoorBuffer.position(0);
    }

    // 初始化带有凹凸贴图的物体顶点数据的方法
    private void initVertexData(float[] vertices, float[] normals, float texCoors[], float[] tangent) {

        vCount = vertices.length / 3;

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);


        ByteBuffer cbb = ByteBuffer.allocateDirect(normals.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        mNormalBuffer = cbb.asFloatBuffer();
        mNormalBuffer.put(normals);
        mNormalBuffer.position(0);


        ByteBuffer tnbb = ByteBuffer.allocateDirect(tangent.length * 4);
        tnbb.order(ByteOrder.nativeOrder());
        mTangentBuffer = tnbb.asFloatBuffer();
        mTangentBuffer.put(tangent);
        mTangentBuffer.position(0);

        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoors.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        mTexCoorBuffer = tbb.asFloatBuffer();
        mTexCoorBuffer.put(texCoors);
        mTexCoorBuffer.position(0);

    }


    // 初始化 凹凸贴图 的着色器
    public void initShader(MySurfaceView mv) {

        String vertexShader = ShaderUtil.loadFromAssetsFile("vertex_ut.sh", mv.getResources());
        String fragmentShader = ShaderUtil.loadFromAssetsFile("frag_ut.sh", mv.getResources());
        mProgram = ShaderUtil.createProgram(vertexShader, fragmentShader);

        // hhl 顶点属性:顶点坐标 法向量 切向量 纹理坐标(用于纹理贴图 和 法线贴图)
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        maNormalHandle = GLES30.glGetAttribLocation(mProgram,   "aNormal");
        maTangentHandle = GLES30.glGetAttribLocation(mProgram,  "tNormal");
        maTexCoorHandle = GLES30.glGetAttribLocation(mProgram,  "aTexCoor");


        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
        muLightLocationHandle = GLES30.glGetUniformLocation(mProgram, "uLightLocationSun");
        muCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera");

        uTexHandle = GLES30.glGetUniformLocation(mProgram, "sTextureWg");
        uNormalTexHandle = GLES30.glGetUniformLocation(mProgram, "sTextureNormal");
    }


    // 初始化普通物体的着色器
    private void initShaderN(MySurfaceView mv) {

        String vertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        String fragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        mProgram = ShaderUtil.createProgram(vertexShader, fragmentShader);

        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        maNormalHandle = GLES30.glGetAttribLocation(mProgram, "aNormal");
        maTexCoorHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoor");

        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
        muLightLocationHandle = GLES30.glGetUniformLocation(mProgram, "uLightLocation");
        muCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera");
    }

    // 绘制带有凹凸贴图的物体
    public void drawSelf(int texId, int texIdNormal) {

        GLES30.glUseProgram(mProgram);
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        GLES30.glUniform3fv(muLightLocationHandle, 1, MatrixState.lightPositionFB);
        GLES30.glUniform3fv(muCameraHandle, 1, MatrixState.cameraFB);
        GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, mVertexBuffer);


        //    hhl 粗模的法向量        通过顶点属性传入
        //        细模的法向量/纹理图    通过纹理传入
        GLES30.glVertexAttribPointer(maNormalHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, mNormalBuffer);

        GLES30.glVertexAttribPointer(maTangentHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, mTangentBuffer);
        GLES30.glVertexAttribPointer(maTexCoorHandle, 2, GLES30.GL_FLOAT, false, 2 * 4, mTexCoorBuffer);

        GLES30.glEnableVertexAttribArray(maPositionHandle);     // 启用顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maNormalHandle);       // 启用法向量、纹理坐标数据数组
        GLES30.glEnableVertexAttribArray(maTangentHandle);      // 启用切向量数据数组
        GLES30.glEnableVertexAttribArray(maTexCoorHandle);      // 启用纹理坐标数据数组

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);       // 绑定外观纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texIdNormal); // 绑定法向量纹理/法线贴图
        GLES30.glUniform1i(uTexHandle, 0);
        GLES30.glUniform1i(uNormalTexHandle, 1);


        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);      // 绘制加载的物体
    }

    // 绘制普通物体
    public void drawSelfN(int texId) {

        GLES30.glUseProgram(mProgram);

        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        GLES30.glUniform3fv(muLightLocationHandle, 1, MatrixState.lightPositionFB);
        GLES30.glUniform3fv(muCameraHandle, 1, MatrixState.cameraFB);

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

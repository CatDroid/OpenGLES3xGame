package com.bn.Sample3_12;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES30;

//加载后的物体——仅携带顶点信息，颜色随机
public class LoadedObjectVertexNormal {
    int mProgram;               // 自定义渲染管线着色器程序id
    int muMVPMatrixHandle;      // 总变换矩阵引用
    int muMMatrixHandle;        // 位置、旋转变换矩阵
    int maPositionHandle;       // 顶点位置属性引用
    int maNormalHandle;         // 顶点法向量属性引用
    int muLightLocationHandle;  // 光源位置属性引用
    int muCameraHandle;         // 摄像机位置属性引用
    String mVertexShader;       // 顶点着色器代码脚本
    String mFragmentShader;     // 片元着色器代码脚本

    FloatBuffer mVertexBuffer;  // 顶点坐标数据缓冲
    FloatBuffer mNormalBuffer;  // 顶点法向量数据缓冲
    int vCount = 0;

    public LoadedObjectVertexNormal(MySurfaceView mv, float[] vertices, float[] normals) {
        //初始化顶点坐标与着色数据
        initVertexData(vertices, normals);
        //初始化shader
        initShader(mv);
    }

    //初始化顶点坐标与着色数据的方法
    public void initVertexData(float[] vertices, float[] normals) {

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
    }

    //初始化shader
    public void initShader(MySurfaceView mv) {
        // 加载顶点着色器/片元着色器的脚本内容
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());

        // 基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);

        // 获取程序中 顶点位置/法向量 属性引用  HHL 没有使用纹理坐标!!
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        maNormalHandle = GLES30.glGetAttribLocation(mProgram, "aNormal");

        // 获取程序中 总变换 位置、旋转变换矩阵 光源位置 摄像机位置 的引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
        muLightLocationHandle = GLES30.glGetUniformLocation(mProgram, "uLightLocation");
        muCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera");
    }

    void drawSelf(int texId) {

        //制定使用某套着色器程序
        GLES30.glUseProgram(mProgram);

        //将最终变换矩阵 位置、旋转变换矩阵传 光源位置 摄像机位置传入着色器程序
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        GLES30.glUniform3fv(muLightLocationHandle, 1, MatrixState.lightPositionFB);
        GLES30.glUniform3fv(muCameraHandle, 1, MatrixState.cameraFB);

        // 将 顶点位置和法向量 数据传入渲染管线
        GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        GLES30.glVertexAttribPointer(maNormalHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, mNormalBuffer);
        GLES30.glEnableVertexAttribArray(maPositionHandle);
        GLES30.glEnableVertexAttribArray(maNormalHandle);


        //绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_3D, texId); //3D纹理  对应纹理坐标应该是s,t,w
        // OpenGL ES 3.0才有的:
        // 2D数组纹理  GL_TEXTURE_2D_ARRAY
        // 3D纹理     GL_TEXTURE_3D

        //绘制加载的物体
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);
    }
}

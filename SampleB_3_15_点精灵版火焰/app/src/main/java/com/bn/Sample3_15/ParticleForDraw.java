package com.bn.Sample3_15;//声明包

import static com.bn.Sample3_15.ParticleDataConstant.lock;
import static com.bn.Sample3_15.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES30;

//纹理三角形
public class ParticleForDraw {
    private int mProgram;           // 自定义渲染管线程序id
    private int muMVPMatrixHandle;  // 总变换矩阵引用id
    private int muLifeSpan;         // 衰减因子引用id
    private int muBj;               // 半径引用id
    private int muStartColor;       // 起始颜色引用id
    private int muEndColor;         // 终止颜色引用id
    private int muCameraPosition;   // 摄像机位置              Add 使点精灵可以做成近大远小的效果
    private int muMMatrix;          // 基本变换矩阵总矩阵        Add 使点精灵可以做成近大远小的效果
    private int maPositionHandle;   // 顶点位置属性引用id

    ParticleForDraw(MySurfaceView mv) {
        initShader(mv);
    }


    private void initShader(MySurfaceView mv) {

        String mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        String mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        mProgram = createProgram(mVertexShader, mFragmentShader);

        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");

        // 下面是粒子所属于的粒子系统都不一样的: 最大生命周期 半径(内部到外部颜色变淡) 起始颜色 结束颜色
        muLifeSpan = GLES30.glGetUniformLocation(mProgram, "maxLifeSpan");
        muBj = GLES30.glGetUniformLocation(mProgram, "bj");
        muStartColor = GLES30.glGetUniformLocation(mProgram, "startColor");
        muEndColor = GLES30.glGetUniformLocation(mProgram, "endColor");

        // 下面是为了实现点精灵近大远小的效果
        muCameraPosition = GLES30.glGetUniformLocation(mProgram, "cameraPosition");
        muMMatrix = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
    }

    void drawSelf(int texId,                                                               // 所属粒子系统的 纹理贴图
                  float[] startColor, float[] endColor, float maxLifeSpan, float halfSize, // 所属粒子系统的 起始颜色 终止颜色 半径 最大生命周期
                  FloatBuffer vertexBuffer, FloatBuffer texCoorBuffer, int drawCount       // 所属粒子系统的 物体/图元/形状
                                                                                           // 当前该粒子的状态 保存在顶点的w坐标
    ) {

        GLES30.glUseProgram(mProgram);

        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);


        GLES30.glUniform1f(muLifeSpan, maxLifeSpan);        // 粒子特有的 传入管线
        //GLES30.glUniform1f(muBj, halfSize * 60 );         // hhl 其实这个给到渲染管线 是来计算片元/粒子与火盘底部中心的距离
        GLES30.glUniform1f(muBj, halfSize);                 // 普通版本的 这里是*60 然后半径是0.x
        GLES30.glUniform4fv(muStartColor, 1, startColor, 0);
        GLES30.glUniform4fv(muEndColor, 1, endColor, 0);
        GLES30.glUniform3f(muCameraPosition, MatrixState.cx, MatrixState.cy, MatrixState.cz);
        GLES30.glUniformMatrix4fv(muMMatrix, 1, false, MatrixState.getMMatrix(), 0);


        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);

        GLES30.glVertexAttribPointer(maPositionHandle, 4, GLES30.GL_FLOAT, false, 4 * 4, vertexBuffer);

        GLES30.glEnableVertexAttribArray(maPositionHandle);

        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, drawCount);
    }
}

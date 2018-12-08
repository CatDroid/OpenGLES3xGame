package com.bn.Sample3_14;

import static com.bn.Sample3_14.ShaderUtil.createProgram;

import java.nio.FloatBuffer;
import android.opengl.GLES30;


// 代表一个要绘制的粒子
// 每个粒子系统有自己的粒子半径 粒子初始位置 粒子的初始颜色 结束颜色  以及粒子总数
// 每个粒子 每次要绘制的状态(当前生命时间)应该由粒子系统ParticleSystem来控制并传入
class ParticleForDraw {

    private int mProgram;           // 自定义渲染管线程序id
    private int muMVPMatrixHandle;  // 总变换矩阵引用id
    private int muLifeSpan;         // 衰减因子引用id
    private int muBj;               // 半径引用id
    private int muStartColor;       // 起始颜色引用id
    private int muEndColor;         // 终止颜色引用id
    private int maPositionHandle;   // 顶点位置属性引用id
    private int maTexCoorHandle;    // 顶点纹理坐标属性引用id

    ParticleForDraw(MySurfaceView mv ) {
        initShader(mv);
    }

    private void initShader(MySurfaceView mv) {

        String mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        String mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        mProgram = createProgram(mVertexShader, mFragmentShader);

        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        maTexCoorHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");

        // 下面是粒子所属于的粒子系统都不一样的: 最大生命周期 半径(内部到外部颜色变淡) 起始颜色 结束颜色
        muLifeSpan = GLES30.glGetUniformLocation(mProgram, "maxLifeSpan");
        muBj = GLES30.glGetUniformLocation(mProgram, "bj");
        muStartColor = GLES30.glGetUniformLocation(mProgram, "startColor");
        muEndColor = GLES30.glGetUniformLocation(mProgram, "endColor");
    }

    void drawSelf(int texId,                                                     // 所属粒子系统的 纹理贴图
        float[] startColor, float[] endColor, float maxLifeSpan, float halfSize, // 所属粒子系统的 起始颜色 终止颜色 半径 最大生命周期
        FloatBuffer vertexBuffer , FloatBuffer texCoorBuffer , int drawCount     // 所属粒子系统的 物体/图元/形状
                                                                                 // 当前该粒子的状态 保存在顶点的w坐标
    ) {

        GLES30.glUseProgram(mProgram);

        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);


        GLES30.glUniform1f(muLifeSpan, maxLifeSpan);        // 粒子特有的 传入管线
        //GLES30.glUniform1f(muBj, halfSize * 60 );           // hhl 其实这个给到渲染管线 是来计算片元/粒子与火盘底部中心的距离 halfSize*60是最大距离,实际粒子最大距离是4
        GLES30.glUniform1f(muBj, 3.5f );
        GLES30.glUniform4fv(muStartColor, 1, startColor, 0);
        GLES30.glUniform4fv(muEndColor,   1, endColor, 0);


        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);

        GLES30.glVertexAttribPointer(maPositionHandle, 4, GLES30.GL_FLOAT, false, 4 * 4, vertexBuffer);


        GLES30.glVertexAttribPointer(maTexCoorHandle, 2, GLES30.GL_FLOAT, false, 2 * 4, texCoorBuffer);
        GLES30.glEnableVertexAttribArray(maPositionHandle);
        GLES30.glEnableVertexAttribArray(maTexCoorHandle);


        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, drawCount);
    }
}

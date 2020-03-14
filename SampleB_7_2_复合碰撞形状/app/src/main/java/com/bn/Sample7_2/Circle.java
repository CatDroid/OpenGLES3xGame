package com.bn.Sample7_2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES30;

//顶点法画圆，不限制顶点数
public class Circle {

    private int mProgram;           // 自定义渲染管线程序id
    private int muMVPMatrixHandle;  // 总变换矩阵引用id
    private int muMMatrixHandle;    // 位置、旋转变换矩阵
    private int uTexHandle;         // 外观纹理属性引用id

    private int maCameraHandle;     // 摄像机位置属性引用id
    private int maPositionHandle;   // 顶点位置属性引用id
    private int maTexCoorHandle;    // 顶点纹理坐标属性引用id

    private FloatBuffer vertexBuffer;   // 顶点坐标数据缓冲
    private FloatBuffer textureBuffer;  // 顶点纹理数据缓冲
    private int vCount = 0;                     // 顶点个数

    // r 半径
    // n 圆拆分成多少个三角形
    // Circle 在模型坐标系的XOY平面, 圆心在原点
    public Circle(float r, int n, float[] normal, int mProgram) {



        this.mProgram = mProgram;

        float angdegSpan = 360.0f / n;

        vCount = 3 * n;             //顶点个数，共有n个三角形，每个三角形都有三个顶点

        float[] vertices = new float[vCount * 3];//坐标数据
        float[] textures = new float[vCount * 2];//顶点纹理S、T坐标值数组
        float[] normals = new float[vCount * 3];
        //坐标数据初始化
        int count = 0;
        int stCount = 0;
        for (int i = 0; i < vCount; i++) {
            normals[3 * i] = normal[0];
            normals[3 * i + 1] = normal[1];
            normals[3 * i + 2] = normal[2];
        }
        for (float angdeg = 0; Math.ceil(angdeg) < 360; angdeg += angdegSpan) {

            double angrad = Math.toRadians(angdeg);//当前弧度
            double angradNext = Math.toRadians(angdeg + angdegSpan);//下一弧度
            //中心点
            vertices[count++] = 0;//顶点坐标
            vertices[count++] = 0;
            vertices[count++] = 0;

            textures[stCount++] = 0.5f;//st坐标
            textures[stCount++] = 0.5f;

            //当前点
            vertices[count++] = (float) (-r * Math.sin(angrad));//顶点坐标
            vertices[count++] = (float) (r * Math.cos(angrad));
            vertices[count++] = 0;

            textures[stCount++] = (float) (0.5f - 0.5f * Math.sin(angrad));//st坐标
            textures[stCount++] = (float) (0.5f - 0.5f * Math.cos(angrad));

            //下一点
            vertices[count++] = (float) (-r * Math.sin(angradNext));//顶点坐标
            vertices[count++] = (float) (r * Math.cos(angradNext));
            vertices[count++] = 0;

            textures[stCount++] = (float) (0.5f - 0.5f * Math.sin(angradNext));//st坐标
            textures[stCount++] = (float) (0.5f - 0.5f * Math.cos(angradNext));
        }
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);//创建顶点坐标数据缓冲
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        vertexBuffer = vbb.asFloatBuffer();//转换为float型缓冲
        vertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        vertexBuffer.position(0);//设置缓冲区起始位置

        //st坐标数据初始化
        ByteBuffer cbb = ByteBuffer.allocateDirect(textures.length * 4);//创建顶点纹理数据缓冲
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        textureBuffer = cbb.asFloatBuffer();//转换为float型缓冲
        textureBuffer.put(textures);//向缓冲区中放入顶点纹理数据
        textureBuffer.position(0);//设置缓冲区起始位置
    }

    //初始化shader
    public void intShader(MySurfaceView mv) {
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点经纬度属性引用id   
        maTexCoorHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用id 
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");

        //获取位置、旋转变换矩阵引用id
        //muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
        //获取程序中摄像机位置引用id
        //maCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera");

        uTexHandle = GLES30.glGetUniformLocation(mProgram, "sTexture");
    }

    public void drawSelf(int texId) {
        //制定使用某套shader程序
        GLES30.glUseProgram(mProgram);
        //将最终变换矩阵传入shader程序
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);

        //将位置、旋转变换矩阵传入shader程序
        //GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        //将摄像机位置传入shader程序
        //GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);

        //为画笔指定顶点位置数据
        GLES30.glVertexAttribPointer
                (
                        maPositionHandle,
                        3,
                        GLES30.GL_FLOAT,
                        false,
                        3 * 4,
                        vertexBuffer
                );
        //为画笔指定顶点纹理坐标数据
        GLES30.glVertexAttribPointer
                (
                        maTexCoorHandle,
                        2,
                        GLES30.GL_FLOAT,
                        false,
                        2 * 4,
                        textureBuffer
                );
        //允许顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maPositionHandle);
        GLES30.glEnableVertexAttribArray(maTexCoorHandle);
        //绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
        GLES30.glUniform1i(uTexHandle, 0);

        //绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);
    }
}

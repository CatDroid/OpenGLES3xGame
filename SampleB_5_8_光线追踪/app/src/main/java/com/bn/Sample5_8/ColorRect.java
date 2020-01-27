package com.bn.Sample5_8;//声明包

import static com.bn.Sample5_8.Constant.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.annotation.SuppressLint;
import android.opengl.GLES30;

//矩形
@SuppressLint("NewApi")
public class ColorRect {
    private int mProgram;           // 自定义渲染管线着色器程序id
    private int muMVPMatrixHandle;  // 总变换矩阵引用
    private int maPositionHandle;   // 顶点位置属性引用

    private int muColorHandle;      // 片元颜色属性引用
    private int mu3DPosHandle;      // 基本块对应的第一碰撞点位置一致变量引用
    private int muNormalHandle;     // 基本块对应的第一碰撞点法向量一致变量引用
    private int muLightLocationHandle;  // 光源位置一致变量引用
    private int muCameraHandle;         // 摄像机位置一致变量引用
    private int muIsShadow;             // 是否绘制阴影一致变量引用

    private String mVertexShader;       // 顶点着色器脚本字符串
    private String mFragmentShader;     // 片元着色器脚本字符串

    private FloatBuffer mVertexBuffer;          // 顶点坐标数据缓冲
    private int vCount = 0;                     // 顶点数量
    private float[] color3 = new float[3];      // 基本块对应的第一碰撞点的颜色
    private float[] vertexPos3D = new float[3]; // 基本块对应的第一碰撞点的位置
    private float[] normal3D = new float[3];    // 基本块对应的第一碰撞点的法向量
    private float[] lightPos3D = new float[3];  // 光源位置
    private float[] cameraPos3D = new float[3]; // 摄像机位置
    private int isShadow;                       // 基本块对应的第一碰撞点是否在阴影中标志

    float u;                                    // 基本块在屏幕上的位置
    float v;

    public ColorRect(MySurfaceView mv) {
        //初始化顶点坐标数据
        initVertexData();
        //初始化着色器
        intShader(mv);
    }

    //初始化顶点坐标数据的方法
    public void initVertexData() {

        vCount = 6;

        float vertices[] = new float[]
                {
                        0, 0, 0,//0
                        Constant.blockSize, 0, 0,//1
                        Constant.blockSize, Constant.blockSize, 0,//2

                        0, 0, 0,//0
                        Constant.blockSize, Constant.blockSize, 0,//2
                        0, Constant.blockSize, 0//3
                };

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
    }

    //初始化着色器
    public void intShader(MySurfaceView mv) {
        //加载顶点着色器的脚本内容
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.glsl", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.glsl", mv.getResources());
        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用 
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");

        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取程序中3D世界中顶点颜色属性引用 
        muColorHandle = GLES30.glGetUniformLocation(mProgram, "uColor");
        //获取程序中3D世界中顶点位置属性引用 
        mu3DPosHandle = GLES30.glGetUniformLocation(mProgram, "uPosition");
        //获取程序中顶点法向量属性引用  
        muNormalHandle = GLES30.glGetUniformLocation(mProgram, "uNormal");
        //获取程序中光源位置引用
        muLightLocationHandle = GLES30.glGetUniformLocation(mProgram, "uLightLocation");
        //获取程序中摄像机位置引用
        muCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera");
        //获取程序中是否绘制阴影属性引用
        muIsShadow = GLES30.glGetUniformLocation(mProgram, "isShadow");
    }

    public void drawSelf() {                // 绘制基本块的方法

        MatrixState.pushMatrix();           // 保护现场
        MatrixState.translate(u, v, 0);   // 移动到视口中的u, v位置处

        // 指定使用某套着色器程序
        GLES30.glUseProgram(mProgram);
        // 将最终变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle,1,false, MatrixState.getFinalMatrix(),0);

        // 3D世界中的量
        // 将基本块对应的第一碰撞点颜色传入渲染管线
        GLES30.glUniform3fv(muColorHandle, 1, color3, 0);
        // 将3D世界中顶点的位置传入渲染管线
        GLES30.glUniform3fv(mu3DPosHandle, 1, vertexPos3D, 0);
        // 将3D世界中顶点的法向量传入渲染管线
        GLES30.glUniform3fv(muNormalHandle, 1, normal3D, 0);
        // 将3D世界中灯光位置传入渲染管线
        GLES30.glUniform3fv(muLightLocationHandle, 1, lightPos3D, 0);
        // 将3D世界中摄像机的位置传入渲染管线
        GLES30.glUniform3fv(muCameraHandle, 1, cameraPos3D, 0);
        // 将是否绘制阴影属性传入渲染管线
        GLES30.glUniform1i(muIsShadow, isShadow);

        // 将顶点位置数据传入渲染管线
        GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT,
                false, 3 * 4, mVertexBuffer);
        // 允许顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maPositionHandle);
        //绘制矩形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);

        MatrixState.popMatrix();//恢复现场
    }

    // 设置颜色的方法
    public void setColor(float r, float g, float b)
    {
        this.color3[0] = r;
        this.color3[1] = g;
        this.color3[2] = b;
    }

    public void setPos3D(float x, float y, float z)
    {
        this.vertexPos3D[0] = x;
        this.vertexPos3D[1] = y;
        this.vertexPos3D[2] = z;
    }

    public void setNormal3D(float x, float y, float z)
    {
        this.normal3D[0] = x;
        this.normal3D[1] = y;
        this.normal3D[2] = z;
    }

    public void setLightPos3D(float x, float y, float z)
    {
        this.lightPos3D[0] = x;
        this.lightPos3D[1] = y;
        this.lightPos3D[2] = z;
    }

    public void setCameraPos3D(float x, float y, float z)
    {
        this.cameraPos3D[0] = x;
        this.cameraPos3D[1] = y;
        this.cameraPos3D[2] = z;
    }

    public void setShadow(int isShadow) {
        this.isShadow = isShadow;
    }

    public void setPos(float u, float v)
    {
        this.u = u;
        this.v = v;
    }

    public void setColRow(int col, int row)
    {
        //根据行列数计算基本块在屏幕上的位置
        float u = -W + W * (2 * col / nCols);
        float v = -H + H * (2 * row / nRows);
        this.setPos(u, v);//设置基本块位置
    }
}

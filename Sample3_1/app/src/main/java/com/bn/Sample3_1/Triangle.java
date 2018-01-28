package com.bn.Sample3_1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.annotation.SuppressLint;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

public class Triangle {
    private static final String TAG = "Triangle";

    public static float[] mProjMatrix = new float[16];//4x4 投影矩阵
    public static float[] mVMatrix = new float[16];//摄像机位置朝向的参数矩阵
    public static float[] mMVPMatrix = new float[16];//最后起作用的总变换矩阵

    int mProgram;           //  自定义渲染管线程序id
    int muMVPMatrixHandle;  //  总变换矩阵引用
    int maPositionHandle;   //  顶点位置属性引用
    int maColorHandle;      //  顶点颜色属性引用


    String mVertexShader;   //  顶点着色器代码脚本
    String mFragmentShader; //  片元着色器代码脚本


    static float[] mMMatrix = new float[16];// 具体物体的移动旋转矩阵，包括旋转、平移、缩放  (-->世界坐标系)

    FloatBuffer mVertexBuffer;    //  顶点坐标数据缓冲
    FloatBuffer mColorBuffer;     //  顶点着色数据缓冲
    int vCount = 0;
    float xAngle = 0;//绕x轴旋转的角度

    public Triangle(MyTDView mv) {
        //调用初始化顶点数据的initVertexData方法
        initVertexData();
        //调用初始化着色器的intShader方法
        initShader(mv);
    }

    public void initVertexData()//初始化顶点数据的方法
    {
        //顶点坐标数据的初始化
        vCount = 3;
        final float UNIT_SIZE = 0.2f;
        float vertices[] = new float[]//顶点坐标数组
                {
                        -4 * UNIT_SIZE, 0, 0,
                        0, 4 * UNIT_SIZE, 0, // 向上的三角形  --- 0, -4 * UNIT_SIZE, 0, 向下的三角形
                        4 * UNIT_SIZE, 0, 0,
                };

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder()); // 设置字节顺序为本地操作系统顺序
        mVertexBuffer = vbb.asFloatBuffer();// 转换为浮点(Float)型缓冲
        mVertexBuffer.put(vertices);        // 在缓冲区内写入数据
        mVertexBuffer.position(0);          // 设置缓冲区起始位置

        float colors[] = new float[] //顶点颜色数组
                {
                        1, 1, 1, 0,// 白色
                        0, 0, 1, 0,// 蓝
                        0, 1, 0, 0 // 绿
                };

        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mColorBuffer = cbb.asFloatBuffer();//转换为浮点(Float)型缓冲
        mColorBuffer.put(colors);//在缓冲区内写入数据
        mColorBuffer.position(0);//设置缓冲区起始位置
    }

    //初始化着色器的方法
    @SuppressLint("NewApi")
    public void initShader(MyTDView mv) {
        //加载  顶点着色器的 脚本内容
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        //加载  片元着色器的 脚本内容
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        //基于  顶点着色器与片元着色器   创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取  程序中   顶点位置属性 引用
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取  程序中  顶点颜色属性 引用
        maColorHandle = GLES30.glGetAttribLocation(mProgram, "aColor");
        //获取  程序中   总变换矩阵 引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    @SuppressLint("NewApi")
    public void drawSelf() {
        // 指定使用某套shader程序
        GLES30.glUseProgram(mProgram);

        //     初始化
        //Matrix.setRotateM(mMMatrix,0,   0,   0,1,0);
        Matrix.setIdentityM(mMMatrix, 0);

        //     沿Z轴正向位移1
        Matrix.translateM(mMMatrix, 0, 0, 0, 1);
        //     绕x轴旋转
        Matrix.rotateM(mMMatrix, 0, xAngle, 1, 0, 0);  // 这里旋转 !!!!!! 是右乘旋转矩阵  在世界坐标系中旋转


        //将的 变换矩阵     传入   渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, Triangle.getFianlMatrix(mMMatrix), 0);
        //将  顶点位置数据  传送进  渲染管线
        GLES30.glVertexAttribPointer(
                maPositionHandle,
                3,
                GLES30.GL_FLOAT,
                false,
                3 * 4,
                mVertexBuffer
        );
        //将  顶点颜色数据  传送进  渲染管线
        GLES30.glVertexAttribPointer
                (
                        maColorHandle,
                        4,                  // 顶点属性  组件数量      必须为1、2、3或者4
                        GLES30.GL_FLOAT,    // 顶点属性  组件数据类型   每个组件的大小
                        false,
                        4 * 4,                // 顶点属性  相邻两个之间的偏移量
                        mColorBuffer
                );
        GLES30.glEnableVertexAttribArray(maPositionHandle);    // 启用   顶点位置数据
        GLES30.glEnableVertexAttribArray(maColorHandle);       // 启用   顶点着色数据

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);   // 绘制   三角形
    }

    public static float[] getFianlMatrix(float[] spec) {


        //mMVPMatrix=new float[16];
        float[] tra = new float[16];
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, spec, 0);         // 视图矩阵 ( --> 摄像机坐标系
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);// 投影矩阵 ( --> 标准设备坐标系  投影矩阵+透视除法
        //Matrix.invertM(mMVPMatrix,0, tra,0); // invert 是 逆矩阵
        //Log.d(TAG,"before " + ShaderUtil.printMatricx(mMVPMatrix) );
        Matrix.transposeM(tra, 0, mMVPMatrix, 0); // tranposeM 是 转置矩阵  shader中要改变为 vec4 * MVP
        //Log.d(TAG,"old " + ShaderUtil.printMatricx(mMVPMatrix) + " tra " + ShaderUtil.printMatricx(tra) );


        return tra;
    }
}
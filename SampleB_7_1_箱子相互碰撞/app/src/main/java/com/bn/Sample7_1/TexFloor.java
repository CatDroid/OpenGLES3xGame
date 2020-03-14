package com.bn.Sample7_1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES30;

import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import javax.vecmath.Vector3f;

public class TexFloor {

    private int mProgram;               // 自定义渲染管线程序id
    private int muMVPMatrixHandle;      // 总变换矩阵引用id
    private int muTexHandle;            // 外观纹理属性引用id
    private int maTexCoorHandle;        // 顶点纹理坐标属性引用id
    private int maPositionHandle;       // 顶点位置属性引用id

    private FloatBuffer mVertexBuffer; // 顶点坐标数据缓冲
    private FloatBuffer mTextureBuffer;// 顶点着色数据缓冲

    private int vCount;
    private float yOffset;

    public TexFloor(int program,
                    final float halfSize,
                    float yOffset,
                    CollisionShape groundShape,
                    DiscreteDynamicsWorld dynamicsWorld)
    {

        this.mProgram = program;

        this.yOffset = yOffset; // 地面的高度


        Transform groundTransform = new Transform();    // 创建刚体的初始变换对象
        groundTransform.setIdentity();                  // 对初始变换对象初始化
        groundTransform.origin.set(new Vector3f(0.f, yOffset, 0.f));    // 设置初始的平移变换
        Vector3f localInertia = new Vector3f(0, 0, 0);               // 存储惯性的向量

        // 创建刚体的运动状态对象
        DefaultMotionState myMotionState = new DefaultMotionState(groundTransform);
        // 创建刚体描述信息对象  质量--运动状态--碰撞形状--惯性变量
        // 这里不传入质量,就会标记当前的刚体是 静态物体 ,不会计算质量的倒数,也就不会计算合力和加速度 --静态物体-保持原来的运动状态
        RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(0, myMotionState, groundShape, localInertia);
        // 创建刚体
        RigidBody body = new RigidBody(rbInfo);
        // 设置反弹系数
        body.setRestitution(0.4f);
        // 设置摩擦系数
        body.setFriction(0.8f);


        // 将刚体添加进物理世界
        dynamicsWorld.addRigidBody(body);


        // 初始化顶点数据 UNIT_SIZE 大地的尺寸
        initVertexData(halfSize);
        // 初始化着色器
        initShader(program);
    }

    //初始化顶点数据
    public void initVertexData(final float halfSize)
    {

        vCount = 6;
        float vertices[] = new float[]
                {
                    1 * halfSize, yOffset, 1 * halfSize,
                    -1 * halfSize, yOffset, -1 * halfSize,
                    -1 * halfSize, yOffset, 1 * halfSize,

                    1 * halfSize, yOffset, 1 * halfSize,
                    1 * halfSize, yOffset, -1 * halfSize,
                    -1 * halfSize, yOffset, -1 * halfSize,
                };


        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        float texCoords[] = new float[]
                {
                    halfSize / 2, halfSize / 2,
                    0, 0,
                    0, halfSize / 2,
                    halfSize / 2, halfSize / 2,
                    halfSize / 2, 0,
                    0, 0
                };


        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        mTextureBuffer = tbb.asFloatBuffer();
        mTextureBuffer.put(texCoords);
        mTextureBuffer.position(0);

    }

    //初始化着色器
    public void initShader(int program)
    {

        maPositionHandle = GLES30.glGetAttribLocation(program, "aPosition");
        maTexCoorHandle = GLES30.glGetAttribLocation(program, "aTexCoor");

        muMVPMatrixHandle = GLES30.glGetUniformLocation(program, "uMVPMatrix");

        muTexHandle = GLES30.glGetUniformLocation(program, "sTexture");
    }

    //绘制水平地面
    public void drawSelf(int texId)
    {
        // 制定使用某套shader程序
        GLES30.glUseProgram(mProgram);
        // 将最终变换矩阵传入shader程序
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);


        // 为画笔指定顶点位置数据
        GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        // 为画笔指定顶点纹理坐标数据
        GLES30.glVertexAttribPointer(maTexCoorHandle, 2, GLES30.GL_FLOAT, false, 2 * 4, mTextureBuffer);

        // 允许顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maPositionHandle);
        GLES30.glEnableVertexAttribArray(maTexCoorHandle);

        // 绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
        GLES30.glUniform1i(muTexHandle, 0);

        // 绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);
    }
}

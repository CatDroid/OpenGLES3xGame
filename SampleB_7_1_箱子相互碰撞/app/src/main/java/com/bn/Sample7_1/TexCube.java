package com.bn.Sample7_1;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class TexCube
{

    private TextureRect tr;     // 用于绘制箱子各个面的纹理矩形
    private float halfSize;     // 立方体箱子的半边长
    RigidBody body;             // 对应的刚体对象
    private int mProgram;       // 着色器程序引用id
    private MySurfaceView mv;   // MySurfaceView的引用

    public TexCube(MySurfaceView mv,
                   float halfSize,
                   CollisionShape colShape,
                   DiscreteDynamicsWorld dynamicsWorld,
                   float mass,
                   float cx, float cy, float cz,
                   int mProgram)
    {

        boolean isDynamic = (mass != 0f);                       // 物体是否可以运动
        Vector3f localInertia = new Vector3f(0, 0, 0);// 惯性向量

        if (isDynamic) // 如果物体可以运动  地面就是没有质量的
        {
            colShape.calculateLocalInertia(mass, localInertia); // 计算惯性
        }

        Transform startTransform = new Transform();             // 创建刚体的初始变换对象
        startTransform.setIdentity();                           // 变换初始化
        startTransform.origin.set(new Vector3f(cx, cy, cz));    // 设置刚体初始的位置

        // 创建刚体的运动状态对象
        DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
        // 创建刚体描述信息对象
        RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, colShape, localInertia);

        // 创建刚体
        body = new RigidBody(rbInfo);
        body.setRestitution(0.6f);          // 设置反弹系数
        body.setFriction(0.8f);             // 设置摩擦系数
        dynamicsWorld.addRigidBody(body);   // 将刚体添加进物理世界

        this.mv = mv;                   // 保存MySurfaceView引用
        tr = new TextureRect(halfSize); // 创建纹理矩形
        this.mProgram = mProgram;       // 保存着色器程序引用
        this.halfSize = halfSize;       // 保存箱子的半边长
    }

    // 绘制立方体箱子的方法
    public void drawSelf(int[] texIda)
    {
        tr.initShader(mv, mProgram);// 初始化纹理矩形的绘制着色器
        int texId = texIda[0];      // 箱子运动时的纹理id
        if (!body.isActive()) {     // 箱子静止时的纹理id
            texId = texIda[1];
        }


        MatrixState.pushMatrix();//保护现场

        // Cube的位置和旋转 是从物理世界获取到

        // 从物理世界中获取这个箱子对应刚体的变换信息对象
        Transform trans = body.getMotionState().getWorldTransform(new Transform());
        // 进行平移变换
        MatrixState.translate(trans.origin.x, trans.origin.y, trans.origin.z);
        Quat4f ro = trans.getRotation(new Quat4f());        // 获取当前旋转变换的信息进入四元数
        if (ro.x != 0 || ro.y != 0 || ro.z != 0)            // 若四元数3个轴的分量不都为0
        {
            float[] fa = SYSUtil.fromSYStoAXYZ(ro);         // 将四元数转换成AXYZ的形式
            MatrixState.rotate(fa[0], fa[1], fa[2], fa[3]); // 进行旋转变换
        }


        // Cube的质心在原点

        MatrixState.pushMatrix();//保护现场
        MatrixState.translate(0, halfSize, 0);//沿y轴进行平移
        MatrixState.rotate(-90, 1, 0, 0);//绕x轴进行旋转
        tr.drawSelf(texId);//绘制箱子的上面

        MatrixState.popMatrix();//恢复现场
        MatrixState.pushMatrix();//保护现场
        MatrixState.translate(0, -halfSize, 0);//执行平移
        MatrixState.rotate(90, 1, 0, 0);//执行旋转
        tr.drawSelf(texId);//绘制下面
        MatrixState.popMatrix();//恢复现场
        MatrixState.pushMatrix();//保护现场
        MatrixState.translate(-halfSize, 0, 0);//执行平移
        MatrixState.rotate(-90, 0, 1, 0);//执行旋转
        tr.drawSelf(texId);//绘制左面
        MatrixState.popMatrix();//恢复现场
        MatrixState.pushMatrix();//保护现场
        MatrixState.translate(halfSize, 0, 0);//执行平移
        MatrixState.rotate(90, 0, 1, 0);//执行旋转
        tr.drawSelf(texId);//绘制右面
        MatrixState.popMatrix();//恢复现场
        MatrixState.pushMatrix();//保护现场
        MatrixState.translate(0, 0, halfSize);//执行平移
        tr.drawSelf(texId);//绘制前面
        MatrixState.popMatrix();//恢复现场
        MatrixState.pushMatrix();//保护现场
        MatrixState.translate(0, 0, -halfSize);//执行平移
        MatrixState.rotate(180, 0, 1, 0);//执行旋转
        tr.drawSelf(texId);//绘制后面
        MatrixState.popMatrix();//恢复现场
        MatrixState.popMatrix();//恢复现场
    }
}

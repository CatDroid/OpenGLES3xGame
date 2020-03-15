package com.bn.Sample7_4;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class LoadRigidBody extends BNThing {
    LoadedObjectVertexNormal lovo;//加载物体对象引用
    RigidBody body;//对应的刚体对象引用
    int mProgram;//着色器程序引用

    public LoadRigidBody(int mProgram,
                         float mass,
                         LoadedObjectVertexNormal lovo,
                         float cx, float cy, float cz,
                         DiscreteDynamicsWorld dynamicsWorld)
    {

        this.lovo = lovo;                           // 保存加载物体对象引用
        this.mProgram = mProgram;                   // 保存着色器程序索引

        CollisionShape colShape = lovo.loadShape;   // 保存碰撞形状引用

        boolean isDynamic = (mass != 0f);                             // 确定物体是否可运动
        Vector3f localInertia = new Vector3f(0f, 0f, 0f);   // 创建存放惯性的向量
        if (isDynamic) {
            colShape.calculateLocalInertia(mass, localInertia);       // 计算惯性
        }

        Transform startTransform = new Transform();         // 创建刚体的初始变换对象
        startTransform.setIdentity();                       // 初始化变换对象
        startTransform.origin.set(new Vector3f(cx, cy, cz));// 设置刚体的初始位置 (质心在世界坐标系中的位置)
        // 创建刚体的运动状态对象
        DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
        // 创建刚体描述信息对象
        RigidBodyConstructionInfo cInfo = new RigidBodyConstructionInfo
                (mass, myMotionState, colShape, localInertia);


        body = new RigidBody(cInfo);        // 创建刚体对象


        body.setRestitution(0.4f);          // 设置反弹系数
        body.setFriction(0.8f);             // 设置摩擦系数


        dynamicsWorld.addRigidBody(body);   // 将刚体添加进物理世界
    }

    //绘制刚体
    public void drawSelf()
    {
        lovo.initShader(mProgram);  // 初始化着色器

        MatrixState.pushMatrix();   // 保护现场

        // 获取此刚体对应的变换信息对象
        Transform trans = body.getMotionState().getWorldTransform(new Transform());
        // 进行平移变换
        MatrixState.translate(trans.origin.x, trans.origin.y, trans.origin.z);

        Quat4f ro = trans.getRotation(new Quat4f());        // 获取当前旋转变换的信息进入四元数
        if (ro.x != 0 || ro.y != 0 || ro.z != 0)            // 若四元数3个轴的分量都不为0
        {
            float[] fa = SYSUtil.fromSYStoAXYZ(ro);         // 将四元数转换成AXYZ形式
            MatrixState.rotate(fa[0], fa[1], fa[2], fa[3]); // 进行旋转变换
        }

        lovo.drawSelf();            // 绘制加载的物体

        MatrixState.popMatrix();    // 恢复现场
    }
}

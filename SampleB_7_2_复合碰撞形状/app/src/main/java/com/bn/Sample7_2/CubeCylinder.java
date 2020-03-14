package com.bn.Sample7_2;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class CubeCylinder {
    Cylinder cl;//用于绘制的圆柱
    TexCube tc;//用于绘制的立方体箱子
    float halfSize;//半边长
    RigidBody body;//对应的刚体对象
    MySurfaceView mv;//MySurfaceView的引用

    public CubeCylinder(MySurfaceView mv,
                        float unitSize,
                        CollisionShape[] csa,
                        DiscreteDynamicsWorld dynamicsWorld,
                        float mass,
                        float cx,
                        float cy,
                        float cz,
                        int[] mProgram)

    {

        CompoundShape comShape = new CompoundShape(); //创建组合形状对象

        // 组合形状 中 每一个形状 增加都要加上一个 变换+形状???
        Transform localTransform = new Transform();                     // 创建变换对象
        localTransform.setIdentity();                                   // 初始化变换
        localTransform.origin.set(new Vector3f(0, 0, 0));      // 设置变换的平移参数  !!! 这个应该是在组合形状模型坐标系中 !!!
        comShape.addChildShape(localTransform, csa[0]);//添加子形状（箱子）
        comShape.addChildShape(localTransform, csa[1]);//添加子形状（围绕x轴的圆柱）

        localTransform = new Transform();//创建变换对象
        localTransform.basis.rotX((float) Math.toRadians(90));//绕x旋转90
        comShape.addChildShape(localTransform, csa[2]);//添加子形状（围绕z轴的圆柱）


        boolean isDynamic = (mass != 0f);                       // 判断刚体是否可运动
        Vector3f localInertia = new Vector3f(0, 0, 0); // 创建存放惯性的向量
        if (isDynamic)                                          // 如果刚体可以运动
        {
            comShape.calculateLocalInertia(mass, localInertia); // 计算刚体的惯性
        }


        Transform startTransform = new Transform();         //  创建刚体的初始变换对象 (整个组合形状对象)
        startTransform.setIdentity();                       //  初始化变换对象
        startTransform.origin.set(new Vector3f(cx, cy, cz));//  设置刚体的初始位置 (整个刚体的初始位置,渲染的时候直接从刚体拿到这个坐标/世界坐标系)
        DefaultMotionState myMotionState = new DefaultMotionState(startTransform);//创建刚体的运动状态对象

        RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo//创建刚体描述信息对象
                (
                        mass, myMotionState, comShape, localInertia
                );


        body = new RigidBody(rbInfo);       // 创建刚体对象
        body.setRestitution(0.6f);          // 设置反弹系数
        body.setFriction(0.8f);             // 设置摩擦系数


        dynamicsWorld.addRigidBody(body);   // 将刚体添加进物理世界

        // 立方体的 半边长是 halfSize 质心在 正方体中心
        // 圆柱的  高是 halfSize * 3.6f  半径是 halfSize / 2  (跟碰撞形状一样大小)  底部圆在XOZ平面 质心在 (0,0, 高/2 ) 绘制的时候 绘制2个 会做平移
        tc = new TexCube(unitSize, mProgram[0]);//创建绘制用立方体箱子

        cl = new Cylinder(unitSize / 2, unitSize * 3.6f, 16, mProgram[0], mv);//创建绘制用圆柱

        this.halfSize = unitSize;//保存半边长
        this.mv = mv;//保存MySurfaceView的引用

    }

    public void drawSelf(int[] texIda, int[] texIdb)//绘制方法
    {
        int texId1 = texIda[0];//立方体箱子运动时的纹理
        int texId2 = texIdb[1];//圆柱运动时的纹理
        if (!body.isActive())//若刚体静止
        {
            texId1 = texIda[1];//立方体静止时的纹理
            texId2 = texIdb[0];//圆柱静止时的纹理
        }
        MatrixState.pushMatrix();//保存现场


        Transform trans = body.getMotionState().getWorldTransform(new Transform());//获取这个物体对应刚体的的变换信息对象
        MatrixState.translate(trans.origin.x, trans.origin.y, trans.origin.z);//进行平移变换
        Quat4f ro = trans.getRotation(new Quat4f());//获取当前旋转变换的信息进入四元数
        if (ro.x != 0 || ro.y != 0 || ro.z != 0)//若四元数3个轴的分量都不为0
        {
            if (!Float.isNaN(ro.x) && !Float.isNaN(ro.y) && !Float.isNaN(ro.z)
                    && !Float.isInfinite(ro.x) && !Float.isInfinite(ro.y) && !Float.isInfinite(ro.z)) {
                float[] fa = SYSUtil.fromSYStoAXYZ(ro);//将四元数转换成AXYZ的形式
                MatrixState.rotate(fa[0], fa[1], fa[2], fa[3]);
            }
        }


        tc.drawSelf(texId1);//绘制立方体箱子
        MatrixState.pushMatrix();//保护现场
        MatrixState.rotate(90, 0, 0, 1);//绕z轴旋转90度
        MatrixState.translate(0, -halfSize * 1.8f, 0);//沿y轴进行平移 (这是为了跟碰撞形状的质心一样)
        cl.drawSelf(texId2, texId2, texId2);//绘制绕X轴圆柱
        MatrixState.popMatrix();//恢复现场
        MatrixState.pushMatrix();//保护现场
        MatrixState.translate(0, -halfSize * 1.8f, 0);//沿y轴进行平移  (这是为了跟碰撞形状的质心一样)
        cl.drawSelf(texId2, texId2, texId2);//绘制绕Y轴的圆柱
        MatrixState.popMatrix();//恢复现场
        MatrixState.popMatrix();//恢复现场
    }
}

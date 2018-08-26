package com.bn.addRigidBody;

import javax.vecmath.Vector3f;

import com.bn.MatrixState.MatrixState3D;
import com.bn.catcherFun.MySurfaceView;
import com.bn.object.LoadedObjectVertexNormalTexture;
import com.bn.util.SliderHelper;
import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.Generic6DofConstraint;
import com.bulletphysics.dynamics.constraintsolver.HingeConstraint;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import static com.bn.constant.SourceConstant.*;

public class Claw {
    DiscreteDynamicsWorld dynamicsWorld;
    CollisionShape handSZ;//一个机械手臂上的一个机械手指头
    private CollisionShape[] csa = new CollisionShape[3];//一个机械手臂上的的三个胶囊
    private CollisionShape[] csc = new CollisionShape[3];//一个机械手臂上的的三个胶囊
    private CollisionShape[] csag = new CollisionShape[2];//一个机械手臂上的的三个胶囊
    public static HingeConstraint[] hingeConstraint = new HingeConstraint[4];
    private LoadedObjectVertexNormalTexture[] bodyForDraws;
    public boolean motorFlag;

    int mProgram;
    int clawId;
    int ganId;
    int dunId;
    int ganY = 6;
    int ganZ = 12;
    public static RigidBody[] body = new RigidBody[4];
    public static RigidBody bodyg[] = new RigidBody[2];
    public static RigidBody bodyhuagan[] = new RigidBody[1];

    // 机械手
    public Claw(int clawId, int ganId, int dunId, MySurfaceView mv, DiscreteDynamicsWorld dynamicsWorld,
                LoadedObjectVertexNormalTexture[] bodyForDraws, int mProgram) {
        this.dunId = dunId;                  // 机械手 棕色的物体 绳子下方的那个
        this.clawId = clawId;                // 机械手 手指的纹理 256x256 纯蓝绿色的
        this.ganId = ganId;                  // 杆的纹理 绳子 灰色的 256x256
        this.bodyForDraws = bodyForDraws;    // 机械手的模型数组 (Vertex Normal Texture 顶点坐标 法向量 和 纹理坐标)
        this.mProgram = mProgram;            // 着色器ID
        this.dynamicsWorld = dynamicsWorld;  // 物理世界
        motorFlag = false;                   // 马达是否闭合标记
        initRigidBodys();                    // 初始化刚体
    }

    private void initRigidBodys() {

        // 每个手指 有三段
        csa[0] = new CapsuleShape(clawRadius, claw1th / 2 - 2 * clawRadius); // 胶囊形状  两个参数--半径和高
        csa[1] = new CapsuleShape(clawRadius, claw2th / 2 - 2 * clawRadius);
        csa[2] = new CapsuleShape(clawRadius, claw3th / 2 - 2 * clawRadius);

        csc[0] = addChild(csa[0], claw1th);
        csc[1] = addChild(csa[1], claw2th);
        csc[2] = addChild(csa[2], claw3th);

        // 4个机械手指
        body[0] = addRigidBody(1, csc, clawRadius, ganY, ganZ, 0, false, 0);
        body[1] = addRigidBody(1, csc, 0, ganY, ganZ - clawRadius, 90, false, 0);
        body[2] = addRigidBody(1, csc, -clawRadius, ganY, ganZ, 180, false, 0);
        body[3] = addRigidBody(1, csc, 0, ganY, ganZ + clawRadius, 270, false, 0);

        //机械手的手臂的胶囊的创建
        csag[0] = new CapsuleShape(ganURadius, ganULength - 2 * ganURadius);
        csag[1] = new CapsuleShape(ganLRadius, ganLLength - 2 * ganLRadius);
        bodyg[0] = addRigidBody(1, csag, 0, ganY, ganZ, 0, true, 1);//杆 灰白色的绳子
        bodyg[1] = addRigidBody(1, csag, 0, ganY, ganZ, 0, true, 2);//顿 棕色的

        addjoint6DOF(bodyg[0], bodyg[1]); // 短臂 和 手杆 之间的 6自由度关节
//    	 Transform transform = new Transform();//创建变换对象
//		 transform.setIdentity();//初始化变换
//		 transform.origin.set(new Vector3f(0,0,ganZ));//设置变换的起点
//		bodyg[0].setCenterOfMassTransform(transform);

        // 4个机械手指与机械手短臂 创建 ‘铰链关节’
        addHingeConstraint(body[0], bodyg[1], -clawRadius, 0, 0, 0);
        addHingeConstraint(body[1], bodyg[1], -clawRadius, 0, 0, 1);
        addHingeConstraint(body[2], bodyg[1], -clawRadius, 0, 0, 2);
        addHingeConstraint(body[3], bodyg[1], -clawRadius, 0, 0, 3);

    }

    // 创建‘六自由度关节’   两个刚体之间
    private void addjoint6DOF(RigidBody rbA, RigidBody rbB) {
        Generic6DofConstraint joint6DOF; // 声明 六自由度关节
        Transform localA = new Transform();
        Transform localB = new Transform();
        localA.setIdentity();
        localB.setIdentity();             // 创建和初始化 变换对象
        localA.origin.set(0, ganLLength, 0);
        localB.origin.set(0, ganLLength, 0);// 设置变换对象 的平移部分

        joint6DOF = new Generic6DofConstraint(rbA, rbB, localA, localB, true);

        Vector3f limitTrans = new Vector3f(); // 存储六自由度关节 旋转角度 上下限向量
        limitTrans.set(-0.1f, -BulletGlobals.FLT_EPSILON, -0.1f); // 设置旋转角度的下限3个分量
        joint6DOF.setAngularLowerLimit(limitTrans);
        limitTrans.set(0.1f, BulletGlobals.FLT_EPSILON, 0.1f);    // 设置旋转角度的上限3个分量
        joint6DOF.setAngularUpperLimit(limitTrans);

        dynamicsWorld.addConstraint(joint6DOF, true);// 将约束添加进物理世界
    }

    // 这是 组装 机械手 ‘抓的手指头’ 的‘三节关节’ 的 ‘每节关节的俩个胶囊’ 的组装
    public CompoundShape addChild(CollisionShape shape, float height)//组装出所需的胶囊
    {
        /*
		 * CollisionShape 碰撞形状
		 * CompoundShape 继承 CollisionShape 组合碰撞形状
		 * CapsuleShape  继承 CollisionShape 胶南碰撞形状
		 */
        CompoundShape comShape = new CompoundShape(); //创建组合形状

        Transform localTransform = new Transform();    //	创建变换对象
        localTransform.setIdentity();                //	初始化变换
        localTransform.origin.set(new Vector3f(0, -height / 4, 0));//	设置变换的起点
        comShape.addChildShape(localTransform, shape);//	添加子形状----胶囊

        localTransform.setIdentity();//初始化变换
        localTransform.origin.set(new Vector3f(0, -3 * height / 4, 0));//设置变换的起点
        comShape.addChildShape(localTransform, shape);//	添加子形状----胶囊

        return comShape;// 组合碰撞形状中 有两个子碰撞形状 目前都是同一个胶囊形状实例 但是都不同变换的起点
    }

    // 增加刚体 给定多个碰撞形状 会组合成一个 复合碰撞形状
    private RigidBody addRigidBody(float mass, CollisionShape[] shape,
                                   float cx, float cy, float cz, float angle, boolean isgan, int gbz) {

        CompoundShape comShape = new CompoundShape();  // 创建组合碰撞形状
        if (isgan) {
            //杆的质量中心在杆的中点的下面的1的位置。
            Transform localTransform = new Transform();//创建变换对象
            if (gbz == 1) {
                localTransform.setIdentity();//初始化变换
                localTransform.origin.set(new Vector3f(0, ganTLength / 2 + ganLLength / 2, 0));//设置变换的起点
                comShape.addChildShape(localTransform, shape[0]);//添加子形状----胶囊
            }
            if (gbz == 2) {
                localTransform.setIdentity();//初始化变换-ganULength/2-ganLLength/2+1
                localTransform.origin.set(new Vector3f(0, ganLLength / 2, 0));//设置变换的起点1.403
                comShape.addChildShape(localTransform, shape[1]);//添加子形状---胶囊
            }
        } else {
            //创建一个机械手抓的手指头
            Transform localTransform = new Transform();//创建变换对象
            localTransform.setIdentity();//初始化变换
            localTransform.origin.set(new Vector3f(0, 0, 0));//设置变换的起点
            localTransform.basis.rotZ((float) Math.toRadians(90 + clawAngle1));//90+
            comShape.addChildShape(localTransform, shape[0]);//添加子形状----胶囊

            localTransform.setIdentity();//初始化变换
            localTransform.origin.set(new Vector3f(
                    claw1th * (float) Math.cos(Math.toRadians(clawAngle1)),
                    claw1th * (float) Math.sin(Math.toRadians(clawAngle1)),
                    0));//设置变换的起点
            localTransform.basis.rotZ((float) Math.toRadians(90 - clawAngle2));
            comShape.addChildShape(localTransform, shape[1]);//添加子形状---胶囊

            localTransform.setIdentity();//初始化变换
            localTransform.origin.set(new Vector3f(
                    clawtzx,
                    claw1th * (float) Math.sin(Math.toRadians(clawAngle1)) - claw2th * (float) Math.cos(Math.toRadians(90 - clawAngle2)),
                    0));//设置变换的起点1.403
            localTransform.basis.rotZ((float) Math.toRadians(360 - clawAngle3));
            comShape.addChildShape(localTransform, shape[2]);//添加子形状---胶囊1
        }

        Vector3f localInertia = new Vector3f();
        localInertia.set(0f, 0f, 0f);
        if (mass != 0f) {    // 如果质量不是0，那么需要加入惯性
            comShape.calculateLocalInertia(mass, localInertia);
        }

        // 一个手指 整体的变换矩阵
        Transform startTransform = new Transform();                //	创建刚体的初始变换对象
        startTransform.setIdentity();                            //	初始化变换对象
        startTransform.basis.rotY((float) Math.toRadians(angle));//  设置变换绕Y轴旋转了angle
        startTransform.origin.set(new Vector3f(cx, cy, cz));    //  设置变换的起点
        DefaultMotionState myMotionState = new DefaultMotionState(startTransform);// 创建刚体的‘运动状态对象’
        RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(
                mass,
                myMotionState,
                comShape,
                localInertia);// 创建刚体的‘描述信息对象’ 包含 质量, 运动状态, 碰撞形状引用, ‘惯性向量’

        RigidBody body = new RigidBody(rbInfo);
        body.setRestitution(0.8f);    // 反弹系数  restitution 返还
        body.setFriction(0.0f);        // 摩擦系数  friction 摩擦力；摩擦
        body.setGravity(new Vector3f(0, 0, 0)); // 若不需要重力,设置为0
        body.setActivationState(RigidBody.DISABLE_DEACTIVATION); // 先不要激活

        dynamicsWorld.addRigidBody(body); // 刚体加入物理世界
        return body;
    }

    // 一个手指与短臂 之间的 '铰链约束'  Hinge 铰链
    private void addHingeConstraint(RigidBody rbA, RigidBody rbB, float cx, float cy, float cz, int index) {//添加铰链约束
        Transform transA = new Transform();//创建变换
        Transform transB = new Transform();//创建变换
        if (index == 0) {
            transA.setIdentity();//初始化变换     getCenterOfMassPosition(out);
            transA.origin.set(cx, cy, cz);//约束的位置

            transB.setIdentity();//初始化变换
            transB.origin.set(new Vector3f(0, ganLLength / 2, 0));

            hingeConstraint[index] = new HingeConstraint(rbA, rbB, transA, transB);//创建约束
            hingeConstraint[0].setLimit(-0.7f, 0); // 设置‘铰链’ ‘约束的转动范围’
            dynamicsWorld.addConstraint(hingeConstraint[index], true);//将约束添加到物理世界 关于rbA和rbB两个‘刚体之间的约束’

            hingeConstraint[0].enableAngularMotor(true, motorFlag ? -1.4f : 1.4f, 500f);
        }
        if (index == 1) {
            transA.setIdentity();//初始化变换
            transA.origin.set(cx, cy, cz);//约束的位置
            transB.setIdentity();//初始化变换
            transB.basis.rotY((float) Math.toRadians(90));
            transB.origin.set(new Vector3f(0, ganLLength / 2, 0));
            hingeConstraint[1] = new HingeConstraint(rbA, rbB, transA, transB);//创建约束
            hingeConstraint[1].setLimit(-0.7f, 0, 0.9f, 0.3f, 1.0f);
            dynamicsWorld.addConstraint(hingeConstraint[1], true);//将约束添加到物理世界
            hingeConstraint[1].enableAngularMotor(true, motorFlag ? -1.4f : 1.4f, 500f);
        }
        if (index == 2) {
            transA.setIdentity();//初始化变换
            transA.basis.rotY((float) Math.toRadians(180));
            transA.origin.set(cx, cy, cz);//约束的位置
            transB.setIdentity();//初始化变换
            transB.origin.set(new Vector3f(0, ganLLength / 2, 0));
            hingeConstraint[index] = new HingeConstraint(rbA, rbB, transA, transB);//创建约束
            hingeConstraint[index].setLimit(0.0f, 0.7f, 0.9f, 0.3f, 1.0f);
            dynamicsWorld.addConstraint(hingeConstraint[index], true);//将约束添加到物理世界
            hingeConstraint[index].enableAngularMotor(true, motorFlag ? 1.4f : -1.4f, 500f);
        }
        if (index == 3) {
            transA.setIdentity();//初始化变换
            transB.setIdentity();//初始化变换
            transB.basis.rotY((float) Math.toRadians(-90));
            transB.origin.set(new Vector3f(0, ganLLength / 2, 0));
            hingeConstraint[index] = new HingeConstraint(rbA, rbB, transA, transB);//创建约束
            hingeConstraint[index].setLimit(-0.7f, 0, 0.9f, 0.3f, 1.0f);
            dynamicsWorld.addConstraint(hingeConstraint[index], true);//将约束添加到物理世界
            hingeConstraint[index].enableAngularMotor(true, motorFlag ? -1.4f : 1.4f, 500f);
        }
    }

    public void drawSelf() {
        drawclaw(); // 渲染4个手指
        drawgan();  // 渲染绳子和棕色的盘子
    }

    public void drawclaw() {
        for (int i = 0; i < body.length; i++) {

            MatrixState3D.pushMatrix();

            Transform trans = body[i].getMotionState().getWorldTransform(new Transform());//获取这个物体的变换信息对象
            // MatrixState3D.translate(trans.origin.x, trans.origin.y, trans.origin.z);//进行移位变换 hhl fix 多余
            trans.getOpenGLMatrix(MatrixState3D.getMMatrix());
//            MatrixState3D.pushMatrix(); // hhl fix 多余
            MatrixState3D.scale(scaleblclaw * 1, scaleblclaw * 1, scaleblclaw * 1);
            bodyForDraws[i].drawSelf(clawId);
//            MatrixState3D.popMatrix();  // hhl fix 多余
            MatrixState3D.popMatrix();
        }
    }

    public void drawgan() {
        for (int i = 0; i < bodyg.length; i++) {
            MatrixState3D.pushMatrix();
            Transform trans = bodyg[i].getMotionState().getWorldTransform(new Transform());//获取这个物体的变换信息对象
            //MatrixState3D.translate(trans.origin.x, trans.origin.y, trans.origin.z);//进行移位变换 hhl fix 多余 getOpenGLMatrix会做这个处理
            trans.getOpenGLMatrix(MatrixState3D.getMMatrix()); // hhl 实际会导致了 trans的矩阵 写入了 MatrixState3D

            //MatrixState3D.pushMatrix(); // hhl fix 多余
            MatrixState3D.scale(scalebl * 1, scalebl * 1, scalebl * 1);
            if (i == 0) {
                bodyForDraws[4].drawSelf(ganId); // 灰白色的绳子/杆
            }
            if (i == 1) {
                bodyForDraws[5].drawSelf(dunId); // 棕色的盘
            }
            //MatrixState3D.popMatrix();

            MatrixState3D.popMatrix();
        }
    }

    public void changeMotor() {
        for (int i = 0; i < hingeConstraint.length; i++) {

            hingeConstraint[0].enableAngularMotor(true, motorFlag ? 1.4f : -1.4f, 500f);
            hingeConstraint[3].enableAngularMotor(true, motorFlag ? 1.4f : -1.4f, 500f);
            hingeConstraint[1].enableAngularMotor(true, motorFlag ? 1.4f : -1.4f, 500f);
            hingeConstraint[2].enableAngularMotor(true, motorFlag ? -1.4f : 1.4f, 500f);
        }

    }

    public void moveBy(Vector3f vec3) {
        MotionState ms1 = SliderHelper.cubeBody.getMotionState();
        Transform trans1 = ms1.getWorldTransform(new Transform());
        trans1.origin.add(vec3);
        ms1.setWorldTransform(trans1);
        SliderHelper.cubeBody.setMotionState(ms1);
    }
}

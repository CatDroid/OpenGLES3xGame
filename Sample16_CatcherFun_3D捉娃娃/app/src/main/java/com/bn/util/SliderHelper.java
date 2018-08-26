package com.bn.util;

import static com.bn.constant.SourceConstant.*;
import javax.vecmath.Vector3f;
import com.bn.MatrixState.MatrixState3D;
import com.bn.addRigidBody.Claw;
import com.bn.catcherFun.MySurfaceView;
import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.SliderConstraint;
import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.Transform;
import android.annotation.SuppressLint;

// 滑动 关节 辅助类
@SuppressLint("UseSparseArrays")
public class SliderHelper 
{	

	public static RigidBody cubeBody;//刚体引用

	private int TextureId;//纹理引用
	private SliderConstraint sliderUD;//滑动关节引用 滑块约束
	private DiscreteDynamicsWorld dynamicsWorld;//世界对象  离散动力学世界??
	public SliderHelper(int TextureId,MySurfaceView viewManager,DiscreteDynamicsWorld dynamicsWorld)
	{
		this.TextureId=TextureId;
		this.dynamicsWorld=dynamicsWorld;
		initWorld();
	}

	/**
	 * Bullet库 刚体库
	 * LinearMath 线性数学模块
	 * BulletCollision 碰撞检测模块
	 * BulletDynamics 刚体模拟模块
	 * BulletSoftBody 可变形体模拟模块
	 *
	 * Bullet是一个开源的物理引擎，使用C++编写
	 * https://github.com/bulletphysics
	 * Bullet Physics SDK
	 * Bullet(version 2.77)中提供了6中基本的约束：
	 *		点点约束 btPoint2PointConstraint
	 * 		铰链约束 btHingeConstraint
	 * 		滑动约束 btSliderConstraint
	 * 		锥形约束 btConeTwistConstraint
	 * 		通用的6自由度约束 btGeneric6DofConstraint
	 * 		接触点约束 btContactConstraint
	 */

	// 设置滑动关节变换矩阵参数
	private void initWorld()
	{
		CollisionShape boxShape;// ‘碰撞形状’引用
		boxShape=new BoxShape(new Vector3f(cubeSize,cubeSize/2,cubeSize));// 创建长方体盒 ‘碰撞形状’
		cubeBody = RigidBodyHelper.addRigidBody( // 为长方体盒添加刚体 rigid 坚硬的 RigidBody 刚体
				0f,
				boxShape,
				0,8,12,
				dynamicsWorld,
				true);

		// 添加长方体与爪子之间的约束
        Vector3f originA = new Vector3f(0, 0, 0);
 		Vector3f originB = new Vector3f(0, 0, 0);
		
		originA.set(0, 0, 0);			//	设置从 关节 到长方体‘质心’的‘平移变换’信息 origin 原点
	    originB.set(0,ganULength/2, 0);	//	设置从 关节 到机器手杆/爪子杆‘质心’的‘平移变换’信息
	 	addSliderConstraint(			//	添加长方体与爪子之间的约束  长方体与机械手之间的‘滑动关节’
	 			cubeBody,				//  两个刚体
				Claw.bodyg[0],
				BulletGlobals.SIMD_PI/2,// 	角度
				originA,originB,		//  之间的位移
				true
		);
	}

	// 刚体之间滑动关节的方法
	private void addSliderConstraint(RigidBody ra,RigidBody rb,
									 float angle,
									 Vector3f originA,Vector3f originB,
									 boolean force){

		// 变换矩阵包含缩放 位移 和 旋转
		// Transform 目前认为是  列主 右手坐标系  setEulerZYX 是 ZYX*vec3
		Transform localA = new Transform();//创建变换对象A  创建两个刚体的变换对象并初始化
		Transform localB = new Transform();//创建变换对象B
		localA.setIdentity();//初始化变换对象A  Transform类中包含一个Vector3f origin和Matrix3f basis
		localB.setIdentity();//初始化变换对象B  Vector3f 初始化为0,0,0  Matrix3f 初始化为单位矩阵
		MatrixUtil.setEulerZYX(localA.basis, 0, 0, angle);//设置变换对象A的旋转部分
		MatrixUtil.setEulerZYX(localB.basis, 0, 0, angle);//设置变换对象B的旋转部分
		localA.origin.set(originA);// 设置变换对象A的平移部分
		localB.origin.set(originB);	//设置变换对象B的平移部分


		sliderUD = new SliderConstraint(ra, rb, localA, localB, force);//创建‘滑动关节约束’对象  ra和rb之间的约束
		sliderUD.setLowerLinLimit(-2.8f);	// 控制滑动的最小距离 Lin Limit
		sliderUD.setUpperLinLimit(0f);		// 控制滑动的最大距离 Lin Limit
		sliderUD.setLowerAngLimit(0);		// 控制转动的下限
		sliderUD.setUpperAngLimit(0);		// 控制转动的上限
		sliderUD.setDampingDirAng(1.0f);	// 设置转动阻尼
		sliderUD.setDampingDirLin(1f); 		// 设置线性阻尼 滑动阻尼 Sliding damping
		dynamicsWorld.addConstraint(sliderUD,true);//将滑动约束，添加进，物理世界(离散动力学世界??)
		
	}

	public void slideUD(float mulFactor){		//  捉娃娃时候上升和下降
		sliderUD.getRigidBodyB().activate();	//	激活箱子刚体  Claw.bodyg[0] RigidBody rbB
		sliderUD.setPoweredLinMotor(true);		//	启动关节对应的‘马达’
		sliderUD.setMaxLinMotorForce(200.0f);	//	设置‘马达’的‘滑动驱动力‘
		sliderUD.setTargetLinMotorVelocity(20.0f*mulFactor);//	设置‘马达’的‘滑动驱动速度’
	}


	public void drawSelf() {
		
         	MatrixState3D.pushMatrix();
         	Transform trans = cubeBody.getMotionState().getWorldTransform(new Transform());
			MatrixState3D.translate(trans.origin.x,trans.origin.y, trans.origin.z);
			trans.getOpenGLMatrix(MatrixState3D.getMMatrix());
			MatrixState3D.translate(0, -0.6f, 0);
         	ganbox.drawSelf(TextureId);
			MatrixState3D.popMatrix();
         
	}
}

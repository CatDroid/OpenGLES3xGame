package com.bn.util;

import javax.vecmath.Vector3f;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

// 刚体加载辅助类
public class RigidBodyHelper {

	// 添加刚体 在物理世界创建单个刚体，并设置 ’刚体运动状态，刚体恢复系数，摩擦系数‘  最后将’刚体‘添加到’物理世界‘，以备’模拟‘时使用
	// 返回刚体 GameView update() 会生成9个娃娃 分别是Niu Doll(doll0 doll1) Phone Parrot Tv  Car  Camera 和 Robot
	public static RigidBody addRigidBody(float mass,	// 刚体的‘质量’
										 CollisionShape shape, // 碰撞形状  Collision 碰撞
										 float cx,float cy,float cz,// 变换的起点
										 DiscreteDynamicsWorld dynamicsWorld,
										 boolean noGravity // 是否需要重力 不需要重力的话 添加到物理世界后 把重力设置为0
	) {
		
		CollisionShape comShape=shape; 		//	保存‘碰撞形状引用’
  		boolean isDynamic = (mass != 0f);	//	确定物体是否可运动  mass!=0 说明物体有质量
		Vector3f localInertia = new Vector3f(0f, 0f, 0f);		// 创建存放‘惯性’的向量 局部惯性 Inertia 惯性
		if (isDynamic) {
			comShape.calculateLocalInertia(mass, localInertia);	//	‘计算惯性’
		}
		
		Transform startTransform = new Transform();			//	创建刚体的初始变换对象
		startTransform.setIdentity();						//	初始化变换对象
		startTransform.origin.set(new Vector3f(cx, cy, cz));// 	设置变换的起点
		DefaultMotionState myMotionState = new DefaultMotionState(startTransform);//创建刚体的‘运动状态对象’

		RigidBodyConstructionInfo rbInfo =
				new RigidBodyConstructionInfo(mass, myMotionState, comShape, localInertia);
		// 创建刚体的‘描述信息对象’ 包含 质量   初始运动状态对象(含有位移)  碰撞形状引用 ‘惯性向量’

		RigidBody body = new RigidBody(rbInfo);	// 创建刚体对象
		body.setRestitution(0.0f);				// 设置反弹系数
		body.setFriction(0.2f);					// 设置摩擦系数
		body.forceActivationState(RigidBody.DISABLE_DEACTIVATION);// 刚体一开始是非激活状态
		dynamicsWorld.addRigidBody(body);		// 将刚体添加进物理世界
		if(noGravity){
	 		body.setGravity(new Vector3f(0,0,0));//	若不需要重力，设为0
	 	}
		return body;
    }

}

package com.bn.util;

import javax.vecmath.Vector3f;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
public class RigidBodyHelper {	
	public static RigidBody addRigidBody(float mass, CollisionShape shape,
			float cx,float cy,float cz,DiscreteDynamicsWorld dynamicsWorld,boolean noGravity) 
    {
		
		CollisionShape comShape=shape; //保存碰撞形状引用
  		boolean isDynamic = (mass != 0f);//确定物体是否可运动
		Vector3f localInertia = new Vector3f(0f, 0f, 0f);//创建存放惯性的向量
		if (isDynamic) {
			comShape.calculateLocalInertia(mass, localInertia);//计算惯性
		}
		
		Transform startTransform = new Transform();//创建刚体的初始变换对象
		startTransform.setIdentity();//初始化变换对象
		startTransform.origin.set(new Vector3f(cx, cy, cz));//设置变换的起点
		DefaultMotionState myMotionState = new DefaultMotionState(startTransform);//创建刚体的运动状态对象
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState,
				comShape, localInertia);//创建刚体描述信息对象
		RigidBody body = new RigidBody(rbInfo);//创建刚体对象
		body.setRestitution(0.0f);//设置反弹系数
		body.setFriction(0.2f);//设置摩擦系数
		body.forceActivationState(RigidBody.DISABLE_DEACTIVATION);//刚体一开始是非激活状态
		dynamicsWorld.addRigidBody(body);//将刚体添加进物理世界
		if(noGravity){
	 		body.setGravity(new Vector3f(0,0,0));//若不需要重力，设为0
	 	}
		return body;
    }

}

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

@SuppressLint("UseSparseArrays")
public class SliderHelper 
{	
	CollisionShape boxShape;//碰撞形状引用
	public static RigidBody cubeBody;//刚体引用
	SliderConstraint sliderUD;//滑动关节引用
	int TextureId;//纹理引用
    MySurfaceView viewManager;//场景管理器引用
    DiscreteDynamicsWorld dynamicsWorld;//世界对象
	public SliderHelper(int TextureId,MySurfaceView viewManager,DiscreteDynamicsWorld dynamicsWorld)
	{
		this.TextureId=TextureId;
		this.viewManager=viewManager;
		this.dynamicsWorld=dynamicsWorld;
		initWorld();
	}
	public void initWorld()
	{
		boxShape=new BoxShape(new Vector3f(cubeSize,cubeSize/2,cubeSize));//创建长方体盒碰撞形状
		cubeBody = RigidBodyHelper.addRigidBody(0f,boxShape,0,8,12,dynamicsWorld,true);	//添加刚体
		//添加长方体与爪子之间的约束
        Vector3f originA = new Vector3f(0, 0, 0);
 		Vector3f originB = new Vector3f(0, 0, 0);
		
		originA.set(0, 0, 0);//设置从约束到长方体质心的平移变换信息
	    originB.set(0,ganULength/2, 0);//设置从约束到爪子杆质心的平移变换信息
	 	addSliderConstraint(0,cubeBody,Claw.bodyg[0],BulletGlobals.SIMD_PI/2,originA,originB,true);//添加长方体与爪子之间的约束
	}
	public void addSliderConstraint(int index,RigidBody ra,RigidBody rb,float angle,Vector3f originA,Vector3f originB,boolean force){
		Transform localA = new Transform();//创建变换对象A
		Transform localB = new Transform();//创建变换对象B
		localA.setIdentity();//初始化变换对象A
		localB.setIdentity();//初始化变换对象B
		MatrixUtil.setEulerZYX(localA.basis, 0, 0, angle);//设置变换对象A的旋转部分
		MatrixUtil.setEulerZYX(localB.basis, 0, 0, angle);//设置变换对象的旋转部分
		localA.origin.set(originA);//设置变换对象A的平移部分
		localB.origin.set(originB);	//设置变换对象B的平移部分
		sliderUD = new SliderConstraint(ra, rb, localA, localB, force);//创建滑动关节约束对象
		sliderUD.setLowerLinLimit(-2.8f);//控制滑动的最小距离
		sliderUD.setUpperLinLimit(0f);//控制滑动的最大距离
		sliderUD.setLowerAngLimit(0);//控制转动的下限
		sliderUD.setUpperAngLimit(0);//控制转动的上限
		sliderUD.setDampingDirAng(1.0f);//设置转动阻尼
		sliderUD.setDampingDirLin(1f); //设置线性阻尼
		dynamicsWorld.addConstraint(sliderUD,true);//将约束添加进物理世界
		
	}
	public void slideUD(float mulFactor){
		sliderUD.getRigidBodyB().activate();//激活箱子刚体
		sliderUD.setPoweredLinMotor(true);//启动关节对应的马达
		sliderUD.setMaxLinMotorForce(200.0f);//设置马达的滑动驱动力
		sliderUD.setTargetLinMotorVelocity(20.0f*mulFactor);//设置马达的滑动驱动速度
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

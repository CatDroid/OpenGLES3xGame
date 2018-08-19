package com.bn.addRigidBody;

import javax.vecmath.Vector3f;
import com.bn.MatrixState.MatrixState3D;
import com.bn.object.BNAbstractDoll;
import com.bn.object.LoadedObjectVertexNormalTexture;
import com.bn.util.RigidBodyHelper;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.linearmath.Transform;
import static com.bn.constant.SourceConstant.*;
public class Robot  extends BNAbstractDoll{
	int texId;
	DiscreteDynamicsWorld dynamicsWorld;
//	LoadedObjectVertexNormalTexture lovo;
	Vector3f position;
	
	CollisionShape[] robotshape=new CollisionShape[6];
	
	CollisionShape Robotz;
	public Robot(int texId,DiscreteDynamicsWorld dynamicsWorld,
			LoadedObjectVertexNormalTexture lovo,Vector3f position,int bianhao){
		this.texId=texId;
		this.dynamicsWorld=dynamicsWorld;
		this.lovo=lovo;
		this.position=position;
		this.bianhao=bianhao;
		initRigidBodys();
	}
	public void initRigidBodys()
	{
		robotshape[0]=new BoxShape(new Vector3f(robotfootx,robotfooty,robotfootz));//这是机器人的脚的胶囊
		robotshape[1]=new BoxShape(new Vector3f(robottuix,robottuiy,robottuiz));//这是机器人的腿的胶囊
		robotshape[2]=new BoxShape(new Vector3f(robotbodyx,robotbodyy,robotbodyz));//这是机器人的身体的胶囊
		robotshape[3]=new BoxShape(new Vector3f(robottopx,robottopy,robottopz));//这是机器人的脑袋的胶囊
		robotshape[4]=new BoxShape(new Vector3f(robothand1x,robothand1y,robothand1z));//这是机器人的大手臂1的胶囊
		robotshape[5]=new BoxShape(new Vector3f(robothand2x,robothand2y,robothand2y));//这是机器人的小手臂的胶囊
		Robotz=addChild(robotshape);//组装出来机器人的组合胶囊
		
		RigidBodydoll=RigidBodyHelper.addRigidBody(1,Robotz,position.x,position.y,position.z,dynamicsWorld,false);
	
	}
	 //胶囊的组装
  	public CompoundShape addChild(CollisionShape[] shape)//组装出所需的胶囊
  	{
  		CompoundShape comShape=new CompoundShape(); //创建组合形状
  		Transform localTransform = new Transform();//创建变换对象
  		//左脚
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(robotfootx+robotfooty/4,-robotfooty-robottuiy*2-robotbodyy,0));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[0]);//添加子形状----胶囊
  		//右脚
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(-robotfootx-robotfooty/4,-robotfooty-robottuiy*2-robotbodyy,0));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[0]);//添加子形状----胶囊
  		//左腿
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(robotfootx,-robottuiy-robotbodyy,0));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[1]);//添加子形状----胶囊
  		//右腿
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(-robotfootx,-robottuiy-robotbodyy,0));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[1]);//添加子形状----胶囊
  		//身体
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(0,0,0));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[2]);//添加子形状----胶囊
  		//脑袋
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(0,robotbodyy+robottopy,0));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[3]);//添加子形状----胶囊
  		//左手臂
  		//---大手臂
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(robotbodyx+robothand1x*2,0,0));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[4]);//添加子形状----胶囊
  		//---小手臂
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(robotbodyx+robothand1x*2,-robotbodyy+robothand2y,robothand1z+robothand2z));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[5]);//添加子形状----胶囊
  		//右手臂
  		//---大手臂
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(-robotbodyx-robothand1x*2,0,0));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[4]);//添加子形状----胶囊
  		//---小手臂
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(-robotbodyx-robothand1x*2,-robotbodyy+robothand2y,robothand1z+robothand2z));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[5]);//添加子形状----胶囊
  		
  		return comShape;
  	}
  	public void drawSelf()
	{
  		    MatrixState3D.pushMatrix();
			Transform trans=RigidBodydoll.getMotionState().getWorldTransform(new Transform());//获取这个物体的变换信息对象
			MatrixState3D.translate(trans.origin.x,trans.origin.y-speed, trans.origin.z);//进行移位变换
			trans.getOpenGLMatrix(MatrixState3D.getMMatrix());
			
			MatrixState3D.pushMatrix();
			MatrixState3D.scale(robotbl,robotbl,robotbl);
			lovo.drawSelf(texId);
			MatrixState3D.popMatrix();

			MatrixState3D.popMatrix();
  		
	}
}

package com.bn.addRigidBody;

import javax.vecmath.Vector3f;
import com.bn.MatrixState.MatrixState3D;
import com.bn.catcherFun.MySurfaceView;
import com.bn.object.BNAbstractDoll;
import com.bn.object.LoadedObjectVertexNormalTexture;
import com.bn.util.RigidBodyHelper;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.linearmath.Transform;
import static com.bn.constant.SourceConstant.*;
public class Phone extends BNAbstractDoll{
	int texId;
	DiscreteDynamicsWorld dynamicsWorld;
//	LoadedObjectVertexNormalTexture lovo;
	Vector3f position;
	CollisionShape[]  phoneshape=new CollisionShape[4];
	CollisionShape phone;

	
	//这是将刚体位置画出来的一个长方体类
	MySurfaceView mv;
	public Phone(int texId,DiscreteDynamicsWorld dynamicsWorld,
			LoadedObjectVertexNormalTexture lovo,Vector3f position,int bianhao)
	{
		this.texId=texId;
		this.dynamicsWorld=dynamicsWorld;
		this.position=position;
		this.lovo=lovo;
		this.bianhao=bianhao;
		initRigidBodys();
	}
	public void initRigidBodys()
	{
		phoneshape[0]=new BoxShape(new Vector3f(phone3c,phone3g,phone3k));
		phoneshape[1]=new BoxShape(new Vector3f(phone2c,phone2g,phone2k));
		phoneshape[2]=new BoxShape(new Vector3f(phone1c,phone1g,phone1k));
		phoneshape[3]=new CapsuleShape(phoneyr,phoneyh-phoneyr*2);
		phone=addChild(phoneshape);
		RigidBodydoll=RigidBodyHelper.addRigidBody(1,phone,position.x,position.y,position.z,dynamicsWorld,false);
	
	}
	//胶囊的组装
  	public CompoundShape addChild(CollisionShape[] shape)//组装出所需的胶囊
  	{
  		CompoundShape comShape=new CompoundShape(); //创建组合形状
  		Transform localTransform = new Transform();//创建变换对象
  		
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(0,-phone3g-phone2g*2,phone3g/2));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[0]);//添加子形状----胶囊
  		
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(0,-phone2g,phone3g));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[1]);//添加子形状----胶囊
  		
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(0,phone1g,0));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[2]);//添加子形状----胶囊
  		
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(0,phone1g*2+phoneyh,
  				phone1k-phoneyr*2));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[3]);//添加子形状----胶囊
  		
  		return comShape;
  	}
  	public void drawSelf()
  	{
  		
	  		MatrixState3D.pushMatrix();
			Transform trans=RigidBodydoll.getMotionState().getWorldTransform(new Transform());//获取这个物体的变换信息对象
			MatrixState3D.translate(trans.origin.x,trans.origin.y-speed, trans.origin.z);//进行移位变换
			trans.getOpenGLMatrix(MatrixState3D.getMMatrix());
			
			MatrixState3D.pushMatrix();
			MatrixState3D.scale(phonebz, phonebz, phonebz);
			lovo.drawSelf(texId);
			MatrixState3D.popMatrix();
			MatrixState3D.popMatrix();
  	
  	}
}

package com.bn.addRigidBody;

import javax.vecmath.Vector3f;
import com.bn.MatrixState.MatrixState3D;
import com.bn.object.BNAbstractDoll;
import com.bn.object.LoadedObjectVertexNormalTexture;
import com.bn.util.RigidBodyHelper;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.linearmath.Transform;
import static com.bn.constant.SourceConstant.*;
public class Car extends BNAbstractDoll {
	int texId;
	DiscreteDynamicsWorld dynamicsWorld;
//	LoadedObjectVertexNormalTexture lovo;
	Vector3f position;
	CollisionShape[] Carshape=new CollisionShape[3];
	CollisionShape CarS;
	public Car(int texId,DiscreteDynamicsWorld dynamicsWorld,
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
		Carshape[0]=new BoxShape(new Vector3f(Carfootx,Carfooty,Carfootz));
		Carshape[1]=new BoxShape(new Vector3f(Carbuttomx,Carbuttomy,Carbuttomz));
		Carshape[2]=new SphereShape(CarR);
		
		CarS=addChild(Carshape);
		
		RigidBodydoll=RigidBodyHelper.addRigidBody(1,CarS,position.x,position.y,position.z,dynamicsWorld,false);
	}
	 //胶囊的组装
  	public CompoundShape addChild(CollisionShape[] shape)//组装出所需的胶囊
  	{
  		CompoundShape comShape=new CompoundShape(); //创建组合形状
  		Transform localTransform = new Transform();//创建变换对象
  		//左前轮
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(Carbuttomx-Carfootx*2,Carfooty,Carbuttomz-Carfootz*3));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[0]);//添加子形状----胶囊
  		//左后轮
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(Carbuttomx-Carfootx*2,Carfooty,-Carbuttomz+Carfootz*3));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[0]);//添加子形状----胶囊
  		//右前轮
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(-Carbuttomx+Carfootx*2,Carfooty,Carbuttomz-Carfootz*3));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[0]);//添加子形状----胶囊
  		//右后轮
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(-Carbuttomx+Carfootx*2,Carfooty,-Carbuttomz+Carfootz*3));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[0]);//添加子形状----胶囊
  		//车底座
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(0,Carfooty+Carbuttomy,0));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[1]);//添加子形状----胶囊
  		//车顶棚
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(0,Carfooty+Carbuttomy*2,0));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[2]);//添加子形状----胶囊
  		return comShape;
  	}
  	public void drawSelf()
	{

  	
	  		MatrixState3D.pushMatrix();
			Transform trans=RigidBodydoll.getMotionState().getWorldTransform(new Transform());//获取这个物体的变换信息对象
			MatrixState3D.translate(trans.origin.x,trans.origin.y-speed, trans.origin.z);//进行移位变换
			trans.getOpenGLMatrix(MatrixState3D.getMMatrix());
			
			MatrixState3D.pushMatrix();
			MatrixState3D.scale(Carbl,Carbl,Carbl);
			lovo.drawSelf(texId);
			MatrixState3D.popMatrix();
			
			MatrixState3D.popMatrix();
  	
	}
	
}

package com.bn.addRigidBody;

import javax.vecmath.Vector3f;
import com.bn.MatrixState.MatrixState3D;
import com.bn.object.BNAbstractDoll;
import com.bn.object.LoadedObjectVertexNormalTexture;
import com.bn.util.RigidBodyHelper;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.linearmath.Transform;
import static com.bn.constant.SourceConstant.*;
public class Tv extends BNAbstractDoll {
	int texId;
	DiscreteDynamicsWorld dynamicsWorld;
	Vector3f position;
//	LoadedObjectVertexNormalTexture lovo;
	
	CollisionShape[] tvShape=new CollisionShape[5];
	CollisionShape tv;

	
	public Tv(int texId,DiscreteDynamicsWorld dynamicsWorld,
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
		tvShape[0]=new BoxShape(new Vector3f(tvfootx,tvfooty,tvfootz));
		tvShape[1]=new BoxShape(new Vector3f(tvbodyx,tvbodyy,tvbodyz));
		tvShape[2]=new SphereShape(tvtopr);
		tvShape[3]=new CapsuleShape(tvyzr,tvyzh-tvyzr*2);
		tvShape[4]=new SphereShape(tvtopmr);
		tv=addChild(tvShape);
		RigidBodydoll=RigidBodyHelper.addRigidBody(1,tv,position.x,position.y,position.z,dynamicsWorld,false);
	}
	//胶囊的组装
  	public CompoundShape addChild(CollisionShape[] shape)//组装出所需的胶囊
  	{
  		CompoundShape comShape=new CompoundShape(); //创建组合形状
  		Transform localTransform = new Transform();//创建变换对象
  		
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(tvbodyx-tvfootx*2,tvfooty,tvbodyz-2*tvfootz));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[0]);//添加子形状----胶囊
  		
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(tvbodyx-tvfootx*2,tvfooty,-tvbodyz+2*tvfootz));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[0]);//添加子形状----胶囊
  		
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(-tvbodyx+tvfootx*2,tvfooty,-tvbodyz+2*tvfootz));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[0]);//添加子形状----胶囊
  		
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(-tvbodyx+tvfootx*2,tvfooty,tvbodyz-2*tvfootz));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[0]);//添加子形状----胶囊
  		
  		
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(0,tvfooty*2+tvbodyy,0));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[1]);//添加子形状----胶囊
  		
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(0,tvfooty*2+tvbodyy*2,0));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[2]);//添加子形状----胶囊
  		
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(-tvtopr-tvyzh*(float)Math.cos((float)Math.toRadians(tvangle)),
  				tvfooty*2+tvbodyy*2
  				+tvyzh*(float)Math.cos((float)Math.toRadians(tvangle)),0));//设置变换的起点
  		localTransform.basis.rotY((float)Math.toRadians(tvangle));
  		comShape.addChildShape(localTransform, shape[3]);//添加子形状----胶囊
  		
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(tvtopr+tvyzh*(float)Math.cos((float)Math.toRadians(tvangle)),
  				tvfooty*2+tvbodyy*2+tvyzh*(float)Math.cos((float)Math.toRadians(tvangle)),0));//设置变换的起点
  		localTransform.basis.rotY((float)Math.toRadians(-tvangle));
  		comShape.addChildShape(localTransform, shape[3]);//添加子形状----胶囊
  		
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(tvtopr+tvyzh*(float)Math.cos((float)Math.toRadians(tvangle))
  				+tvtopmr,
  				tvfooty*2+tvbodyy*2+tvyzh*(float)Math.cos((float)Math.toRadians(tvangle))+tvtopmr,0));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[4]);//添加子形状----胶囊
  		
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(-tvtopr+tvyzh*(float)Math.cos((float)Math.toRadians(tvangle))-
  				tvtopmr,
  				tvfooty*2+tvbodyy*2+tvyzh*(float)Math.cos((float)Math.toRadians(tvangle))+tvtopmr,0));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[4]);//添加子形状----胶囊
  		
  		return comShape;
  	}
  	public void drawSelf()
  	{
  		
	  		MatrixState3D.pushMatrix();
			Transform trans=RigidBodydoll.getMotionState().getWorldTransform(new Transform());//获取这个物体的变换信息对象
			MatrixState3D.translate(trans.origin.x,trans.origin.y-speed, trans.origin.z);//进行移位变换
			trans.getOpenGLMatrix(MatrixState3D.getMMatrix());
			
			MatrixState3D.pushMatrix();
			MatrixState3D.scale(tvbz, tvbz, tvbz);
			lovo.drawSelf(texId);
			MatrixState3D.popMatrix();			
			MatrixState3D.popMatrix();
  	
  	}
}

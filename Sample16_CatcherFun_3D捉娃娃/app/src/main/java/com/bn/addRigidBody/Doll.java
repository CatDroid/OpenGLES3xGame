package com.bn.addRigidBody;

import javax.vecmath.Vector3f;

import com.bn.catcherFun.MySurfaceView;
import com.bn.object.BNAbstractDoll;
import com.bn.object.LoadedObjectVertexNormalTexture;
import com.bn.util.RigidBodyHelper;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.linearmath.Transform;
import com.bn.MatrixState.MatrixState3D;
import static com.bn.constant.SourceConstant.*;
public class Doll extends BNAbstractDoll {
	
		DiscreteDynamicsWorld dynamicsWorld;
//		public LoadedObjectVertexNormalTexture lovo;
		int texId;
		
		CollisionShape body;
		BoxShape foot;
		BoxShape nose;
		CollisionShape[]  pigbody=new CollisionShape[5];
		CollisionShape  pigshpae;
		
		Vector3f position;
		
		
		//这是将刚体位置画出来的一个长方体类
		MySurfaceView mv;
		public Doll(int texId,DiscreteDynamicsWorld dynamicsWorld,
				LoadedObjectVertexNormalTexture lovo,Vector3f position,int bianhao)
		{
			this.dynamicsWorld=dynamicsWorld;
			this.lovo=lovo;
			this.texId=texId;
			this.position=position;
			this.bianhao=bianhao;
			initRigidBodys();
		}
		public void initRigidBodys()
		{
			//三个参数的值的代表队的含义是                            长     宽      高 半边的长
			body=new BoxShape(new Vector3f(bodyc,bodyg,bodyk)); 
//			body=new CapsuleShape(bodyr,bodyc*2-bodyr);
			foot=new BoxShape(new Vector3f(footc,footg,footk));
			nose=new BoxShape(new Vector3f(nosec,noseg,nosek));
			pigbody[0]=body;
			pigbody[1]=nose;
			pigbody[2]=foot;
			pigbody[3]=new BoxShape(new Vector3f(bodyadd1x,bodyadd1y,bodyadd1z));
			pigbody[4]=new BoxShape(new Vector3f(bodyadd2x,bodyadd2y,bodyadd2z));
			pigshpae=addChild(pigbody);
			
			RigidBodydoll=RigidBodyHelper.addRigidBody(1,pigshpae,position.x,position.y,position.z,dynamicsWorld,false);
		}
		//胶囊的组装
	  	public CompoundShape addChild(CollisionShape[] shape)//组装出所需的胶囊
	  	{
	  		CompoundShape comShape=new CompoundShape(); //创建组合形状
	  		
	  		Transform localTransform = new Transform();//创建变换对象
	  		
	  		localTransform.setIdentity();//初始化变换
	  		localTransform.origin.set(new Vector3f(0,bodyg+footg*2,0));//设置变换的起点
//	  		localTransform.basis.rotZ((float)Math.toRadians(90));
	  		comShape.addChildShape(localTransform, shape[0]);//添加子形状----胶囊
	  		
	  		localTransform.setIdentity();//初始化变换
	  		localTransform.origin.set(new Vector3f(0,bodyg+footg*2,bodyk));//设置变换的起点
	  		comShape.addChildShape(localTransform, shape[3]);//添加子形状----胶囊
	  		
	  		localTransform.setIdentity();//初始化变换
	  		localTransform.origin.set(new Vector3f(0,bodyg+footg*2,-bodyk));//设置变换的起点
	  		comShape.addChildShape(localTransform, shape[3]);//添加子形状----胶囊
	  		
	  		localTransform.setIdentity();//初始化变换
	  		localTransform.origin.set(new Vector3f(0,bodyg*2+footg*2,0));//设置变换的起点
	  		comShape.addChildShape(localTransform, shape[4]);//添加子形状----胶囊
	  		
	  		localTransform.setIdentity();//初始化变换
	  		localTransform.origin.set(new Vector3f(bodyc+nosec,bodyg+footg*2,0));//设置变换的起点
	  		comShape.addChildShape(localTransform, shape[1]);//添加子形状----胶囊
	  		
	  		//==============foot================================
	  		localTransform.setIdentity();//初始化变换1
	  		localTransform.origin.set(new Vector3f(bodyc-footc*2,footg,bodyk-footk*2));//设置变换的起点
	  		comShape.addChildShape(localTransform, shape[2]);//添加子形状----胶囊
	  		
	  		
	  		
	  		localTransform.setIdentity();//初始化变换2
	  		localTransform.origin.set(new Vector3f(bodyc-footc*2,footg,-bodyk+footk*2));//设置变换的起点
	  		comShape.addChildShape(localTransform, shape[2]);//添加子形状----胶囊
	  		
	  		
	  		
	  		localTransform.setIdentity();//初始化变换3
	  		localTransform.origin.set(new Vector3f(-bodyc+footc*2,footg,-bodyk+footk*2));//设置变换的起点
	  		comShape.addChildShape(localTransform, shape[2]);//添加子形状----胶囊
	  		
	  		
	  		
	  		localTransform.setIdentity();//初始化变换4
	  		localTransform.origin.set(new Vector3f(-bodyc+footc*2,footg,bodyk-footk*2));//设置变换的起点
	  		comShape.addChildShape(localTransform, shape[2]);//添加子形状----胶囊
//	  		
	  		
	  		
	  		return comShape;
	  	}
		public void drawSelf()
		{
			
		    	MatrixState3D.pushMatrix();
				Transform trans=RigidBodydoll.getMotionState().getWorldTransform(new Transform());//获取这个物体的变换信息对象
				trans.origin.y=trans.origin.y-speed;
				MatrixState3D.translate(trans.origin.x,trans.origin.y, trans.origin.z);//进行移位变换
				trans.getOpenGLMatrix(MatrixState3D.getMMatrix());
				
				MatrixState3D.pushMatrix();
				MatrixState3D.translate(0.1f,0,0);
				MatrixState3D.scale(pigbl, pigbl, pigbl);
				lovo.drawSelf(texId);
				MatrixState3D.popMatrix();
				MatrixState3D.popMatrix();
			
		}
}

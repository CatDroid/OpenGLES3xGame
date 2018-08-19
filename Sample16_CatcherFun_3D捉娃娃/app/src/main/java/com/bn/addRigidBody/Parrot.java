package com.bn.addRigidBody;

import javax.vecmath.Vector3f;

import com.bn.MatrixState.MatrixState3D;
import com.bn.object.BNAbstractDoll;
import com.bn.object.LoadedObjectVertexNormalTexture;
import com.bn.util.RigidBodyHelper;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.collision.shapes.CylinderShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.linearmath.Transform;
import static com.bn.constant.SourceConstant.*;
public class Parrot extends BNAbstractDoll{
	CollisionShape[] ParrotShape=new CollisionShape[3];
	CollisionShape ParrotFH;
	int texId;
	DiscreteDynamicsWorld dynamicsWorld;
//	LoadedObjectVertexNormalTexture lovo;
	Vector3f position;

	public Parrot(int texId,DiscreteDynamicsWorld dynamicsWorld,
			LoadedObjectVertexNormalTexture lovo,Vector3f position,int bianhao)
	{
		this.texId=texId;
		this.dynamicsWorld=dynamicsWorld;
		this.lovo=lovo;
		this.position=position;
		this.bianhao=bianhao;
		initRigidBodys();
	}
    public void initRigidBodys()
    {
    	//第一个跟第三个参数是圆柱截面的长短半径，第二个参数是圆柱高度
    	ParrotShape[0]=new CylinderShape(new Vector3f(Parrotx,Parroty,Parrotz));
    	ParrotShape[1]=new BoxShape(new Vector3f(ParrotFootx,ParrotFooty,ParrotFootz));
    	ParrotFH=addChild(ParrotShape);//这是组合出来的鹦鹉
    	RigidBodydoll=RigidBodyHelper.addRigidBody(1,ParrotFH,position.x,position.y,position.z,dynamicsWorld,false);
    	
    }
  //胶囊的组装
  	public CompoundShape addChild(CollisionShape[] shape)//组装出所需的胶囊
  	{
  		CompoundShape comShape=new CompoundShape(); //创建组合形状
  		Transform localTransform = new Transform();//创建变换对象
  		
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(0,Parroty,0));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[0]);//添加子形状----胶囊
  		
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(Parrotx-ParrotFooty*2,ParrotFooty,Parrotz));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[1]);//添加子形状----胶囊
  		
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(-Parrotx+ParrotFooty*2,ParrotFooty,Parrotz));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[1]);//添加子形状----胶囊
  		return comShape;
  	}
  	public void drawSelf()
	{
  		
	  		MatrixState3D.pushMatrix();
			Transform trans=RigidBodydoll.getMotionState().getWorldTransform(new Transform());//获取这个物体的变换信息对象
			MatrixState3D.translate(trans.origin.x,trans.origin.y-speed, trans.origin.z);//进行移位变换
			trans.getOpenGLMatrix(MatrixState3D.getMMatrix());
			
			MatrixState3D.pushMatrix();
			MatrixState3D.scale(ParrotBL, ParrotBL, ParrotBL);
			lovo.drawSelf(texId);
			MatrixState3D.popMatrix();
			
			MatrixState3D.popMatrix();
  		
		
	}
}

package com.bn.addRigidBody;

import javax.vecmath.Vector3f;
import com.bn.MatrixState.MatrixState3D;
import com.bn.catcherFun.MySurfaceView;
import com.bn.object.BNAbstractDoll;
import com.bn.object.LoadedObjectVertexNormalTexture;
import com.bn.util.RigidBodyHelper;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.collision.shapes.CylinderShapeZ;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.linearmath.Transform;
import static com.bn.constant.SourceConstant.*;
public class Camera extends BNAbstractDoll{
	int texId;
	DiscreteDynamicsWorld dynamicsWorld;
	//LoadedObjectVertexNormalTexture lovo;
	Vector3f position;
	CollisionShape[] camerashape=new CollisionShape[5];
	//这是辅助的一个刚体位置所在
	MySurfaceView mv;
	public Camera(int texId,DiscreteDynamicsWorld dynamicsWorld,
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
		//这是照相机的主体Body的一个长方体胶囊
		camerashape[0]=new BoxShape(new Vector3f(Camerabodyx,Camerabodyy,Camerabodyz));
		//这是照相机的相机筒的一个圆柱胶囊
		camerashape[1]=new CylinderShapeZ(new Vector3f(CameraR,CameraH,CameraR));
		//这是照相机最上面的一个长方体胶囊
		camerashape[2]=new BoxShape(new Vector3f(CameraTopx,CameraTopy,CameraTopz));
		
		camerashape[3]=addChild(camerashape);
		RigidBodydoll=RigidBodyHelper.addRigidBody(1,camerashape[3],position.x,position.y,position.z,dynamicsWorld,false);
		
		
	} 
	 //胶囊的组装
  	public CompoundShape addChild(CollisionShape[] shape)//组装出所需的胶囊
  	{
  		CompoundShape comShape=new CompoundShape(); //创建组合形状
  		Transform localTransform = new Transform();//创建变换对象
  		
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(0,Camerabodyy,-Camerabodyz-0.08f));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[0]);//添加子形状----胶囊
  		
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(-0.06f,CameraR,CameraH/2-0.03f));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[1]);//添加子形状----胶囊
  		
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(-0.022f,Camerabodyy*2+CameraTopy,-Camerabodyz-0.08f));//设置变换的起点
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
			MatrixState3D.scale(Camerabl,Camerabl,Camerabl);
			lovo.drawSelf(texId);
			MatrixState3D.popMatrix();
			MatrixState3D.popMatrix();
	}

}

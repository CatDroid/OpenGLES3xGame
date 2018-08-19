package com.bn.addRigidBody;

import javax.vecmath.Vector3f;
import com.bn.MatrixState.MatrixState3D;
import com.bn.object.LoadedObjectVertexNormalTexture;
import com.bn.util.RigidBodyHelper;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import static com.bn.constant.SourceConstant.*;
public class BoxRigidBody {
	
	int texId;
	DiscreteDynamicsWorld dynamicsWorld;
	LoadedObjectVertexNormalTexture lovo;
	Vector3f position;
	CollisionShape[] Boxbian=new CollisionShape[2];
	CollisionShape Box;
	public RigidBody Boxrg;
	public BoxRigidBody(int texId,DiscreteDynamicsWorld dynamicsWorld,
			LoadedObjectVertexNormalTexture lovo,Vector3f position)
	{
		this.texId=texId;
		this.dynamicsWorld=dynamicsWorld;
		this.lovo=lovo;
		this.position=position;
		initRigidBody();
	}
	public void initRigidBody()
	{
		Boxbian[0]=new BoxShape(new Vector3f(boxbian1x,boxbian1y,boxbian1z));//这是一个x方向的胶囊
		Boxbian[1]=new BoxShape(new Vector3f(boxbian2x,boxbian2y,boxbian2z));//这是一个y方向的胶囊
		Box=addChild(Boxbian);
		
		Boxrg=RigidBodyHelper.addRigidBody(0,Box,position.x,position.y,position.z,dynamicsWorld,false);
	}
	//胶囊的组装
  	public CompoundShape addChild(CollisionShape[] shape)//组装出所需的胶囊
  	{
  		CompoundShape comShape=new CompoundShape(); //创建组合形状
  		Transform localTransform = new Transform();//创建变换对象
  		
  		//最前面的边  
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(0,boxbian1y,boxbian1x));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[0]);//添加子形状----胶囊
  		
  		//最后面的边  
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(0,boxbian1y,-boxbian1x));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[0]);//添加子形状----胶囊
  		
  		
  		//最左面的边  
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(-boxbian1x,boxbian2y,0));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[0]);//添加子形状----胶囊
  		
  		//最右面的边  
  		localTransform.setIdentity();//初始化变换
  		localTransform.origin.set(new Vector3f(boxbian1x,boxbian2y,0));//设置变换的起点
  		comShape.addChildShape(localTransform, shape[0]);//添加子形状----胶囊
  		
  		return comShape;
  	}
  	public void drawSelf()
  	{
  		MatrixState3D.pushMatrix();
		Transform trans=Boxrg.getMotionState().getWorldTransform(new Transform());//获取这个物体的变换信息对象
		MatrixState3D.translate(trans.origin.x,trans.origin.y, trans.origin.z);//进行移位变换
		trans.getOpenGLMatrix(MatrixState3D.getMMatrix());
		
		MatrixState3D.pushMatrix();
		lovo.drawSelf(texId);
		MatrixState3D.popMatrix();
		MatrixState3D.popMatrix();
  	}
}

package com.bn.object;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.dynamics.RigidBody;
public abstract class BNAbstractDoll 
{
	public  RigidBody RigidBodydoll;
	public boolean isInBox;
	public int bianhao;//编号
	public LoadedObjectVertexNormalTexture lovo;
	public abstract void initRigidBodys();
	public abstract CompoundShape addChild(CollisionShape[] shape);
	public abstract void drawSelf();
}
package com.bn.object;

import android.opengl.GLSurfaceView;

import com.bn.MatrixState.MatrixState3D;
import com.bn.util.manager.ShaderManager;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

public class Cube {
	
	TexRect rect;//正方形
	float halfSize;
	
	RigidBody body;
	GLSurfaceView mv;
	
	public Cube
		(
		   GLSurfaceView mv,
			float halfSize,
			RigidBody body
		){
		this.mv=mv;
		this.halfSize=halfSize;
		rect = new TexRect(mv,ShaderManager.getShader(0),1,halfSize,halfSize);
		this.body=body;
	}
  
	public void drawSelf(int texId){
		
		 
		MatrixState3D.pushMatrix();
			Transform trans = body.getMotionState().getWorldTransform(new Transform());
			MatrixState3D.translate(trans.origin.x,trans.origin.y, trans.origin.z);
			trans.getOpenGLMatrix(MatrixState3D.getMMatrix());
		
		//绘制上面
		MatrixState3D.pushMatrix();
	    MatrixState3D.translate(0, halfSize, 0);
	    MatrixState3D.rotate(-90, 1, 0, 0);
	    rect.drawSelf(texId);
		MatrixState3D.popMatrix();
		
		//绘制下面
		MatrixState3D.pushMatrix();
	    MatrixState3D.translate(0, -halfSize, 0);
	    MatrixState3D.rotate(90, 1, 0, 0);
	    rect.drawSelf(texId);
		MatrixState3D.popMatrix();
		
		//绘制左面
		MatrixState3D.pushMatrix();
	    MatrixState3D.translate(-halfSize, 0, 0);
	    MatrixState3D.rotate(-90, 0, 1, 0);
	    rect.drawSelf(texId);
		MatrixState3D.popMatrix();
		
		//绘制右面
		MatrixState3D.pushMatrix();
	    MatrixState3D.translate(halfSize, 0, 0);
	    MatrixState3D.rotate(90, 0, 1, 0);
	    rect.drawSelf(texId);
		MatrixState3D.popMatrix();
		 
		//绘制前面
		MatrixState3D.pushMatrix();
		MatrixState3D.translate(0, 0, halfSize);
		rect.drawSelf(texId);
		MatrixState3D.popMatrix();
		
		//绘制后面
		MatrixState3D.pushMatrix();
		MatrixState3D.translate(0, 0, -halfSize);
		MatrixState3D.rotate(180, 0, 1, 0);
		rect.drawSelf(texId);
		MatrixState3D.popMatrix();
		
		MatrixState3D.popMatrix();
	}
}

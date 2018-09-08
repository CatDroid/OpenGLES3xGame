package com.bn.util;

import android.opengl.GLES30;
import com.bn.MatrixState.MatrixState2D;
import com.bn.object.BN2DObject;


public class CLpng {

	private BN2DObject Clpng;

	public CLpng(float x,float y,float width,float height,int texId,int programId)
	{
    	Clpng=new BN2DObject(x,y,width,height,texId,programId,1);
    	
	}
    public void drawSelf()
    {
    	GLES30.glDisable(GLES30.GL_DEPTH_TEST); 

		MatrixState2D.pushMatrix();
		Clpng.drawSelf();
		MatrixState2D.popMatrix();

    	GLES30.glEnable(GLES30.GL_DEPTH_TEST); 
    }
}

package com.bn.Sample8_7;

import static com.bn.Sample8_7.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.opengl.GLES30;

/*
 * 泰姬陵顶部组建3
 */
public class TopPart3 {	
	int mProgram;
    int muMVPMatrixHandle;
    int maPositionHandle;
    int maTexCoorHandle;

    String mVertexShader;
    String mFragmentShader;

	FloatBuffer   mVertexBuffer;
	FloatBuffer   mTexCoorBuffer;

    int vCount=0;
    float xAngle=0;
    float yAngle=0;
    float zAngle=0;

    float scale;

    public TopPart3(MySurfaceView mv,float scale, int nCol ,int nRow)
    {
    	this.scale=scale;
    	initVertexData(scale,nCol,nRow);
    	initShader(mv);
    }

    //自定义的初始化顶点数据的方法
    public void initVertexData(float scale, int nCol ,int nRow){

		float angdegSpan=360.0f/nCol;
		vCount=3*nCol*nRow*2;
		ArrayList<Float> alVertix=new ArrayList<Float>();
		ArrayList<Integer> alFaceIndex=new ArrayList<Integer>();


		BezierUtil.al.clear();
		BezierUtil.al.add(new BNPosition(97, 35));
		BezierUtil.al.add(new BNPosition(165, 175));
		BezierUtil.al.add(new BNPosition(81, 143));
		BezierUtil.al.add(new BNPosition(58, 162));
		BezierUtil.al.add(new BNPosition(42, 172));
		BezierUtil.al.add(new BNPosition(26, 186));
		BezierUtil.al.add(new BNPosition(72, 231));
		BezierUtil.al.add(new BNPosition(38, 187));
		BezierUtil.al.add(new BNPosition(18, 159));
		BezierUtil.al.add(new BNPosition(77, 214));
		BezierUtil.al.add(new BNPosition(83, 186));
		BezierUtil.al.add(new BNPosition(69, 169));
		BezierUtil.al.add(new BNPosition(55, 206));
		BezierUtil.al.add(new BNPosition(0, 246));
		ArrayList<BNPosition> alCurve=BezierUtil.getBezierData(1.0f/nRow);


		for(int i=0;i<nRow+1;i++)
		{
			double r=alCurve.get(i).x*Constant.DATA_RATIO*scale;
			float y=alCurve.get(i).y*Constant.DATA_RATIO*scale;
			for(float angdeg=0;Math.ceil(angdeg)<360+angdegSpan;angdeg+=angdegSpan)
			{
				double angrad=Math.toRadians(angdeg);
				float x=(float) (-r*Math.sin(angrad));
				float z=(float) (-r*Math.cos(angrad));
				alVertix.add(x); alVertix.add(y); alVertix.add(z);
			}
		}
		//索引
		for(int i=0;i<nRow;i++){
			for(int j=0;j<nCol;j++){
				int index=i*(nCol+1)+j;
				alFaceIndex.add(index+1);
				alFaceIndex.add(index+nCol+2);
				alFaceIndex.add(index+nCol+1);

				alFaceIndex.add(index+1);
				alFaceIndex.add(index+nCol+1);
				alFaceIndex.add(index);
			}
		}
		//计算卷绕顶点
		float[] vertices=new float[vCount*3];
		vertices=VectorUtil.calVertices(alVertix, alFaceIndex);

		//顶点坐标数据初始化
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);

		//纹理
		ArrayList<Float> alST=new ArrayList<Float>();//原顶点列表（未卷绕）
		float yMin=999999999;
		float yMax=0;
		for(BNPosition pos:alCurve){
			yMin=Math.min(yMin, pos.y);
			yMax=Math.max(yMax, pos.y);
		}
		for(int i=0;i<nRow+1;i++)
		{
			float y=alCurve.get(i).y;
			float t=1-(y-yMin)/(yMax-yMin);
			for(float angdeg=0;Math.ceil(angdeg)<360+angdegSpan;angdeg+=angdegSpan)
			{
				float s=angdeg/360;
				alST.add(s); alST.add(t);
			}
		}
		//计算卷绕后纹理坐标
		float[] textures=VectorUtil.calTextures(alST, alFaceIndex);
        ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length*4);
        tbb.order(ByteOrder.nativeOrder());
        mTexCoorBuffer = tbb.asFloatBuffer();
        mTexCoorBuffer.put(textures);
        mTexCoorBuffer.position(0);
	}

    //自定义初始化着色器initShader方法
    public void initShader(MySurfaceView mv)
    {
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex_tex.sh", mv.getResources());
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_tex.sh", mv.getResources());
        mProgram = createProgram(mVertexShader, mFragmentShader);
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        maTexCoorHandle= GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void drawSelf(int texId)
    {
		MatrixState.rotate(xAngle, 1, 0, 0);
		MatrixState.rotate(yAngle, 0, 1, 0);
		MatrixState.rotate(zAngle, 0, 0, 1);

		GLES30.glUseProgram(mProgram);

		GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);


		GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3*4, mVertexBuffer);
		GLES30.glVertexAttribPointer(maTexCoorHandle, 2, GLES30.GL_FLOAT, false, 2*4, mTexCoorBuffer);


		GLES30.glEnableVertexAttribArray(maPositionHandle);
		GLES30.glEnableVertexAttribArray(maTexCoorHandle);


		GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);


		GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);
    }
}

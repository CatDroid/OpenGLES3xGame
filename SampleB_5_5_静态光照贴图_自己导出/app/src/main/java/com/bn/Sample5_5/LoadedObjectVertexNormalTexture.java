package com.bn.Sample5_5;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES30;

public class LoadedObjectVertexNormalTexture
{	
	private int mProgram;					// 自定义渲染管线着色器程序id
	private int muMVPMatrixHandle;			// 总变换矩阵引用
	private int muMMatrixHandle;			// 位置、旋转变换矩阵
	private int maPositionHandle; 			// 顶点位置属性引用
	private int maNormalHandle; 			// 顶点法向量属性引用
	private int maLightLocationHandle;		// 光源位置属性引用
	private int maCameraHandle; 			// 摄像机位置属性引用
	private int maTexCoorHandle; 			// 顶点纹理坐标属性引用
	private String mVertexShader;			// 顶点着色器代码脚本
	private String mFragmentShader;			// 片元着色器代码脚本
	private int muIsShadow;					// 是否绘制阴影属性引用
	private int muProjCameraMatrixHandle;	// 投影矩阵引用
	
    private int vCount = 0;

	private FloatBuffer   mVertexBuffer;			// 顶点坐标数据缓冲
	private FloatBuffer   mNormalBuffer;			// 顶点法向量数据缓冲
	private FloatBuffer   mTexCoorBuffer;			// 顶点纹理坐标数据缓冲
    
	public LoadedObjectVertexNormalTexture(MySurfaceView mv,
										   float[] vertices,
										   float[] normals,
										   float texCoors[],
										   boolean useLightingMap)
	{	

		initVertexData(vertices,normals,texCoors);
		if(useLightingMap)
		{
			initShader1(mv);
		}
		else
		{
			initShader(mv);
		}
	}
	
	//初始化顶点数据的方法
    public void initVertexData(float[] vertices,float[] normals,float texCoors[])
    {
    	vCount=vertices.length/3;
		
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        ByteBuffer cbb = ByteBuffer.allocateDirect(normals.length*4);
        cbb.order(ByteOrder.nativeOrder());
        mNormalBuffer = cbb.asFloatBuffer();
        mNormalBuffer.put(normals);
        mNormalBuffer.position(0);


        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoors.length*4);
        tbb.order(ByteOrder.nativeOrder());
        mTexCoorBuffer = tbb.asFloatBuffer();
        mTexCoorBuffer.put(texCoors);
        mTexCoorBuffer.position(0);
    }


    private void initShader(MySurfaceView mv)
    {
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex_shadow.glsl", mv.getResources());
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_shadow.glsl", mv.getResources());
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);


		maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        maNormalHandle= GLES30.glGetAttribLocation(mProgram, "aNormal");
		maLightLocationHandle=GLES30.glGetUniformLocation(mProgram, "uLightLocation");
		maTexCoorHandle= GLES30.glGetAttribLocation(mProgram, "aTexCoor");
		maCameraHandle=GLES30.glGetUniformLocation(mProgram, "uCamera");

        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
        muIsShadow=GLES30.glGetUniformLocation(mProgram, "isShadow");
        muProjCameraMatrixHandle=GLES30.glGetUniformLocation(mProgram, "uMProjCameraMatrix");
    }


	private void initShader1(MySurfaceView mv)
    {
        mVertexShader=ShaderUtil.loadFromAssetsFile("jtvertex.glsl", mv.getResources());
        mFragmentShader=ShaderUtil.loadFromAssetsFile("jtfrag.glsl", mv.getResources());
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);

        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        maTexCoorHandle= GLES30.glGetAttribLocation(mProgram, "aTexCoor");
    }


    public void drawSelf1(int texId)
    {
    	GLES30.glUseProgram(mProgram);
    	GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
    	GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3*4, mVertexBuffer);
    	GLES30.glVertexAttribPointer(maTexCoorHandle, 2, GLES30.GL_FLOAT, false, 2*4, mTexCoorBuffer);

    	GLES30.glEnableVertexAttribArray(maPositionHandle);  
    	GLES30.glEnableVertexAttribArray(maTexCoorHandle);
    	GLES30.glActiveTexture(GLES30.GL_TEXTURE0);//启用0号纹理
    	GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);//绑定纹理--普通
    	GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
    }


    public void drawSelf(int texId,int isShadow)
    {

    	GLES30.glUseProgram(mProgram);

    	GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 

    	GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);   

    	GLES30.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);

    	GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);

    	GLES30.glUniform1i(muIsShadow, isShadow);  

    	GLES30.glUniformMatrix4fv(muProjCameraMatrixHandle, 1, false, MatrixState.getViewProjMatrix(), 0);

    	GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3*4, mVertexBuffer);
    	GLES30.glVertexAttribPointer(maNormalHandle, 3, GLES30.GL_FLOAT, false, 3*4, mNormalBuffer);
    	GLES30.glVertexAttribPointer(maTexCoorHandle, 2, GLES30.GL_FLOAT, false, 2*4, mTexCoorBuffer);

    	GLES30.glEnableVertexAttribArray(maPositionHandle);  
    	GLES30.glEnableVertexAttribArray(maNormalHandle);  
    	GLES30.glEnableVertexAttribArray(maTexCoorHandle);
    	GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
    	GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
    	GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
    }
}

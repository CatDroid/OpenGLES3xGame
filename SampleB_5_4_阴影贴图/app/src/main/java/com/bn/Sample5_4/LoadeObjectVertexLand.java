package com.bn.Sample5_4;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.annotation.SuppressLint;
import android.opengl.GLES30;


@SuppressLint("NewApi")
public class LoadeObjectVertexLand
{	
	private int mProgram;               // 自定义渲染管线着色器程序id
    private int muMVPMatrixHandle;      // 总变换矩阵引用
    private int muMMatrixHandle;        // 位置、旋转变换矩阵
    private int maPositionHandle;       // 顶点位置属性引用
    private int maNormalHandle;         // 顶点法向量属性引用
    private int maLightLocationHandle;  // 光源位置属性引用
    private int muMVPMatrixGYHandle;    // 光源总变换矩阵引用
    private int maCameraHandle;         // 摄像机位置属性引用
    private int muProjCameraMatrixHandle;


    private FloatBuffer   mVertexBuffer;// 顶点坐标数据缓冲
    private FloatBuffer   mNormalBuffer;// 顶点法向量数据缓冲
    private int vCount=0;
    
    LoadeObjectVertexLand(MySurfaceView mv,float[] vertices,float[] normals)
    {
    	initVertexData(vertices,normals);
    	intShader(mv);
    }

    private void initVertexData(float[] vertices,float[] normals)
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

    }


    private void intShader(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容
        String mVertexShader=ShaderUtil.loadFromAssetsFile("vertex_land.glsl", mv.getResources());
        //加载片元着色器的脚本内容
        String mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_land.glsl", mv.getResources());
        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用 
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点颜色属性引用 
        maNormalHandle= GLES30.glGetAttribLocation(mProgram, "aNormal");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");  
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix"); 
        //获取程序中光源位置引用
        maLightLocationHandle=GLES30.glGetUniformLocation(mProgram, "uLightLocation");
        //获取程序中摄像机位置引用
        maCameraHandle=GLES30.glGetUniformLocation(mProgram, "uCamera"); 
        //获取光源总变换矩阵引用
        muMVPMatrixGYHandle=GLES30.glGetUniformLocation(mProgram, "uMVPMatrixGY");
        //获取程序中投影、摄像机组合矩阵引用
        muProjCameraMatrixHandle=GLES30.glGetUniformLocation(mProgram, "uMProjCameraMatrix"); 
    } 
    
    public void drawSelf(int texId,float[] mMVPMatrixGY,int isShadow)
    {        
    	 //制定使用某套着色器程序
    	 GLES30.glUseProgram(mProgram);
         //将最终变换矩阵传入着色器程序
         GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
         //将光源最终变换矩阵传入着色器程序
         GLES30.glUniformMatrix4fv(muMVPMatrixGYHandle, 1, false, mMVPMatrixGY, 0);          
         //将位置、旋转变换矩阵传入着色器程序
         GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);   
         //将光源位置传入着色器程序   
         GLES30.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
         //将摄像机位置传入着色器程序   
         GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
         //将投影、摄像机组合矩阵传入着色器程序
         GLES30.glUniformMatrix4fv(muProjCameraMatrixHandle, 1, false, MatrixState.getViewProjMatrix(), 0);
         //将顶点位置数据传入渲染管线
         GLES30.glVertexAttribPointer  
         (
         		maPositionHandle,   
         		3, 
         		GLES30.GL_FLOAT, 
         		false,
                3*4,   
                mVertexBuffer
         );       
         //将顶点法向量数据传入渲染管线
         GLES30.glVertexAttribPointer  
         (
        		maNormalHandle, 
         		3,   
         		GLES30.GL_FLOAT, 
         		false,
                3*4,   
                mNormalBuffer
         );   
         //启用顶点位置、法向量数据数组
         GLES30.glEnableVertexAttribArray(maPositionHandle);  
         GLES30.glEnableVertexAttribArray(maNormalHandle);  
         //绑定纹理
         GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
         GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
         //绘制加载的物体
         GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
    }
}

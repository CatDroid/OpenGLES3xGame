package com.bn.Sample5_6;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.annotation.SuppressLint;
import android.opengl.GLES30;

//加载后的物体——携带顶点信息，自动计算面平均法向量
@SuppressLint("NewApi")
public class LoadeObjectVertexLand
{	
	int mProgram;//自定义渲染管线着色器程序id  
    int muMVPMatrixHandle;//总变换矩阵引用
    int muMMatrixHandle;//位置、旋转变换矩阵
    int maPositionHandle; //顶点位置属性引用 
    int maNormalHandle; //顶点法向量属性引用 
    int maLightLocationHandle;//光源位置属性引用 
    int muMVPMatrixGYHandle;//光源总变换矩阵引用
    int maCameraHandle; //摄像机位置属性引用
    int muProjCameraMatrixHandle;
    int mLight;//聚光灯的方向向量的引用
    String mVertexShader;//顶点着色器    	 
    String mFragmentShader;//片元着色器    
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲  
	FloatBuffer   mNormalBuffer;//顶点法向量数据缓冲
    int vCount=0;     
    
    public LoadeObjectVertexLand(MySurfaceView mv,float[] vertices,float[] normals)
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

    //初始化shader
    private void intShader(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex_land.sh", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_land.sh", mv.getResources());  
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
        //获取聚光灯方向向量的引用
        mLight=GLES30.glGetUniformLocation(mProgram, "light"); 
        //获取光源总变换矩阵引用
       // muMVPMatrixGYHandle=GLES30.glGetUniformLocation(mProgram, "uMVPMatrixGY");
        //获取程序中投影、摄像机组合矩阵引用
        muProjCameraMatrixHandle=GLES30.glGetUniformLocation(mProgram, "uMProjCameraMatrix"); 
    } 
    
    public void drawSelf()
    {        

    	 GLES30.glUseProgram(mProgram);
         GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 


         //GLES30.glUniformMatrix4fv(muMVPMatrixGYHandle, 1, false, mMVPMatrixGY, 0);
         GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
         GLES30.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
         GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);

         //将聚光灯方向向量传入着色器程序   
         GLES30.glUniform3fv(mLight, 1,MySurfaceView.dis);


         GLES30.glUniformMatrix4fv(muProjCameraMatrixHandle, 1, false, MatrixState.getViewProjMatrix(), 0);

         GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3*4, mVertexBuffer);
         GLES30.glVertexAttribPointer(maNormalHandle, 3, GLES30.GL_FLOAT, false, 3*4, mNormalBuffer);

         GLES30.glEnableVertexAttribArray(maPositionHandle);  
         GLES30.glEnableVertexAttribArray(maNormalHandle);  

         GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
    }
}

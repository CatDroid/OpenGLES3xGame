package com.bn.Sample3_15;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES30;

//加载后的物体——仅携带顶点信息，颜色随机
public class LoadedObjectVertexNormalTexture
{
    private int mProgram;               // 自定义渲染管线着色器程序id

    private int muMVPMatrixHandle;      // 总变换矩阵引用
    private int muMMatrixHandle;        // 位置、旋转变换矩阵
    private int muLightLocationHandle;  // 光源位置属性引用
    private int muCameraHandle;         // 摄像机位置属性引用

    private int maPositionHandle;       // 顶点位置属性引用
    private int maNormalHandle;         // 顶点法向量属性引用

    private int maTexCoorHandle;        // 顶点纹理坐标属性引用


    private FloatBuffer mVertexBuffer;  // 顶点坐标数据缓冲
    private FloatBuffer mNormalBuffer;  // 顶点法向量数据缓冲
    private FloatBuffer mTexCoorBuffer; // 顶点纹理坐标数据缓冲
    private int vCount=0;


    public LoadedObjectVertexNormalTexture(MySurfaceView mv,float[] vertices,float[] normals,float texCoors[]) {

    	initVertexData(vertices,normals,texCoors);
    	initShader(mv);
    }


    private void initVertexData(float[] vertices,float[] normals,float texCoors[])
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

        String mVertexShader=ShaderUtil.loadFromAssetsFile("vertex_brazier.sh", mv.getResources());
        String mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_brazier.sh", mv.getResources());
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);


        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
        muLightLocationHandle =GLES30.glGetUniformLocation(mProgram, "uLightLocation");
        muCameraHandle =GLES30.glGetUniformLocation(mProgram, "uCamera");


        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点颜色属性引用  
        maNormalHandle= GLES30.glGetAttribLocation(mProgram, "aNormal");
        //获取程序中顶点纹理坐标属性引用  
        maTexCoorHandle= GLES30.glGetAttribLocation(mProgram, "aTexCoor"); 
 
    }
    
    public void drawSelf(int texId)
    {          

    	 GLES30.glUseProgram(mProgram);

         GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
         GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
         GLES30.glUniform3fv(muLightLocationHandle, 1, MatrixState.lightPositionFB);
         GLES30.glUniform3f(muCameraHandle, MatrixState.cx,MatrixState.cy,MatrixState.cz);


         GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3*4, mVertexBuffer);
         GLES30.glVertexAttribPointer(maNormalHandle, 3, GLES30.GL_FLOAT, false, 3*4, mNormalBuffer);
         GLES30.glVertexAttribPointer(maTexCoorHandle, 2, GLES30.GL_FLOAT, false, 2*4, mTexCoorBuffer);

         GLES30.glEnableVertexAttribArray(maPositionHandle);  
         GLES30.glEnableVertexAttribArray(maNormalHandle);  
         GLES30.glEnableVertexAttribArray(maTexCoorHandle); 
         //绑定纹理
         GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
         GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
         //绘制加载的物体
         GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
    }
}

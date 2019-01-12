package com.bn.Sample4_1;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.annotation.SuppressLint;
import android.opengl.GLES30;

//加载后的物体——携带顶点、法向量信息
public class LoadedObjectVertexNormalTexture
{	
	private int mProgram;           //自定义渲染管线着色器程序id
    private int muMVPMatrixHandle;  //总变换矩阵引用
    private int muMMatrixHandle;    //位置、旋转变换矩阵
    private int maPositionHandle;   //顶点位置属性引用
    private int maNormalHandle;     //顶点法向量属性引用
    private int muCameraHandle;     //摄像机位置属性引用

	
	private FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	private FloatBuffer   mNormalBuffer;//顶点法向量数据缓冲
    private int vCount=0;
    
    public LoadedObjectVertexNormalTexture(MySurfaceView mv,float[] vertices,float[] normals)
    {
    	initVertexData(vertices,normals);
    	intShader(mv);
    }
    
    // 初始化顶点坐标与着色数据的方法
    public void initVertexData(float[] vertices,float[] normals)
    {

    	vCount = vertices.length / 3;

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder()); // 设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();// 转换为Float型缓冲
        mVertexBuffer.put(vertices);        // 向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);          // 设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题

        ByteBuffer cbb = ByteBuffer.allocateDirect(normals.length*4);
        cbb.order(ByteOrder.nativeOrder());
        mNormalBuffer = cbb.asFloatBuffer();
        mNormalBuffer.put(normals);
        mNormalBuffer.position(0);

    }

    @SuppressLint("NewApi")
	private void intShader(MySurfaceView mv) {


        String mVertexShader=ShaderUtil.loadFromAssetsFile("vertex_tex_cube.sh", mv.getResources());
        String mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_tex_cube.sh", mv.getResources());
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);


        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        maNormalHandle= GLES30.glGetAttribLocation(mProgram, "aNormal");


        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
        muCameraHandle =GLES30.glGetUniformLocation(mProgram, "uCamera");
    }
    
    @SuppressLint("NewApi")
	public void drawSelf(int texId)
    { 

    	 GLES30.glUseProgram(mProgram);

         GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
         GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
         GLES30.glUniform3fv(muCameraHandle, 1, MatrixState.cameraFB);


         GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3*4, mVertexBuffer);
         GLES30.glVertexAttribPointer(maNormalHandle, 3, GLES30.GL_FLOAT, false, 3*4, mNormalBuffer);
         GLES30.glEnableVertexAttribArray(maPositionHandle);  
         GLES30.glEnableVertexAttribArray(maNormalHandle);  


         // HHL 注意!!  使用立方体贴图要先使能GL_TEXTURE_CUBE_MAP
    	 //GLES30.glEnable(GLES30.GL_TEXTURE_CUBE_MAP);         // 启用立方图纹理
    	 GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP,texId);// 绑定立方图纹理
         
         //绘制加载的物体
         GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
    }
}

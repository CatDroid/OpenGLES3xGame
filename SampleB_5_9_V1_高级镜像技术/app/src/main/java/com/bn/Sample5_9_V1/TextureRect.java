package com.bn.Sample5_9_V1;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES30;

//纹理矩形
public class TextureRect 
{	
	int mProgram;//自定义渲染管线着色器程序id  
    int muMVPMatrixHandle;//总变换矩阵引用
    
    int muMMatrixHandle;//位置、旋转变换矩阵
    int maPositionHandle; //顶点位置属性引用 
    int muMVPMatrixMirrorHandle;//镜像摄像机的观察与投影组合矩阵引用
    
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
    int vCount=0;   
    
    
    public TextureRect(MySurfaceView mv)
    {    	
    	//初始化顶点数据的方法
    	initVertexData();
    	//初始化着色器的方法        
    	initShader(mv);
    }
    //初始化顶点数据的方法
    public void initVertexData()
    {	
    	//顶点坐标数据的初始化================begin============================
    	vCount=6;//每个格子两个三角形，每个三角形3个顶点    
    	
        float vertices[] = new float[] {
        		-Constant.UNIT_SIZE, -Constant.UNIT_SIZE, 0,
				Constant.UNIT_SIZE, Constant.UNIT_SIZE, 0,
				-Constant.UNIT_SIZE, Constant.UNIT_SIZE, 0,
				
				-Constant.UNIT_SIZE, -Constant.UNIT_SIZE, 0,
				Constant.UNIT_SIZE, -Constant.UNIT_SIZE, 0,
				Constant.UNIT_SIZE, Constant.UNIT_SIZE, 0
		};
       
        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
    }
    //初始化着色器的方法
    public void initShader(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容
        String mVertexShader=ShaderUtil.loadFromAssetsFile("mirror_vertex.glsl", mv.getResources());
        //加载片元着色器的脚本内容
        String mFragmentShader=ShaderUtil.loadFromAssetsFile("mirror_frag.glsl", mv.getResources());
        //基于顶点着色器与片元着色器创建程序
        mProgram= ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用  
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");  
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix"); 
        //获取镜像摄像机的观察与投影组合矩阵引用
        muMVPMatrixMirrorHandle=GLES30.glGetUniformLocation(mProgram, "uMVPMatrixMirror");
    }
    
  
    public void drawSelf(int texId,float[] mMVPMatrixMirror)
    {
    	//指定使用某套shader程序
    	GLES30.glUseProgram(mProgram); 
    	//将最终变换矩阵传入渲染管线
    	GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
    	//将镜像摄像机的观察与投影组合矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixMirrorHandle, 1, false, mMVPMatrixMirror, 0);        
    	//将位置、旋转变换矩阵传入渲染管线
    	GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);   
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

    	GLES30.glEnableVertexAttribArray(maPositionHandle);      	//启用顶点位置数据数组
    	GLES30.glActiveTexture(GLES30.GL_TEXTURE0);//激活纹理
    	GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);        	//绑定纹理
    	GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);     	//以三角形方式执行绘制
    }
}

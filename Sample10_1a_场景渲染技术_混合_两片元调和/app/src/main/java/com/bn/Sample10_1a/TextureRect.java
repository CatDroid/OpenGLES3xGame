package com.bn.Sample10_1a;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES30;
public class TextureRect 
{
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用  
    int muMMatrixHandle;//位置、旋转变换矩阵
    int maCameraHandle; //摄像机位置属性引用 
    int maPositionHandle; //顶点位置属性引用 
    int maNormalHandle; //顶点法向量属性引用 
    int maTexCoorHandle; //顶点纹理坐标属性引用 
    int maSunLightLocationHandle;//光源位置属性引用 
    
    String mVertexShader;//顶点着色器    	 
    String mFragmentShader;//片元着色器
	
    private FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
    private FloatBuffer   mTextureBuffer;//顶点着色数据缓冲
    int vCount;//顶点数量
    int texId;//纹理Id
    
    float width;
    float height;
    
	public TextureRect(MySurfaceView mv, 
			float width,float height	//纹理矩形的宽高
			)
	{

		this.width=width;
    	this.height=height;
    	
		initVertexData();
        initShader(mv);
        
	}
    //初始化顶点数据的方法
    public void initVertexData()
    {
    	//顶点坐标数据的初始化================begin============================
        vCount=6;//每个格子两个三角形，每个三角形3个顶点        
        float vertices[]=
        {
        		-width/2, height/2,0,
        		-width/2, -height/2,0,
        		width/2, height/2,0,
        		
        		-width/2, -height/2,0,
        		width/2, -height/2,0,
        		width/2, height/2,0
        };
        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为int型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        float textures[]=
        {
        		0f,0f, 0f,1, 1,0f,
        		0f,1,  1,1,  1,0f
        };
        //创建顶点纹理数据缓冲
        ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length*4);
        tbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTextureBuffer= tbb.asFloatBuffer();//转换为Float型缓冲
        mTextureBuffer.put(textures);//向缓冲区中放入顶点着色数据
        mTextureBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点纹理数据的初始化================end============================  
    }
    public void initShader(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex_tex.sh", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_tex.sh", mv.getResources());  
        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用 
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用 
        maTexCoorHandle= GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵id
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");  
    }
	public void drawSelf(int texId)
	{
		 //指定使用某套着色器程序
   	 	GLES30.glUseProgram(mProgram);
        //将最终变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
        //将顶点法向量数据传入渲染管线
        GLES30.glVertexAttribPointer  
        (
        		maPositionHandle,   
        		3, 
        		GLES30.GL_FLOAT, 
        		false,
               3*4,   
               mVertexBuffer
        );       
        //将纹理数据传入渲染管线
        GLES30.glVertexAttribPointer  
        (
       		maTexCoorHandle, 
        		2, 
        		GLES30.GL_FLOAT, 
        		false,
               2*4,   
               mTextureBuffer
        );   
        //启用顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maPositionHandle);  
        //启用顶点纹理坐标数据数组
        GLES30.glEnableVertexAttribArray(maTexCoorHandle);  
        
        //绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
        
        //绘制纹理矩形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
	}
	
}

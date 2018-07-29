package com.bn.Sample13_8;

import static com.bn.Sample13_8.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.annotation.SuppressLint;
import android.opengl.GLES30;

//矩形平面
public class TextureRect 
{	
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用 
    int maTexCoorHandle; //顶点纹理坐标属性引用
    
    int muMMatrixHandle;//位置、旋转、缩放变换矩阵
    int maCameraHandle; //摄像机位置属性引用
    int maNormalHandle; //顶点法向量属性引用
    int maLightLocationHandle;//光源位置属性引用 
    
    
    String mVertexShader;//顶点着色器代码脚本	 
    String mFragmentShader;//片元着色器代码脚本
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲
	FloatBuffer   mNormalBuffer;//顶点法向量数据缓冲
    int vCount=0;   
    float xAngle=0;//绕x轴旋转的角度
    float yAngle=0;//绕y轴旋转的角度
    float zAngle=0;//绕z轴旋转的角度
    
    public TextureRect(MySurfaceView mv,float scale,float a,float b)
    {    	
    	//调用初始化顶点数据的initVertexData方法
    	initVertexData(scale,a,b);
    	//调用初始化着色器的intShader方法   
    	initShader(mv);
    }
    
    //自定义初始化顶点坐标数据的方法
    public void initVertexData(float scale,float a, float b) {

		a*=scale;
		b*=scale;
		float xOffset=a/2;
		float yOffset=b/2;
		vCount=6;
		//坐标数据初始化
		float[] vertices=new float[]{
			-xOffset,-yOffset,0,
			xOffset,yOffset,0,
			-xOffset,yOffset,0,
        	
        	-xOffset,-yOffset,0,
        	xOffset,-yOffset,0,
        	xOffset,yOffset,0
		};
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);//创建顶点坐标数据缓冲
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //法向量数据初始化 
		float[] normals=new float[]{
				0,0,1,
				0,0,1,
				0,0,1,
				
				0,0,1,
				0,0,1,
				0,0,1,
		};//法向量数组
        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length*4);//创建顶点法向量数据缓冲
        nbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = nbb.asFloatBuffer();//转换为float型缓冲
        mNormalBuffer.put(normals);//向缓冲区中放入顶点法向量数据
        mNormalBuffer.position(0);//设置缓冲区起始位置
        //纹理数据的初始化
        float[] textures=new float[]{//顶点纹理S、T坐标值数组
        		0,1,
        		1,0,
        		0,0,
        		
        		0,1,
        		1,1,
        		1,0,
        };
        ByteBuffer cbb = ByteBuffer.allocateDirect(textures.length*4);//创建顶点纹理数据缓冲
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTexCoorBuffer = cbb.asFloatBuffer();//转换为float型缓冲
        mTexCoorBuffer.put(textures);//向缓冲区中放入顶点纹理数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
	}

    //自定义初始化着色器的initShader方法
    @SuppressLint("NewApi")
	public void initShader(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex_tex_light.sh", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_tex_light.sh", mv.getResources());  
        //基于顶点着色器与片元着色器创建程序
        mProgram = createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用
        maTexCoorHandle= GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        
        //获取程序中顶点法向量属性引用
        maNormalHandle= GLES30.glGetAttribLocation(mProgram, "aNormal"); 
        //获取程序中摄像机位置引用
        maCameraHandle=GLES30.glGetUniformLocation(mProgram, "uCamera"); 
        //获取程序中光源位置引用
        maLightLocationHandle=GLES30.glGetUniformLocation(mProgram, "uLightLocation"); 
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");         
        
    }
    
    @SuppressLint("NewApi")
	public void drawSelf(int texId)
    {        
    	 //指定使用某套shader程序
    	 GLES30.glUseProgram(mProgram);        
         //将最终变换矩阵传入渲染管线
         GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
         //将位置、旋转变换矩阵传入渲染管线
         GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0); 
         //将摄像机位置传入渲染管线
         GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
         //将光源位置传入渲染管线
         GLES30.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
         
         //传送顶点位置数据进渲染管线
         GLES30.glVertexAttribPointer  
         (
         		maPositionHandle,   
         		3, 
         		GLES30.GL_FLOAT, 
         		false,
                3*4,   
                mVertexBuffer
         );       
         //传送顶点纹理坐标数据进渲染管线
         GLES30.glVertexAttribPointer  
         (
        		maTexCoorHandle, 
         		2, 
         		GLES30.GL_FLOAT, 
         		false,
                2*4,   
                mTexCoorBuffer
         ); 
         //传送顶点法向量数据进渲染管线
         GLES30.glVertexAttribPointer  
         (
        		maNormalHandle, 
         		4, 
         		GLES30.GL_FLOAT, 
         		false,
                3*4,   
                mNormalBuffer
         ); 
         
         //启用顶点位置数据
         GLES30.glEnableVertexAttribArray(maPositionHandle);
         //启用顶点纹理数据
         GLES30.glEnableVertexAttribArray(maTexCoorHandle);  
         //启用顶点法向量数据
         GLES30.glEnableVertexAttribArray(maNormalHandle);
         //绑定纹理
         GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
         GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
         
         //绘制纹理矩形
         GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
    }
}

package com.bn.Sample7_3;
import static com.bn.Sample7_3.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES30;
import android.opengl.Matrix;

//纹理矩形
public class TextureRect 
{	
	int mProgram;//自定义渲染管线程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用
    int maTexCoorHandle; //顶点纹理坐标属性引用
    String mVertexShader;//顶点着色器代码脚本
    String mFragmentShader;//片元着色器代码脚本
    static float[] mMMatrix = new float[16];//具体物体的3D变换矩阵，包括旋转、平移、缩放
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲
    int vCount=0;   
    float xAngle=0;//绕x轴旋转的角度
    float yAngle=0;//绕y轴旋转的角度
    float zAngle=0;//绕z轴旋转的角度
    
    float sRange;//s纹理坐标范围
    float tRange;//t纹理坐标范围
    
    public TextureRect(MySurfaceView mv,float sRange,float tRange)
    {    	
    	//接收s纹理坐标范围
    	this.sRange=sRange;
    	//接收t纹理坐标范围
    	this.tRange=tRange;
    	
    	//调用初始化顶点数据的initVertexData方法
    	initVertexData();
    	//调用初始化着色器的initShader方法     
    	initShader(mv);
    }
    
    //初始化顶点数据的initVertexData方法
    public void initVertexData()
    {
    	//顶点坐标数据的初始化================begin============================
        vCount=6;
        final float UNIT_SIZE=0.3f;
        float vertices[]=new float[]
        {
        	-4*UNIT_SIZE,4*UNIT_SIZE,0,
        	-4*UNIT_SIZE,-4*UNIT_SIZE,0,
        	4*UNIT_SIZE,-4*UNIT_SIZE,0,
        	
        	4*UNIT_SIZE,-4*UNIT_SIZE,0,
        	4*UNIT_SIZE,4*UNIT_SIZE,0,
        	-4*UNIT_SIZE,4*UNIT_SIZE,0
        };
		
        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点坐标数据的初始化================end============================
        
        //顶点纹理坐标数据的初始化================begin============================
        float texCoor[]=new float[]//顶点颜色值数组，每个顶点4个色彩值RGBA
  	    {
  	      		0,0, 0,tRange, sRange,tRange,
  	      		sRange,tRange, sRange,0, 0,0        		
  	    };  
        //创建顶点纹理坐标数据缓冲
        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTexCoorBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mTexCoorBuffer.put(texCoor);//向缓冲区中放入顶点纹理数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点纹理坐标数据的初始化================end============================

    }

    //自定义的初始化着色器的方法
    public void initShader(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());  
        //基于顶点着色器与片元着色器创建程序
        mProgram = createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用
        maTexCoorHandle= GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");  
    }
    
    public void drawSelf(int texId)
    {        
    	 //指定使用某套shader程序
    	 GLES30.glUseProgram(mProgram);        
    	 //初始化变换矩阵
         Matrix.setRotateM(mMMatrix,0,0,0,1,0);
         //设置沿Z轴正向位移1
         Matrix.translateM(mMMatrix,0,0,0,1);
         //设置绕y轴旋转
         Matrix.rotateM(mMMatrix,0,yAngle,0,1,0);
         //设置绕z轴旋转
         Matrix.rotateM(mMMatrix,0,zAngle,0,0,1);  
         //设置绕x轴旋转
         Matrix.rotateM(mMMatrix,0,xAngle,1,0,0);
         //将最终变换矩阵传入渲染管线
         GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(mMMatrix), 0); 
         //将顶点位置数据传送进渲染管线
         GLES30.glVertexAttribPointer  
         (
         		maPositionHandle,   
         		3, 
         		GLES30.GL_FLOAT, 
         		false,
                3*4,   
                mVertexBuffer
         );       
         //将纹理坐标数据传送进渲染管线
         GLES30.glVertexAttribPointer  
         (
        		maTexCoorHandle, 
         		2, 
         		GLES30.GL_FLOAT, 
         		false,
                2*4,   
                mTexCoorBuffer
         );   

         GLES30.glEnableVertexAttribArray(maPositionHandle);  //启用顶点位置数据数组
         GLES30.glEnableVertexAttribArray(maTexCoorHandle);  //启用顶点纹理坐标数据数组
         
         //绑定纹理
         GLES30.glActiveTexture(GLES30.GL_TEXTURE0);//设置使用的纹理编号
         GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);//绑定指定的纹理id
         
         //绘制
         GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
    }
}

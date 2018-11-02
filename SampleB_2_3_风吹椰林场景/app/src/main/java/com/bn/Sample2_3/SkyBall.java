package com.bn.Sample2_3;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.opengl.GLES30;
/*
 * 绘制纹理天空球
 */
public class SkyBall
{
	//自定义渲染管线着色器程序id
	int mProgram;
	//总变换矩阵的引用
	int muMVPMatrixHandle;
	//顶点属性的引用
	int maPositionHandle;
	//顶点纹理坐标属性的引用
	int maTexCoorHandle;
	//顶点数据缓冲以及顶点纹理坐标数据缓冲
	FloatBuffer mVertexBuffer;
	FloatBuffer mTexCoorBuffer;
	//顶点数量
	int vCount=0;
	float startX;
	float startY;
	float startZ;
	public SkyBall(float radius,int mProgram,float startX,float startY,float startZ)
	{
		this.mProgram=mProgram;
		this.startX=startX;
		this.startY=startY;
		this.startZ=startZ;
		initVertexData(radius,startX,startY,startZ);
		initShader();
	}
	//初始化顶点数据的方法
	public void initVertexData(float radius,float startX,float startY,float startZ)
	{
		float ANGLE_SPAN=18;//切分间隔
    	float angleV=90;//纵向上的起始度数
    	//获取切分整图的纹理数组
    	float[] texCoorArray=  // hhl 按照球水平和俯仰方向分多少个格子，矩形纹理图也在水平和垂直方向等分割
         generateTexCoor
    	 ( 
    		(int)(360/ANGLE_SPAN), //纹理图切分的列数
    		(int)(angleV/ANGLE_SPAN)  //纹理图切分的行数
    	);
        int tc=0;//纹理数组计数器
        int ts=texCoorArray.length;//纹理数组长度
    	
    	ArrayList<Float> alVertix=new ArrayList<Float>();//存放顶点坐标的ArrayList
    	ArrayList<Float> alTexture=new ArrayList<Float>();//存放纹理坐标的ArrayList

		// hhl 这个就是 俯仰角和水平角 等度数分开
        for(float vAngle=angleV;vAngle>0;vAngle=vAngle-ANGLE_SPAN)//垂直方向angleSpan度一份
        {
        	for(float hAngle=360;hAngle>0;hAngle=hAngle-ANGLE_SPAN)//水平方向angleSpan度一份
        	{
        		//纵向横向各到一个角度后计算对应的此点在球面上的四边形顶点坐标
        		//并构建两个组成四边形的三角形
        		double xozLength=radius*Math.cos(Math.toRadians(vAngle));
        		float x1=(float)(xozLength*Math.cos(Math.toRadians(hAngle)));
        		float z1=(float)(xozLength*Math.sin(Math.toRadians(hAngle)));
        		float y1=(float)(radius*Math.sin(Math.toRadians(vAngle)));
        		
        		xozLength=radius*Math.cos(Math.toRadians(vAngle-ANGLE_SPAN));
        		float x2=(float)(xozLength*Math.cos(Math.toRadians(hAngle)));
        		float z2=(float)(xozLength*Math.sin(Math.toRadians(hAngle)));
        		float y2=(float)(radius*Math.sin(Math.toRadians(vAngle-ANGLE_SPAN)));
        		
        		xozLength=radius*Math.cos(Math.toRadians(vAngle-ANGLE_SPAN));
        		float x3=(float)(xozLength*Math.cos(Math.toRadians(hAngle-ANGLE_SPAN)));
        		float z3=(float)(xozLength*Math.sin(Math.toRadians(hAngle-ANGLE_SPAN)));
        		float y3=(float)(radius*Math.sin(Math.toRadians(vAngle-ANGLE_SPAN)));
        		
        		xozLength=radius*Math.cos(Math.toRadians(vAngle));
        		float x4=(float)(xozLength*Math.cos(Math.toRadians(hAngle-ANGLE_SPAN)));
        		float z4=(float)(xozLength*Math.sin(Math.toRadians(hAngle-ANGLE_SPAN)));
        		float y4=(float)(radius*Math.sin(Math.toRadians(vAngle)));   
        		
        		//构建第一三角形
        		alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);
        		alVertix.add(x4);alVertix.add(y4);alVertix.add(z4); 
        		alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
        		       		
        		//构建第二三角形
        		
        		alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
        		alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);
        		alVertix.add(x3);alVertix.add(y3);alVertix.add(z3); 
        		
        		//第一三角形3个顶点的6个纹理坐标
        		alTexture.add(texCoorArray[tc++%ts]);
        		alTexture.add(texCoorArray[tc++%ts]);
        		alTexture.add(texCoorArray[tc++%ts]);        		
        		alTexture.add(texCoorArray[tc++%ts]);
        		alTexture.add(texCoorArray[tc++%ts]);
        		alTexture.add(texCoorArray[tc++%ts]);
        		//第二三角形3个顶点的6个纹理坐标
        		alTexture.add(texCoorArray[tc++%ts]);
        		alTexture.add(texCoorArray[tc++%ts]);
        		alTexture.add(texCoorArray[tc++%ts]);        		
        		alTexture.add(texCoorArray[tc++%ts]);
        		alTexture.add(texCoorArray[tc++%ts]);
        		alTexture.add(texCoorArray[tc++%ts]);       		
        	}
        } 	
        vCount=alVertix.size()/3;//顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标
        //将alVertix中的坐标值转存到一个float数组中
        float vertices[]=new float[vCount*3];
    	for(int i=0;i<alVertix.size();i++)
    	{
    		vertices[i]=alVertix.get(i);
    	}
        //创建绘制顶点数据缓冲
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        
        //创建纹理坐标缓冲
        float textureCoors[]=new float[alTexture.size()];//顶点纹理值数组
        for(int i=0;i<alTexture.size();i++) 
        {
        	textureCoors[i]=alTexture.get(i);
        }
        ByteBuffer tbb = ByteBuffer.allocateDirect(textureCoors.length*4);
        tbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTexCoorBuffer = tbb.asFloatBuffer();//转换为int型缓冲
        mTexCoorBuffer.put(textureCoors);//向缓冲区中放入顶点着色数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
	}
	//初始化着色器程序的方法
	public void initShader()
	{
		//获得顶点坐标数据的引用
		maPositionHandle=GLES30.glGetAttribLocation(mProgram, "aPosition");
		//顶点纹理坐标的引用
		maTexCoorHandle=GLES30.glGetAttribLocation(mProgram, "aTexCoor");
		muMVPMatrixHandle=GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
	}
	public void drawSelf(int texId,float rotationAngle_Y)
	{
		MatrixState.pushMatrix();
		MatrixState.translate(startX, startY, startZ);
		MatrixState.rotate(rotationAngle_Y, 0, 1, 0);
		//使用某套指定的着色器程序
		GLES30.glUseProgram(mProgram);
		//将最终变换矩阵传入渲染管线
		GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
		//将坐标数据传入渲染管线
		GLES30.glVertexAttribPointer
		(
			maPositionHandle, 
			3, 
			GLES30.GL_FLOAT, 
			false, 
			3*4, 
			mVertexBuffer
		);
		//将纹理坐标数据传入渲染管线
		GLES30.glVertexAttribPointer
		(
			maTexCoorHandle, 
			2, 
			GLES30.GL_FLOAT, 
			false, 
			2*4, 
			mTexCoorBuffer
		);
		//启用顶点位置数据、顶点纹理坐标数据数组
        GLES30.glEnableVertexAttribArray(maPositionHandle);  
        GLES30.glEnableVertexAttribArray(maTexCoorHandle);  
        
        //绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
        
        //绘制纹理矩形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
        MatrixState.popMatrix();
	}    
    
	//自动切分纹理产生纹理数组的方法
    public float[] generateTexCoor(int bw,int bh)
    {
    	float[] result=new float[bw*bh*6*2];
    	float sizew=1.0f/bw;//列数
    	float sizeh=1.0f/bh;//行数
    	int c=0;
    	for(int i=0;i<bh;i++)
    	{
    		for(int j=0;j<bw;j++)
    		{
    			//每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
    			float s=j*sizew;
    			float t=i*sizeh;
    			
    			result[c++]=s;
    			result[c++]=t;
    			
    			result[c++]=s+sizew;
    			result[c++]=t;
    			
    			result[c++]=s;
    			result[c++]=t+sizeh;
    			
    			result[c++]=s;
    			result[c++]=t+sizeh;
    			
    			result[c++]=s+sizew;
    			result[c++]=t;
    			
    			result[c++]=s+sizew;
    			result[c++]=t+sizeh;    			
    		}
    	}
    	return result;
    }
}
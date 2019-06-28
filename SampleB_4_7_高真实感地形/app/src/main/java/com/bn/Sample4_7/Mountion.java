package com.bn.Sample4_7;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES30;

public class Mountion
{
	//单位长度
	float UNIT_SIZE=3.0f;
	
	//自定义渲染管线的id
	int mProgram;
	//总变化矩阵引用的id
	int muMVPMatrixHandle;
	//顶点位置属性引用id
	int maPositionHandle;
	//顶点纹理坐标属性引用id
	int maTexCoorHandle;
	
    int muMMatrixHandle;//位置、旋转变换矩阵引用
    int maNormalHandle; //顶点法向量属性引用
    int maLightLocationHandle;//光源位置属性引用
    int maCameraHandle; //摄像机位置属性引用 
	
	//砂石
	int ssTextureHandle;
	//绿草皮
	int lcpTextureHandle;
	//道路
	int dlTextureHandle;
	//黄草皮
	int hcpTextureHandle;
	//RGB
	int rgbTextureHandle;
	//起始x值
	int landStartYYHandle;
	//长度
	int landYSpanHandle;
	int repeatHandle;
	
	//顶点数据缓冲和纹理坐标数据缓冲
	FloatBuffer mVertexBuffer;
	FloatBuffer mTexCoorBuffer; 
	FloatBuffer mNormalBuffer;//顶点法向量数据缓冲
	//顶点数量
	int vCount=0;
	
	public Mountion(MySurfaceView mv,float[][] yArray,float [][][] normols,int rows,int cols)
	{
		initVertexData(yArray,normols,rows,cols);
		initShader(mv);
	}
	//初始化顶点坐标与着色数据的方法
    public void initVertexData(float[][] yArray,float [][][] normols,int rows,int cols)
    {
    	//顶点坐标数据的初始化
    	vCount=cols*rows*2*3;//每个格子两个三角形，每个三角形3个顶点   
        float vertices[]=new float[vCount*3];//每个顶点xyz三个坐标
        float vnormols[]=new float[vCount*3];//每个顶点xyz三个坐标

        int count=0;	// 顶点位置 计数器
        int count2=0;	// 顶点法向量 计数器
		float x_offset = -UNIT_SIZE*cols/2;
		float y_offset = -UNIT_SIZE*rows/2;
        for(int j=0; j<rows; j++)
        {
        	for(int i=0; i<cols; i++)
        	{        		
        		//计算当前格子左上侧点坐标 
        		float zsx= x_offset + i*UNIT_SIZE;
        		float zsz= y_offset + j*UNIT_SIZE;
        		
        		vertices[count++]=zsx;
        		vertices[count++]=yArray[j][i];
        		vertices[count++]=zsz;
        		
        		vnormols[count2++]=-normols[j][i][0];
        		vnormols[count2++]=-normols[j][i][1];
        		vnormols[count2++]=-normols[j][i][2];
        		
        		vertices[count++]=zsx;
        		vertices[count++]=yArray[j+1][i];
        		vertices[count++]=zsz+UNIT_SIZE;
        		
        		vnormols[count2++]=-normols[j+1][i][0];
        		vnormols[count2++]=-normols[j+1][i][1];
        		vnormols[count2++]=-normols[j+1][i][2];
        		
        		vertices[count++]=zsx+UNIT_SIZE;
        		vertices[count++]=yArray[j][i+1];
        		vertices[count++]=zsz;
        		
        		vnormols[count2++]=-normols[j][i+1][0];
        		vnormols[count2++]=-normols[j][i+1][1];
        		vnormols[count2++]=-normols[j][i+1][2];
        		

        		
        		vertices[count++]=zsx+UNIT_SIZE;
        		vertices[count++]=yArray[j][i+1];
        		vertices[count++]=zsz;
        		
        		vnormols[count2++]=-normols[j][i+1][0];
        		vnormols[count2++]=-normols[j][i+1][1];
        		vnormols[count2++]=-normols[j][i+1][2];
        		
        		vertices[count++]=zsx;
        		vertices[count++]=yArray[j+1][i];
        		vertices[count++]=zsz+UNIT_SIZE;
        		
        		vnormols[count2++]=-normols[j+1][i][0];
        		vnormols[count2++]=-normols[j+1][i][1];
        		vnormols[count2++]=-normols[j+1][i][2];
        		
        		vertices[count++]=zsx+UNIT_SIZE;
        		vertices[count++]=yArray[j+1][i+1];
        		vertices[count++]=zsz+UNIT_SIZE;
        		
        		vnormols[count2++]=-normols[j+1][i+1][0];
        		vnormols[count2++]=-normols[j+1][i+1][1];
        		vnormols[count2++]=-normols[j+1][i+1][2];
        	}
        }
		

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        float[] texCoor=generateTexCoor(cols,rows);
        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
        cbb.order(ByteOrder.nativeOrder());
        mTexCoorBuffer = cbb.asFloatBuffer();
        mTexCoorBuffer.put(texCoor);
        mTexCoorBuffer.position(0);
        

        ByteBuffer nbb = ByteBuffer.allocateDirect(vnormols.length*4);
        nbb.order(ByteOrder.nativeOrder());
        mNormalBuffer = nbb.asFloatBuffer();
        mNormalBuffer.put(vnormols);
        mNormalBuffer.position(0);
    }
	
	//初始化Shader的方法
	public void initShader(MySurfaceView mv) 
	{
		String mVertexShader=ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
		String mFragmentShader=ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
		//基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用id  
        maTexCoorHandle= GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");  
 
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");  
        //获取程序中顶点法向量属性引用  
        maNormalHandle= GLES30.glGetAttribLocation(mProgram, "aNormal");
        //获取程序中光源位置引用
        maLightLocationHandle=GLES30.glGetUniformLocation(mProgram, "uLightLocation");
        //获取程序中摄像机位置引用
        maCameraHandle=GLES30.glGetUniformLocation(mProgram, "uCamera"); 

        //纹理
		//砂石
        ssTextureHandle=GLES30.glGetUniformLocation(mProgram, "ssTexture");
		//绿草皮
        lcpTextureHandle=GLES30.glGetUniformLocation(mProgram, "lcpTexture");
		//道路
        dlTextureHandle=GLES30.glGetUniformLocation(mProgram, "dlTexture");
        //黄草皮
        hcpTextureHandle=GLES30.glGetUniformLocation(mProgram, "hcpTexture");
        //RGB
        rgbTextureHandle=GLES30.glGetUniformLocation(mProgram, "rgbTexture");
        
		repeatHandle=GLES30.glGetUniformLocation(mProgram, "repeatVaule");
	}
	
	//自定义的绘制方法drawSelf
	public void drawSelf(int ssTexId,int lcpTexId,int dlTexId,int hcpTexId,int rgbTexId)
	{
		//指定使用某套shader程序
   	 	GLES30.glUseProgram(mProgram); 
        //将最终变换矩阵传入shader程序
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 

        //将位置、旋转变换矩阵传入着色器程序
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        //将光源位置传入着色器程序   
        GLES30.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
        //将摄像机位置传入着色器程序   
        GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);

        
        //传送顶点位置数据
		GLES30.glVertexAttribPointer
		(
			maPositionHandle, 
			3, 
			GLES30.GL_FLOAT, 
			false, 
			3*4, 
			mVertexBuffer
		);
		
		//传送顶点纹理坐标数据
		GLES30.glVertexAttribPointer
		(
			maTexCoorHandle, 
			2, 
			GLES30.GL_FLOAT, 
			false, 
			2*4, 
			mTexCoorBuffer
		);
		
        // 将顶点法向量数据传入渲染管线
		GLES30.glVertexAttribPointer(maNormalHandle,
				3,
				GLES30.GL_FLOAT,
				false,
				3 * 4,
				mNormalBuffer);
		// 允许顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maPositionHandle);  
        GLES30.glEnableVertexAttribArray(maTexCoorHandle);  
        GLES30.glEnableVertexAttribArray(maNormalHandle);// 启用顶点法向量数据

        // 绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, ssTexId);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, lcpTexId);
		GLES30.glActiveTexture(GLES30.GL_TEXTURE2);
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, dlTexId);
		GLES30.glActiveTexture(GLES30.GL_TEXTURE3);
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, hcpTexId);
		GLES30.glActiveTexture(GLES30.GL_TEXTURE4);
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, rgbTexId);
		GLES30.glUniform1i(ssTextureHandle, 0);	// 使用0号纹理
        GLES30.glUniform1i(lcpTextureHandle, 1); // 使用1号纹理
        GLES30.glUniform1i(dlTextureHandle, 2); 	// 使用2号纹理
        GLES30.glUniform1i(hcpTextureHandle, 3); // 使用3号纹理
        GLES30.glUniform1i(rgbTextureHandle, 4); // 使用4号纹理
        
        GLES30.glUniform1f(repeatHandle,16);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); // 绘制纹理矩形
	}
	//自动切分纹理产生纹理数组的方法
    public float[] generateTexCoor(int bw,int bh)
    {
    	float[] result=new float[bw*bh*6*2]; 
    	float sizew=16.0f/bw;//列数 
    	float sizeh=16.0f/bh;//行数
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
    			
    			result[c++]=s;
    			result[c++]=t+sizeh;
    			
    			result[c++]=s+sizew;
    			result[c++]=t;
    			
    			result[c++]=s+sizew;
    			result[c++]=t;
    			
    			result[c++]=s;
    			result[c++]=t+sizeh;
    			
    			result[c++]=s+sizew;
    			result[c++]=t+sizeh;    			
    		}
    	}
    	return result;
    }
}
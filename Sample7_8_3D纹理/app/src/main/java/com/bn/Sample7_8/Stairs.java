package com.bn.Sample7_8;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES30;

public class Stairs
{	
	int mProgram;//自定义渲染管线着色器程序id  
    int muMVPMatrixHandle;//总变换矩阵引用
    int muMMatrixHandle;//位置、旋转变换矩阵
    int maPositionHandle; //顶点位置属性引用  
    int maNormalHandle; //顶点法向量属性引用  
    int maLightLocationHandle;//光源位置属性引用  
    int maCameraHandle; //摄像机位置属性引用 
    String mVertexShader;//顶点着色器代码脚本    	 
    String mFragmentShader;//片元着色器代码脚本    
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲  
	FloatBuffer   mNormalBuffer;//顶点法向量数据缓冲
    int vCount=0;  
    float xSize=0.2f;
	float y1Size=0.1f;
	float y2Size=0.2f;
	float y3Size=0.3f;
	float y4Size=0.4f;  // hhl. y3Size y4Size 其实并不是大小 而是位置
	float z1Size=0.4f;
	float z2Size=0.3f;
	float z3Size=0.2f;
	float z4Size=0.1f;
    public Stairs(MySurfaceView mv)
    {    	
    	//初始化顶点坐标与着色数据
    	initVertexData();
    	//初始化着色器
    	initShader(mv);
    }
    
    //初始化顶点坐标与着色数据的方法
    public void initVertexData()
    {
    	//顶点坐标数据的初始化================begin============================
    	float[] vertices=new float[]{
    			//第四个（最下面）立方体的下面   // hhl 第一个立方体  高 0.1  长和宽都是0.4   一共四层 每层是一个立方体 每层高都是0.1 但是 长晚上递减0.1
    			xSize,0,0,
    			xSize,0,z1Size,
    			-xSize,0,z1Size,
    			-xSize,0,z1Size,
    			-xSize,0,0,
    			xSize,0,0,
    			//第四个（最下面）立方体的上面
    			xSize,y1Size,0,
    			-xSize,y1Size,0,
    			-xSize,y1Size,z1Size, // hhl. 除了0--> y1Size   x和z刚好上下到了顺序 因为绕卷方向都要一致
    			-xSize,y1Size,z1Size,
    			xSize,y1Size,z1Size,
    			xSize,y1Size,0,
    			//第四个（最下面）立方体的前面
    			xSize,y1Size,z1Size,
    			-xSize,y1Size,z1Size,
    			-xSize,0,z1Size,
    			-xSize,0,z1Size,
    			xSize,0,z1Size,
    			xSize,y1Size,z1Size,
    			//第四个（最下面）立方体的后面
    			xSize,y1Size,0,
    			xSize,0,0,
    			-xSize,0,0,
    			-xSize,0,0,
    			-xSize,y1Size,0,
    			xSize,y1Size,0,
    			//第四个（最下面）立方体的左面
    			-xSize,y1Size,z1Size,
    			-xSize,y1Size,0,
    			-xSize,0,0,
    			-xSize,0,0,
    			-xSize,0,z1Size,
    			-xSize,y1Size,z1Size,
    			//第四个（最下面）立方体的右面
    			xSize,y1Size,z1Size,
    			xSize,0,z1Size,
    			xSize,0,0,
    			xSize,0,0,
    			xSize,y1Size,0,
    			xSize,y1Size,z1Size,
    			
    			//第三个立方体的上面
    			xSize,y2Size,0,
    			-xSize,y2Size,0,
    			-xSize,y2Size,z2Size,
    			-xSize,y2Size,z2Size,
    			xSize,y2Size,z2Size,
    			xSize,y2Size,0,
    			//第三个立方体的前面
    			xSize,y2Size,z2Size,
    			-xSize,y2Size,z2Size,
    			-xSize,y1Size,z2Size,
    			-xSize,y1Size,z2Size,
    			xSize,y1Size,z2Size,
    			xSize,y2Size,z2Size,
    			//第三个立方体的后面
    			xSize,y2Size,0,
    			xSize,y1Size,0,
    			-xSize,y1Size,0,
    			-xSize,y1Size,0,
    			-xSize,y2Size,0,
    			xSize,y2Size,0,
    			//第三个立方体的左面
    			-xSize,y2Size,z2Size,
    			-xSize,y2Size,0,
    			-xSize,y1Size,0,
    			-xSize,y1Size,0,
    			-xSize,y1Size,z2Size,
    			-xSize,y2Size,z2Size,
    			//第三个立方体的右面
    			xSize,y2Size,z2Size,
    			xSize,y1Size,z2Size,
    			xSize,y1Size,0,
    			xSize,y1Size,0,
    			xSize,y2Size,0,
    			xSize,y2Size,z2Size,
    			
    			//第二个立方体的上面
    			xSize,y3Size,0,
    			-xSize,y3Size,0,
    			-xSize,y3Size,z3Size,
    			-xSize,y3Size,z3Size,
    			xSize,y3Size,z3Size,
    			xSize,y3Size,0,
    			//第二个立方体的前面
    			xSize,y3Size,z3Size,
    			-xSize,y3Size,z3Size,
    			-xSize,y2Size,z3Size,
    			-xSize,y2Size,z3Size,
    			xSize,y2Size,z3Size,
    			xSize,y3Size,z3Size,
    			//第二个立方体的后面
    			xSize,y3Size,0,
    			xSize,y2Size,0,
    			-xSize,y2Size,0,
    			-xSize,y2Size,0,
    			-xSize,y3Size,0,
    			xSize,y3Size,0,
    			//第二个立方体的左面
    			-xSize,y3Size,z3Size,
    			-xSize,y3Size,0,
    			-xSize,y2Size,0,
    			-xSize,y2Size,0,
    			-xSize,y2Size,z3Size,
    			-xSize,y3Size,z3Size,
    			//第二个立方体的右面
    			xSize,y3Size,z3Size,
    			xSize,y2Size,z3Size,
    			xSize,y2Size,0,
    			xSize,y2Size,0,
    			xSize,y3Size,0,
    			xSize,y3Size,z3Size,
    			
    			//第一个立方体的上面
    			xSize,y4Size,0,
    			-xSize,y4Size,0,
    			-xSize,y4Size,z4Size,
    			-xSize,y4Size,z4Size,
    			xSize,y4Size,z4Size,
    			xSize,y4Size,0,
    			//第一个立方体的前面
    			xSize,y4Size,z4Size,
    			-xSize,y4Size,z4Size,
    			-xSize,y3Size,z4Size,
    			-xSize,y3Size,z4Size,
    			xSize,y3Size,z4Size,
    			xSize,y4Size,z4Size,
    			//第一个立方体的后面
    			xSize,y4Size,0,
    			xSize,y3Size,0,
    			-xSize,y3Size,0,
    			-xSize,y3Size,0,
    			-xSize,y4Size,0,
    			xSize,y4Size,0,
    			//第一个立方体的左面
    			-xSize,y4Size,z4Size,
    			-xSize,y4Size,0,
    			-xSize,y3Size,0,
    			-xSize,y3Size,0,
    			-xSize,y3Size,z4Size,
    			-xSize,y4Size,z4Size,
    			//第一个立方体的右面
    			xSize,y4Size,z4Size,
    			xSize,y3Size,z4Size,
    			xSize,y3Size,0,
    			xSize,y3Size,0,
    			xSize,y4Size,0,
    			xSize,y4Size,z4Size,
    	};
    	vCount=vertices.length/3;
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
        
        float[] normals=
    		{
    		//第四个（最下面）立方体的下面
    		0,-1,0, 0,-1,0, 0,-1,0, // hhl 按照点法向量法
    		0,-1,0, 0,-1,0, 0,-1,0,
    		//第四个（最下面）立方体的上面
    		0,1,0, 0,1,0, 0,1,0,
    		0,1,0, 0,1,0, 0,1,0,
    		//第四个（最下面）立方体的前面
    		0,0,1, 0,0,1, 0,0,1,
    		0,0,1, 0,0,1, 0,0,1,
    		//第四个（最下面）立方体的后面
    		0,0,-1, 0,0,-1, 0,0,-1,
    		0,0,-1, 0,0,-1, 0,0,-1,
    		//第四个（最下面）立方体的左面
    		-1,0,0, -1,0,0, -1,0,0,
    		-1,0,0, -1,0,0, -1,0,0,
    		//第四个（最下面）立方体的右面
    		1,0,0, 1,0,0, 1,0,0,
    		1,0,0, 1,0,0, 1,0,0,

    		//第三个立方体的上面
    		0,1,0, 0,1,0, 0,1,0,
    		0,1,0, 0,1,0, 0,1,0,
    		//第三个立方体的前面
    		0,0,1, 0,0,1, 0,0,1,
    		0,0,1, 0,0,1, 0,0,1,
    		//第三个立方体的后面
    		0,0,-1, 0,0,-1, 0,0,-1,
    		0,0,-1, 0,0,-1, 0,0,-1,
    		//第三个立方体的左面
    		-1,0,0, -1,0,0, -1,0,0,
    		-1,0,0, -1,0,0, -1,0,0,
    		//第三个立方体的右面
    		1,0,0, 1,0,0, 1,0,0,
    		1,0,0, 1,0,0, 1,0,0,

    		//第二个立方体的上面
    		0,1,0, 0,1,0, 0,1,0,
    		0,1,0, 0,1,0, 0,1,0,
    		//第二个立方体的前面
    		0,0,1, 0,0,1, 0,0,1,
    		0,0,1, 0,0,1, 0,0,1,
    		//第二个立方体的后面
    		0,0,-1, 0,0,-1, 0,0,-1,
    		0,0,-1, 0,0,-1, 0,0,-1,
    		//第二个立方体的左面
    		-1,0,0, -1,0,0, -1,0,0,
    		-1,0,0, -1,0,0, -1,0,0,
    		//第二个立方体的右面
    		1,0,0, 1,0,0, 1,0,0,
    		1,0,0, 1,0,0, 1,0,0,
    		
    		//第一个立方体的上面
    		0,1,0, 0,1,0, 0,1,0,
    		0,1,0, 0,1,0, 0,1,0,
    		//第一个立方体的前面
    		0,0,1, 0,0,1, 0,0,1,
    		0,0,1, 0,0,1, 0,0,1,
    		//第一个立方体的后面
    		0,0,-1, 0,0,-1, 0,0,-1,
    		0,0,-1, 0,0,-1, 0,0,-1,
    		//第一个立方体的左面
    		-1,0,0, -1,0,0, -1,0,0,
    		-1,0,0, -1,0,0, -1,0,0,
    		//第一个立方体的右面
    		1,0,0, 1,0,0, 1,0,0,
    		1,0,0, 1,0,0, 1,0,0,
    		};
        //顶点法向量数据的初始化================begin============================  
        ByteBuffer cbb = ByteBuffer.allocateDirect(normals.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mNormalBuffer.put(normals);//向缓冲区中放入顶点法向量数据
        mNormalBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点法向量数据的初始化================end============================
    }

    public void initShader(MySurfaceView mv)
    {
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        maNormalHandle= GLES30.glGetAttribLocation(mProgram, "aNormal");
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
        maLightLocationHandle=GLES30.glGetUniformLocation(mProgram, "uLightLocation");
        maCameraHandle=GLES30.glGetUniformLocation(mProgram, "uCamera"); 
    }
    
    public void drawSelf(int texId)
    {        

    	 GLES30.glUseProgram(mProgram);

         GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
         GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
         GLES30.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
         GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);


         GLES30.glVertexAttribPointer  
         (
         		maPositionHandle,   
         		3, 
         		GLES30.GL_FLOAT, 
         		false,
                3*4,   
                mVertexBuffer
         );
         GLES30.glVertexAttribPointer  
         (
        		maNormalHandle, 
         		3,   
         		GLES30.GL_FLOAT, 
         		false,
                3*4,   
                mNormalBuffer
         );   
         

         GLES30.glEnableVertexAttribArray(maPositionHandle);  //启用顶点位置数据
         GLES30.glEnableVertexAttribArray(maNormalHandle);  //启用顶点法向量数据
         //绑定纹理
         GLES30.glActiveTexture(GLES30.GL_TEXTURE0);//设置使用的纹理编号
         GLES30.glBindTexture(GLES30.GL_TEXTURE_3D, texId);//绑定纹理
         //绘制楼梯
         GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
    }
}

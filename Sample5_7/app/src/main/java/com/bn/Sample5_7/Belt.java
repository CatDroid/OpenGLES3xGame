package com.bn.Sample5_7;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES30;

//颜色条状物
public class Belt
{	
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用  
    int maColorHandle; //顶点颜色属性引用 
    String mVertexShader;//顶点着色器代码脚本  
    String mFragmentShader;//片元着色器代码脚本
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mColorBuffer;//顶点着色数据缓冲
    int vCount=0; //顶点数量 
    
    public Belt(MySurfaceView mv)
    {    	
    	//初始化顶点坐标与着色数据
    	initVertexData();
    	//初始化shader        
    	initShader(mv);
    }
    
    //初始化顶点坐标与着色数据的方法
    public void initVertexData()
    {
    	//顶点坐标数据的初始化================begin============================
    	int n = 6;          // 180度 划分为6个
        vCount=2*(n+1);     // 顶点数目  由于是用 GL_TRIANGLE_STRIP 三角形条带 绘制 所以顶点不用重复  而且四边形只需要4个顶点
        //float angdegBegin = -90;
        //float angdegEnd = 90;
        float angdegBegin = 0 ;
        float angdegEnd = 180 ;
        float angdegSpan = (angdegEnd-angdegBegin)/n;
		
		float[] vertices=new float[vCount*3];//顶点坐标数据数组  *3 代表有 x y z坐标
		//坐标数据初始化
		int count=0;
		for(float angdeg=angdegBegin; angdeg<=angdegEnd; angdeg+=angdegSpan) {
			double angrad=Math.toRadians(angdeg);//当前弧度

            // 0.6f*Constant.UNIT_SIZE  内部圆形半径
            // Constant.UNIT_SIZE       外部圆形半径

            // 后面绘制方式 是用 GL_TRIANGLE_STRIP 方式 所以

			//当前点
//			vertices[count++]=(float) (-0.6f*Constant.UNIT_SIZE*Math.sin(angrad));//顶点x坐标
//			vertices[count++]=(float) (0.6f*Constant.UNIT_SIZE*Math.cos(angrad));//顶点y坐标
            vertices[count++]=(float) (0.6f*Constant.UNIT_SIZE*Math.cos(angrad));
            vertices[count++]=(float) (0.6f*Constant.UNIT_SIZE*Math.sin(angrad));
			vertices[count++]=0;//顶点z坐标		
			//当前点
//			vertices[count++]=(float) (-Constant.UNIT_SIZE*Math.sin(angrad));//顶点x坐标
//			vertices[count++]=(float) (Constant.UNIT_SIZE*Math.cos(angrad));//顶点y坐标
            vertices[count++]=(float) (Constant.UNIT_SIZE*Math.cos(angrad));
            vertices[count++]=(float) (Constant.UNIT_SIZE*Math.sin(angrad));
			vertices[count++]=0;//顶点z坐标
		}
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
       
    	//顶点颜色值数组，每个顶点4个色彩值RGBA
       	count = 0;
        float colors[]=new float[vCount*4];
        for(int i=0; i<colors.length; i+=8){
        	colors[count++] = 1;   //  更上面一样 每次 设置两个顶点的颜色
        	colors[count++] = 1; 
        	colors[count++] = 1; 
        	colors[count++] = 0;
        	
        	colors[count++] = 0; 
        	colors[count++] = 1; 
        	colors[count++] = 1; 
        	colors[count++] = 0;
        }
        
        //创建顶点着色数据缓冲
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mColorBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mColorBuffer.put(colors);//向缓冲区中放入顶点着色数据
        mColorBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点着色数据的初始化================end============================
    }
    //初始化着色器
    public void initShader(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());  
        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点颜色属性引用id  
        maColorHandle= GLES30.glGetAttribLocation(mProgram, "aColor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix"); 
    }
    
    public void drawSelf()
    {        
    	//指定使用某套着色器程序
    	 GLES30.glUseProgram(mProgram);
         //将最终变换矩阵传入渲染管线
         GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
         //将顶点位置数据送入渲染管线
         GLES30.glVertexAttribPointer  
         (
         		maPositionHandle,   
         		3, 
         		GLES30.GL_FLOAT, 
         		false,
                3*4,   
                mVertexBuffer
         );       
       //将顶点颜色数据送入渲染管线
         GLES30.glVertexAttribPointer  
         (
        		maColorHandle, 
         		4, 
         		GLES30.GL_FLOAT, 
         		false,
                4*4,   
                mColorBuffer
         );   
         //启用顶点位置数据数组
         GLES30.glEnableVertexAttribArray(maPositionHandle); 
         //启用顶点颜色数据数组
         GLES30.glEnableVertexAttribArray(maColorHandle);  
         //采用三角形条带方式绘制
         GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0 , vCount); 
    }
}

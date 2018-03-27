package com.bn.Sample8_6;

import static com.bn.Sample8_6.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import android.opengl.GLES30;

/*
 * 连接两个球之间的圆柱
 */
public class Stick 
{	
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用
    int maColorHandle; //顶点颜色属性引用 
    int muMMatrixHandle;
    
    int maCameraHandle; //摄像机位置属性引用
    int maNormalHandle; //顶点法向量属性引用
    int maLightLocationHandle;//光源位置属性引用 
    
    
    String mVertexShader;//顶点着色器    	 
    String mFragmentShader;//片元着色器
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mColorBuffer;	//顶点颜色数据缓冲
	FloatBuffer   mNormalBuffer;//顶点法向量数据缓冲
    int vCount=0;   
    
	float length=10f;//圆柱长度
	float circle_radius=2f;//圆截环半径
	float degreespan=18f;  //圆截环每一份的度数大小
    
    public Stick(MySurfaceView mv,float length,float circle_radius,float degreespan,float[] colorValue)
    {
    	//调用初始化顶点数据的initVertexData方法
    	initVertexData( length, circle_radius, degreespan,colorValue);
    	//调用初始化着色器的intShader方法
    	initShader(mv);
    }
	//将一个向量规格化的方法
	public static float[] normalizeVector(float x, float y, float z){
		float mod=module(x,y,z);
		mod=(mod==0)?1:mod;
		return new float[]{x/mod, y/mod, z/mod};//返回规格化后的向量
	}
	//求向量的模的方法
	public static float module(float x, float y, float z){
		return (float) Math.sqrt(x*x+y*y+z*z);
	}
    //自定义的初始化顶点数据的方法
    @SuppressWarnings("static-access")
	public void initVertexData(float length,float circle_radius,float degreespan,float[] colorValue)
    {
    	//顶点坐标数据的初始化
		ArrayList<Float> val=new ArrayList<Float>();//顶点存放列表
		ArrayList<Float> ial=new ArrayList<Float>();//法向量存放列表
    	
    	this.length = length;
    	this.circle_radius = circle_radius;
    	this.degreespan = degreespan;

		// hhl 棍子没有按长度划分  直接首尾顶点连接 侧面按照360中每18度划分为一个长方形  长方形首尾就是圆柱的底边
    	for(float circle_degree=360.0f;circle_degree>0.0f;circle_degree-=degreespan)//循环行
		{
				float x1 =(float)(-length/2);
				float y1=(float) (circle_radius*Math.sin(Math.toRadians(circle_degree)));
				float z1=(float) (circle_radius*Math.cos(Math.toRadians(circle_degree)));
				
				float a1=0;
				float b1=y1;
				float c1=z1;
				//向量规格化
				float[] result=this.normalizeVector(a1,b1,c1);
				a1=result[0];
				b1=result[1];
				c1=result[2];
				
				float x2 =(float)(-length/2);
				float y2=(float) (circle_radius*Math.sin(Math.toRadians(circle_degree-degreespan)));
				float z2=(float) (circle_radius*Math.cos(Math.toRadians(circle_degree-degreespan)));
				
				float a2=0;
				float b2=y2;
				float c2=z2;
				//向量规格化
				result=this.normalizeVector(a2,b2,c2);
				a2=result[0];
				b2=result[1];
				c2=result[2];
				
				float x3 =(float)(length/2);
				float y3=(float) (circle_radius*Math.sin(Math.toRadians(circle_degree-degreespan)));
				float z3=(float) (circle_radius*Math.cos(Math.toRadians(circle_degree-degreespan)));
				
				float a3=0;
				float b3=y3;
				float c3=z3;
				//向量规格化
				result=this.normalizeVector(a3,b3,c3);
				a3=result[0];
				b3=result[1];
				c3=result[2];
				
				float x4 =(float)(length/2);
				float y4=(float) (circle_radius*Math.sin(Math.toRadians(circle_degree)));
				float z4=(float) (circle_radius*Math.cos(Math.toRadians(circle_degree)));
				
				float a4=0;
				float b4=y4;
				float c4=z4;
				//向量规格化
				result=this.normalizeVector(a4,b4,c4);
				a4=result[0];
				b4=result[1];
				c4=result[2];
				
				val.add(x1);val.add(y1);val.add(z1);//两个三角形，共6个顶点的坐标
				val.add(x2);val.add(y2);val.add(z2);
				val.add(x4);val.add(y4);val.add(z4);
				
				val.add(x2);val.add(y2);val.add(z2);
				val.add(x3);val.add(y3);val.add(z3);
				val.add(x4);val.add(y4);val.add(z4);
				
				ial.add(a1);ial.add(b1);ial.add(c1);//顶点对应的法向量
				ial.add(a2);ial.add(b2);ial.add(c2);
				ial.add(a4);ial.add(b4);ial.add(c4);
				
				ial.add(a2);ial.add(b2);ial.add(c2);
				ial.add(a3);ial.add(b3);ial.add(c3);
				ial.add(a4);ial.add(b4);ial.add(c4);
		}   
    	
    	vCount=val.size()/3;//顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标
    	
        //将alVertix中的坐标值转存到一个float数组中
        float vertices[]=new float[vCount*3];
        for(int i=0;i<val.size();i++)
    	{
    		vertices[i]=val.get(i);
    	}
		
        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为int型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        
        //将alVertix中的坐标值转存到一个float数组中
        float normals[]=new float[ial.size()];
    	for(int i=0;i<ial.size();i++)
    	{
    		normals[i]=ial.get(i);
    	}
		
        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length*4);
        nbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = nbb.asFloatBuffer();//转换为int型缓冲
        mNormalBuffer.put(normals);//向缓冲区中放入顶点坐标数据
        mNormalBuffer.position(0);//设置缓冲区起始位置
        
        //顶点着色数据的初始化
        float colors[]=new float[vCount*4];
        for(int i=0;i<vCount;i++){
        	colors[4*i]=colorValue[0];
        	colors[4*i+1]=colorValue[1];
        	colors[4*i+2]=colorValue[2];
        	colors[4*i+3]=colorValue[3];
        }
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mColorBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mColorBuffer.put(colors);//向缓冲区中放入顶点着色数据
        mColorBuffer.position(0);//设置缓冲区起始位置
    }

    //初始化着色器
    public void initShader(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex_color_light.sh", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_color_light.sh", mv.getResources());  
        //基于顶点着色器与片元着色器创建程序
        mProgram = createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点颜色属性引用id  
        maColorHandle= GLES30.glGetAttribLocation(mProgram, "aColor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix"); 
        
        
        //获取程序中顶点法向量属性引用id  
        maNormalHandle= GLES30.glGetAttribLocation(mProgram, "aNormal"); 
        //获取程序中摄像机位置引用id
        maCameraHandle=GLES30.glGetUniformLocation(mProgram, "uCamera"); 
        //获取程序中光源位置引用id
        maLightLocationHandle=GLES30.glGetUniformLocation(mProgram, "uLightLocation"); 
        //获取位置、旋转变换矩阵引用id
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");  
    }
    
    public void drawSelf()
    {     
    	
    	
    	 //制定使用某套shader程序
    	 GLES30.glUseProgram(mProgram);        
         
         //将最终变换矩阵传入shader程序
         GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
         
         //将位置、旋转变换矩阵传入shader程序
         GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0); 
         //将摄像机位置传入shader程序   
         GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
         //将光源位置传入shader程序

		 synchronized (MatrixState.lightPositionFB_lock){
			 GLES30.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
		 }

         
         
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
         //传送顶点颜色数据
         GLES30.glVertexAttribPointer  
         (
        		maColorHandle, 
         		4, 
         		GLES30.GL_FLOAT, 
         		false,
                4*4,   
                mColorBuffer
         );  
         //传送顶点法向量数据
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
         //启用顶点颜色数据
         GLES30.glEnableVertexAttribArray(maColorHandle);  
         //启用顶点法向量数据
         GLES30.glEnableVertexAttribArray(maNormalHandle);
         
         //绘制线条的粗细
         GLES30.glLineWidth(2);
         //绘制
         GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
    }
}

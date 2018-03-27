package com.bn.Sample8_6;

import static com.bn.Sample8_6.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import android.opengl.GLES30;

/*
 * 球体
 */
public class Ball 
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
    
    float bHalf=0;//黄金长方形的宽
    float r=0;//球的半径
    
    public Ball(MySurfaceView mv,float r,float[] colorValue)
    {
    	//调用初始化顶点数据的initVertexData方法
    	initVertexData(r,colorValue);
    	//调用初始化着色器的intShader方法
    	initShader(mv);
    }
    //自定义的初始化顶点数据的方法
    public void initVertexData(float r,float[] colorValue)
    {
    	//顶点坐标数据的初始化================begin============================
    	final float UNIT_SIZE=0.4f;
    	ArrayList<Float> alVertix=new ArrayList<Float>();//存放顶点坐标的ArrayList
    	final float angleSpan=15f;//将球进行单位切分的角度
        for(float vAngle=-90;vAngle<90;vAngle=vAngle+angleSpan)//垂直方向angleSpan度一份
        {
        	for(float hAngle=0;hAngle<=360;hAngle=hAngle+angleSpan)//水平方向angleSpan度一份
        	{//纵向横向各到一个角度后计算对应的此点在球面上的坐标    	
        		float x0=(float)(r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle))*Math.cos(Math.toRadians(hAngle)));
        		float y0=(float)(r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle))*Math.sin(Math.toRadians(hAngle)));
        		float z0=(float)(r*UNIT_SIZE*Math.sin(Math.toRadians(vAngle)));
        		
        		float x1=(float)(r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle))*Math.cos(Math.toRadians(hAngle+angleSpan)));
        		float y1=(float)(r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle))*Math.sin(Math.toRadians(hAngle+angleSpan)));
        		float z1=(float)(r*UNIT_SIZE*Math.sin(Math.toRadians(vAngle)));
        		
        		float x2=(float)(r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle+angleSpan))*Math.cos(Math.toRadians(hAngle+angleSpan)));
        		float y2=(float)(r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle+angleSpan))*Math.sin(Math.toRadians(hAngle+angleSpan)));
        		float z2=(float)(r*UNIT_SIZE*Math.sin(Math.toRadians(vAngle+angleSpan)));
        		
        		float x3=(float)(r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle+angleSpan))*Math.cos(Math.toRadians(hAngle)));
        		float y3=(float)(r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle+angleSpan))*Math.sin(Math.toRadians(hAngle)));
        		float z3=(float)(r*UNIT_SIZE*Math.sin(Math.toRadians(vAngle+angleSpan)));
        		
        		//将计算出来的XYZ坐标加入存放顶点坐标的ArrayList        		
        		alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);  
        		alVertix.add(x3);alVertix.add(y3);alVertix.add(z3);
        		alVertix.add(x0);alVertix.add(y0);alVertix.add(z0);
        		
        		alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);
        		alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
        		alVertix.add(x3);alVertix.add(y3);alVertix.add(z3);
        	}
        } 	
        vCount=alVertix.size()/3;//顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标
    	
        //将alVertix中的坐标值转存到一个float数组中
        float vertices[]=new float[vCount*3];
    	for(int i=0;i<alVertix.size();i++)
    	{
    		vertices[i]=alVertix.get(i);
    	}
		
        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为int型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        
        //顶点颜色数据的初始化
        float colors[]=new float[vCount*4];
        for(int i=0;i<vCount;i++){
        	colors[4*i]=colorValue[0];
        	colors[4*i+1]=colorValue[1];
        	colors[4*i+2]=colorValue[2];
        	colors[4*i+3]=colorValue[3];
        }
        /*
         * 在指定颜色时，发生了顶点颜色融合
         */
        //创建顶点着色数据缓冲
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
            synchronized (MatrixState.lightPositionFB_lock) { // hhl 保证不会在更新光源位置的同时 设置这个!!
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
                mVertexBuffer
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

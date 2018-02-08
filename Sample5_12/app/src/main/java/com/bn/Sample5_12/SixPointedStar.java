package com.bn.Sample5_12;
import static com.bn.Sample5_12.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import android.opengl.GLES30;
import android.opengl.Matrix;

//六角星
public class SixPointedStar 
{	
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用  
    int maColorHandle; //顶点颜色属性引用  
    String mVertexShader;	//顶点着色器代码脚本 
    String mFragmentShader;	//片元着色器代码脚本
    static float[] mMMatrix = new float[16];	//具体物体的3D变换矩阵，包括旋转、平移、缩放
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
    int vCount=0;    
    float yAngle=0;//绕y轴旋转的角度
    float xAngle=0;//绕z轴旋转的角度
    final float UNIT_SIZE=1;
    final float UNIT_COLOR=1;
    float color[]=new float[3];//五角星的颜色
    
    public SixPointedStar(MySurfaceView mv,float r,float R,float z,float[] color)
    {    	
    	this.color=color;//五角星的颜色
    	//调用初始化顶点数据的initVertexData方法
    	initVertexData(R,r,z);
    	//调用初始化着色器的intShader方法     
    	initShader(mv);
    }
    
 // 初始化顶点坐标数据的方法
    public void initVertexData(float R,float r,float z)
    {
		List<Float> flist=new ArrayList<Float>();
		float tempAngle=360/6;//平均角度值
		for(float angle=0;angle<360;angle+=tempAngle)
		{
			//第一个三角形
			//第一个中心点
			flist.add(0f);
			flist.add(0f);
			flist.add(z);
			//第二个点
			flist.add((float) (R*UNIT_SIZE*Math.cos(Math.toRadians(angle))));
			flist.add((float) (R*UNIT_SIZE*Math.sin(Math.toRadians(angle))));
			flist.add(z);
			//第三个点
			flist.add((float) (r*UNIT_SIZE*Math.cos(Math.toRadians(angle+tempAngle/2))));
			flist.add((float) (r*UNIT_SIZE*Math.sin(Math.toRadians(angle+tempAngle/2))));
			flist.add(z);
			
			//第二个三角形
			//第一个中心点
			flist.add(0f);
			flist.add(0f);
			flist.add(z);
			//第二个点
			flist.add((float) (r*UNIT_SIZE*Math.cos(Math.toRadians(angle+tempAngle/2))));
			flist.add((float) (r*UNIT_SIZE*Math.sin(Math.toRadians(angle+tempAngle/2))));
			flist.add(z);
			//第三个点
			flist.add((float) (R*UNIT_SIZE*Math.cos(Math.toRadians(angle+tempAngle))));
			flist.add((float) (R*UNIT_SIZE*Math.sin(Math.toRadians(angle+tempAngle))));
			flist.add(z);
		}
		vCount=flist.size()/3;//顶点个数
		float[] vertexArray=new float[flist.size()];//顶点坐标数组
		for(int i=0;i<vCount;i++)//循环遍历顶点坐标数组
		{
			vertexArray[i*3]=flist.get(i*3);//为顶点坐标数组赋值-x
			vertexArray[i*3+1]=flist.get(i*3+1);//为顶点坐标数组赋值-y
			vertexArray[i*3+2]=flist.get(i*3+2);//为顶点坐标数组赋值-z
		}
		ByteBuffer vbb=ByteBuffer.allocateDirect(vertexArray.length*4);
		vbb.order(ByteOrder.nativeOrder());	//设置字节顺序为本地操作系统顺序
		mVertexBuffer=vbb.asFloatBuffer();
		mVertexBuffer.put(vertexArray);//将顶点坐标数据放进缓冲
		mVertexBuffer.position(0);//设置缓冲起始位置
    }

   // 初始化着色器
    public void initShader(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());  
        //基于顶点着色器与片元着色器创建程序
        mProgram = createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点颜色属性引用id  
        maColorHandle= GLES30.glGetAttribLocation(mProgram, "aColor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");  
    }
    //绘制方法
    public void drawSelf()
    {        
    	// 指定使用某套着色器程序
    	 GLES30.glUseProgram(mProgram);  
    	 //初始化变换矩阵
         Matrix.setRotateM(mMMatrix,0,0,0,1,0);
         //设置沿Z轴正向位移1
         Matrix.translateM(mMMatrix,0,0,0,1);
         //设置绕y轴旋转
         Matrix.rotateM(mMMatrix,0,yAngle,0,1,0);
         //设置绕z轴旋转
         Matrix.rotateM(mMMatrix,0,xAngle,1,0,0);  
         //将最终变换矩阵传入渲染管线
         GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(mMMatrix), 0);


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
         GLES30.glVertexAttrib4f(maColorHandle, color[0],color[1],color[2], 1.0f);
		// Mark.0 所有顶点的该顶点属性值都一样 避免拷贝
		// Mark.1 顶点属性变量是不能够是数组的 !!!  可以多个顶点 每个顶点对应一些非数组的属性
		//
		// Mark.2 glVertexAttrib[N]f[v] 与 glVertexAttribPointer 在shader中使用是一样 不用改shader的代码
		// Mark.3 glVertexAttrib[N]f[v] 只能用于 一个物体中 某些方面顶点属性(e.g颜色) 所有顶点都一样
		// Mark.4 glVertexAttrib4f 参数是float,float,float,float glVertexAttrib4fv 参数是byte[]或者ByteBuffer 但在shader中不是数组


       //启用顶点位置数据数组
         GLES30.glEnableVertexAttribArray(maPositionHandle);  
         //绘制六角星
         GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
    }
}

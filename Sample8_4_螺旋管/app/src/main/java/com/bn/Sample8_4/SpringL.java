package com.bn.Sample8_4;

import static com.bn.Sample8_4.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.opengl.GLES30;

/*
 * 圆环骨架类
 */
public class SpringL 
{	
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用
    int maColorHandle; //顶点颜色属性引用 
    
    String mVertexShader;//顶点着色器    	 
    String mFragmentShader;//片元着色器
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mColorBuffer;	//顶点颜色数据缓冲
	
    int vCount=0;   
    float xAngle=0;//绕x轴旋转的角度
    float yAngle=0;//绕y轴旋转的角度
    float zAngle=0;//绕z轴旋转的角度
    float h;
    
    public SpringL(MySurfaceView mv,
    		float rBig, float rSmall,
    		float h, float nCirclef, //高度，圈数
    		int nCol ,int nRow)
    {
    	this.h=h;
    	//调用初始化顶点数据的initVertexData方法
    	initVertexData(rBig,rSmall,h,nCirclef,nCol,nRow);
    	//调用初始化着色器的intShader方法
    	initShader(mv);
    }
    
    //自定义的初始化顶点数据的方法
    public void initVertexData(
			float rBig, float rSmall,//大半径，小半径
			float h, float nCirclef, //高度，圈数
			int nCol ,int nRow) {//列数，行数
		//成员变量初始化
		float angdegTotal=nCirclef*360.0f;//总度数
		float angdegColSpan=360.0f/nCol;
		float angdegRowSpan=angdegTotal/nRow;
		float A=(rBig-rSmall)/2;//用于旋转的小圆半径
		float D=rSmall+A;//旋转轨迹形成的大圆周半径
		vCount=3*nCol*nRow*2;//顶点个数，共有nColumn*nRow*2个三角形，每个三角形都有三个顶点
		//坐标数据初始化
		ArrayList<Float> alVertix=new ArrayList<Float>();//原顶点列表（未卷绕）
		ArrayList<Integer> alFaceIndex=new ArrayList<Integer>();//组织成面的顶点的索引值列表（按逆时针卷绕）
		
		//顶点
		for(float angdegCol=0;Math.ceil(angdegCol)<360+angdegColSpan;angdegCol+=angdegColSpan)
		{
			double a=Math.toRadians(angdegCol);//当前小圆周弧度
			for(float angdegRow=0;Math.ceil(angdegRow)<angdegTotal+angdegRowSpan;angdegRow+=angdegRowSpan)//重复了一列顶点，方便了索引的计算
			{
				float yVec=(angdegRow/angdegTotal)*h;//根据旋转角度增加y的值
				double u=Math.toRadians(angdegRow);//当前大圆周弧度
				float y=(float) (A*Math.cos(a));
				float x=(float) ((D+A*Math.sin(a))*Math.sin(u));
				float z=(float) ((D+A*Math.sin(a))*Math.cos(u));
				//将计算出来的XYZ坐标加入存放顶点坐标的ArrayList
        		alVertix.add(x); alVertix.add(y+yVec); alVertix.add(z);
			}
		}
		//索引
		for(int i=0;i<nCol;i++){
			for(int j=0;j<nRow;j++){
				int index=i*(nRow+1)+j;//当前索引
				//卷绕索引
				alFaceIndex.add(index+1);//下一列---1
				alFaceIndex.add(index+nRow+1);//下一列---2
				alFaceIndex.add(index+nRow+2);//下一行下一列---3
				
				alFaceIndex.add(index+1);//下一列---1
				alFaceIndex.add(index);//当前---0
				alFaceIndex.add(index+nRow+1);//下一列---2
			}
		}
		//计算卷绕顶点和平均法向量
		float[] vertices=new float[vCount*3];
		cullVertex(alVertix, alFaceIndex, vertices);
		
		//顶点坐标数据初始化
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);//创建顶点坐标数据缓冲
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        
        float[] colors=new float[vCount*4];//顶点颜色数组
		int Count=0;
		for(int i=0;i<vCount;i++)
		{
			colors[Count++]=1;	//r
			colors[Count++]=1;	//g
			colors[Count++]=1;	//b
			colors[Count++]=1;	//a
			
		}
        //创建顶点着色数据缓冲
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mColorBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mColorBuffer.put(colors);//向缓冲区中放入顶点着色数据
        mColorBuffer.position(0);//设置缓冲区起始位置
	}
    
	//通过原顶点和面的索引值，得到用顶点卷绕的数组
	public static void cullVertex(
			ArrayList<Float> alv,//原顶点列表（未卷绕）
			ArrayList<Integer> alFaceIndex,//组织成面的顶点的索引值列表（按逆时针卷绕）
			float[] vertices//用顶点卷绕的数组（顶点结果放入该数组中，数组长度应等于索引列表长度的3倍）
		){
		//生成顶点的数组
		int vCount=0;
		for(int i:alFaceIndex){
			vertices[vCount++]=alv.get(3*i);
			vertices[vCount++]=alv.get(3*i+1);
			vertices[vCount++]=alv.get(3*i+2);
		}
	}
    //初始化着色器
    public void initShader(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex_color.sh", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_color.sh", mv.getResources());  
        //基于顶点着色器与片元着色器创建程序
        mProgram = createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点颜色属性引用id  
        maColorHandle= GLES30.glGetAttribLocation(mProgram, "aColor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");  
    }
    
    public void drawSelf()
    {     
   	 	MatrixState.rotate(xAngle, 1, 0, 0);
   	 	MatrixState.rotate(yAngle, 0, 1, 0);
   	 	MatrixState.rotate(zAngle, 0, 0, 1);
   	 	
		MatrixState.pushMatrix();
    	MatrixState.translate(0, -h/2, 0);
   	 	
    	 //制定使用某套shader程序
    	 GLES30.glUseProgram(mProgram);        
         //将最终变换矩阵传入shader程序
         GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
         
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
         
         //启用顶点位置数据
         GLES30.glEnableVertexAttribArray(maPositionHandle);
         //启用顶点颜色数据
         GLES30.glEnableVertexAttribArray(maColorHandle);  
         
         //绘制线条的粗细
         GLES30.glLineWidth(2);
         //绘制
         GLES30.glDrawArrays(GLES30.GL_LINES, 0, vCount); 
         
         MatrixState.popMatrix();
         
    }
}

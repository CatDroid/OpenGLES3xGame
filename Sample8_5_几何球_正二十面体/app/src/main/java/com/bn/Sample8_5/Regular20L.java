package com.bn.Sample8_5;

import static com.bn.Sample8_5.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.opengl.GLES30;

/*
 * 正二十面体
 * 基于三个互相垂直的黄金长方形
 */
public class Regular20L 
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
    float xAngle=0;//绕x轴旋转的角度
    float yAngle=0;//绕y轴旋转的角度
    float zAngle=0;//绕z轴旋转的角度
    
    float bHalf=0;//黄金长方形的宽
    float r=0;//球的半径
    
    public Regular20L(MySurfaceView mv,float scale,float aHalf,int n)
    {
    	//调用初始化顶点数据的initVertexData方法
    	initVertexData(scale,aHalf,n);
    	//调用初始化着色器的intShader方法
    	initShader(mv);
    }
    
    //自定义的初始化顶点数据的方法
    public void initVertexData(float scale, float aHalf, int n) //大小，黄金长方形的长的一半，分段数
	{
		aHalf*=scale;
		//初始化成员变量
		bHalf=aHalf*0.618034f;
		r=(float) Math.sqrt(aHalf*aHalf+bHalf*bHalf);
		vCount=3*20*n*n;//顶点个数，共有20个三角形，每个三角形都有三个顶点
		//正20面体坐标数据初始化
		ArrayList<Float> alVertix20=new ArrayList<Float>();//正20面体的顶点列表（未卷绕）
		ArrayList<Integer> alFaceIndex20=new ArrayList<Integer>();//正20面体组织成面的顶点的索引值列表（按逆时针卷绕）
		//正20面体顶点
		alVertix20.add(0f); alVertix20.add(aHalf); alVertix20.add(-bHalf);//顶正棱锥顶点
		
		alVertix20.add(0f); alVertix20.add(aHalf); alVertix20.add(bHalf);//棱柱上的点
		alVertix20.add(aHalf); alVertix20.add(bHalf); alVertix20.add(0f);
		alVertix20.add(bHalf); alVertix20.add(0f); alVertix20.add(-aHalf);
		alVertix20.add(-bHalf); alVertix20.add(0f); alVertix20.add(-aHalf);
		alVertix20.add(-aHalf); alVertix20.add(bHalf); alVertix20.add(0f);
		
		alVertix20.add(-bHalf); alVertix20.add(0f); alVertix20.add(aHalf);
		alVertix20.add(bHalf); alVertix20.add(0f); alVertix20.add(aHalf);
		alVertix20.add(aHalf); alVertix20.add(-bHalf); alVertix20.add(0f);
		alVertix20.add(0f); alVertix20.add(-aHalf); alVertix20.add(-bHalf);
		alVertix20.add(-aHalf); alVertix20.add(-bHalf); alVertix20.add(0f);
		
		alVertix20.add(0f); alVertix20.add(-aHalf); alVertix20.add(bHalf);//底棱锥顶点
		//正20面体索引
		alFaceIndex20.add(0); alFaceIndex20.add(1); alFaceIndex20.add(2);
		alFaceIndex20.add(0); alFaceIndex20.add(2); alFaceIndex20.add(3);
		alFaceIndex20.add(0); alFaceIndex20.add(3); alFaceIndex20.add(4);
		alFaceIndex20.add(0); alFaceIndex20.add(4); alFaceIndex20.add(5);
		alFaceIndex20.add(0); alFaceIndex20.add(5); alFaceIndex20.add(1);
		
		alFaceIndex20.add(1); alFaceIndex20.add(6); alFaceIndex20.add(7);
		alFaceIndex20.add(1); alFaceIndex20.add(7); alFaceIndex20.add(2);
		alFaceIndex20.add(2); alFaceIndex20.add(7); alFaceIndex20.add(8);
		alFaceIndex20.add(2); alFaceIndex20.add(8); alFaceIndex20.add(3);
		alFaceIndex20.add(3); alFaceIndex20.add(8); alFaceIndex20.add(9);
		alFaceIndex20.add(3); alFaceIndex20.add(9); alFaceIndex20.add(4);
		alFaceIndex20.add(4); alFaceIndex20.add(9); alFaceIndex20.add(10);
		alFaceIndex20.add(4); alFaceIndex20.add(10); alFaceIndex20.add(5);
		alFaceIndex20.add(5); alFaceIndex20.add(10); alFaceIndex20.add(6);
		alFaceIndex20.add(5); alFaceIndex20.add(6); alFaceIndex20.add(1);
		
		alFaceIndex20.add(6); alFaceIndex20.add(11); alFaceIndex20.add(7);
		alFaceIndex20.add(7); alFaceIndex20.add(11); alFaceIndex20.add(8);
		alFaceIndex20.add(8); alFaceIndex20.add(11); alFaceIndex20.add(9);
		alFaceIndex20.add(9); alFaceIndex20.add(11); alFaceIndex20.add(10);
		alFaceIndex20.add(10); alFaceIndex20.add(11); alFaceIndex20.add(6);
		//计算卷绕顶点
		float[] vertices20=VectorUtil.cullVertex(alVertix20, alFaceIndex20);//只计算顶点

		//坐标数据初始化
		ArrayList<Float> alVertix=new ArrayList<Float>();//原顶点列表（未卷绕）
		ArrayList<Integer> alFaceIndex=new ArrayList<Integer>();//组织成面的顶点的索引值列表（按逆时针卷绕）
		int vnCount=0;//前i-1行前所有顶点数的和
		for(int k=0;k<vertices20.length;k+=9)//对正20面体每个大三角形循环
		{
			float [] v1=new float[]{vertices20[k+0], vertices20[k+1], vertices20[k+2]};
			float [] v2=new float[]{vertices20[k+3], vertices20[k+4], vertices20[k+5]};
			float [] v3=new float[]{vertices20[k+6], vertices20[k+7], vertices20[k+8]};
			//顶点
			for(int i=0;i<=n;i++)
			{
				float[] viStart=VectorUtil.devideBall(r, v1, v2, n, i);
				float[] viEnd=VectorUtil.devideBall(r, v1, v3, n, i);
				for(int j=0;j<=i;j++)
				{
					float[] vi=VectorUtil.devideBall(r, viStart, viEnd, i, j);
					alVertix.add(vi[0]); alVertix.add(vi[1]); alVertix.add(vi[2]);
				}
			}
			//索引
			for(int i=0;i<n;i++)
			{
				if(i==0){//若是第0行，直接加入卷绕后顶点索引012
					alFaceIndex.add(vnCount+0); alFaceIndex.add(vnCount+1);alFaceIndex.add(vnCount+2);
					vnCount+=1;
					if(i==n-1){//如果是每个大三角形的最后一次循环，将下一列的顶点个数也加上
						vnCount+=2;
					}
					continue;
				}
				int iStart=vnCount;//第i行开始的索引
				int viCount=i+1;//第i行顶点数
				int iEnd=iStart+viCount-1;//第i行结束索引
				
				int iStartNext=iStart+viCount;//第i+1行开始的索引
				int viCountNext=viCount+1;//第i+1行顶点数
				int iEndNext=iStartNext+viCountNext-1;//第i+1行结束的索引
				//前面的四边形
				for(int j=0;j<viCount-1;j++)
				{
					int index0=iStart+j;//四边形的四个顶点索引
					int index1=index0+1;
					int index2=iStartNext+j;
					int index3=index2+1;
					alFaceIndex.add(index0); alFaceIndex.add(index2);alFaceIndex.add(index3);//加入前面的四边形
					alFaceIndex.add(index0); alFaceIndex.add(index3);alFaceIndex.add(index1);				
				}// j
				alFaceIndex.add(iEnd); alFaceIndex.add(iEndNext-1);alFaceIndex.add(iEndNext); //最后一个三角形
				vnCount+=viCount;//第i行前所有顶点数的和
				if(i==n-1){//如果是每个大三角形的最后一次循环，将下一列的顶点个数也加上
					vnCount+=viCountNext;
				}
			}// i
		}
		
		//计算卷绕顶点
		float[] vertices=VectorUtil.cullVertex(alVertix, alFaceIndex);//只计算顶点
		float[] normals=vertices;//顶点就是法向量
		
		//顶点坐标数据初始化
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);//创建顶点坐标数据缓冲
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //法向量数据初始化  
        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length*4);//创建顶点法向量数据缓冲
        nbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mNormalBuffer = nbb.asFloatBuffer();//转换为float型缓冲
        mNormalBuffer.put(normals);//向缓冲区中放入顶点法向量数据
        mNormalBuffer.position(0);//设置缓冲区起始位置
        
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
    	
   	 	MatrixState.rotate(xAngle, 1, 0, 0);
   	 	MatrixState.rotate(yAngle, 0, 1, 0);
   	 	MatrixState.rotate(zAngle, 0, 0, 1);
    	
    	 //制定使用某套shader程序
    	 GLES30.glUseProgram(mProgram);        
         
         //将最终变换矩阵传入shader程序
         GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
         
         //将位置、旋转变换矩阵传入shader程序
         GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0); 
         //将摄像机位置传入shader程序   
         GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
         //将光源位置传入shader程序   
         GLES30.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
         
         
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
         GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, vCount); 
    }
}

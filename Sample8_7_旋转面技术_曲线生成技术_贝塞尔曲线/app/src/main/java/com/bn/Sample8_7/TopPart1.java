package com.bn.Sample8_7;

import static com.bn.Sample8_7.ShaderUtil.createProgram;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import android.opengl.GLES30;
/*
 * 泰姬陵顶部组建1
 */
public class TopPart1 {	
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用
    int maTexCoorHandle; //顶点纹理坐标属性引用
    
    String mVertexShader;//顶点着色器代码脚本  	 
    String mFragmentShader;//片元着色器代码脚本
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲
	
    int vCount=0;   
    float xAngle=0;//绕x轴旋转的角度
    float yAngle=0;//绕y轴旋转的角度
    float zAngle=0;//绕z轴旋转的角度
    
    float scale;
    
    public TopPart1(MySurfaceView mv,float scale, int nCol ,int nRow)
    {
    	this.scale=scale;
    	//调用初始化顶点数据的initVertexData方法
    	initVertexData(scale,nCol,nRow);
    	//调用初始化着色器的intShader方法
    	initShader(mv);
    }
    
  //初始化顶点数据的方法
    public void initVertexData(float scale, int nCol ,int nRow 
			){
		
		float angdegSpan=360.0f/nCol;
		vCount=3*nCol*nRow*2;//顶点个数，共有nColumn*nRow*2个三角形，每个三角形都有三个顶点(会有重复的，也计算在内 一个有nCOl*nRow个长方形 *2个三角形 *3个顶点)
		
		ArrayList<Float> alVertix=new ArrayList<Float>();//原顶点列表（未卷绕）
		ArrayList<Integer> alFaceIndex=new ArrayList<Integer>();//组织成面的顶点的编号值列表
		
		//以下是贝赛尔曲线的实现代码
		BezierUtil.al.clear();//清空控制点列表

		//加入数据点   // hhl 中央的塔顶由4部分组成 分别是TopPart1 TopPort2 TopPart3 TopPart4 这几部分除了贝塞尔曲线不一样 其他代码和逻辑都一样
		BezierUtil.al.add(new BNPosition(-1, 171));//控制点坐标数据1
		BezierUtil.al.add(new BNPosition(14, 191));//控制点坐标数据2
		BezierUtil.al.add(new BNPosition(17, 183));//控制点坐标数据3
		BezierUtil.al.add(new BNPosition(5, 154));	//控制点坐标数据4	
		BezierUtil.al.add(new BNPosition(31, 274));//控制点坐标数据5
		BezierUtil.al.add(new BNPosition(32, 243));	//控制点坐标数据6	
		BezierUtil.al.add(new BNPosition(30, 230));	//控制点坐标数据7	
		BezierUtil.al.add(new BNPosition(0, 253));//控制点坐标数据8
		
		
		//根据控制点生成贝赛尔曲线上点的列表
		ArrayList<BNPosition> alCurve=BezierUtil.getBezierData(1.0f/nRow);
		
		for(int i=0;i<nRow+1;i++)
		{//根据得到的曲线上点的列表生成旋转面上点的坐标列表
			double r=alCurve.get(i).x*Constant.DATA_RATIO*scale;	//当前圆的半径
			float y=alCurve.get(i).y*Constant.DATA_RATIO*scale;//当前y值
			for(float angdeg=0;Math.ceil(angdeg)<360+angdegSpan;angdeg+=angdegSpan)//重复了一列顶点，方便了索引的计算
			{
				double angrad=Math.toRadians(angdeg);//当前弧度
				float x=(float) (-r*Math.sin(angrad));//计算顶点的坐标值
				float z=(float) (-r*Math.cos(angrad));//计算顶点的坐标值
				
				alVertix.add(x); alVertix.add(y); alVertix.add(z);//将计算出来的XYZ坐标加入存放顶点坐标的ArrayList
			}
		}
		
		for(int i=0;i<nRow;i++){//通过循环计算出卷绕成三角形的
			for(int j=0;j<nCol;j++){//顶点编号列表
				int index=i*(nCol+1)+j;//当前四边形第一个顶点的编号   hhl 每一行都是 nCol+1 个的顶点 因为最后一个顶点不会用第0号顶点代替
				//卷绕索引
				alFaceIndex.add(index+1);		//第一个三角形1号点编号
				alFaceIndex.add(index+nCol+2);	//第一个三角形2号点编号
				alFaceIndex.add(index+nCol+1);	//第一个三角形3号点编号
				
				alFaceIndex.add(index+1);		//第二个三角形1号点编号
				alFaceIndex.add(index+nCol+1);	//第二个三角形2号点编号
				alFaceIndex.add(index);			//第二个三角形3号点编号

				/* hhl 注意生成顶点的方向是这样的 , 所以卷绕方向如下就是逆时针了

				*            index+1	  index (小的在这边)
				*             |      ^
				*             |       \
				*            \/        \
				*   index+nCol+1 +1 ---> index+nCol+1
				*
				* */
			}
		}
		
		float[] vertices = VectorUtil.calVertices(alVertix, alFaceIndex); //卷绕成三角形的顶点坐标数组
		
		//顶点坐标数据初始化
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);
        
		//纹理
		ArrayList<Float> alST=new ArrayList<Float>();//顶点的纹理坐标列表

		float yMin=999999999;//y最小值
		float yMax=0;//y最大值
		for(BNPosition pos:alCurve){
			yMin=Math.min(yMin, pos.y);//y最小值   	// hhl  曲线或者旋转体的高度
			yMax=Math.max(yMax, pos.y);//y最大值
		}
		for(int i=0;i<nRow+1;i++) 					// hhl 由于使用同一个 卷绕的索引数组 alFaceIndex  所以纹理坐标生成的方向要跟顶点坐标的一样 目前都是一行行的
		{
			float y=alCurve.get(i).y;//当前y值
			float t=1-(y-yMin)/(yMax-yMin);//t坐标	// 旋转体高度 平分到 纹理t轴上
			for(float angdeg=0;Math.ceil(angdeg)<360+angdegSpan;angdeg+=angdegSpan)	// hhl 这个跟旋转体 同个高度上的顶点
			{
				float s=angdeg/360;//s坐标
				
				alST.add(s); alST.add(t);//将计算出来的S、T纹理坐标加入列表
			}
		}
		//计算卷绕后纹理坐标
		float[] textures=VectorUtil.calTextures(alST, alFaceIndex);


        ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length*4);//创建顶点纹理数据缓冲
        tbb.order(ByteOrder.nativeOrder());
        mTexCoorBuffer = tbb.asFloatBuffer();
        mTexCoorBuffer.put(textures);
        mTexCoorBuffer.position(0);
	}
    
    //自定义初始化着色器initShader方法
    public void initShader(MySurfaceView mv)
    {
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex_tex.sh", mv.getResources());
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_tex.sh", mv.getResources());
        mProgram = createProgram(mVertexShader, mFragmentShader);
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        maTexCoorHandle= GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix"); 
    }
    
    public void drawSelf(int texId)
    {
		MatrixState.rotate(xAngle, 1, 0, 0);
		MatrixState.rotate(yAngle, 0, 1, 0);
		MatrixState.rotate(zAngle, 0, 0, 1);

		GLES30.glUseProgram(mProgram);  // 使用某套shader程序
		GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);// 将最终变换矩阵传入shader程序

		GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3*4, mVertexBuffer); // 传送顶点位置数据
		GLES30.glVertexAttribPointer(maTexCoorHandle, 2, GLES30.GL_FLOAT, false, 2*4, mTexCoorBuffer); // 传送顶点纹理坐标数据

		GLES30.glEnableVertexAttribArray(maPositionHandle); // 启用顶点位置数据
		GLES30.glEnableVertexAttribArray(maTexCoorHandle);  // 启用顶点纹理数据

		GLES30.glActiveTexture(GLES30.GL_TEXTURE0);// 绑定纹理
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);

		GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);// 绘制纹理矩形
    }
}

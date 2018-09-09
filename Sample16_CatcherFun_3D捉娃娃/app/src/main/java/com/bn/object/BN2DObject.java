package com.bn.object;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import com.bn.MatrixState.MatrixState2D;
import com.bn.constant.Constant;
import com.bn.constant.SourceConstant;

import android.opengl.GLES30;
import static com.bn.constant.SourceConstant.*;
public class BN2DObject
{
	public FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
	public FloatBuffer mTexCoorBuffer;//顶点纹理坐标数据缓冲
    int muMVPMatrixHandle;//总变换矩阵引用id
    int maPositionHandle;//顶点位置属性引用id  
    int maTexCoorHandle;//顶点纹理坐标属性引用id
    int CLStepHandle;//改变透明度属性引用id
    int xHandle;
    
    int programId;//自定义渲染管线程序id
	int texId;//纹理图片名
	int vCount;//顶点个数
    boolean initFlag=false;//判断是否初始化着色器  
    float x;//需要平移的x坐标
	float y;//需要平移的y坐标
	boolean isLoad=false;
	
	int han=0;
	int lie=0;
	int HZ;
	int LZ;
	int muSjFactor;//衰减因子引用id
	int count=0;
	int spng=0;
	public BN2DObject(float x,float y,float picWidth,float picHeight,int texId,int programId)
	{
		// hhl 这里假定整个场景是1920x1080 所有的物体都是渲染到这个1920x1080的'画布'上
		// hhl  x和y是物体中心点位置  以1920x1080的左上角为原点

		this.x=Constant.fromScreenXToNearX(x);//将屏幕x转换成视口x坐标  hhl 转换成 中间为原点的 并且做了归一化(按高为最长边)
		this.y=Constant.fromScreenYToNearY(y);//将屏幕y转换成视口y坐标  hhl 后面draw时候用来做平移的
		this.texId=texId;
		this.programId=programId;
		initVertexData(picWidth,picHeight);//初始化顶点数据
	}
	public BN2DObject(float x,float y,float picWidth,float picHeight,int texId,int programId,int spng)
	{
		this.spng=spng;
		this.x=Constant.fromScreenXToNearX(x);//将屏幕x转换成视口x坐标
		this.y=Constant.fromScreenYToNearY(y);//将屏幕y转换成视口y坐标
		this.texId=texId;
		this.programId=programId;
		initVertexData(picWidth,picHeight);//初始化顶点数据
	}
	public BN2DObject(
			float x,float y,float width,float height,
			int han,int lie,
			int HZ,int LZ,
			int texId,int programId)
	{//这是一个loadView中的图片的new
		this.x=Constant.fromScreenXToNearX(x);//将屏幕x转换成视口x坐标
		this.y=Constant.fromScreenYToNearY(y);//将屏幕y转换成视口y坐标
		isLoad=true;
		this.HZ=HZ;
		this.LZ=LZ;
		this.han=han;
		this.lie=lie;
		this.texId=texId;
		this.programId=programId;
		initVertexData(width,height);//初始化顶点数据
	}
	
	public void initVertexData(float width,float height)//初始化顶点数据
	{
		vCount=4;//顶点个数
		width=Constant.fromPixSizeToNearSize(width);//屏幕宽度转换成视口宽度
		height=Constant.fromPixSizeToNearSize(height);//屏幕高度转换成视口高度

		//初始化顶点坐标数据
		float vertices[]=new float[] // hhl 也是做了归一化 按高为最长边
		{
				-width/2,height/2,0,
				-width/2,-height/2,0,
				width/2,height/2,0,
				width/2,-height/2,0
		};
		ByteBuffer vbb=ByteBuffer.allocateDirect(vertices.length*4);//创建顶点坐标数据缓冲
		vbb.order(ByteOrder.nativeOrder());//设置字节顺序
		mVertexBuffer=vbb.asFloatBuffer();//转换为Float型缓冲
		mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
		mVertexBuffer.position(0);//设置缓冲区起始位置

		float[] texCoor=new float[12];//初始化纹理坐标数据
		//其他图形的纹理坐标
		if(!isLoad)
		{
			texCoor=new float[]{
					0,0,0,1,
					1,0,1,1,
					//1,0,0,1
			};
		}else
		{	// hhl 像load.png这样 一张纹理图中包含5x5的小图 连续图片 ，只需要显示纹理图中的其中一个
			//给出一副纹理图的行跟列，这里就在对应的纹理图的地方，计算出来相应的纹理坐标
			float sstep=(float)1/LZ;
			float tstep=(float)1/HZ;
			texCoor=new float[] // han 和 lie 从1开始 [1,5]
					{
					   sstep*han-sstep,	tstep*lie-tstep,
					   sstep*han-sstep,	tstep*lie,
					   sstep*han,		tstep*lie-tstep,
					   sstep*han,tstep*lie,

					   //sstep*han,tstep*lie-tstep,
					   //sstep*han-sstep,tstep*lie
					};
		}
		ByteBuffer cbb=ByteBuffer.allocateDirect(texCoor.length*4);
		cbb.order(ByteOrder.nativeOrder());
		mTexCoorBuffer=cbb.asFloatBuffer();
		mTexCoorBuffer.put(texCoor);
		mTexCoorBuffer.position(0);
	}


	//初始化着色器
	public void initShader()
	{
		// attribute 顶点属性 两个 顶点坐标和纹理坐标
		maPositionHandle = GLES30.glGetAttribLocation(programId, "aPosition");
		maTexCoorHandle= GLES30.glGetAttribLocation(programId, "aTexCoor");

		// uniform 程序总变换矩阵引用id
        muMVPMatrixHandle = GLES30.glGetUniformLocation(programId, "uMVPMatrix");
		// uniform 改变透明度的参数引用
        CLStepHandle=GLES30.glGetUniformLocation(programId, "CLStep");
        xHandle=GLES30.glGetUniformLocation(programId, "xPosition");
	}

	// 将屏幕y转换成视口y坐标
	public void setY(float y) {
		this.y=Constant.fromScreenYToNearY(y);
	}
	
	public void setX(float x) {
		this.x=Constant.fromScreenXToNearX(x);
	}


	public void drawSelf()	// 绘制图形
	{        
		if(!initFlag) { 	// 编译shader程序
    		initShader();
    		initFlag=true;
    	}

		MatrixState2D.pushMatrix();//保护场景

    	GLES30.glEnable(GLES30.GL_BLEND);// 打开混合
		GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA,GLES30.GL_ONE_MINUS_SRC_ALPHA);

    	GLES30.glUseProgram(programId);

		// BN2DObject的共同点 就是 更新stop loadPostion 和 translate scale rotate
    	GLES30.glUniform1f(CLStepHandle, step);
    	GLES30.glUniform1f(xHandle, loadPosition);

		MatrixState2D.translate(x,y, 0);// 平移 XOY平面 z是近远深度 x,y坐标可以在构造时传入 也可以setX setY更改

		// 根据实例化时候的配置，不同的2D-Object会先做以下缩放或者旋转(z轴),然后都会平移到各自指定位置(XOY平面)
		if(spng==1){
			// 控制缩放 如果不设置，为给定的width和height
			// scale 5~30 --> 0.05~0.3
			MatrixState2D.scale(SourceConstant.step/100,SourceConstant.step/100,SourceConstant.step/100);
			MatrixState2D.rotate(SourceConstant.AngleSpng, 0, 0, 1);
		}else if (spng==2){
			MatrixState2D.rotate(SourceConstant.Angle2D, 0, 0, 1);
		}else if (spng==3){
			MatrixState2D.rotate(SourceConstant.AngleSpng, 0, 0, 1);
		}else if (spng==4){
        	MatrixState2D.scale(SourceConstant.step/100,SourceConstant.step/100,SourceConstant.step/100);
        }


    	// 将最终变换矩阵传入shader程序
    	GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState2D.getFinalMatrix(), 0);

		// 更新顶点属性
    	GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3*4, mVertexBuffer);
    	GLES30.glVertexAttribPointer(maTexCoorHandle, 2, GLES30.GL_FLOAT, false, 2*4, mTexCoorBuffer);
    	GLES30.glEnableVertexAttribArray(maPositionHandle);  
    	GLES30.glEnableVertexAttribArray(maTexCoorHandle);  
    	
    	// 绑定纹理
    	GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
    	GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,texId);
    	
    	// 绘制纹理矩形--条带法
    	GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, vCount); 
    	
    	// 关闭混合
		GLES30.glDisable(GLES30.GL_BLEND);

		// 恢复场景
    	MatrixState2D.popMatrix();
	}


	// 这个draw只做平移 而且不是用构造时候传入的 ，而是每次draw时候传入
	public void drawSelf(float lx,float ly)
	{        
		if(!initFlag)
		{
			//初始化着色器        
			initShader();
			initFlag=true;
		}

		// 保护场景
		MatrixState2D.pushMatrix();

		// 打开混合 设置混合因子
		GLES30.glEnable(GLES30.GL_BLEND);
		GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA,GLES30.GL_ONE_MINUS_SRC_ALPHA);


		GLES30.glUseProgram(programId);
		

		lx=Constant.fromScreenXToNearX(lx);
		ly=Constant.fromScreenYToNearY(ly);
		MatrixState2D.translate(lx,ly, 0);// 只做 平移

		// 将最终变换矩阵传入shader程序
		GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState2D.getFinalMatrix(), 0);

		// 更新顶点属性
		// size 该顶点属性的组件数量，比如顶点坐标 组件数量是4，纹理坐标 组件数量是2，法向量 组件数量是4
		GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3*4, mVertexBuffer);
		GLES30.glVertexAttribPointer(maTexCoorHandle, 2, GLES30.GL_FLOAT, false, 2*4, mTexCoorBuffer);
		GLES30.glEnableVertexAttribArray(maPositionHandle);  
		GLES30.glEnableVertexAttribArray(maTexCoorHandle);  
		

		GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,texId);

		// 绘制纹理矩形--条带法
		GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, vCount);

		// 关闭混合
		GLES30.glDisable(GLES30.GL_BLEND);

		// 恢复场景
		MatrixState2D.popMatrix();
	}
}

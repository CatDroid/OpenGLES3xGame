package com.bn.object;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import com.bn.MatrixState.MatrixState2D;
import com.bn.constant.Constant;
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
		this.x=Constant.fromScreenXToNearX(x);//将屏幕x转换成视口x坐标
		this.y=Constant.fromScreenYToNearY(y);//将屏幕y转换成视口y坐标
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
	public BN2DObject(float x,float y,float width,float height,int han,int lie,int HZ,int LZ,
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
		float vertices[]=new float[]
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
					0,0,0,1,1,0,
					1,1,1,0,0,1};
		}else
		{
			//给出一副纹理图的行跟列，这里就在对应的纹理图的地方，计算出来相应的纹理坐标
			float sstep=(float)1/LZ;
			float tstep=(float)1/HZ;
			texCoor=new float[]
					{
					   sstep*han-sstep,tstep*lie-tstep,
					   sstep*han-sstep,tstep*lie,
					   sstep*han,tstep*lie-tstep,
					   
					   sstep*han,tstep*lie,
					   sstep*han,tstep*lie-tstep,
					   sstep*han-sstep,tstep*lie
					};
		}
		ByteBuffer cbb=ByteBuffer.allocateDirect(texCoor.length*4);//创建顶点纹理坐标数据缓冲
		cbb.order(ByteOrder.nativeOrder());//设置字节顺序
		mTexCoorBuffer=cbb.asFloatBuffer();//转换为Float型缓冲
		mTexCoorBuffer.put(texCoor);//向缓冲区中放入顶点着色数据
		mTexCoorBuffer.position(0);//设置缓冲区起始位置
	}
	//初始化着色器
	public void initShader()
	{
		//获取程序中顶点位置属性引用id  
		maPositionHandle = GLES30.glGetAttribLocation(programId, "aPosition");
		//获取程序中顶点纹理坐标属性引用id  
		maTexCoorHandle= GLES30.glGetAttribLocation(programId, "aTexCoor");
		//获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES30.glGetUniformLocation(programId, "uMVPMatrix");  
        CLStepHandle=GLES30.glGetUniformLocation(programId, "CLStep");//改变透明度的参数引用Id
        xHandle=GLES30.glGetUniformLocation(programId, "xPosition");//
	}
	public void setY(float y)
	{
		this.y=Constant.fromScreenYToNearY(y);//将屏幕y转换成视口y坐标
	}
	
	public void setX(float x)
	{
		this.x=Constant.fromScreenXToNearX(x);
	}
	//绘制图形
	public void drawSelf()
	{        
		if(!initFlag)
		{
			//初始化着色器        
    		initShader();
    		initFlag=true;
    	}
    	GLES30.glEnable(GLES30.GL_BLEND);//打开混合
//    	//设置混合因子
		GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA,GLES30.GL_ONE_MINUS_SRC_ALPHA);
    	//制定使用某套shader程序
    	GLES30.glUseProgram(programId);
    	GLES30.glUniform1f(CLStepHandle, step);
    	GLES30.glUniform1f(xHandle, loadPosition);
    	MatrixState2D.pushMatrix();//保护场景
		MatrixState2D.translate(x,y, 0);//平移
		if(spng==1){
			MatrixState2D.scale(step/100,step/100,step/100);
		}
		if(spng==1){
			MatrixState2D.rotate(AngleSpng, 0, 0, 1);
		}
		if(spng==2){
			MatrixState2D.rotate(Angle2D, 0, 0, 1);
		}
		if(spng==3){
			MatrixState2D.rotate(AngleSpng, 0, 0, 1);
		}
        if(spng==4){
        	MatrixState2D.scale(step/100,step/100,step/100);
        }
    	//将最终变换矩阵传入shader程序
    	GLES30.glUniformMatrix4fv
    	(
    			muMVPMatrixHandle, 
    			1, 
    			false, 
    			MatrixState2D.getFinalMatrix(), 
    			0
    	); 
    	
    	//为画笔指定顶点位置数据
    	GLES30.glVertexAttribPointer  
    	(
    			maPositionHandle,
    			3, 
    			GLES30.GL_FLOAT,
    			false,
    			3*4,
    			mVertexBuffer
    			);
    	//为画笔指定顶点纹理坐标数据
    	GLES30.glVertexAttribPointer
    	(
    			maTexCoorHandle,
    			2,
    			GLES30.GL_FLOAT,
    			false,
    			2*4,
    			mTexCoorBuffer
    			);   
    	//允许顶点位置数据数组
    	GLES30.glEnableVertexAttribArray(maPositionHandle);  
    	GLES30.glEnableVertexAttribArray(maTexCoorHandle);  
    	
    	//绑定纹理
    	GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
    	GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,texId);
    	
    	//绘制纹理矩形--条带法
    	GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, vCount); 
    	
    	//关闭混合

    	MatrixState2D.popMatrix();//恢复场景
    	GLES30.glDisable(GLES30.GL_BLEND);
    
    	
	}
	
	public void drawSelf(float lx,float ly)
	{        
		if(!initFlag)
		{
			//初始化着色器        
			initShader();
			initFlag=true;
		}
		GLES30.glEnable(GLES30.GL_BLEND);//打开混合
		//设置混合因子
		GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA,GLES30.GL_ONE_MINUS_SRC_ALPHA);
		//制定使用某套shader程序
		GLES30.glUseProgram(programId);
		
		MatrixState2D.pushMatrix();//保护场景
		lx=Constant.fromScreenXToNearX(lx);
		ly=Constant.fromScreenYToNearY(ly);
		MatrixState2D.translate(lx,ly, 0);//平移
		//将最终变换矩阵传入shader程序
		GLES30.glUniformMatrix4fv
		(
				muMVPMatrixHandle, 
				1, 
				false, 
				MatrixState2D.getFinalMatrix(), 
				0
				); 
		//为画笔指定顶点位置数据
		GLES30.glVertexAttribPointer  
		(
				maPositionHandle,
				3, 
				GLES30.GL_FLOAT,
				false,
				3*4,
				mVertexBuffer
				);
		//为画笔指定顶点纹理坐标数据
		GLES30.glVertexAttribPointer
		(
				maTexCoorHandle,
				2,
				GLES30.GL_FLOAT,
				false,
				2*4,
				mTexCoorBuffer
				);   
		//允许顶点位置数据数组
		GLES30.glEnableVertexAttribArray(maPositionHandle);  
		GLES30.glEnableVertexAttribArray(maTexCoorHandle);  
		
		//绑定纹理
		GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,texId);
		
		//绘制纹理矩形--条带法
		GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, vCount); 
		//关闭混合
		GLES30.glDisable(GLES30.GL_BLEND);
		MatrixState2D.popMatrix();//恢复场景
	}
}

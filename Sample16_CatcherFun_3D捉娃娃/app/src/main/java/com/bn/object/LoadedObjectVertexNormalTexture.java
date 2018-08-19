package com.bn.object;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import com.bn.MatrixState.MatrixState3D;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import static com.bn.constant.SourceConstant.*;
//加载后的物体——仅携带顶点信息，颜色随机
public class LoadedObjectVertexNormalTexture
{	
	int mProgram;//自定义渲染管线着色器程序id  
    int muMVPMatrixHandle;//总变换矩阵引用
    int muMMatrixHandle;//位置、旋转变换矩阵
    int maPositionHandle; //顶点位置属性引用  
    int maTexCoorHandle; //顶点纹理坐标属性引用  
    int vCount=0;  
	int mVertexBufferId;//顶点坐标数据缓冲 id
	int mTexCoorBufferId;//顶点纹理坐标数据缓冲id
	int vaoId=0;
    int SwitchcolorHandle;//这是holebox.obj变换颜色的值的参数引用
    public LoadedObjectVertexNormalTexture(GLSurfaceView mv,float[] vertices,float[] normals,float texCoors[],int programId)
    {    	
    	this.mProgram=programId;
    	//初始化shader        
    	initShader();
    	//初始化顶点坐标与着色数据
    	initVertexData(vertices,normals,texCoors);
    }
    
    //初始化顶点坐标与着色数据的方法
    public void initVertexData(float[] vertices,float[] normals,float texCoors[])
    {
    	//缓冲id数组
    	int[] buffIds=new int[2];
    	//生成3个缓冲id
    	GLES30.glGenBuffers(2, buffIds, 0);
    	//顶点坐标数据缓冲 id
    	mVertexBufferId=buffIds[0];
    	//顶点纹理坐标数据缓冲id
    	mTexCoorBufferId=buffIds[1];
    	
    	//顶点坐标数据的初始化================begin============================
    	vCount=vertices.length/3;   
		
    	//创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        FloatBuffer mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //绑定到顶点坐标数据缓冲 
    	GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,mVertexBufferId);
    	//向顶点坐标数据缓冲送入数据
    	GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertices.length*4, mVertexBuffer, GLES30.GL_STATIC_DRAW);    	
        //顶点坐标数据的初始化================end============================
        
        
        //顶点纹理坐标数据的初始化================begin============================  
        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoors.length*4);
        tbb.order(ByteOrder.nativeOrder());//设置字节顺序
        FloatBuffer mTexCoorBuffer = tbb.asFloatBuffer();//转换为Float型缓冲
        mTexCoorBuffer.put(texCoors);//向缓冲区中放入顶点纹理坐标数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //绑定到顶点纹理坐标数据缓冲
    	GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,mTexCoorBufferId);
    	//向顶点纹理坐标数据缓冲送入数据
    	GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, texCoors.length*4, mTexCoorBuffer, GLES30.GL_STATIC_DRAW);
    	//顶点纹理坐标数据的初始化================end============================
    	//绑定到系统默认缓冲
    	GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);    
    	initVAO();
    }
    public void initVAO()
    {
    	int[] vaoIds=new int[1];
      	//生成VAO
      	GLES30.glGenVertexArrays(1, vaoIds, 0);
      	vaoId=vaoIds[0];
    	//绑定VAO
      	GLES30.glBindVertexArray(vaoId);
         
         //启用顶点位置、纹理坐标数据
         GLES30.glEnableVertexAttribArray(maPositionHandle);  
         GLES30.glEnableVertexAttribArray(maTexCoorHandle); 
     	

         //绑定到顶点坐标数据缓冲 
     	 GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,mVertexBufferId); 
         //指定顶点位置数据     	 
         GLES30.glVertexAttribPointer  
         (
         		maPositionHandle,   
         		3, 
         		GLES30.GL_FLOAT, 
         		false,
                3*4,   
                0
         );     
         //绑定到顶点纹理坐标数据缓冲
     	 GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,mTexCoorBufferId);
         //指定顶点纹理坐标数据
         GLES30.glVertexAttribPointer  
         (
        		maTexCoorHandle, 
         		2, 
         		GLES30.GL_FLOAT, 
         		false,
                2*4,   
                0
         );
         // 绑定到系统默认缓冲
     	 GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);
       	GLES30.glBindVertexArray(0);
    }
    //初始化shader
    public void initShader()
    {
        //获取程序中顶点位置属性引用  
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用  
        maTexCoorHandle= GLES30.glGetAttribLocation(mProgram, "aTexCoor"); 
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix"); 
        SwitchcolorHandle=GLES30.glGetUniformLocation(mProgram, "ColorCS");//这是箱子的变换的参数值
    
    }
    
    public void drawSelf(int texId)
    {   	
    	//制定使用某套着色器程序
    	GLES30.glUseProgram(mProgram);
    	//将最终变换矩阵传入着色器程序
    	GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState3D.getFinalMatrix(), 0); 
    	//将位置、旋转变换矩阵传入着色器程序
    	GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState3D.getMMatrix(), 0);  
    	GLES30.glUniform1f(SwitchcolorHandle, ColorCS);
    	GLES30.glBindVertexArray(vaoId);
    	
    	
    	//绑定纹理
    	GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
    	GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
    	//绘制加载的物体
    	GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
    	GLES30.glBindVertexArray(0);
         
    }
}

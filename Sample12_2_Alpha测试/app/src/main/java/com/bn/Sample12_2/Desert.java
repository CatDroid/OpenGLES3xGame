package com.bn.Sample12_2;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES30;
import static com.bn.Sample12_2.MySurfaceView.*;

//表示沙漠的纹理矩形
public class Desert 
{	
	int mProgram;//自定义渲染管线程序id
    int muMVPMatrixHandle;//总变换矩阵引用id
    int maPositionHandle; //顶点位置属性引用id  
    int maTexCoorHandle; //顶点纹理坐标属性引用id  
    String mVertexShader;//顶点着色器
    String mFragmentShader;//片元着色器
    static float[] mMMatrix = new float[16];//具体物体的移动旋转矩阵
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲
    int vCount=0;   
    
    public Desert(MySurfaceView mv,float[] texCoor,int width,int height)
    {    	
    	//初始化顶点数据
    	initVertexData(texCoor,width,height);
    	//初始化着色器         
    	initShader(mv);
    }
    
    //初始化顶点数据
    public void initVertexData(float[] texCoor,int width,int height)
    {
        vCount=6;
        float vertices[]=new float[]
        {
    		-UNIT_SIZE*width,0,-UNIT_SIZE*height,
    		UNIT_SIZE*width,0,-UNIT_SIZE*height,
    		UNIT_SIZE*width,0,UNIT_SIZE*height,
    		
    		-UNIT_SIZE*width,0,-UNIT_SIZE*height,
    		UNIT_SIZE*width,0,UNIT_SIZE*height,
    		-UNIT_SIZE*width,0,UNIT_SIZE*height
        };
        //创建顶点坐标数据缓冲
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        
        //创建顶点纹理坐标数据缓冲
        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTexCoorBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mTexCoorBuffer.put(texCoor);//向缓冲区中放入顶点着色数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
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
        //获取程序中顶点纹理坐标属性引用id  
        maTexCoorHandle= GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");  
    }
    
    public void drawSelf(int texId)
    {        
    	 //指定使用某套着色器程序
    	 GLES30.glUseProgram(mProgram); 
         //将最终变换矩阵传入渲染管线
         GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
         //将顶点位置数据传入渲染管线
         GLES30.glVertexAttribPointer  
         (
         		maPositionHandle,   
         		3, 
         		GLES30.GL_FLOAT, 
         		false,
                3*4,   
                mVertexBuffer
         );       
         //将纹理坐标数据传入渲染管线
         GLES30.glVertexAttribPointer  
         (
        		maTexCoorHandle, 
         		2, 
         		GLES30.GL_FLOAT, 
         		false,
                2*4,   
                mTexCoorBuffer
         );   
         //启用顶点位置、纹理坐标数据数组
         GLES30.glEnableVertexAttribArray(maPositionHandle);  
         GLES30.glEnableVertexAttribArray(maTexCoorHandle);  
         //绑定纹理
         GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
         GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
         //绘制纹理矩形
         GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
    }
}
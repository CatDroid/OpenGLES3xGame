package com.bn.Sample2_1;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.annotation.SuppressLint;
import android.opengl.GLES30;

//有波浪效果的纹理矩形
public class TextureRect 
{	
	int[] mPrograms=new int[3];//自定义渲染管线着色器程序id
    int[] muMVPMatrixHandle=new int[3];//总变换矩阵引用
    int[] maPositionHandle=new int[3]; //顶点位置属性引用  
    int[] maTexCoorHandle=new int[3]; //顶点纹理坐标属性引用  
    int[] maStartAngleHandle=new int[3]; //本帧起始角度属性引用
    int[] muWidthSpanHandle=new int[3];//横向长度总跨度引用    
    int currIndex=0;//当前着色器索引
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲
    int vCount=0;   
    final float WIDTH_SPAN=3.3f;//2.8f;//横向长度总跨度
    float currStartAngle=0;//当前帧的起始角度0~2PI
    
    public TextureRect(MySurfaceView mv)
    {    	
    	//初始化顶点坐标与着色数据
    	initVertexData();
    	//初始化shader        
    	initShader(mv,0,"vertex_tex_x.sh");
    	initShader(mv,1,"vertex_tex_xie.sh");
    	initShader(mv,2,"vertex_tex_xy.sh");
    	//启动一个线程定时换帧
    	new Thread()
    	{
    		public void run()
    		{
    			while(Constant.threadFlag)
    			{
    				currStartAngle+=(float) (Math.PI/16);
        			try 
        			{
    					Thread.sleep(50); // w =  Math.PI/16 / 0.05
    				} catch (InterruptedException e) 
    				{
    					e.printStackTrace();
    				}
    			}     
    		}    
    	}.start();  
    }
    //初始化顶点坐标与着色数据的方法
    public void initVertexData()
    {
    	final int cols = 12;			// 列数
    	final int rows = cols*3/4;		// 行数
										// hhl 这样物体长宽就有比例了,因为下面UNIT_SIZE按照cols来定的
										// 		相当于长WIDTH_SPAN 高WIDTH_SPAN*3/4
										//		跟纹理图坐标 1
    	final float UNIT_SIZE = WIDTH_SPAN/cols;// 每格的单位长度
    	// 顶点坐标数据的初始化================begin============================
    	vCount=cols*rows*6;						// 每个格子两个三角形，每个三角形3个顶点
        float vertices[]=new float[vCount*3];	// 每个顶点xyz三个坐标
        int count=0;//顶点计数器
        for(int j=0;j<rows;j++)
        {
        	for(int i=0;i<cols;i++)
        	{        		
        		//计算当前格子左上侧点坐标 
        		float zsx=-UNIT_SIZE*cols/2+i*UNIT_SIZE; // 矩形物体的中心点在原点
        		float zsy=UNIT_SIZE*rows/2-j*UNIT_SIZE;
        		float zsz=0;
       
        		vertices[count++]=zsx;
        		vertices[count++]=zsy;
        		vertices[count++]=zsz;
        		
        		vertices[count++]=zsx;
        		vertices[count++]=zsy-UNIT_SIZE;
        		vertices[count++]=zsz;
        		
        		vertices[count++]=zsx+UNIT_SIZE;
        		vertices[count++]=zsy;
        		vertices[count++]=zsz;
        		
        		vertices[count++]=zsx+UNIT_SIZE;
        		vertices[count++]=zsy;
        		vertices[count++]=zsz;
        		
        		vertices[count++]=zsx;
        		vertices[count++]=zsy-UNIT_SIZE;
        		vertices[count++]=zsz;
        		        		
        		vertices[count++]=zsx+UNIT_SIZE;
        		vertices[count++]=zsy-UNIT_SIZE;
        		vertices[count++]=zsz; 
        	}
        }
        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //顶点纹理坐标数据的初始化================begin============================
        float texCoor[]=generateTexCoor(cols,rows);     
        //创建顶点纹理坐标数据缓冲
        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTexCoorBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mTexCoorBuffer.put(texCoor);//向缓冲区中放入顶点着色数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
    }
    //初始化shader
    @SuppressLint("NewApi")
	public void initShader(MySurfaceView mv,int index,String vertexName)
    {
    	//加载顶点着色器的脚本内容
        String mVertexShader=ShaderUtil.loadFromAssetsFile(vertexName, mv.getResources());
        //加载片元着色器的脚本内容
        String mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_tex.sh", mv.getResources());  
        //基于顶点着色器与片元着色器创建程序
        mPrograms[index] = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用  
        maPositionHandle[index] = GLES30.glGetAttribLocation(mPrograms[index], "aPosition");
        //获取程序中顶点纹理坐标属性引用  
        maTexCoorHandle[index]= GLES30.glGetAttribLocation(mPrograms[index], "aTexCoor");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle[index] = GLES30.glGetUniformLocation(mPrograms[index], "uMVPMatrix");  
        //获取本帧起始角度属性引用
        maStartAngleHandle[index]=GLES30.glGetUniformLocation(mPrograms[index], "uStartAngle");  
        //获取横向长度总跨度引用
        muWidthSpanHandle[index]=GLES30.glGetUniformLocation(mPrograms[index], "uWidthSpan");  
    }
    @SuppressLint("NewApi")
	public void drawSelf(int texId)
    {        
    	 //制定使用某套shader程序
    	 GLES30.glUseProgram(mPrograms[currIndex]); 
         //将最终变换矩阵传入shader程序
         GLES30.glUniformMatrix4fv(muMVPMatrixHandle[currIndex], 1, false, MatrixState.getFinalMatrix(), 0); 
         //将本帧起始角度传入shader程序
         GLES30.glUniform1f(maStartAngleHandle[currIndex], currStartAngle); // 每次只更新 最开头那一列的角度
         //将横向长度总跨度传入shader程序
         GLES30.glUniform1f(muWidthSpanHandle[currIndex], WIDTH_SPAN);  	// 旗帜的长度
         //将顶点位置数据传入渲染管线
         GLES30.glVertexAttribPointer  
         (
         		maPositionHandle[currIndex],            
         		3, 
         		GLES30.GL_FLOAT,   
         		false,
                3*4,   
                mVertexBuffer
         );       
         //将顶点纹理坐标数据传入渲染管线
         GLES30.glVertexAttribPointer  
         (
        		maTexCoorHandle[currIndex], 
         		2, 
         		GLES30.GL_FLOAT, 
         		false,
                2*4,   
                mTexCoorBuffer
         );   
         //启用顶点位置、纹理坐标数据
         GLES30.glEnableVertexAttribArray(maPositionHandle[currIndex]);  
         GLES30.glEnableVertexAttribArray(maTexCoorHandle[currIndex]);  
         //绑定纹理
         GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
         GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
         GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
    }
    //自动切分纹理产生纹理数组的方法
    public float[] generateTexCoor(int bw,int bh)
    {
    	float[] result=new float[bw*bh*6*2]; 
    	float sizew=1.0f/bw;	// 列数
    	float sizeh=0.75f/bh;	// 行数 hhl 高只取0.75是为了按照 矩形物体的大小 WIDTH_SPAN 3/4*WIDTH_SPAN
    	int c=0;
    	for(int i=0;i<bh;i++)
    	{
    		for(int j=0;j<bw;j++)
    		{
    			//每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
    			float s=j*sizew;
    			float t=i*sizeh;
    			
    			result[c++]=s;
    			result[c++]=t;
    			
    			result[c++]=s;
    			result[c++]=t+sizeh;
    			
    			result[c++]=s+sizew;
    			result[c++]=t;
    			
    			
    			result[c++]=s+sizew;
    			result[c++]=t;
    			
    			result[c++]=s;
    			result[c++]=t+sizeh;
    			
    			result[c++]=s+sizew;
    			result[c++]=t+sizeh;    			
    		}
    	}
    	return result;
    }
}

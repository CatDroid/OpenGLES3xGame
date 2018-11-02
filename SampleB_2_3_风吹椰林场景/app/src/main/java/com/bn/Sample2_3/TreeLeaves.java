package com.bn.Sample2_3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES30;
/*
 * 用于绘制树叶矩形
 */
public class TreeLeaves // hhl 一个实例只是代表一片树叶
{	
	int mProgram;//自定义渲染管线着色器程序id   
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用  
    int maTexCoorHandle; //顶点纹理坐标属性引用  
    String mVertexShader;//顶点着色器    	 
    String mFragmentShader;//片元着色器
    
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲
    int vCount=0;   
    float centerX;//树叶中心点X坐标
    float centerZ;//树叶中心点Z坐标
    int index;//当前树的索引
    
    public TreeLeaves(int mProgram,float width,float height,float absolute_height,int index)
    {    	
    	this.mProgram=mProgram;
    	//初始化顶点数据
    	initVertexData(width,height,absolute_height,index);
    	//初始化着色器        
    	intShader();
    	this.index=index;
    }
    //初始化顶点数据的方法
    public void initVertexData(float width,float height,float absolute_height,int index)
    {
        vCount=6;
        float vertices[]=null;//顶点坐标数组
        float texCoor[]=null;//纹理坐标数组
        switch(index)//根据情况编号生成对应角度树叶纹理矩形的顶点数据
        {
        case 0://第一种情况，树叶纹理矩形的边与x轴重合，对应旋转角度为0度
            vertices=new float[]
            {
        		0,height+absolute_height,0,
        		0,absolute_height,0,
        		width,height+absolute_height,0,
            	
        		width,height+absolute_height,0,
        		0,absolute_height,0,
        		width,absolute_height,0,
            };
            texCoor=new float[]//纹理坐标
            {
            	1,0, 1,1, 0,0,
            	0,0, 1,1, 0,1
            };
            //确定中心点坐标
            centerX=width/2;
            centerZ=0;
        	break;
        case 1://第二种情况，与x轴夹角60度的树叶纹理矩形  Hhl 这个是绕着y轴旋转,矩形底边与x轴的夹角是60度
           vertices=new float[]
           {
	       		0,height+absolute_height,0,
	       		0,absolute_height,0,
	       		width/2,height+absolute_height,(float) (-width*Math.sin(Math.PI/3)),
	           	
	       		width/2,height+absolute_height,(float) (-width*Math.sin(Math.PI/3)),
	       		0,absolute_height,0,
	       		width/2,absolute_height,(float) (-width*Math.sin(Math.PI/3))
           };
           texCoor=new float[]
           {
	           	1,0, 1,1, 0,0,
	           	0,0, 1,1, 0,1
           };
           //确定中心点坐标
           centerX=width/4;
           centerZ=(float) (-width*Math.sin(Math.PI/3))/2;
        	break;
        case 2://与x轴夹角120度的树叶纹理矩形
        	vertices=new float[]
            { // hhl -width/2 其实是 -width * Math.cos(Math.PI/3)
        		-width/2,height+absolute_height,(float) (-width*Math.sin(Math.PI/3)),
        		-width/2,absolute_height,(float) (-width*Math.sin(Math.PI/3)),
        		0,height+absolute_height,0,
            	
        		0,height+absolute_height,0,
        		-width/2,absolute_height,(float) (-width*Math.sin(Math.PI/3)),
        		0,absolute_height,0,
            };
            texCoor=new float[]
            {
        		0,0, 0,1, 1,0,
            	1,0, 0,1, 1,1
            };
            //确定中心点坐标
            centerX=-width/4;
            centerZ=(float) (-width*Math.sin(Math.PI/3))/2;
        	break;
        case 3://与x轴夹角180度的树叶纹理矩形
           vertices=new float[]
           {
	       		-width,height+absolute_height,0,
	       		-width,absolute_height,0,
	       		0,height+absolute_height,0,
	           	
	       		0,height+absolute_height,0,
	       		-width,absolute_height,0,
	       		0,absolute_height,0,
           };
           texCoor=new float[]
           {
	       		0,0, 0,1, 1,0,
	           	1,0, 0,1, 1,1
           };
           //确定中心点坐标
           centerX=-width/2;
           centerZ=0;
        	break;
        case 4://与x轴夹角240度的树叶纹理矩形
           vertices=new float[]
           {
	       		-width/2,height+absolute_height,(float) (width*Math.sin(Math.PI/3)),
	       		-width/2,absolute_height,(float) (width*Math.sin(Math.PI/3)),
	       		0,height+absolute_height,0,
	           	
	       		0,height+absolute_height,0,
	       		-width/2,absolute_height,(float) (width*Math.sin(Math.PI/3)),
	       		0,absolute_height,0,
           };
           texCoor=new float[]
           {
	       		0,0, 0,1, 1,0,
	           	1,0, 0,1, 1,1
           };
           //确定中心点坐标
           centerX=-width/4;
           centerZ=(float) (width*Math.sin(Math.PI/3))/2;
           break;
        case 5://与x轴夹角300度的树叶纹理矩形
           vertices=new float[]
	       {
		   		0,height+absolute_height,0,
		   		0,absolute_height,0,
		   		width/2,height+absolute_height,(float) (width*Math.sin(Math.PI/3)),
		       	
		   		width/2,height+absolute_height,(float) (width*Math.sin(Math.PI/3)),
		   		0,absolute_height,0,
		   		width/2,absolute_height,(float) (width*Math.sin(Math.PI/3))
	       };
	       texCoor=new float[]
	       {
		       	1,0, 1,1, 0,0,
		       	0,0, 1,1, 0,1
	       };
	       //确定中心点坐标
           centerX=width/4;
           centerZ=(float) (width*Math.sin(Math.PI/3))/2;
        	break;
        }
        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点坐标数据的初始化================end============================
        
        //顶点纹理坐标数据的初始化================begin============================
        //创建顶点纹理坐标数据缓冲
        
        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTexCoorBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mTexCoorBuffer.put(texCoor);//向缓冲区中放入顶点着色数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
    }
    //初始化着色器
    public void intShader()
    {
        //获取程序中顶点位置属性引用  
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用  
        maTexCoorHandle= GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用
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
         //将顶点纹理坐标数据传入渲染管线
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

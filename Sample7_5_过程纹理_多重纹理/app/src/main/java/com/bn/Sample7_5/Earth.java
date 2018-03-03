package com.bn.Sample7_5;
import static com.bn.Sample7_5.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import android.opengl.GLES30;
import android.util.Log;

//表示地球的类，采用多重纹理
public class Earth  {	
	int mProgram;//自定义渲染管线程序id 
    int muMVPMatrixHandle;//总变换矩阵引用   
    int muMMatrixHandle;//位置、旋转变换矩阵
    int maCameraHandle; //摄像机位置属性引用  
    int maPositionHandle; //顶点位置属性引用  
    int maNormalHandle; //顶点法向量属性引用 
    int maTexCoorHandle; //顶点纹理坐标属性引用 
    int maSunLightLocationHandle;//光源位置属性引用     
    int uDayTexHandle;//白天纹理属性引用
    int uNightTexHandle;//黑夜纹理属性引用 
    String mVertexShader;//顶点着色器代码脚本    	 
    String mFragmentShader;//片元着色器代码脚本
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲
    int vCount=0; 
    public Earth(MySurfaceView mv,float r){
    	initVertexData(r);//调用初始化顶点数据的initVertexData的方法
    	initShader(mv);//调用初始化着色器的initShader方法
    } 

    public void initVertexData(float r){

    	final float UNIT_SIZE=0.5f;
    	ArrayList<Float> alVertix=new ArrayList<Float>();//存放顶点坐标的ArrayList
    	final float angleSpan=10f;//将球进行单位切分的角度
    	for(float vAngle=90;vAngle>-90;vAngle=vAngle-angleSpan){//垂直方向angleSpan度一份
        	for(float hAngle=360;hAngle>0;hAngle=hAngle-angleSpan){//水平方向angleSpan度一份
        		//纵向横向各到一个角度后计算对应的此点在球面上的坐标
        		double xozLength=r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle));
        		float x1=(float)(xozLength*Math.cos(Math.toRadians(hAngle)));
        		float z1=(float)(xozLength*Math.sin(Math.toRadians(hAngle)));
        		float y1=(float)(r*UNIT_SIZE*Math.sin(Math.toRadians(vAngle)));
        		xozLength=r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle-angleSpan));
        		float x2=(float)(xozLength*Math.cos(Math.toRadians(hAngle)));
        		float z2=(float)(xozLength*Math.sin(Math.toRadians(hAngle)));
        		float y2=(float)(r*UNIT_SIZE*Math.sin(Math.toRadians(vAngle-angleSpan)));
        		xozLength=r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle-angleSpan));
        		float x3=(float)(xozLength*Math.cos(Math.toRadians(hAngle-angleSpan)));
        		float z3=(float)(xozLength*Math.sin(Math.toRadians(hAngle-angleSpan)));
        		float y3=(float)(r*UNIT_SIZE*Math.sin(Math.toRadians(vAngle-angleSpan)));
        		xozLength=r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle));
        		float x4=(float)(xozLength*Math.cos(Math.toRadians(hAngle-angleSpan)));
        		float z4=(float)(xozLength*Math.sin(Math.toRadians(hAngle-angleSpan)));
        		float y4=(float)(r*UNIT_SIZE*Math.sin(Math.toRadians(vAngle)));   
        		//构建第一三角形
        		alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);
        		alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
        		alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);        		
        		//构建第二三角形
        		alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);
        		alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
        		alVertix.add(x3);alVertix.add(y3);alVertix.add(z3); 
        }} 	
        vCount=alVertix.size()/3;//顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标
        float vertices[]=new float[vCount*3];//将alVertix中的坐标值转存到一个float数组中
    	for(int i=0;i<alVertix.size();i++){
    		vertices[i]=alVertix.get(i);
    	}
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);//创建顶点坐标数据缓冲
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //将alTexCoor中的纹理坐标值转存到一个float数组中
        float[] texCoor=generateTexCoor(//获取切分整图的纹理数组    
   			 (int)(360/angleSpan), //纹理图切分的列数
   			 (int)(180/angleSpan)  //纹理图切分的行数
        );
        ByteBuffer llbb = ByteBuffer.allocateDirect(texCoor.length*4);
        llbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTexCoorBuffer=llbb.asFloatBuffer();
        mTexCoorBuffer.put(texCoor);
        mTexCoorBuffer.position(0);

		Log.w("TOM","法向量与光源线夹角 白天 边界 "  + Math.acos(0.21)/Math.PI * 180.0f   );// 77.87度
		Log.w("TOM","法向量与光源线夹角 黑夜 边界  "  + Math.acos(0.05)/Math.PI * 180.0f   );// 87.13度

    }
    public void initShader(MySurfaceView mv){ //初始化着色器

        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex_earth.sh", mv.getResources());
        ShaderUtil.checkGlError("vertex_earth.sh");
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_earth.sh", mv.getResources());
        ShaderUtil.checkGlError("frag_earth.sh");
        mProgram = createProgram(mVertexShader, mFragmentShader);

        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");   // 获取程序中顶点位置属性引用
        maTexCoorHandle=GLES30.glGetAttribLocation(mProgram, "aTexCoor");       // 获取程序中顶点纹理属性引用
        maNormalHandle= GLES30.glGetAttribLocation(mProgram, "aNormal");        // 获取程序中顶点法向量属性引用

        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");  // 获取程序中总变换矩阵引用
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");   //获取位置、旋转变换矩阵引用

        maCameraHandle=GLES30.glGetUniformLocation(mProgram, "uCamera"); // 获取程序中摄像机位置引用
        maSunLightLocationHandle=GLES30.glGetUniformLocation(mProgram, "uLightLocationSun"); //获取程序中光源位置引用

        uDayTexHandle=GLES30.glGetUniformLocation(mProgram, "sTextureDay");    //获取白天、黑夜两个纹理引用
        uNightTexHandle=GLES30.glGetUniformLocation(mProgram, "sTextureNight");  

    }
    public void drawSelf(int texId,int texIdNight) {        
    	 //指定使用某套着色器程序
    	 GLES30.glUseProgram(mProgram);
         //将最终变换矩阵传入渲染管线
         GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);  
         //将位置、旋转变换矩阵传入渲染管线
         GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);    
         //将摄像机位置传入渲染管线
         GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
         //将光源位置传入渲染管线 
         GLES30.glUniform3fv(maSunLightLocationHandle, 1, MatrixState.lightPositionFBSun);
         GLES30.glVertexAttribPointer(//将顶点位置数据送入渲染管线
         		maPositionHandle,   
         		3, 
         		GLES30.GL_FLOAT, 
         		false,
                3*4, 
                mVertexBuffer   
         );       
         GLES30.glVertexAttribPointer(  //将顶点纹理数据送入渲染管线
        		maTexCoorHandle,  
         		2, 
         		GLES30.GL_FLOAT, 
         		false,
                2*4,   
                mTexCoorBuffer
         );   
         GLES30.glVertexAttribPointer   //将顶点法向量数据送入渲染管线
         (
        		maNormalHandle, 
         		4, 
         		GLES30.GL_FLOAT, 
         		false,
                3*4,   
                mVertexBuffer
         );            

         GLES30.glEnableVertexAttribArray(maPositionHandle);        // 启用顶点位置数据数组
         GLES30.glEnableVertexAttribArray(maTexCoorHandle);         // 启用顶点纹理数据数组
         GLES30.glEnableVertexAttribArray(maNormalHandle);          // 启用顶点法向量数据数组

         GLES30.glActiveTexture(GLES30.GL_TEXTURE0);                // 绑定纹理
         GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);         // 白天纹理
         GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
         GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texIdNight);    // 黑夜纹理
         GLES30.glUniform1i(uDayTexHandle, 0);                      // 通过引用指定白天纹理
         GLES30.glUniform1i(uNightTexHandle, 1);                    // 通过引用指定黑夜纹理

         GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);       // 以三角形方式执行绘制
    }

    public float[] generateTexCoor(int bw,int bh){                  // 自动切分纹理产生纹理数组的方法
    	float[] result=new float[bw*bh*6*2]; 
    	float sizew=1.0f/bw;    // 列数
    	float sizeh=1.0f/bh;    // 行数
    	int c=0;
    	for(int i=0;i<bh;i++){  // 每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
    		for(int j=0;j<bw;j++){
    			float s=j*sizew;
    			float t=i*sizeh;    // 得到i行j列小矩形的左上点的纹理坐标值
    			result[c++]=s;
    			result[c++]=t;      // 该矩形左上点纹理坐标值
    			result[c++]=s;
    			result[c++]=t+sizeh;// 该矩形左下点纹理坐标值
    			result[c++]=s+sizew;
    			result[c++]=t;      // 该矩形右上点纹理坐标值
    			result[c++]=s+sizew;
    			result[c++]=t;      // 该矩形右上点纹理坐标值
    			result[c++]=s;
    			result[c++]=t+sizeh;// 该矩形左下点纹理坐标值
    			result[c++]=s+sizew;
    			result[c++]=t+sizeh;// 该矩形右下点纹理坐标值
    	}}
    	return result;
    }


}

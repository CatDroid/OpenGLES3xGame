package com.bn.Sample1_4;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import android.opengl.GLES30;
import android.util.Log;

//表示为普通纹理球
public class BallAndCube
{	
	int mProgram;//自定义渲染管线程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用
    int maTexCoorHandle; //顶点纹理坐标属性引用
    String mVertexShader;//顶点着色器代码脚本
    String mFragmentShader;//片元着色器代码脚本
    
    int vCount=0; //顶点数量
    int iCount=0;
	float yAngle = 0;// 绕y轴旋转的角度
	float xAngle = 0;// 绕x轴旋转的角度
	float zAngle = 0;// 绕z轴旋转的角度
	


    IntBuffer mIndicesBuffer;
	
	int mVertexBufferId;//顶点坐标数据缓冲 id
	int mTexCoorBufferId;//顶点纹理数据缓冲id
	int mIndicesBufferId;
	
	ByteBuffer vbb1;//顶点坐标数据的映射缓冲
	float[]vertices;//原始球的顶点坐标数组
	float[] verticesCube;//原始正方体的顶点坐标数组
	int[] indices;
	float[] texCoors;//纹理数组


	private final boolean CONFIG_FULL_OR_HALF = false ; // full true ; half false 每次变换只是一半还是整个 一半的话umap也只是一半
    public float[] curBallForDraw;
    public float[] curBallForCal;
    float span=30;//切分为30份
    
	ArrayList<Float> alVertix=new ArrayList<Float>();//存放顶点坐标的ArrayList
	ArrayList<Float> alVertix1=new ArrayList<Float>();//存放对应正方体的顶点坐标的ArrayList
	ArrayList<Float> alVertixTexCoor=new ArrayList<Float>();//存放纹理坐标的ArrayList
	ArrayList<Integer> alVertixIndice=new ArrayList<Integer>();//存放顶点坐标的ArrayList
	
	ArrayList<Float> alVertix2=new ArrayList<Float>();//临时存放顶点坐标的ArrayList
	ArrayList<Float> alVertixCube2=new ArrayList<Float>();//临时存放对应正方体的顶点坐标的ArrayList
	ArrayList<Float> alVertixTexCoor2=new ArrayList<Float>();//临时存放纹理坐标的ArrayList
	
	private final Object lock=new Object();//锁对象
	BallAndCube(MySurfaceView mv,float r) {
    	initYSData(r);		// 计算顶点数据
    	initVertexData();	// 调用初始化顶点数据的方法
    	initShader(mv);		// 调用初始化着色器的方法
    }
    
    private void initYSData(float r)
    {
    	// 1. r*UNIT_SIZE 为半径
		// 2. 俯仰角 和 水平角 的间隔都是 angleSpan
		// 3. 球体赤道上的圆 与 正方体在赤道上的正方形 是 正方形4个角内接外面的圆形!!!   整个正方体的8个角与球不是内接的!!
		//		也就是球体和正方体在赤道上的正方形4个顶点重合 (不是正方体的对角线是外接球) 在俯仰角45度的时候,球截面的圆内接正方体表面的正方形
		// 4. 暂时还没有看明白投影的方法!!

    	final float UNIT_SIZE=0.5f; // r*UNIT_SIZE是球的半径
    	final float angleSpan=5;	// 将球进行单位切分的角度

		float length=(float) (r*UNIT_SIZE*Math.sin(Math.toRadians(45)));// 正方体 边长的一半
		float length2 = length*2;   // 正方体 边长

    	for(float vAngle=90;vAngle>=-90;vAngle=vAngle-angleSpan)// 俯仰角  垂直方向angleSpan度一份
        {
        	for(float hAngle=360;hAngle>0;hAngle=hAngle-angleSpan)// 水平角 水平方向angleSpan度一份
        	{
				// 纵向横向各到一个角度后计算对应的此点在球面上的坐标
        		double xozLength=r*UNIT_SIZE*Math.cos(Math.toRadians(vAngle));
        		// 球面上第一个顶点
        		float x1=(float)(xozLength*Math.cos(Math.toRadians(hAngle)));
        		float z1=(float)(xozLength*Math.sin(Math.toRadians(hAngle)));
        		float y1=(float)(r*UNIT_SIZE*Math.sin(Math.toRadians(vAngle)));
        		
        		// 对应正方体上的第一个顶点
        		float x10=x1;
        		float z10=z1;
        		float y10=y1;
        		        		
        		// 第一个顶点的纹理坐标
        		float s1=0;
        		float t1=0;

				// 在计算上下面的临界边缘
        		if(vAngle==50||vAngle==-45)
        		{
        			float x1Temp=0;	float z1Temp=0;		float y1Temp=0;//球上的顶点
        			float x10Temp=0;float z10Temp=0;	float y10Temp=0;     //对应正方体上的顶点   			
        			float s2=0;float t2=0;//纹理坐标
        			float xozLengthTemp=0;
        			
        			if(vAngle==50)
        			{
        				//当vAngle等于50时，上面少了45时的一圈顶点，所以计算45度时球的顶点、对应正方体上的顶点、纹理坐标
        				xozLengthTemp=(float) (r*UNIT_SIZE*Math.cos(Math.toRadians(45)));
            			x1Temp=(float)(xozLengthTemp*Math.cos(Math.toRadians(hAngle)));
            			z1Temp=(float)(xozLengthTemp*Math.sin(Math.toRadians(hAngle)));
            			y1Temp=(float)(r*UNIT_SIZE*Math.sin(Math.toRadians(45)));            			
            			y10Temp=length;        
        			}
        			else if(vAngle==-45)
        			 { 
        				//当vAngle等于-45时，下面少了-45时的一圈顶点，所以计算-45度时球的顶点、对应正方体上的顶点、纹理坐标
        				xozLengthTemp=(float) (r*UNIT_SIZE*Math.cos(Math.toRadians(-45)));
            			x1Temp=(float)(xozLengthTemp*Math.cos(Math.toRadians(hAngle)));
            			z1Temp=(float)(xozLengthTemp*Math.sin(Math.toRadians(hAngle)));
            			y1Temp=(float)(r*UNIT_SIZE*Math.sin(Math.toRadians(-45)));            			
            			y10Temp=-length;        	
        			}      
        			 
         			if(Math.abs(x1Temp)>Math.abs(z1Temp)){ // 正方体表面对应的点
        				if(x1Temp>0){
        					x10Temp=(float) xozLengthTemp;
        				}else{
        					x10Temp=(float) -xozLengthTemp;
        				}
        				z10Temp=(float) (x10Temp*Math.tan(Math.toRadians(hAngle)));
        			}else{
        				if(z1Temp>0){
        					z10Temp=(float) xozLengthTemp;
        				}else{
        					z10Temp=(float) -xozLengthTemp;
        				}
        				x10Temp=(float) (z10Temp/Math.tan(Math.toRadians(hAngle)));
        			}
        			
        			if(vAngle==50){//计算纹理坐标
        				s2=0.5f+x10Temp/length2;
            			t2=0.5f+z10Temp/length2;
        			}
        			else  if(vAngle==-45){//计算纹理坐标
        			    s2=0.5f+x10Temp/length2;
            			t2=(-0.5f+z10Temp/length2)*-1;
        			}
        			//球
        			alVertix2.add(x1Temp);alVertix2.add(y1Temp);alVertix2.add(z1Temp);
        			//正方体
        			alVertixCube2.add(x10Temp);alVertixCube2.add(y10Temp);alVertixCube2.add(z10Temp);        			
        			//纹理
        			alVertixTexCoor2.add(s2); alVertixTexCoor2.add(t2);        			
        		}



        		// 对于俯仰角度 在 大于45 和 小于-45 y坐标固定是正方体的上面=length(正方体边长的一半)
        		if(vAngle>45){//如果vAngle大于45时，对应正方体的上面
        			if(Math.abs(x1)>Math.abs(z1)){
        				if(x1>0){
        					x10=(float) xozLength;
        				}else{
        					x10=(float) -xozLength;
        				}
        				z10=(float) (x10*Math.tan(Math.toRadians(hAngle)));
        			}else{
        				if(z1>0){
        					z10=(float) xozLength;
        				}else{
        					z10=(float) -xozLength;
        				}
        				x10=(float) (z10/Math.tan(Math.toRadians(hAngle)));
        			}
        			y10=length; 			// 球表面上的点 映射到正方体的上面 y坐标固定
        			s1=0.5f + x10 / length2;// 根据正方体表面上的点的x和z坐标,纹理坐标从0~1.0 正方体边长length2 范围在[-length2/2,length2/2]
        			t1=0.5f + z10 / length2;// -0.5 < z10 / length2 < 0.5
        		}
        		else  if(vAngle<(-45))
        		{//如果vAngle小于-45时，对应正方体的下面
        			if(Math.abs(x1)>Math.abs(z1)){
        				if(x1>0){
        					x10=(float) xozLength;
        				}else{
        					x10=(float) -xozLength;
        				}
        				z10=(float) (x10*Math.tan(Math.toRadians(hAngle)));
        			}else{
        				if(z1>0){
        					z10=(float) xozLength;
        				}else{
        					z10=(float) -xozLength;
        				}
        				x10=(float) (z10/Math.tan(Math.toRadians(hAngle)));
        			}
        			y10=-length;
        			s1=0.5f+x10/length2;
        			t1=1-(0.5f+z10/length2);
        			
        		}
        		else{            			
        			if(Math.abs(x1)>Math.abs(z1))
            		{//x>z
            			if(x1>0){
            				x10=length;
            				z10=(float) (x10*Math.tan(Math.toRadians(hAngle)));           
            				s1=0.5f+z10/length2;
            			}else{
            				x10=-length;
            				z10=(float) (x10*Math.tan(Math.toRadians(hAngle)));           
            				s1=(-0.5f+z10/length2)*-1;
            			}    			
            			t1=1-(0.5f+(y10)/length2);
            		}else{
            			if(z1>0)
            			{
            				z10=length;
            				x10=(float)(z10/Math.tan(Math.toRadians(hAngle)));
            				s1=0.5f+x10/length2;
            			}else{
            				z10=-length;
            				x10=(float)(z10/Math.tan(Math.toRadians(hAngle)));
            				s1=-1*(-0.5f+x10/length2);
            			}
            			t1=1-(0.5f+(y10)/length2);
            		}
        		}


    			//将球当前的顶点放入列表中=============
        		alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);
        		//将立方体对应的顶点放入列表中=============
        		alVertix1.add(x10);alVertix1.add(y10);alVertix1.add(z10);
        		//将纹理坐标放入列表中
        		alVertixTexCoor.add(s1); alVertixTexCoor.add(t1);        		
        	}
        	
        	if(vAngle==50||vAngle==-45)
        	{       		
        		for(int i=0;i<alVertix2.size()/3;i++)
        		{
        			alVertix.add(alVertix2.get(i*3));
        			alVertix.add(alVertix2.get(i*3+1));
        			alVertix.add(alVertix2.get(i*3+2));        			    		
        		}
        		for(int i=0;i<alVertixCube2.size()/3;i++)
        		{
        			alVertix1.add(alVertixCube2.get(i*3));
        			alVertix1.add(alVertixCube2.get(i*3+1));
        			alVertix1.add(alVertixCube2.get(i*3+2));
        		}        		
        		for(int i=0;i<alVertixTexCoor2.size()/3;i++)
        		{
        			alVertixTexCoor.add(alVertixTexCoor2.get(i*3));
        			alVertixTexCoor.add(alVertixTexCoor2.get(i*3+1));
        			alVertixTexCoor.add(alVertixTexCoor2.get(i*3+2));        			
        		}
        		
        		alVertix2.clear();
        		alVertixCube2.clear();
        		alVertixTexCoor2.clear();        
        	}	
        }  // 到这里产生了 球体和正方体 对应的顶点坐标 和 纹理坐标

    	//外面卷绕，防止出现断裂情况==========start=============
    	int w = (int) (360 / angleSpan);
    	for(int i = 0; i <(w+2); i++){
    		for(int j = 0; j < w; j++){
    			int x = i * w + j;
    			alVertixIndice.add(x); // 绘制方法是 三角形  所以6个顶点的索引 构成一个长方形/格子
    			alVertixIndice.add(x + w);
    			alVertixIndice.add(x + 1);
    			alVertixIndice.add(x + 1);
    			alVertixIndice.add(x + w);
    			alVertixIndice.add(x + w + 1);
    		}
    	}
    	//外面卷绕，防止出现断裂情况==========end=============
    	
        vCount=alVertix.size()/3;//顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标
        
        iCount=alVertixIndice.size();
        vertices=new float[vCount*3];
        verticesCube=new float[vCount*3];
        texCoors=new float[alVertixTexCoor.size()];

        curBallForDraw=new float[CONFIG_FULL_OR_HALF?vertices.length:vertices.length/2];
        curBallForCal=new float[CONFIG_FULL_OR_HALF?vertices.length:vertices.length/2 ];
        indices=new int[iCount];

    	for(int i=0;i<alVertix.size();i++) {		// 顶点坐标
    		vertices[i]=alVertix.get(i);
    	}
    	for(int i=0;i<iCount;i++) {
    		indices[i]=alVertixIndice.get(i);
    	}
    	for(int i=0;i<alVertix1.size();i++){		// 对应正方体的顶点坐标
    		verticesCube[i]=alVertix1.get(i);
    	}
    	for(int i=0;i<alVertixTexCoor.size();i++){	// 顶点纹理坐标
    		texCoors[i]=alVertixTexCoor.get(i);
    	}
    	
    }
    
    // 1.使用索引法绘制
	// 2.两个VBO存放顶点索引和纹理坐标
	// 3.还有一个VBO存放顶点坐标需要不端更新(使用glMapRange方法) 顶点索引不用变化，但是对应的顶点坐标(的VBO)会更新
    private void initVertexData()
    {
    	int[] buffIds=new int[3];
    	GLES30.glGenBuffers(3, buffIds, 0);
    	mVertexBufferId=buffIds[0];//将顶点位置数据送入渲染管线
    	mTexCoorBufferId=buffIds[1];//顶点纹理坐标数据缓冲 id
    	mIndicesBufferId=buffIds[2];


        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoors.length*4);
        tbb.order(ByteOrder.nativeOrder());
        FloatBuffer mTexCoorBuffer = tbb.asFloatBuffer();
        mTexCoorBuffer.put(texCoors);
        mTexCoorBuffer.position(0);
    	GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,mTexCoorBufferId);
    	GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, texCoors.length*4, mTexCoorBuffer, GLES30.GL_STATIC_DRAW);


    	ByteBuffer ibb= ByteBuffer.allocateDirect(indices.length*4);
    	ibb.order(ByteOrder.nativeOrder());
    	mIndicesBuffer=ibb.asIntBuffer();
        mIndicesBuffer.put(indices);
        mIndicesBuffer.position(0);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,mIndicesBufferId);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, indices.length*4, mIndicesBuffer, GLES30.GL_STATIC_DRAW);
        

    	GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,mVertexBufferId);
    	GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertices.length*4, null, GLES30.GL_STATIC_DRAW);
    	vbb1=(ByteBuffer)GLES30.glMapBufferRange(GLES30.GL_ARRAY_BUFFER, 0, vertices.length*4, GLES30.GL_MAP_WRITE_BIT|GLES30.GL_MAP_INVALIDATE_BUFFER_BIT);
    	if(vbb1==null) {
			Log.e("TOM","glMapBufferRange fail return null");
    		return;
    	}
    	vbb1.order(ByteOrder.nativeOrder());//设置字节顺序
    	FloatBuffer vertexMappedBuffer =vbb1.asFloatBuffer();// 转换为Float型缓冲
		vertexMappedBuffer.put(vertices);		 // 向映射的缓冲区中放入顶点坐标数据   verticesCube
		vertexMappedBuffer.position(0);		 // 设置缓冲区起始位置
    	if(!GLES30.glUnmapBuffer(GLES30.GL_ARRAY_BUFFER)) {
			Log.e("TOM","glUnmapBuffer Fail!!");
    		return;
    	}

    	GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);
    }
  

    private void initShader(MySurfaceView mv)
    {
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);

        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        maTexCoorHandle=GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");

    }

	private void updateMapping(float[] currVertex)
	{
		// 绑定到顶点坐标数据缓冲
    	GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,mVertexBufferId);
    	vbb1=(ByteBuffer)GLES30.glMapBufferRange(
    			GLES30.GL_ARRAY_BUFFER, 
    			0, 							// 偏移量
    			currVertex.length*4, 		// 长度
    			GLES30.GL_MAP_WRITE_BIT	// 访问标志
						| GLES30.GL_MAP_INVALIDATE_RANGE_BIT
						 //|GLES30.GL_MAP_INVALIDATE_BUFFER_BIT // 整个缓冲区的内容可以被驱动放弃
    	);
		// 部分映射(球的一半进行变换) 如果加上GL_MAP_INVALIDATE_BUFFER_BIT 在小米5或者一加5上面跑1分钟后会突然没有了一半
		// 改成 不使用 或者使用 GL_MAP_INVALIDATE_RANGE_BIT 更加合适
    	if(vbb1==null) {
			Log.e("TOM","glMapBufferRange fail return null");
    		return;
    	}
    	vbb1.order(ByteOrder.nativeOrder());		// 设置字节顺序
    	FloatBuffer vertexMappedBuffer=vbb1.asFloatBuffer();	// 转换为Float型缓冲
		vertexMappedBuffer.put(currVertex)  ;		// 向映射的缓冲区中放入顶点坐标数据
		vertexMappedBuffer.position(0);			// 设置缓冲区起始位置
    	if(!GLES30.glUnmapBuffer(GLES30.GL_ARRAY_BUFFER)) { // 必须unmap解除映射 否则渲染管线在绘制时 无法使用被映射缓冲区的数据
			Log.e("TOM","glUnmapBuffer fail !" + GLES30.glGetError() );
    	}    	
	}
    public void drawSelf(int texId)
    {        
    	MatrixState.rotate(xAngle, 1, 0, 0);//绕X轴转动
    	MatrixState.rotate(yAngle, 0, 1, 0);//绕Y轴转动
    	MatrixState.rotate(zAngle, 0, 0, 1);//绕Z轴转动


    	GLES30.glUseProgram(mProgram);				// 指定使用某套着色器程序


    	GLES30.glUniformMatrix4fv(muMVPMatrixHandle,// 将最终变换矩阵传入渲染管线
									1, false, MatrixState.getFinalMatrix(), 0);
  
    	GLES30.glEnableVertexAttribArray(maTexCoorHandle);  		 	// 启用纹理数据数组
    	GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,mTexCoorBufferId);	// 绑定到顶点纹理坐标数据缓冲
    	GLES30.glVertexAttribPointer(maTexCoorHandle, 2, GLES30.GL_FLOAT, false, 2*4, 0); // 将顶点纹理数据送入渲染管线

    	GLES30.glEnableVertexAttribArray(maPositionHandle);				//启用顶点位置数据数组
    	GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,mVertexBufferId);	//绑定到顶点坐标数据缓冲
    	GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3*4, 0); //将顶点位置数据送入渲染管线

    	GLES30.glActiveTexture(GLES30.GL_TEXTURE0);			// 激活纹理
    	GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);  // 绑定纹理
    	
    	synchronized(lock) {
        	updateMapping(curBallForDraw);// 更新顶点数据
    	}
    	GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,mIndicesBufferId);


    	// 以三角形方式执行绘制  索引法
    	GLES30.glDrawElements(GLES30.GL_TRIANGLES, iCount, GLES30.GL_UNSIGNED_INT, 0);

    	GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,0);
    	GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);
    }
    
    //计算顶点坐标数据方法
    public void calVertices(int count,boolean flag)
    {
		int transformVertices = CONFIG_FULL_OR_HALF?vertices.length:vertices.length/2;
		for(int i=0;i< transformVertices ;i++)
		{
			curBallForCal[i]=insertValue(vertices[i],verticesCube[i],
											span, // 由 vertices[i] 到 verticesCube[i] 之间的步进
											count,flag);
		}
    	synchronized(lock)
    	{
        	//curBallForDraw=Arrays.copyOf(curBallForCal, curBallForCal.length);
			// Arrays.copyOf每次都创建  System.arraycopy拷贝到给定的数组
			System.arraycopy(curBallForCal,0,curBallForDraw,0,curBallForCal.length);
    	}
    }
    
    //计算插值方法(线性插值)
    public float insertValue(float start,float end,float span,int count,boolean isBallToCubeY)
	{
		float result=0;
		if(isBallToCubeY)
		{
			result=start+count*(end-start)/span;
		}else{
			result=end-count*(end-start)/span;
		}
		return result;
	}
}

package com.bn.Sample3_16;
import static com.bn.Sample3_16.ShaderUtil.createProgram;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

public class ParticleForDraw 
{	
	private int mProgram;           // 自定义渲染管线程序id
    private int muMVPMatrixHandle;  // 总变换矩阵引用id
    private int mmaxLifeSpan;       //
    private int muBj;               // 单个粒子的半径引用id
    private int muStartColor;       // 起始颜色引用id
    private int muEndColor;         // 终止颜色引用id
    private int muCameraPosition;   // 摄像机位置
    private int muMMatrix;          // 基本变换矩阵总矩阵
    private int maPositionHandle;   // 顶点位置属性引用id


    private int mProgram0;
    private int maPositionHandle0;
    private int mtPositionHandle0;
    private int mGroupCountHandle0;
    private int mCountHandle0;
    private int mLifeSpanStepHandle0;
    

    
	FloatBuffer mVertexBuffer;  // 顶点数据缓冲
	FloatBuffer tmVertexBuffer; // 顶点数据缓冲
	
    int vCount=0;   
    float halfSize;
    
	int mVertexBufferIds[]=new int[2];// 顶点数据缓冲 id
	int mVertexBufferId0;
	
	int[] a={0,1};// 缓冲区数组的索引值数组
    int[] b={1,0};// 缓冲区数组的索引值数组
    int index = 0;// 索引值数组的索引值
    
    public ParticleForDraw(MySurfaceView mv,float halfSize,float x,float y)
    {    	
    	this.halfSize=halfSize;
    	initShader0(mv);    // 变换反馈着色器
    	initShader(mv);     // 粒子着色器
    }
   
    //初始化顶点坐标数据的方法
    public void initVertexData(float[] points,float[] tpoints)
    {
       	//缓冲id数组
    	int[] buffIds=new int[3];
    	//生成3个缓冲id
    	GLES30.glGenBuffers(3, buffIds, 0);
    	//顶点基本属性数据缓冲 id
    	this.mVertexBufferIds[0]=buffIds[0];
    	//顶点基本属性数据缓冲 id
    	this.mVertexBufferIds[1]=buffIds[1];
    	//顶点固定属性数据缓冲 id
    	this.mVertexBufferId0=buffIds[2];

    	//顶点数据的初始化================begin============================
    	this.vCount=points.length/4;//顶点个数

        //创建顶点基本属性数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(points.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        this.mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        this.mVertexBuffer.put(points);//向缓冲区中放入顶点基本属性数据
        this.mVertexBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题

        //绑定到顶点基本属性数据缓冲 --用于存放顶点的当前基本属性值
    	GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,this.mVertexBufferIds[0]);
    	//向顶点基本属性数据缓冲送入数据
    	GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, points.length*4, this.mVertexBuffer, GLES30.GL_STATIC_DRAW);

    	//绑定到顶点基本属性数据缓冲 --用于存放顶点下一位置的基本属性值（变换反馈缓冲区）
    	GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,mVertexBufferIds[1]);
    	//向顶点基本属性数据缓冲送入数据
    	GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, points.length*4, this.mVertexBuffer, GLES30.GL_STATIC_DRAW);
        //顶点数据的初始化================end============================

    	///////////////////////////////////////////////////

    	//创建顶点固定属性数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb0 = ByteBuffer.allocateDirect(tpoints.length*4);
        vbb0.order(ByteOrder.nativeOrder());//设置字节顺序
        this.tmVertexBuffer = vbb0.asFloatBuffer();//转换为Float型缓冲
        this.tmVertexBuffer.put(tpoints);//向缓冲区中放入顶点固定属性数据
        this.tmVertexBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题

        //绑定到顶点固定属性数据缓冲
    	GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,this.mVertexBufferId0);
    	//向顶点固定属性数据缓冲送入数据
    	GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, tpoints.length*4, this.tmVertexBuffer, GLES30.GL_STATIC_DRAW);
    	//绑定到系统默认缓冲
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);



        GLES30.glGenTransformFeedbacks(2,mTBO,0);
        GLES30.glBindTransformFeedback(GLES30.GL_TRANSFORM_FEEDBACK,mTBO[0]);
        GLES30.glBindBufferBase(GLES30.GL_TRANSFORM_FEEDBACK_BUFFER, 0, mVertexBufferIds[0]);
//        GLES30.glBindBufferBase(GLES30.GL_TRANSFORM_FEEDBACK_BUFFER, 5, mVertexBufferIds[0]);
//        int error = GLES30.glGetError() ;
//        if(error != GLES30.GL_NO_ERROR)throw new RuntimeException("GL_TRANSFORM_FEEDBACK_BUFFER 0x" + Integer.toHexString(error));
        // TF绑定点 最多是4个 0~3
        int[] status = new int[1];
        GLES30.glGetIntegerv(GLES30.GL_MAX_TRANSFORM_FEEDBACK_SEPARATE_ATTRIBS,status,0);
        android.util.Log.w("TOM","Max GL_SEPARATE_ATTRIBS   " + status[0] );
        // Max GL_SEPARATE_ATTRIBS   4
        GLES30.glGetIntegerv(GLES30.GL_MAX_UNIFORM_BUFFER_BINDINGS,status,0);
        android.util.Log.w("TOM","Max GL_MAX_UNIFORM_BUFFER_BINDINGS   " + status[0] );
        // Max GL_MAX_UNIFORM_BUFFER_BINDINGS   84

        GLES30.glBindTransformFeedback(GLES30.GL_TRANSFORM_FEEDBACK,mTBO[1]);
        GLES30.glBindBufferBase(GLES30.GL_TRANSFORM_FEEDBACK_BUFFER, 0, mVertexBufferIds[1]);
        GLES30.glBindTransformFeedback(GLES30.GL_TRANSFORM_FEEDBACK,0);
        GLES30.glBindBufferBase(GLES30.GL_TRANSFORM_FEEDBACK_BUFFER, 0, 0);

    }
    int[] mTBO = new int[2];

    
    // 初始化 变换反馈着色器陈程序
    private void initShader0(MySurfaceView mv)
    {
        String mVertexShader0=ShaderUtil.loadFromAssetsFile("vertex_TransformFeedback.sh", mv.getResources());
        String mFragmentShader0=ShaderUtil.loadFromAssetsFile("frag_TransformFeedback.sh", mv.getResources());

        mProgram0 = createProgram_TransformFeedback(mVertexShader0, mFragmentShader0);

        // 获取程序中顶点位置属性引用id
        maPositionHandle0 = GLES30.glGetAttribLocation(mProgram0, "aPosition");
        // 获取程序中顶点固定属性引用id
        mtPositionHandle0 = GLES30.glGetAttribLocation(mProgram0, "tPosition");

        // 获取程序中激活粒子的位置属性引用id
        mCountHandle0=GLES30.glGetUniformLocation(mProgram0, "count");
        // 获取程序中一层粒子的数量属性引用id
        mGroupCountHandle0=GLES30.glGetUniformLocation(mProgram0, "groupCount");
        // 获取程序中粒子生命周期步进属性引用id
        mLifeSpanStepHandle0=GLES30.glGetUniformLocation(mProgram0, "lifeSpanStep");
        
    }

    // 创建变换反馈着色器程序
    public static int createProgram_TransformFeedback(String vertexSource, String fragmentSource) {

        // Step.1 加载顶点着色器
        int vertexShader = ShaderUtil.loadShader(GLES30.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }

        // Step.2 加载片元着色器  只是空的  #version 300 es void main(){}
        int pixelShader = ShaderUtil.loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        // Step.3 创建程序
        int program = GLES30.glCreateProgram();
        if (program != 0) {
            // Step.3.1 向程序中加入顶点着色器
            GLES30.glAttachShader(program, vertexShader);
            ShaderUtil.checkGlError("glAttachShader");
            // Step.3.2 向程序中加入片元着色器
            GLES30.glAttachShader(program, pixelShader);
            ShaderUtil.checkGlError("glAttachShader");


            // Step.3.3 设置顶点着色器中进行变换反馈的所有变量名字符串***
            //          必须在link程序之前调用设置要捕获的顶点着色器输出易变变量
            String feedbackVaryings[] = { "vPosition",  };
            GLES30.glTransformFeedbackVaryings(
                    program,//用于变换反馈的着色器程序
                    feedbackVaryings,//用于变换反馈的变量名字符串数组
                    GLES30.GL_INTERLEAVED_ATTRIBS//交叉存取
            );
            // 程序的vPosition引用 对应绑定点为 0



            // Step.3.4 链接程序
            GLES30.glLinkProgram(program);

            // Step.3.5 返回链接状态
            int[] linkStatus = new int[1];
            GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES30.GL_TRUE) {
                Log.e("ES20_ERROR", "Could not link program: ");
                Log.e("ES20_ERROR", GLES30.glGetProgramInfoLog(program));
                GLES30.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    // 绘制 只做顶点着色器  不做光栅化
    public void drawSelf0(int count,int groupCount,float lifeSpanStep)
    {
    	// 指定使用某套着色器程序
   	 	GLES30.glUseProgram(mProgram0);

        //开启禁止栅格化，则顶点着色器中的out变量不进入片元着色器，而是写入变换反馈缓冲对象中
        GLES30.glEnable(GLES30.GL_RASTERIZER_DISCARD);


   	 	// 设置变换反馈缓冲区对象（绑定到存放下一位置的顶点基本属性值的顶点基本属性数据缓冲）
        // glBindBufferBase
        // target: GLES3.0 只能是 GL_TRANSFORM_FEEDBACK_BUFFER变换反馈缓冲区对象 or GL_UNIFORM_BUFFER一致缓冲区对象
        // index:  指定绑定点
        // buffer: 缓冲区的名字 可以是GL_ARRAY_BUFFER或者是GL_UNIFORM_BUFFER
//   	 	GLES30.glBindBufferBase(GLES30.GL_TRANSFORM_FEEDBACK_BUFFER, 0, mVertexBufferIds[b[index]]);
        GLES30.glBindTransformFeedback(GLES30.GL_TRANSFORM_FEEDBACK, mTBO[b[index]]);


   	 	GLES30.glUniform1i(mCountHandle0, count);               // 将激活粒子位置的计算器送入渲染管线
   	 	GLES30.glUniform1i(mGroupCountHandle0, groupCount);     // 将每批粒子的个数送入渲染管线
   	 	GLES30.glUniform1f(mLifeSpanStepHandle0, lifeSpanStep); // 将粒子生命期步进送入渲染管线
   	 
		// 启用顶点基本属性数据
		GLES30.glEnableVertexAttribArray(maPositionHandle0);
		// 绑定到存放当前顶点基本属性的顶点基本属性数据缓冲
		GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,mVertexBufferIds[a[index]]);
		// 将顶点基本属性数据送入渲染管线
		GLES30.glVertexAttribPointer(maPositionHandle0, 4, GLES30.GL_FLOAT, false, 4*4, 0);


        // 启用顶点固定属性数据
        GLES30.glEnableVertexAttribArray(mtPositionHandle0);
		// 绑定到顶点固定属性数据缓冲
		GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,mVertexBufferId0); 
		// 将顶点固定属性数据送入渲染管线
		GLES30.glVertexAttribPointer(mtPositionHandle0, 4, GLES30.GL_FLOAT, false, 4*4, 0);

//        int[] state = new int[3];
//        // 查询当前绑定的TFO
//        GLES30.glGetIntegerv(GLES30.GL_TRANSFORM_FEEDBACK_BINDING,state,0);
//        android.util.Log.w("TOM",this + "] GL_TRANSFORM_FEEDBACK_BINDING " + Arrays.toString(state));
//
//        // 查询当前/最近GL_TRANSFORM_FEEDBACK_BUFFER_BINDING目标的绑定的VBO
//        GLES30.glGetIntegerv(GLES30.GL_TRANSFORM_FEEDBACK_BUFFER_BINDING,state,0);
//        android.util.Log.w("TOM",this + "] GL_TRANSFORM_FEEDBACK_BUFFER_BINDING v " + Arrays.toString(state));
//
//        // 查询GL_TRANSFORM_FEEDBACK_BUFFER_BINDING  目标下给定的绑定点(转换反馈属性流/TransformFeedback Attribute Stream)绑定的VBO
//        GLES30.glGetIntegeri_v(GLES30.GL_TRANSFORM_FEEDBACK_BUFFER_BINDING,0,state,0);
//        GLES30.glGetIntegeri_v(GLES30.GL_TRANSFORM_FEEDBACK_BUFFER_BINDING,1,state,1);
//        GLES30.glGetIntegeri_v(GLES30.GL_TRANSFORM_FEEDBACK_BUFFER_BINDING,2,state,2);
//        android.util.Log.w("TOM",this + "] GL_TRANSFORM_FEEDBACK_BUFFER_BINDING _v " + Arrays.toString(state));

          // TFO 使用系统默认的，每个ParitclForDraw实例，目前只有一个绑定点，绑定点对应的VBO在4和5之间切换
//        ParticleForDraw@4fc16d] GL_TRANSFORM_FEEDBACK_BINDING [0, 0, 0]
//        ParticleForDraw@4fc16d] GL_TRANSFORM_FEEDBACK_BUFFER_BINDING v [4, 0, 0]
//        ParticleForDraw@4fc16d] GL_TRANSFORM_FEEDBACK_BUFFER_BINDING _v [4, 0, 0]
//        ParticleForDraw@4fc16d] GL_TRANSFORM_FEEDBACK_BINDING [0, 0, 0]
//        ParticleForDraw@4fc16d] GL_TRANSFORM_FEEDBACK_BUFFER_BINDING v [5, 0, 0]
//        ParticleForDraw@4fc16d] GL_TRANSFORM_FEEDBACK_BUFFER_BINDING _v [5, 0, 0]
//        ParticleForDraw@4fc16d] GL_TRANSFORM_FEEDBACK_BINDING [0, 0, 0]
//        ParticleForDraw@4fc16d] GL_TRANSFORM_FEEDBACK_BUFFER_BINDING v [4, 0, 0]
//        ParticleForDraw@4fc16d] GL_TRANSFORM_FEEDBACK_BUFFER_BINDING _v [4, 0, 0]
//        ParticleForDraw@4fc16d] GL_TRANSFORM_FEEDBACK_BINDING [0, 0, 0]
//        ParticleForDraw@4fc16d] GL_TRANSFORM_FEEDBACK_BUFFER_BINDING v [5, 0, 0]
//        ParticleForDraw@4fc16d] GL_TRANSFORM_FEEDBACK_BUFFER_BINDING _v [5, 0, 0]
//        ParticleForDraw@4fc16d] GL_TRANSFORM_FEEDBACK_BINDING [0, 0, 0]
//        ParticleForDraw@4fc16d] GL_TRANSFORM_FEEDBACK_BUFFER_BINDING v [4, 0, 0]
//        ParticleForDraw@4fc16d] GL_TRANSFORM_FEEDBACK_BUFFER_BINDING _v [4, 0, 0]
//        ParticleForDraw@4fc16d] GL_TRANSFORM_FEEDBACK_BINDING [0, 0, 0]
//        ParticleForDraw@4fc16d] GL_TRANSFORM_FEEDBACK_BUFFER_BINDING v [5, 0, 0]
//        ParticleForDraw@4fc16d] GL_TRANSFORM_FEEDBACK_BUFFER_BINDING _v [5, 0, 0]


   	 	// 利用变换反馈计算粒子位置
		// 启用变换反馈渲染-顶点结果按GL_POINTS（点）组织形式输出到指定的变换反馈缓冲区中
   	 	GLES30.glBeginTransformFeedback(GLES30.GL_POINTS);
   	 	// 绘制点--不是真实的绘制
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, vCount);
        // 关闭变换反馈渲染
        GLES30.glEndTransformFeedback();


        //关闭禁止栅格化
        GLES30.glDisable(GLES30.GL_RASTERIZER_DISCARD);


        // 恢复系统默认
        GLES30.glUseProgram(0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);
        GLES30.glBindTransformFeedback(GLES30.GL_TRANSFORM_FEEDBACK,0); // hhl 必须放在 glBindBufferBase 之前，否则 glBindBufferBase就是修改了之前的TFO的
        GLES30.glBindBufferBase(GLES30.GL_TRANSFORM_FEEDBACK_BUFFER, 0, 0);

                        // 交替两个buffer
        index++;        // 索引值数组的索引值
		if(index>=2){   // 索引值超出数组长度
			index=0;    // 索引值设为0
		}
    }


    // ---------- 粒子渲染 -----------

    private void initShader(MySurfaceView mv)
    {
        String mVertexShader=ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        String mFragmentShader=ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        mProgram = createProgram(mVertexShader, mFragmentShader);

        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        muCameraPosition=GLES30.glGetUniformLocation(mProgram, "cameraPosition");
        muMMatrix=GLES30.glGetUniformLocation(mProgram, "uMMatrix");

        mmaxLifeSpan=GLES30.glGetUniformLocation(mProgram, "maxLifeSpan");
        muBj=GLES30.glGetUniformLocation(mProgram, "bj");
        muStartColor=GLES30.glGetUniformLocation(mProgram, "startColor");
        muEndColor=GLES30.glGetUniformLocation(mProgram, "endColor");

    }



    public void drawSelf(int texId,float maxLifeSpan,float[] startColor,float[] endColor)
    {   
        GLES30.glUseProgram(mProgram);


        GLES30.glUniform1f(mmaxLifeSpan, maxLifeSpan);
        GLES30.glUniform1f(muBj, halfSize);
        GLES30.glUniform4fv(muStartColor, 1, startColor, 0);
        GLES30.glUniform4fv(muEndColor, 1, endColor, 0);


        GLES30.glUniform3f(muCameraPosition,MatrixState.cx, MatrixState.cy, MatrixState.cz);
        GLES30.glUniformMatrix4fv(muMMatrix, 1, false, MatrixState.getMMatrix(), 0);
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);


        GLES30.glEnableVertexAttribArray(maPositionHandle);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,mVertexBufferIds[a[index]]);
        GLES30.glVertexAttribPointer(maPositionHandle, 4, GLES30.GL_FLOAT, false, 4*4, 0);
 		 

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);

        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, vCount);


        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,0);// 绑定到系统默认缓冲
    }
}

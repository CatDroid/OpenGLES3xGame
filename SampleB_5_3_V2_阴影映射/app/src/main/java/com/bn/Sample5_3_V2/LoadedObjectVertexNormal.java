package com.bn.Sample5_3_V2;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES30;

//加载后的物体——携带顶点信息，自动计算面平均法向量
public class LoadedObjectVertexNormal
{	
	int mProgram;//自定义渲染管线着色器程序id   
    int muMVPMatrixHandle;//总变换矩阵引用
    int muMMatrixHandle;//位置、旋转变换矩阵
    int muDiffHandle;
    int muShowDistortionHandle ;
    int muAutiDistortionHandle ;
    int muUsingFrontCullHandle ;
    int maPositionHandle; //顶点位置属性引用  
    int maNormalHandle; //顶点法向量属性引用  
    int maLightLocationHandle;//光源位置属性引用  
    int muMVPMatrixGYHandle;//光源总变换矩阵引用 
    int maCameraHandle; //摄像机位置属性引用 
    String mVertexShader;//顶点着色器代码脚本    	 
    String mFragmentShader;//片元着色器代码脚本    
    
    int mProgramForShadow;              // 自定义渲染管线着色器程序id
    int muMVPMatrixHandleForShadow;     // 总变换矩阵引用
    int muMMatrixHandleForShadow;       // 位置、旋转变换矩阵
    int maPositionHandleForShadow;      // 顶点位置属性引用
    int muLightLocationHandleForShadow; // 光源位置属性引用
    int muUsingRGBATexture ;            // 普通program 是否使用RGBA纹理
    int muUsingRGBATextureForShadow;    // 阴影program 是否使用rgba纹理
    String mVertexShaderForShadow;      // 顶点着色器代码脚本
    String mFragmentShaderForShadow;    // 片元着色器代码脚本
	
	FloatBuffer   mVertexBuffer;        // 顶点坐标数据缓冲
	FloatBuffer   mNormalBuffer;        // 顶点法向量数据缓冲
    int vCount=0;     
    
    public LoadedObjectVertexNormal(MySurfaceView mv,float[] vertices,float[] normals)
    {    	
    	//初始化顶点坐标与着色数据
    	initVertexData(vertices,normals);
    	//初始化shader        
    	intShader(mv);
    	//初始化shader        
    	initShaderForShadow(mv);
    }
    
    //初始化顶点坐标与着色数据的方法
    public void initVertexData(float[] vertices,float[] normals)
    {

    	vCount=vertices.length/3;   

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        ByteBuffer cbb = ByteBuffer.allocateDirect(normals.length*4);
        cbb.order(ByteOrder.nativeOrder());
        mNormalBuffer = cbb.asFloatBuffer();
        mNormalBuffer.put(normals);
        mNormalBuffer.position(0);

    }

    //初始化shader
    public void intShader(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex.glsl", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag.glsl", mv.getResources());
        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用  
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点颜色属性引用  
        maNormalHandle= GLES30.glGetAttribLocation(mProgram, "aNormal");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");  
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix"); 
        //获取程序中光源位置引用
        maLightLocationHandle=GLES30.glGetUniformLocation(mProgram, "uLightLocation");
        //获取程序中摄像机位置引用
        maCameraHandle=GLES30.glGetUniformLocation(mProgram, "uCamera"); 
        //获取光源总变换矩阵引用 
        muMVPMatrixGYHandle=GLES30.glGetUniformLocation(mProgram, "uMVPMatrixGY");


        muDiffHandle = GLES30.glGetUniformLocation(mProgram, "uDiff");

        muShowDistortionHandle = GLES30.glGetUniformLocation(mProgram, "uShowDistortion");

        muAutiDistortionHandle = GLES30.glGetUniformLocation(mProgram, "uAntiDistortion");

        muUsingFrontCullHandle = GLES30.glGetUniformLocation(mProgram, "uUsingFrontCull");

        muUsingRGBATexture = GLES30.glGetUniformLocation(mProgram, "uUsingRGBATexture");
    } 
    
    //初始化shader
    public void initShaderForShadow(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容
        mVertexShaderForShadow=ShaderUtil.loadFromAssetsFile("vertex_shadow.glsl", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShaderForShadow=ShaderUtil.loadFromAssetsFile("frag_shadow.glsl", mv.getResources());
        //基于顶点着色器与片元着色器创建程序
        mProgramForShadow = ShaderUtil.createProgram(mVertexShaderForShadow, mFragmentShaderForShadow);
        //获取程序中顶点位置属性引用  
        maPositionHandleForShadow = GLES30.glGetAttribLocation(mProgramForShadow, "aPosition");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandleForShadow = GLES30.glGetUniformLocation(mProgramForShadow, "uMVPMatrix");  
        //获取位置、旋转变换矩阵引用
        muMMatrixHandleForShadow = GLES30.glGetUniformLocation(mProgramForShadow, "uMMatrix"); 
        //获取程序中光源位置引用
        muLightLocationHandleForShadow =GLES30.glGetUniformLocation(mProgramForShadow, "uLightLocation");

        muUsingRGBATextureForShadow = GLES30.glGetUniformLocation(mProgramForShadow, "uUsingRGBATexture");

    }
    
    public void drawSelf(int texId,float[] mMVPMatrixGY)
    {        
        //制定使用某套着色器程序
        GLES30.glUseProgram(mProgram);
        //将最终变换矩阵传入着色器程序
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        //将光源最终变换矩阵传入着色器程序
        GLES30.glUniformMatrix4fv(muMVPMatrixGYHandle, 1, false, mMVPMatrixGY, 0);
        //将位置、旋转变换矩阵传入着色器程序
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        //将光源位置传入着色器程序
        GLES30.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
        //将摄像机位置传入着色器程序
        GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);

        GLES30.glUniform1f(muDiffHandle, MatrixState.sDiff);

        GLES30.glUniform1f(muShowDistortionHandle, Constant.SHOW_DISTORTION? 1.0f:0.0f );
        GLES30.glUniform1f(muAutiDistortionHandle, Constant.AUTO_ANTI_DISTORTION? 1.0f:0.0f);
        GLES30.glUniform1f(muUsingFrontCullHandle , Constant.USING_FRONT_CULL?1.0f:0.0f);

        GLES30.glUniform1f(muUsingRGBATexture, Constant.USING_R16F_TEXTURE?0.0f:1.0f);

        //将顶点位置数据
        GLES30.glVertexAttribPointer
        (
            maPositionHandle,
            3,
            GLES30.GL_FLOAT,
            false,
            3*4,
            mVertexBuffer
        );
        //将顶点法向量数据
        GLES30.glVertexAttribPointer
        (
            maNormalHandle,
            3,
            GLES30.GL_FLOAT,
            false,
            3*4,
            mNormalBuffer
        );
        //启用顶点位置、法向量数据
        GLES30.glEnableVertexAttribArray(maPositionHandle);
        GLES30.glEnableVertexAttribArray(maNormalHandle);
        //绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
        //绘制加载的物体
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);
    }
    
    public void drawSelfForShadow()
    {        
   	 	//制定使用某套着色器程序
   	 	GLES30.glUseProgram(mProgramForShadow);
        //将最终变换矩阵传入着色器程序
        GLES30.glUniformMatrix4fv(muMVPMatrixHandleForShadow, 1, false, MatrixState.getFinalMatrix(), 0);      
        //将位置、旋转变换矩阵传入着色器程序
        GLES30.glUniformMatrix4fv(muMMatrixHandleForShadow, 1, false, MatrixState.getMMatrix(), 0);   
        //将光源位置传入着色器程序   
        GLES30.glUniform3fv(muLightLocationHandleForShadow, 1, MatrixState.lightPositionFB);


        GLES30.glUniform1f(muUsingRGBATextureForShadow, Constant.USING_R16F_TEXTURE?0.0f:1.0f);

        //将顶点位置数据传入渲染管线
        GLES30.glVertexAttribPointer  
        (
        		maPositionHandleForShadow,   
        		3, 
        		GLES30.GL_FLOAT, 
        		false,
               3*4,   
               mVertexBuffer
        );       
        //启用顶点位置、法向量数据
        GLES30.glEnableVertexAttribArray(maPositionHandleForShadow);  
        //绘制加载的物体
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
    }
}

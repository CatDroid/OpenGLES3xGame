package com.bn.Sample10_3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES30;

import static com.bn.Sample10_3.Config.CONFIG_USING_FOG_MODE;
import static com.bn.Sample10_3.Config.FOG_MODE_VERTEX_SHARDER;

public class TextureRect
{
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用  
    int muMMatrixHandle;//位置、旋转变换矩阵
    int maCameraHandle; //摄像机位置属性引用 
    int maPositionHandle; //顶点位置属性引用 
    int maNormalHandle; //顶点法向量属性引用  
    int maLightLocationHandle;//光源位置属性引用

    int muFrog ; // hhl 雾浓度 只用在指数雾
    
    String mVertexShader;//顶点着色器    	 
    String mFragmentShader;//片元着色器
	
    private FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mNormalBuffer;//顶点法向量数据缓冲
    int vCount;//顶点数量
    
    float width;
    float height;
    
	public TextureRect(MySurfaceView mv, 
			float width,float height	//矩形的宽高
			)
	{

		this.width=width;
    	this.height=height;
    	
		initVertexData();
        initShader(mv);
        
	}
    //初始化顶点数据的方法
    public void initVertexData()
    {
        vCount=6;
        float vertices[]=
        {
        		-width/2, 0,-height/2,
        		-width/2, 0,height/2,
        		width/2, 0,height/2,
        		
        		-width/2, 0,-height/2,
        		width/2, 0,height/2,
        		width/2, 0, -height/2,
        };
        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
        
        float[] normals = {
        		0,1,0,
        		0,1,0,
        		0,1,0,
        		0,1,0,
        		0,1,0,
        		0,1,0,
        };

        ByteBuffer cbb = ByteBuffer.allocateDirect(normals.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mNormalBuffer.put(normals);
        mNormalBuffer.position(0);

    }
    public void initShader(MySurfaceView mv)
    {
    	//加载顶点着色器的脚本内容
        mVertexShader=ShaderUtil.loadFromAssetsFile(FOG_MODE_VERTEX_SHARDER, mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_light.sh", mv.getResources());  
        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);

        //获取程序中attribute和uniform变量引用
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        maNormalHandle= GLES30.glGetAttribLocation(mProgram, "aNormal");
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        maCameraHandle=GLES30.glGetUniformLocation(mProgram, "uCamera");
        maLightLocationHandle=GLES30.glGetUniformLocation(mProgram, "uLightLocation");
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");

        // 雾浓度
        if(CONFIG_USING_FOG_MODE == Config.FOG_MODE.MODE_EXP){
            muFrog = GLES30.glGetUniformLocation(mProgram, "U_Density");
        }

    }
	public void drawSelf(float currentFrogDensity)
	{
		// 指定使用某套着色器程序
   	 	GLES30.glUseProgram(mProgram);
        // 将 最终变换矩阵、位置旋转变换矩阵、摄像机位置、光源位置，传入渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
        GLES30.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);

        if(CONFIG_USING_FOG_MODE == Config.FOG_MODE.MODE_EXP){
            GLES30.glUniform1f(muFrog,currentFrogDensity);
        }

        // 将顶点法向量数据传入渲染管线
        GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3*4, mVertexBuffer);
        // 将顶点法向量数据传入渲染管线
        GLES30.glVertexAttribPointer(maNormalHandle, 3, GLES30.GL_FLOAT, false, 3*4, mNormalBuffer);
        // 启用顶点位置、法向量数据数组
        GLES30.glEnableVertexAttribArray(maPositionHandle);  
        GLES30.glEnableVertexAttribArray(maNormalHandle);  
        
        // 绘制矩形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
	}
	
}

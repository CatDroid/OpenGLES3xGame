package com.bn.Sample1_2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES30;

//加载后的物体——仅携带顶点信息，颜色随机(VAO)
public class LoadedObjectVertexNormalTexture {
    int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int muMMatrixHandle;//位置、旋转变换矩阵

    int maPositionHandle; //顶点位置属性引用  
    int maNormalHandle; //顶点法向量属性引用  
    int maTexCoorHandle; //顶点纹理坐标属性引用  

    int maLightLocationHandle;//光源位置属性引用  
    int maCameraHandle; //摄像机位置属性引用 

    String mVertexShader;//顶点着色器代码脚本    	 
    String mFragmentShader;//片元着色器代码脚本    

    int mVertexBufferId;//顶点坐标数据缓冲
    int mNormalBufferId;//顶点法向量数据缓冲
    int mTexCoorBufferId;//顶点纹理坐标数据缓冲

    int vCount = 0;
    int vaoId = 0;

    public LoadedObjectVertexNormalTexture(MySurfaceView mv, float[] vertices, float[] normals, float texCoors[]) {
        //调用初始化着色器的方法
        initShader(mv);
        //调用初始化顶点数据的方法
        initVertexData(vertices, normals, texCoors);

    }

    //初始化顶点数据的方法
    public void initVertexData(float[] vertices, float[] normals, float texCoors[]) {
        //缓冲id数组
        int[] buffIds = new int[3];
        //生成3个缓冲id
        GLES30.glGenBuffers(3, buffIds, 0);
        //顶点坐标数据缓冲 id
        mVertexBufferId = buffIds[0];
        //顶点法向量数据缓冲id
        mNormalBufferId = buffIds[1];
        //顶点纹理坐标数据缓冲id
        mTexCoorBufferId = buffIds[2];

        //顶点坐标数据的初始化================begin============================
        vCount = vertices.length / 3;

        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        FloatBuffer mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //绑定到顶点坐标数据缓冲 
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVertexBufferId);
        //向顶点坐标数据缓冲送入数据
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertices.length * 4, mVertexBuffer, GLES30.GL_STATIC_DRAW);
        //顶点坐标数据的初始化================end============================

        //顶点法向量数据的初始化================begin============================  
        ByteBuffer cbb = ByteBuffer.allocateDirect(normals.length * 4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        FloatBuffer mNormalBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mNormalBuffer.put(normals);//向缓冲区中放入顶点法向量数据
        mNormalBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //绑定到顶点法向量数据缓冲
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mNormalBufferId);
        //向顶点法向量数据缓冲送入数据
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, normals.length * 4, mNormalBuffer, GLES30.GL_STATIC_DRAW);
        //顶点着色数据的初始化================end============================

        //顶点纹理坐标数据的初始化================begin============================  
        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoors.length * 4);
        tbb.order(ByteOrder.nativeOrder());//设置字节顺序
        FloatBuffer mTexCoorBuffer = tbb.asFloatBuffer();//转换为Float型缓冲
        mTexCoorBuffer.put(texCoors);//向缓冲区中放入顶点纹理坐标数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //绑定到顶点纹理坐标数据缓冲
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mTexCoorBufferId);
        //向顶点纹理坐标数据缓冲送入数据
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, texCoors.length * 4, mTexCoorBuffer, GLES30.GL_STATIC_DRAW);
        //顶点纹理坐标数据的初始化================end============================
        //绑定到系统默认缓冲
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);

        initVAO();
    }

    public void initVAO() {
        int[] vaoIds = new int[1];
        //生成VAO
        GLES30.glGenVertexArrays(1, vaoIds, 0);
        vaoId = vaoIds[0];
        //绑定VAO
        GLES30.glBindVertexArray(vaoId);

        //启用顶点位置、法向量、纹理坐标数据
        GLES30.glEnableVertexAttribArray(maPositionHandle);
        GLES30.glEnableVertexAttribArray(maNormalHandle);
        GLES30.glEnableVertexAttribArray(maTexCoorHandle);
        //绑定到顶点坐标数据缓冲
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVertexBufferId);
        //将顶点位置数据送入渲染管线
        GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, 0);
        //绑定到顶点法向量数据缓冲
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mNormalBufferId);
        //将顶点法向量数据送入渲染管线
        GLES30.glVertexAttribPointer(maNormalHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, 0);
        //绑定到顶点纹理坐标数据缓冲
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mTexCoorBufferId);
        //将顶点纹理坐标数据送入渲染管线
        GLES30.glVertexAttribPointer(maTexCoorHandle, 2, GLES30.GL_FLOAT, false, 2 * 4, 0);
        // 绑定到系统默认缓冲
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);

        GLES30.glBindVertexArray(0);
    }

    //初始化着色器
    public void initShader(MySurfaceView mv) {
        //加载顶点着色器的脚本内容
        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        //加载片元着色器的脚本内容
        mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用  
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点颜色属性引用  
        maNormalHandle = GLES30.glGetAttribLocation(mProgram, "aNormal");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
        //获取程序中光源位置引用
        maLightLocationHandle = GLES30.glGetUniformLocation(mProgram, "uLightLocation");
        //获取程序中顶点纹理坐标属性引用  
        maTexCoorHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中摄像机位置引用
        maCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera");
    }

    public void drawSelf(int texId) {
        //指定使用某套着色器程序
        GLES30.glUseProgram(mProgram);

        // hhl Uniform变量还是需要单独每次调用
        //      但是顶点属性的操作(glEnableVertexAttribArray glVertexAttribPointer)就会直接用VAO就可以

        //将最终变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        //将位置、旋转变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        //将光源位置传入渲染管线
        GLES30.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
        //将摄像机位置传入渲染管线
        GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);

        GLES30.glBindVertexArray(vaoId);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);                 // 激活纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);          // 绑定纹理
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);        // 绘制加载的物体

        GLES30.glBindVertexArray(0);
    }
}

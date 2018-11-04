package com.bn.Sample2_6;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES30;

//加载后的物体——仅携带顶点信息，颜色随机
public class LoadedObjectVertexNormalTexture {
    int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int muMMatrixHandle;//位置、旋转变换矩阵
    int muFatFactor;//变胖系数
    int maPositionHandle; //顶点位置属性引用  
    int maNormalHandle; //顶点法向量属性引用  
    int maLightLocationHandle;//光源位置属性引用  
    int maCameraHandle; //摄像机位置属性引用 
    int maTexCoorHandle; //顶点纹理坐标属性引用  
    String mVertexShader;//顶点着色器代码脚本    	 
    String mFragmentShader;//片元着色器代码脚本    

    FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    FloatBuffer mNormalBuffer;//顶点法向量数据缓冲
    FloatBuffer mTexCoorBuffer;//顶点纹理坐标数据缓冲
    int vCount = 0;

    float fatFacror = 0;//变胖系数
    float fatFacrorStep=0.1f;//膨胀变胖系数步进

    LoadedObjectVertexNormalTexture(MySurfaceView mv, float[] vertices, float[] normals, float texCoors[]) {
        initVertexData(vertices, normals, texCoors);
        initShader(mv);
        if(MySurfaceView.CONFIG_USING_HEAD_INSTEAD_OF_BOOM){
            fatFacrorStep=0.001f;
        }else{
            fatFacrorStep=0.1f;
        }
    }

    private void initVertexData(float[] vertices, float[] normals, float texCoors[]) {

        vCount = vertices.length / 3;

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        ByteBuffer cbb = ByteBuffer.allocateDirect(normals.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        mNormalBuffer = cbb.asFloatBuffer();
        mNormalBuffer.put(normals);
        mNormalBuffer.position(0);


        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoors.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        mTexCoorBuffer = tbb.asFloatBuffer();
        mTexCoorBuffer.put(texCoors);
        mTexCoorBuffer.position(0);
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点纹理坐标数据的初始化================end============================
    }

    //初始化着色器
    private void initShader(MySurfaceView mv) {
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
        //获取变胖系数引用
        muFatFactor = GLES30.glGetUniformLocation(mProgram, "uFatFactor");
        //获取程序中光源位置引用
        maLightLocationHandle = GLES30.glGetUniformLocation(mProgram, "uLightLocation");
        //获取程序中顶点纹理坐标属性引用  
        maTexCoorHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中摄像机位置引用
        maCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera");
    }



    void drawSelf(int texId) {



        fatFacror += fatFacrorStep;                 //  计算新的膨胀系数
        if(MySurfaceView.CONFIG_USING_HEAD_INSTEAD_OF_BOOM){
            if(fatFacror>0.05f||fatFacror<0f){
                fatFacrorStep = -fatFacrorStep;         //  将膨胀系数的符号置反
            }
        }else{
            if(fatFacror>50f||fatFacror<0f){
                fatFacrorStep = -fatFacrorStep;         //  将膨胀系数的符号置反
            }
        }


        // Step.1 指定使用某套着色器程序
        GLES30.glUseProgram(mProgram);

        // Step.2 一致变量送入管线
        // a.将最终变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        // b.将平移、旋转变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        // c.将光源位置传入渲染管线
        GLES30.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
        // d.将摄像机位置传入渲染管线
        GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
        // e.将变胖系数传入渲染管线
        GLES30.glUniform1f(muFatFactor, fatFacror);

        // Step.3 顶点属性送入渲染管线
        // a.将顶点位置数送入渲染管线
        GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        // b.将顶点法向量数据送入渲染管线
        GLES30.glVertexAttribPointer(maNormalHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, mNormalBuffer);
        // c.将顶点纹理数据送入渲染管线
        GLES30.glVertexAttribPointer(maTexCoorHandle, 2, GLES30.GL_FLOAT, false, 2 * 4, mTexCoorBuffer);
        // d.启用顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maPositionHandle);
        // e.启用顶点法向量数据数组
        GLES30.glEnableVertexAttribArray(maNormalHandle);
        // f.启用顶点纹理数据数组
        GLES30.glEnableVertexAttribArray(maTexCoorHandle);


        // Step.4 绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);

        // Step.5 绘制加载的物体
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);
    }
}

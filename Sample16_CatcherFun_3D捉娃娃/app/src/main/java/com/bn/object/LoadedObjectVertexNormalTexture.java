package com.bn.object;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.bn.MatrixState.MatrixState3D;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import static com.bn.constant.SourceConstant.*;

//加载后的物体——仅携带顶点信息，颜色随机
public class LoadedObjectVertexNormalTexture {
    int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int muMMatrixHandle;//位置、旋转变换矩阵
    int maPositionHandle; //顶点位置属性引用  
    int maTexCoorHandle; //顶点纹理坐标属性引用  
    int vCount = 0;
    int mVertexBufferId;//顶点坐标数据缓冲 id
    int mTexCoorBufferId;//顶点纹理坐标数据缓冲id
    int vaoId = 0;
    int SwitchcolorHandle;//这是holebox.obj变换颜色的值的参数引用

    public LoadedObjectVertexNormalTexture(GLSurfaceView mv, float[] vertices, float[] normals, float texCoors[], int programId) {
        this.mProgram = programId;
        //初始化shader
        initShader();
        //初始化顶点坐标与着色数据
        initVertexData(vertices, normals, texCoors);
    }

    //初始化顶点坐标与着色数据的方法
    public void initVertexData(float[] vertices, float[] normals, float texCoors[]) {
        // 缓冲id数组
        int[] buffIds = new int[2];
        // 生成2个缓冲id VBO
        GLES30.glGenBuffers(2, buffIds, 0);
        // 顶点坐标数据缓冲 id  VBO
        mVertexBufferId = buffIds[0];
        // 顶点纹理坐标数据缓冲id VBO
        mTexCoorBufferId = buffIds[1];

        vCount = vertices.length / 3;

        // 创建‘顶点坐标数据缓冲’
        // vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
        // 绑定到‘顶点坐标数据缓冲’
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVertexBufferId);
        // 向顶点坐标数据缓冲送入数据
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertices.length * 4, mVertexBuffer, GLES30.GL_STATIC_DRAW);// 只写入一次

        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoors.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        FloatBuffer mTexCoorBuffer = tbb.asFloatBuffer();
        mTexCoorBuffer.put(texCoors);
        mTexCoorBuffer.position(0);

        // 绑定到‘顶点纹理坐标数据缓冲’
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mTexCoorBufferId);
        // 向’顶点纹理坐标数据缓冲‘送入数据
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, texCoors.length * 4, mTexCoorBuffer, GLES30.GL_STATIC_DRAW);
        //绑定到‘系统默认缓’冲
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);

        initVAO();
    }


    /**
     * VAO的全名是Vertex Array Object
     * 1. 它不是Buffer-Object，所以不用作存储数据
     * 2. 它针对·顶点·而言，也就是说它跟·顶点的绘制·息息相关
     * 3. 它的定位是state-object, 状态对象，记录`存储状态信息`,甚至可以认为VAO就是一个状态容器
     * 4. VAO记录的是一次绘制中做需要的信息，包括
     *      数据在哪里 glBindBuffer(GL_ARRAY_BUFFER)(glDrawArrays)或者是顶点索引数据 GL_ELEMENT_ARRAY_BUFFER(glDrawElements)
     *      数据的格式是怎样的 glVertexAttribPointer 明确每个元素由多少成分(3对于顶点坐标) 每个成分的数据类型(GL_FLOAT)
     *      属性关联的shader-attribute的location的启用, glEnableVertexAttribArray
     * 5. 好处: 渲染部分代码
     *
     * 使用注意:
     * 1. glBindBuffer(GL_ARRAY_BUFFER, NULL)/glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, NULL)
     *      一定要在glBindVertexArray(NULL)后面
     *
     * 2. glDrawElements、glDrawArrays的参数没有包含，还需要自己指定和调用glDraw
     *
     */
    public void initVAO() {
        int[] vaoIds = new int[1];
        //生成VAO
        GLES30.glGenVertexArrays(1, vaoIds, 0);
        vaoId = vaoIds[0];
        //绑定VAO
        GLES30.glBindVertexArray(vaoId);

        // 不需要先执行 glUseProgram

        //启用顶点位置、纹理坐标数据
        GLES30.glEnableVertexAttribArray(maPositionHandle);
        GLES30.glEnableVertexAttribArray(maTexCoorHandle);


        //绑定到顶点坐标数据缓冲
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVertexBufferId);
        //指定顶点位置数据
        GLES30.glVertexAttribPointer
                (
                        maPositionHandle,
                        3,
                        GLES30.GL_FLOAT,
                        false,
                        3 * 4,
                        0
                );
        //绑定到顶点纹理坐标数据缓冲
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mTexCoorBufferId);
        //指定顶点纹理坐标数据
        GLES30.glVertexAttribPointer
                (
                        maTexCoorHandle,
                        2,
                        GLES30.GL_FLOAT,
                        false,
                        2 * 4,
                        0
                );

        // 绑定到系统默认缓冲 VBO VAO
        GLES30.glBindVertexArray(0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        /*
        * 按照网上的说法 正确顺序应该是，但这里测试没有发现区别
        * glBindVertexArray(NULL);
        * glBindBuffer(GL_ARRAY_BUFFER, NULL);
        * glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, NULL);
        *
        * */
    }

    //初始化shader
    public void initShader() {
        // 获取程序中顶点位置属性引用
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        // 获取程序中顶点纹理坐标属性引用
        maTexCoorHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        // 获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        // 获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
        // 这是箱子的变换的参数值
        SwitchcolorHandle = GLES30.glGetUniformLocation(mProgram, "ColorCS");

    }

    public void drawSelf(int texId) {
        //制定使用某套着色器程序
        GLES30.glUseProgram(mProgram);
        //将最终变换矩阵传入着色器程序
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState3D.getFinalMatrix(), 0);
        //将位置、旋转变换矩阵传入着色器程序
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState3D.getMMatrix(), 0);
        GLES30.glUniform1f(SwitchcolorHandle, ColorCS);

        // 不需要再 glEnableVertexAttribArray 使能顶点属性
        // VAO 记录了所有 顶点属性 的设置状态,只要绑定VBO就可以
        GLES30.glBindVertexArray(vaoId);


        //绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
        //绘制加载的物体
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);
        GLES30.glBindVertexArray(0);

    }
}

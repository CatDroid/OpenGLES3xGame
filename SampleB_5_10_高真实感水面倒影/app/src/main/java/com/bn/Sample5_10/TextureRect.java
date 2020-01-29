package com.bn.Sample5_10;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import static com.bn.Sample5_10.Constant.*;

import android.opengl.GLES30;

//有波浪效果的纹理矩形
public class TextureRect {

    private int mProgram;

    private int muMVPMatrixHandle;
    private int muMMatrixHandle;
    private int muMVPMatrixMirrorHandle;
    private int muDYTexHandle;          // 倒影纹理属性引用
    private int muWaterTexHandle;       // 水自身纹理属性引用
    private int muNormalTexHandle;      // 法线纹理属性引用

    private int maPositionHandle;
    private int maTexCoorHandle;
    private int maNormalHandle;
    private int maLightLocationHandle;
    private int maCameraHandle;

    private FloatBuffer mVertexBuffer;  // 顶点坐标数据缓冲
    private FloatBuffer mTexCoorBuffer; // 顶点纹理坐标数据缓冲
    private FloatBuffer mNormalBuffer;  // 顶点法向量数据缓冲
    private IntBuffer mIndicesBuffer;

    public float mytime;//计时器
    private float[] zero1;//1号波振源
    private float[] zero2;//2号波振源
    private float[] zero3;//3号波振源

    private int[] indices;//索引数组
    private float vertices[]; //顶点坐标
    private float normals[];//法向量
    private float texCoor[];//纹理

    private float[] verticesForCal;//用于计算的顶点位置坐标数组
    private float[] normalsForCal;//用于计算的顶点法向量数组

    private int iCount;
    private int vCount = 0;   //顶点数量

    public TextureRect(MySurfaceView mv)
    {
        initVertexData();
        initShader(mv);
    }

    // 初始化顶点数据的方法  整个湖面是一个矩形,分成64*64的小矩形
    public void initVertexData()
    {

        int cols = 64;  // 列数
        int rows = 64;  // 行数

        float UNIT_SIZE = RECT_WIDTH / (cols - 1);              // 每格的单位长度

        ArrayList<Integer> alVertixIndices = new ArrayList<Integer>(); // 存放顶点坐标的ArrayList

        ArrayList<Float> alVertixV = new ArrayList<Float>();        // 存放顶点位置坐标的ArrayList
        ArrayList<Float> alVertixN = new ArrayList<Float>();        // 存放顶点法向量的ArrayList
        ArrayList<Float> alVertixT = new ArrayList<Float>();        // 存放顶点纹理坐标的ArrayList

        for (int j = 0; j < rows; j++)      // 行
        {
            for (int i = 0; i < cols; i++)  // 列
            {

                float zsx = -RECT_WIDTH / 2 + i * UNIT_SIZE;   // 矩形框中心在物体坐标系原点
                float zsz = -RECT_WIDTH / 2 + j * UNIT_SIZE;
                float zsy = 0;

                alVertixV.add(zsx);                     // 顶点坐标
                alVertixV.add(zsy);
                alVertixV.add(zsz);

                alVertixN.add(0.0f);                    // 顶点法线
                alVertixN.add(1.0f);
                alVertixN.add(0.0f);

                float s = zsx / RECT_WIDTH + 0.5f;      // 顶点纹理坐标
                float t = zsz / RECT_WIDTH + 0.5f;
                alVertixT.add(s);
                alVertixT.add(t);
            }
        }

        for (int i = 0; i < (rows - 1); i++)            // 行 按照卷绕方式,确定所有索引
        {
            for (int j = 0; j < (cols - 1); j++)        // 列
            {

                int x = i * rows + j;
                alVertixIndices.add(x);
                alVertixIndices.add(x + cols);
                alVertixIndices.add(x + 1);

                alVertixIndices.add(x + 1);
                alVertixIndices.add(x + cols);
                alVertixIndices.add(x + cols + 1);
            }
        }

        vCount = alVertixV.size() / 3;                  // 每个格子两个三角形，每个三角形3个顶点

        vertices = new float[vCount * 3];               // 每个顶点xyz三个坐标
        texCoor = new float[vCount * 2];
        normals = new float[vCount * 3];
        verticesForCal = new float[vCount * 3];
        normalsForCal = new float[vCount * 3];


        iCount = alVertixIndices.size();               // 索引的数目
        indices = new int[alVertixIndices.size()];     // 用int[]数组 代替 ArrayList 存放索引
        for (int i = 0; i < alVertixIndices.size(); i++) {
            indices[i] = alVertixIndices.get(i);
        }

        for (int i = 0; i < alVertixV.size(); i++) {   // 下面分别用float[]数组 代替ArrayList 存放顶点坐标/法线/纹理坐标
            vertices[i] = alVertixV.get(i);
        }

        for (int i = 0; i < alVertixN.size(); i++) {
            normals[i] = alVertixN.get(i);
        }

        for (int i = 0; i < alVertixT.size(); i++) {
            texCoor[i] = alVertixT.get(i);
        }

        // 初始化4个波的振源位置
        zero1 = new float[]{wave1PositionX, wave1PositionY, wave1PositionZ};
        zero2 = new float[]{wave2PositionX, wave2PositionY, wave2PositionZ};
        zero3 = new float[]{wave3PositionX, wave3PositionY, wave3PositionZ};

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        mTexCoorBuffer = cbb.asFloatBuffer();
        mTexCoorBuffer.put(texCoor);
        mTexCoorBuffer.position(0);

        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length * 4);
        nbb.order(ByteOrder.nativeOrder());
        mNormalBuffer = nbb.asFloatBuffer();
        mNormalBuffer.put(normals);
        mNormalBuffer.position(0);

        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 4);
        ibb.order(ByteOrder.nativeOrder());
        mIndicesBuffer = ibb.asIntBuffer();
        mIndicesBuffer.put(indices);
        mIndicesBuffer.position(0);

    }


    //初始化着色器的方法
    public void initShader(MySurfaceView mv)
    {
        //加载顶点着色器的脚本内容
        String mVertexShader = ShaderUtil.loadFromAssetsFile("water_vertex.glsl", mv.getResources());
        //加载片元着色器的脚本内容
        String mFragmentShader = ShaderUtil.loadFromAssetsFile("water_frag.glsl", mv.getResources());
        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用  
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
        //获取程序中顶点纹理坐标属性引用  
        maTexCoorHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        //获取镜像摄像机的观察与投影组合矩阵引用
        muMVPMatrixMirrorHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrixMirror");
        //获取倒影纹理引用
        muDYTexHandle = GLES30.glGetUniformLocation(mProgram, "sTextureDY");
        //获取水面自身的纹理引用
        muWaterTexHandle = GLES30.glGetUniformLocation(mProgram, "sTextureWater");
        //获取法向量纹理引用
        muNormalTexHandle = GLES30.glGetUniformLocation(mProgram, "sTextureNormal");
        //获取程序中顶点法向量属性引用  
        maNormalHandle = GLES30.glGetAttribLocation(mProgram, "aNormal");
        //获取程序中光源位置引用
        maLightLocationHandle = GLES30.glGetUniformLocation(mProgram, "uLightLocation");
        //获取程序中摄像机位置引用
        maCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera");
    }


    public void drawSelf(int texId, int waterId, int textureIdNormal, float[] mMVPMatrixMirror) {
        synchronized (slock) {
            updateData();
        }
        //指定使用某套shader程序
        GLES30.glUseProgram(mProgram);
        //将最终变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        //将镜像摄像机的观察与投影组合矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixMirrorHandle, 1, false, mMVPMatrixMirror, 0);
        //将位置、旋转变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        //将光源位置传入渲染管线
        GLES30.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
        //将摄像机位置传入渲染管线
        GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);

        //将顶点位置数据传入渲染管线
        GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        //将顶点纹理坐标数据传入渲染管线
        GLES30.glVertexAttribPointer(maTexCoorHandle, 2, GLES30.GL_FLOAT, false, 2 * 4, mTexCoorBuffer);
        //将顶点法向量数据传入渲染管线
        GLES30.glVertexAttribPointer(maNormalHandle, 3, GLES30.GL_FLOAT, false, 3 * 4, mNormalBuffer);

        GLES30.glEnableVertexAttribArray(maPositionHandle);  //启用顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maTexCoorHandle);  //启用顶点纹理坐标数据数组
        GLES30.glEnableVertexAttribArray(maNormalHandle);  //启用顶点法向量数据数组


        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);         // 激活0号纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);  // 水面倒影纹理

        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);         // 激活1号纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, waterId);// 水面自身纹理

        GLES30.glActiveTexture(GLES30.GL_TEXTURE2);         // 激活2号纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIdNormal);// 法向量纹理

        GLES30.glUniform1i(muDYTexHandle, 0);       // 使用0号纹理
        GLES30.glUniform1i(muWaterTexHandle, 1);    // 使用1号纹理
        GLES30.glUniform1i(muNormalTexHandle, 2);   // 使用2号纹理


        // 使用索引的方式绘制三角形
        GLES30.glDrawElements(GLES30.GL_TRIANGLES,
                                iCount,
                                GLES30.GL_UNSIGNED_INT,
                                mIndicesBuffer);

    }

    // 更新顶点数据和法向量数据的缓冲数据
    public void updateData() {

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length * 4);
        nbb.order(ByteOrder.nativeOrder());
        mNormalBuffer = nbb.asFloatBuffer();
        mNormalBuffer.put(normals);
        mNormalBuffer.position(0);

    }

    //计算顶点坐标、法向量
    public void calVerticesNormalAndTangent()
    {
        // 计算顶点坐标
        for (int i = 0; i < vCount; i++)
        {
            verticesForCal[i * 3] = vertices[i * 3];
            verticesForCal[i * 3 + 1] = findHeight(vertices[i * 3], vertices[i * 3 + 2]); // 根据x和z计算y
            verticesForCal[i * 3 + 2] = vertices[i * 3 + 2];
        }
        // 计算法向量
        normalsForCal = CalNormal.calNormal(verticesForCal, indices);

        synchronized (slock) {
            vertices = Arrays.copyOf(verticesForCal, verticesForCal.length);
            normals = Arrays.copyOf(normalsForCal, normalsForCal.length);
        }
    }

    // 计算3个波对顶点的影响之后的高度值
    private float findHeight(float x, float z)
    {
        float result = 0;

        // 获取点到中心的距离
        // 1号波 在(0,0,0)
        float distance1 = (float) Math.sqrt((x - zero1[0]) * (x - zero1[0]) + (z - zero1[2]) * (z - zero1[2]));
        // 顶点距离2号波起始位置的距离 在(-200,0,200)
        float distance2 = (float) Math.sqrt((x - zero2[0]) * (x - zero2[0]) + (z - zero2[2]) * (z - zero2[2]));
        // 顶点距离3号波起始位置的距离  在(-300,0,300)
        float distance3 = (float) Math.sqrt((x - zero3[0]) * (x - zero3[0]) + (z - zero3[2]) * (z - zero3[2]));

        // 3号 周期长 频率小 幅度大   Asin( ω*t + θ) = Asin( 2πf*t + θ)

        result = (float) (Math.sin( distance1 * 2 * waveFrequency1 * Math.PI + mytime) * waveAmplitude1);        //设置顶点高度
        result = (float) (result + Math.sin( distance2 * 2 * waveFrequency2 * Math.PI + mytime) * waveAmplitude2);//设置顶点高度
        result = (float) (result + Math.sin( distance3 * 2 * waveFrequency3 * Math.PI + mytime) * waveAmplitude3);//设置顶点高度
        return result;
    }
}

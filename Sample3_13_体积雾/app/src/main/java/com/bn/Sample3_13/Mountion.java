package com.bn.Sample3_13;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES30;

import static com.bn.Sample3_13.Constant.UNIT_SIZE;

public class Mountion {

    //扰动起始角
    float startAngle = 0;

    //自定义渲染管线的id
    private int mProgram;


    //总变化矩阵引用的id
    private int muMVPMatrixHandle;
    //基本变换阵引用的id
    private int muMMatrixHandle;
    //摄像机位置引用的id
    private int muCamaraLocationHandle;
    //顶点位置属性引用id
    private int maPositionHandle;
    //顶点纹理坐标属性引用id
    private int maTexCoorHandle;

    //草地的id
    private int sTextureGrassHandle;
    //石头的id
    private int sTextureRockHandle;

    //体积雾高度引用的id
    private int slabYHandle;
    //体积雾高度 扰动起始角引用的id
    private int startAngleHandle;
    // 草地最高位置
    private int landStartYYHandle;
    // 草地和石头中间过渡带高度
    private int landYSpanHandle;

    //顶点数据缓冲和纹理坐标数据缓冲
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTexCoorBuffer;

    //顶点数量
    int vCount = 0;

    public Mountion(MySurfaceView mv, float[][] yArray, int rows, int cols) {
        initVertexData(yArray, rows, cols);
        initShader(mv);
    }


    private void initVertexData(float[][] yArray, int rows, int cols) {

        vCount = cols * rows * 2 * 3;               // 每个格子两个三角形，每个三角形3个顶点
        float vertices[] = new float[vCount * 3];   // 每个顶点xyz三个坐标

        int count = 0; // 顶点计数器

        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < cols; i++) {

                //计算当前格子左上侧点坐标
                float zsx = (-cols / 2 + i) * UNIT_SIZE;
                float zsz = (-rows / 2 + j) * UNIT_SIZE;

                vertices[count++] = zsx;
                vertices[count++] = yArray[j][i];
                vertices[count++] = zsz;

                vertices[count++] = zsx;
                vertices[count++] = yArray[j + 1][i];
                vertices[count++] = zsz + UNIT_SIZE;

                vertices[count++] = zsx + UNIT_SIZE;
                vertices[count++] = yArray[j][i + 1];
                vertices[count++] = zsz;

                vertices[count++] = zsx + UNIT_SIZE;
                vertices[count++] = yArray[j][i + 1];
                vertices[count++] = zsz;

                vertices[count++] = zsx;
                vertices[count++] = yArray[j + 1][i];
                vertices[count++] = zsz + UNIT_SIZE;

                vertices[count++] = zsx + UNIT_SIZE;
                vertices[count++] = yArray[j + 1][i + 1];
                vertices[count++] = zsz + UNIT_SIZE;
            }
        }


        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        //顶点纹理坐标数据的初始化
        float[] texCoor = generateTexCoor(cols, rows);
        //创建顶点纹理坐标数据缓冲
        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length * 4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTexCoorBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mTexCoorBuffer.put(texCoor);//向缓冲区中放入顶点着色数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
    }

    //初始化Shader的方法
    void initShader(MySurfaceView mv) {

        String mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        String mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());

        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);

        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        maTexCoorHandle = GLES30.glGetAttribLocation(mProgram, "aTexCoor");
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");

        muCamaraLocationHandle = GLES30.glGetUniformLocation(mProgram, "uCamaraLocation");

        sTextureGrassHandle = GLES30.glGetUniformLocation(mProgram, "sTextureGrass");
        sTextureRockHandle = GLES30.glGetUniformLocation(mProgram, "sTextureRock");

        // 获取程序中体积雾产生者平面高度引用的id
        slabYHandle = GLES30.glGetUniformLocation(mProgram, "slabY");
        // 获取程序中体积雾高度扰动起始角引用的id(正弦扰动)
        startAngleHandle = GLES30.glGetUniformLocation(mProgram, "startAngle");
        // 草地的最高位置
        landStartYYHandle = GLES30.glGetUniformLocation(mProgram, "landStartY");
        // 过渡带长度
        landYSpanHandle = GLES30.glGetUniformLocation(mProgram, "landYSpan");
    }


    void drawSelf(int texId, int rock_textId) {

        GLES30.glUseProgram(mProgram);

        //将最终变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        //将基本变换矩阵传入渲染管线
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        //将摄像机位置传入渲染管线
        GLES30.glUniform3fv(muCamaraLocationHandle, 1, MatrixState.cameraFB);

        //将体积雾的雾平面高度传入渲染管线
        GLES30.glUniform1f(slabYHandle, Constant.TJ_GOG_SLAB_Y);
        //将体积雾扰动起始角传入渲染管线
        GLES30.glUniform1f(startAngleHandle, (float) Math.toRadians(startAngle));
        //修改扰动角的值，每次加3，取值范围永远在0~360的范围内
        startAngle = (startAngle + 3f) % 360.0f;

        //传送顶点位置数据
        GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT,false, 3 * 4, mVertexBuffer);
        //传送顶点纹理坐标数据
        GLES30.glVertexAttribPointer(maTexCoorHandle, 2, GLES30.GL_FLOAT, false, 2 * 4, mTexCoorBuffer);
        //允许顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maPositionHandle);
        GLES30.glEnableVertexAttribArray(maTexCoorHandle);

        //绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, rock_textId);
        GLES30.glUniform1i(sTextureGrassHandle, 0);
        GLES30.glUniform1i(sTextureRockHandle , 1);

        //传送相应的x参数
        GLES30.glUniform1f(landStartYYHandle, Constant.GLASS_HIGH_END);
        GLES30.glUniform1f(landYSpanHandle, Constant.GLASS_ROCK_HIGH);

        //绘制纹理矩形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);
    }


    float[] generateTexCoor(int bw, int bh) {

        float[] result = new float[bw * bh * 6 * 2];
        float sizew = 16.0f / bw; // 列数  整个地形纹理0~16 切分成列数为bw 纹理拉伸方式是repeat
        float sizeh = 16.0f / bh; // 行数

        int c = 0;
        for (int i = 0; i < bh; i++) {
            for (int j = 0; j < bw; j++) {
                //每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
                float s = j * sizew;
                float t = i * sizeh;

                result[c++] = s;
                result[c++] = t;

                result[c++] = s;
                result[c++] = t + sizeh;

                result[c++] = s + sizew;
                result[c++] = t;

                result[c++] = s + sizew;
                result[c++] = t;

                result[c++] = s;
                result[c++] = t + sizeh;

                result[c++] = s + sizew;
                result[c++] = t + sizeh;
            }
        }
        return result;
    }
}
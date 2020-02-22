package com.bn.Sample6_7;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

import com.bn.util.Vector3f;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

public class Constant {

    static FloatBuffer mVertexBufferForFlag;                // 顶点缓冲引用
    static Vector3f ballP = new Vector3f(0, 0, 0);  // 球中心点/质点的位置

    static Object lockA = new Object();
    static Object lockB = new Object();

    final static int NUMROWS = 11;// 旗子行列数
    final static int NUMCOLS = 30;

    final static int NUMSPTINGS = (NUMROWS * (NUMCOLS + 1)
                                     + (NUMROWS + 1) * NUMCOLS
                                     + 2 * NUMROWS * NUMCOLS); // 弹簧数目

    final static float RSTER = 1.5f / NUMROWS;// 计算粒子行间距
    final static float CSTER = 4.6f / NUMCOLS;// 计算粒子列间距
    final static float COLLISIONTOLERANCE = 0f;     // 地面位置
    static boolean isC = true;                  // 是否开启碰撞检测

    // 物理仿真参数
    final static float KRESTITUTION = 0.8f;         // 反弹系数
    final static float FRICTIONFACTOR = 0.5f;       // 摩擦系数
    final static float GRAVITY = -0.7f;                 // 重力加速度
    final static float SPRING_TENSION_CONSTANT = 100.f; // 构造弹簧参数
    final static float SPRING_SHEAR_CONSTANT = 100.f;   // 剪力弹簧参数
    final static float SPRING_DAMPING_CONSTANT = 2.f;   // 弹簧阻尼
    final static float WindForce = 0.0f;                // 风力 暂时无风
    final static float DRAGCOEFFICIENT = 0.01f;         // 空气阻力系数  跟空气密度 面积 有关系


    public static int initTexture(int drawableId, GLSurfaceView gsv)//textureId
    {
        //生成纹理ID
        int[] textures = new int[1];
        GLES30.glGenTextures
                (
                        1,          //产生的纹理id的数量
                        textures,   //纹理id的数组
                        0           //偏移量
                );
        int textureId = textures[0];
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);

        //通过输入流加载图片===============begin===================
        InputStream is = gsv.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try {
            bitmapTmp = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //通过输入流加载图片===============end===================== 
        GLUtils.texImage2D
                (
                        GLES30.GL_TEXTURE_2D, //纹理类型
                        0,
                        GLUtils.getInternalFormat(bitmapTmp),
                        bitmapTmp, //纹理图像
                        //GLUtils.getType(bitmapTmp),
                        0 //纹理边框尺寸
                );

        bitmapTmp.recycle();          //纹理加载成功后释放图片
        return textureId;
    }


}

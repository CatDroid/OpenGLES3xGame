package com.bn.Sample12_1;

import android.opengl.Matrix;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by Hanlon on 2018/7/8.
 */

public class TestCameraMatrix {

    private final static String TAG = "TEST";

    public static void test(){
        double[] mVMatrix = new double[16];
        setLookAtM(mVMatrix,0,
                0,      10,    -20,
                0,      0 ,    -20,
                1.0,    0,      0
                );
        Log.w(TAG,"mVMatrix = " + Arrays.toString(mVMatrix));

        setLookAtM(mVMatrix,0,
                0,      10,     -20,
                0,      -4 ,     -20, // 目标方向向量(目标-位置) 如果方向不变的话,只是长度变化的话，那么得到的视图变换矩阵也不变
                1.0,    0,      0
        );
        Log.w(TAG,"mVMatrix = " + Arrays.toString(mVMatrix));

        setLookAtM(mVMatrix,0,
                0,      10,    -20,
                0,      0 ,    -20,
                0,    -1.0,      0  // 摄像机正上方方向向量 与 目标方向向量 方向一致，会导致计算异常NaN
        );
        Log.w(TAG,"mVMatrix = " + Arrays.toString(mVMatrix));

    }

    // 九参数摄像机观察矩阵  右手坐标系
    public static void setLookAtM(double[] rm, int rmOffset,
                                  double eyeX, double eyeY, double eyeZ,           // 摄像机位于什么位置(摄像机坐标)
                                  double centerX, double centerY, double centerZ,  // 摄像机望向什么位置(目标坐标)
                                  double upX, double upY, double upZ               // 摄像机正上方
    ) {

        // See the OpenGL GLUT documentation for gluLookAt for a description
        // of the algorithm. We implement it in a straightforward way:

        double fx = centerX - eyeX;
        double fy = centerY - eyeY;
        double fz = centerZ - eyeZ;

        // Normalize f
        double rlf = 1.0f /length(fx, fy, fz);
        fx *= rlf;
        fy *= rlf;
        fz *= rlf;

        // 得到目标单位方向向量(目标坐标-位置坐标)

        // compute s = f x up (x means "cross product")
        double sx = fy * upZ - fz * upY;
        double sy = fz * upX - fx * upZ;
        double sz = fx * upY - fy * upX;

        // and normalize s
        double rls = 1.0f /length(sx, sy, sz);
        sx *= rls;
        sy *= rls;
        sz *= rls;
        // 得到与目标向量 和 摄像机向上方向向量 所在平面的垂直向量

        // compute u = s x f
        double ux = sy * fz - sz * fy;
        double uy = sz * fx - sx * fz;
        double uz = sx * fy - sy * fx;
        // 相当于 替换掉原来的 摄像机正上方方向向量 作为y轴


        rm[rmOffset + 0] = sx;
        rm[rmOffset + 1] = ux;
        rm[rmOffset + 2] = -fx;
        rm[rmOffset + 3] = 0.0f;

        rm[rmOffset + 4] = sy;
        rm[rmOffset + 5] = uy;
        rm[rmOffset + 6] = -fy;
        rm[rmOffset + 7] = 0.0f;

        rm[rmOffset + 8] = sz;
        rm[rmOffset + 9] = uz;
        rm[rmOffset + 10] = -fz;
        rm[rmOffset + 11] = 0.0f;

        rm[rmOffset + 12] = 0.0f;
        rm[rmOffset + 13] = 0.0f;
        rm[rmOffset + 14] = 0.0f;
        rm[rmOffset + 15] = 1.0f;

        // 摄像机变换矩阵/视图矩阵  可以看成是 坐标系转换+原点移动
        translateM(rm, rmOffset, -eyeX, -eyeY, -eyeZ);
    }


    public static double length(double x, double y, double z) { // 求向量的长度
        return  Math.sqrt(x * x + y * y + z * z);
    }

    public static void translateM(
            double[] m, int mOffset,
            double x, double y, double z) {
        for (int i=0 ; i<4 ; i++) {
            int mi = mOffset + i;
            m[12 + mi] += m[mi] * x + m[4 + mi] * y + m[8 + mi] * z;
            // OpenGLES 是列为主
            // 所以最后12 13 14 15 是代表位移
        }
    }

}

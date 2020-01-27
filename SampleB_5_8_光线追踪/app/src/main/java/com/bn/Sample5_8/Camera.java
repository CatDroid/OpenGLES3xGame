package com.bn.Sample5_8;

import static com.bn.Sample5_8.Constant.*;

import android.opengl.Matrix;


public class Camera {
    private Point3 eye, look, up; // 摄像头的9参数矩阵
    private Vector3 u, v, n;        // 摄像头坐标系三个基轴在世界坐标系中的表示(根据9参数矩阵)
    private Light light;

    public Camera(Light light)
    {
        this.light = light;
        eye = new Point3();
        look = new Point3();
        up = new Point3();
        u = new Vector3();
        v = new Vector3();
        n = new Vector3();
    }

    public Point3 getEye()
    {
        return new Point3(eye);
    }


    //设置摄像机位置的方法，主要是为了计算u、v、n三个向量的值
    public void setMyCamera
    (
            float cx, float cy, float cz,   // 摄像机位置
            float tx, float ty, float tz,   // 摄像机目标点
            float upx, float upy, float upz // 摄像机UP向量
    ) {

        float[] vMatrix = new float[16];//摄像机位置朝向9参数矩阵
        Matrix.setLookAtM(vMatrix, 0,
                        cx, cy, cz,
                        tx, ty, tz,
                        upx, upy, upz);
        eye.x = cx;
        eye.y = cy;
        eye.z = cz;
        look.x = tx;
        look.y = ty;
        look.z = tz;
        up.x = upx;
        up.y = upy;
        up.z = upz;
        //从矩阵中取出u、v、n三个向量的值，注意OpenGL中的向量按列存储
        u.x = vMatrix[0];
        u.y = vMatrix[4];
        u.z = vMatrix[8];
        v.x = vMatrix[1];
        v.y = vMatrix[5];
        v.z = vMatrix[9];
        n.x = vMatrix[2];
        n.y = vMatrix[6];
        n.z = vMatrix[10];
    }

    //光线跟踪的渲染方法
    public void raytrace(Scene mScene, ColorRect rect) {
        //打印开始时间和开始标志
        System.out.println("start...");
        long start = System.currentTimeMillis();

        // 摄像机的光追踪 光线从 '摄像机在世界坐标系的位置' 开始
        Ray theRay = new Ray();
        theRay.setStart(eye);

        // 开始光线跟踪
        for (int col = 0; col < nCols; col += blockSize) {

            for (int row = 0; row < nRows; row += blockSize) {

                // 根据所在行列数，计算光线方向(世界坐标系)
                // 摄像机近平面 位于 摄像机坐标系 (W_3D * (2 * col / nCols - 1), H_3D * (2 * row / nRows - 1), -N_3D)
                // z = -N_3D 为负数,因为物体总是位于摄像机坐标系中z轴的负半轴
                Vector3 dir1 = n.multiConst(-N_3D);
                Vector3 dir2 = u.multiConst(W_3D * (2 * col / nCols - 1)); // 2 * col / nCols - 1) 归一化,转到-1~1范围
                Vector3 dir3 = v.multiConst(H_3D * (2 * row / nRows - 1));
                Vector3 dir = dir1.add(dir2).add(dir3);

                // 设置光线发射方向
                theRay.setDir(dir);

                Color3f color = new Color3f();
                Point3 vertexPos = new Point3();
                Vector3 normal = new Vector3();

                // 计算此光线对应的各个量的值:交点的顶点颜色,顶点坐标,法线
                int isShadowFlag = mScene.shade(theRay, color, vertexPos, normal);

                // 如果光线和物体没有交点，说明是背景色，不进行绘制，继续下一条光线
                if (isShadowFlag == -1)
                {
                    continue;
                }

                rect.setColor(color.red, color.green, color.blue);          // 颜色

                                                                            // 顶点 法线 光源位置 摄像机位置 只用来计算光照
                rect.setPos3D(vertexPos.x, vertexPos.y, vertexPos.z);       // 变换后顶点位置
                rect.setNormal3D(normal.x, normal.y, normal.z);             // 变换后顶点处法向量
                rect.setLightPos3D(light.pos.x, light.pos.y, light.pos.z);  // 光源位置
                rect.setCameraPos3D(eye.x, eye.y, eye.z);                   // 摄像机位置

                rect.setShadow(isShadowFlag);                               // 是否在阴影中


                // 绘制的是基本块 所以要对基本块进行平移等变换
                rect.setColRow(col, row);                                   // 设置基本块在哪一行，哪一列
                rect.drawSelf();                                            // 绘制基本块
            }
        }

        //打印结束时间和结束标志
        long end = System.currentTimeMillis();
        System.out.println("time=" + (end - start) / 1000.0 + "s");
        System.out.println("finish...");
    }
}

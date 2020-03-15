package com.bn.Sample7_4;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Locale;

import android.opengl.GLES30;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.extras.gimpact.GImpactMeshShape;
import com.bulletphysics.linearmath.Transform;

import javax.vecmath.Vector3f;


public class LoadedObjectVertexNormal
{
    int mProgram;//自定义渲染管线程序id
    int muMVPMatrixHandle;//总变换矩阵引用id   
    int muMMatrixHandle;//位置、旋转变换矩阵
    int maCameraHandle; //摄像机位置属性引用id  
    int maPositionHandle; //顶点位置属性引用id 
    int maNormalHandle; //顶点法向量属性引用  
    int maLightLocationHandle;//光源位置属性引用  

    String mVertexShader;//顶点着色器    	 
    String mFragmentShader;//片元着色器

    private FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    private FloatBuffer mNormalBuffer;//顶点法向量数据缓冲
    int vCount = 0;//顶点数量
    CollisionShape loadShape;//碰撞形状引用
    float[] vertices;//顶点坐标数据数组
    float[] normals;//顶点法向量数据数组

    public LoadedObjectVertexNormal(float[] vertices, float[] normals)
    {
        this.vertices = vertices;
        this.normals = normals;

        vCount = vertices.length / 3; // 顶点的数目  一个顶点包含3个坐标float

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


        // 创建为任意碰撞形状（GImpact三角网状形状）服务的顶点数据缓冲
        ByteBuffer gVertices = ByteBuffer.allocateDirect(vCount * 3 * 4).order(ByteOrder.nativeOrder());
        for (int i = 0; i < vertices.length; i++)//将顶点数据添加到缓冲中
        {
            gVertices.putFloat(i * 4, vertices[i]);//将顶点数据添加到缓冲中
        }
        gVertices.position(0);//设置缓冲区起始位置

        // 创建为任意碰撞形状（GImpact三角网状形状）服务的顶点索引缓冲
        //ByteBuffer gIndices = ByteBuffer.allocateDirect(vCount * 4).order(ByteOrder.nativeOrder());
        ByteBuffer gIndices = ByteBuffer.allocateDirect(vCount * 3*  4).order(ByteOrder.nativeOrder());
        for (int i = 0; i < vCount; i++)
        {
            gIndices.putInt(i);//将索引数据添加到缓冲中
        }
        gIndices.position(0);//设置缓冲区起始位置

        int vertStride = 4 * 3;//顶点数据间隔
        int indexStride = 4 * 3;//索引数据间隔

        TriangleIndexVertexArray indexVertexArrays =  //创建三角形组顶点数据数组
                new TriangleIndexVertexArray
                        (
                                vCount / 3,//三角形的个数
                                gIndices,       // 索引缓冲
                                indexStride,    // 索引数据间隔
                                vCount,         // 顶点个数
                                gVertices,      // 顶点缓冲
                                vertStride      // 顶点数据间隔
                        );


        // 加载的任意形状的物体对应的碰撞形状
        GImpactMeshShape trimesh = new GImpactMeshShape(indexVertexArrays);
        trimesh.updateBound();  // 更新碰撞形状的边界

        Transform t = new Transform();
        Vector3f min = new Vector3f();
        Vector3f max = new Vector3f();
        trimesh.getAabb(t, min, max);
        System.out.println( "Aabb = " +
                String.format(Locale.ENGLISH,"min = %f %f %f max = %f %f %f",
                min.x, min.y, min.z,
                max.x, max.y, max.z));
        loadShape = trimesh;    // 保存碰撞形状引用

        System.out.println( "getMeshPartCount = " +
                ((GImpactMeshShape) loadShape).getMeshPartCount() );
    }

    //初始化shader
    public void initShader(int mProgram)
    {
        this.mProgram = mProgram;
        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中总变换矩阵引用id 
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取程序中顶点法向量属性引用  
        maNormalHandle = GLES30.glGetAttribLocation(mProgram, "aNormal");
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
        //获取程序中光源位置引用
        maLightLocationHandle = GLES30.glGetUniformLocation(mProgram, "uLightLocation");
        //获取程序中摄像机位置引用
        maCameraHandle = GLES30.glGetUniformLocation(mProgram, "uCamera");
    }

    public void drawSelf() {
        //制定使用某套shader程序
        GLES30.glUseProgram(mProgram);
        //将最终变换矩阵传入shader程序
        GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        //将位置、旋转变换矩阵传入着色器程序
        GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        //将光源位置传入着色器程序
        GLES30.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFBRed);
        //将摄像机位置传入着色器程序
        GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);

        //为画笔指定顶点位置数据
        GLES30.glVertexAttribPointer
                (
                        maPositionHandle,
                        3,
                        GLES30.GL_FLOAT,
                        false,
                        3 * 4,
                        mVertexBuffer
                );
        //将顶点法向量数据传入渲染管线
        GLES30.glVertexAttribPointer
                (
                        maNormalHandle,
                        3,
                        GLES30.GL_FLOAT,
                        false,
                        3 * 4,
                        mNormalBuffer
                );
        //允许顶点位置数据数组
        GLES30.glEnableVertexAttribArray(maPositionHandle);
        GLES30.glEnableVertexAttribArray(maNormalHandle);
        //绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);
    }
}

package com.bn.Sample7_4;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.extras.gimpact.GImpactCollisionAlgorithm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;

import static com.bn.Sample7_4.Constant.*;

class MySurfaceView extends GLSurfaceView
{

    private SceneRenderer mRenderer;        // 场景渲染器

    DiscreteDynamicsWorld mDynamicsWorld;    // 世界对象


    final ArrayList<BNThing> mThingsList = new ArrayList<BNThing>();
    final ArrayList<BNThing> mTobeAddThingsList = new ArrayList<BNThing>();


    CollisionShape boxShape;        // 共用的立方体
    CollisionShape planeShape;      // 共用的平面形状

    LoadedObjectVertexNormal[] lovoa = new LoadedObjectVertexNormal[3]; // 从obj加载的3个复杂形状物体

    int[] cubeTextureId = new int[2];                                   // 箱子面纹理

    Context mContext ;

    public MySurfaceView(Context context)
    {
        super(context);
        mContext = context ;

        this.setEGLContextClientVersion(3);

        // 初始化物理世界
        initWorld();

        mRenderer = new SceneRenderer();        // 创建场景渲染器
        setRenderer(mRenderer);                 // 设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    // 初始化物理世界的方法
    public void initWorld()
    {
        // 创建碰撞检测配置信息对象
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        // 创建碰撞检测算法分配者对象，其功能为扫描所有的碰撞检测对，并确定适用的检测策略对应的算法
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);

//        //设置整个物理世界的边界信息
//        Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
//        Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
//        int maxProxies = 1024;
//        //创建碰撞检测粗测阶段的加速算法对象
//        AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);

        BroadphaseInterface overlappingPairCache = new DbvtBroadphase();

        // 创建推动约束解决者对象
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

        // 创建物理世界对象
        mDynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
        // 设置重力加速度
        mDynamicsWorld.setGravity(new Vector3f(0, -10, 0));

        // 创建共用的立方体
        boxShape = new BoxShape(new Vector3f(Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE));

        // 创建共用的平面形状
        planeShape = new StaticPlaneShape(new Vector3f(0, 1, 0), 0);

        // 如果不设置 load obj文件的GImpact三角网状 物体 跟地面就不会碰撞 而是直接穿过
        // GImpactCollisionAlgorithm.registerAlgorithm(mDispatcher);
    }

    private class SceneRenderer implements GLSurfaceView.Renderer
    {
        int floorTextureId; // 地面纹理
        TexFloor floor;     // 纹理矩形1

        public void onDrawFrame(GL10 gl)
        {
            // 清除颜色缓存于深度缓存
            GLES30.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

            // 绘制箱子
            synchronized (mThingsList)
            {
                for (BNThing tc : mThingsList)
                {
                    MatrixState.pushMatrix();
                    tc.drawSelf();
                    MatrixState.popMatrix();
                }
            }

            // 绘制地板
            MatrixState.pushMatrix();
            floor.drawSelf(floorTextureId);
            MatrixState.popMatrix();
        }

        public void onSurfaceChanged(GL10 gl, int width, int height)
        {
            // 设置视窗大小及位置
            GLES30.glViewport(0, 0, width, height);
            // 计算透视投影的比例
            float ratio = (float) width / height;
            // 调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);
            MatrixState.setCamera(
                    CAMERA_X,   //人眼位置的X
                    CAMERA_Y,    //人眼位置的Y
                    CAMERA_Z,   //人眼位置的Z
                    TARGET_X,    //人眼球看的点X
                    TARGET_Y,   //人眼球看的点Y
                    TARGET_Z,   //人眼球看的点Z
                    0,
                    1,
                    0);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config)
        {
            // 关闭抗抖动
            GLES30.glDisable(GL10.GL_DITHER);
            // 设置屏幕背景色黑色RGBA
            GLES30.glClearColor(0, 0, 0, 0);
            // 启用深度测试
            GLES30.glEnable(GL10.GL_DEPTH_TEST);
            // 设置为打开背面剪裁
            GLES30.glEnable(GL10.GL_CULL_FACE);

            // 初始化变换矩阵
            MatrixState.setInitStack();
            // 初始化光源位置
            MatrixState.setLightLocationRed(CAMERA_X * 2, 50, CAMERA_Z * 2);

            // 编译shader 一个是 纹理采样+没有光照 1 + 一个是带光照的+固定颜色 2
            ShaderManager.loadCodeFromFile(mContext.getResources());
            ShaderManager.compileShader();

            // 加载不规则物体 顶点和法线信息
            lovoa[0] = LoadUtil.loadFromFile("table.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            lovoa[1] = LoadUtil.loadFromFile("yh.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            lovoa[2] = LoadUtil.loadFromFile("ch.obj", MySurfaceView.this.getResources(), MySurfaceView.this);

            // 立方体的两个纹理图  和  地面纹理图
            cubeTextureId[0] = initTexture(R.drawable.wood_bin2);
            cubeTextureId[1] = initTexture(R.drawable.wood_bin1);
            floorTextureId = initTextureRepeat(R.drawable.f6);

            // 创建地面矩形
            floor = new TexFloor(
                    ShaderManager.getTextureLightShaderProgram(),
                    80 * Constant.UNIT_SIZE,
                    -Constant.UNIT_SIZE,  // 地面在Y轴上的坐标
                    planeShape,
                    mDynamicsWorld);

            new Thread()
            {
                public void run()
                {
                    while (true)
                    {
                        try
                        {
                            synchronized (mTobeAddThingsList)
                            {
                                synchronized (mThingsList)
                                {
                                    for (BNThing tc : mTobeAddThingsList)
                                    {
                                        mThingsList.add(tc);
                                    }
                                }
                                mTobeAddThingsList.clear();
                            }
                            // 模拟
                            mDynamicsWorld.stepSimulation(1f / 200.f, 5);
                            Thread.sleep(5);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
    }

    //触摸事件回调方法
    int index = 0;

    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (index == 0)
                {
                    TexCube tcTemp = new TexCube
                            (
                                    MySurfaceView.this,
                                    Constant.UNIT_SIZE,
                                    boxShape,
                                    mDynamicsWorld,
                                    1,
                                    0,
                                    2,
                                    4,
                                    cubeTextureId,
                                    ShaderManager.getTextureLightShaderProgram()
                            );

                    // 设置箱子的初始速度
                    tcTemp.body.setLinearVelocity(new Vector3f(0, 2, -6));
                    tcTemp.body.setAngularVelocity(new Vector3f(5, 0, 0));

                    // 将新立方体加入到列表中
                    synchronized (mTobeAddThingsList)
                    {
                        mTobeAddThingsList.add(tcTemp);
                    }

                }
                else
                {

                    LoadRigidBody tcTemp = new LoadRigidBody
                            (
                                    ShaderManager.getColorShaderProgram(),
                                    1,
                                    lovoa[index - 1],
                                    0,
                                    2,
                                    4,
                                    mDynamicsWorld
                            );
                    // 设置物体的初始速度
                    tcTemp.body.setLinearVelocity(new Vector3f(0, 2, -6));
                    tcTemp.body.setAngularVelocity(new Vector3f(5, 0, 0));

                    // 将新立方体加入到列表中
                    synchronized (mTobeAddThingsList)
                    {
                        mTobeAddThingsList.add(tcTemp);
                    }

                    // ???? 什么作用
                    //GImpactCollisionAlgorithm.registerAlgorithm(dispatcher)
                    GImpactCollisionAlgorithm.registerAlgorithm(
                            (CollisionDispatcher)mDynamicsWorld.getDispatcher());
                }
                index = (index + 1) % 4;
                break;
        }
        return true;
    }

    public int initTexture(int drawableId)//textureId
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
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

        //通过输入流加载图片===============begin===================
        InputStream is = this.getResources().openRawResource(drawableId);
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

        //实际加载纹理
        GLUtils.texImage2D
                (
                        GLES30.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
                        0,                      //纹理的层次，0表示基本图像层，可以理解为直接贴图
                        bitmapTmp,              //纹理图像
                        0                      //纹理边框尺寸
                );
        bitmapTmp.recycle();          //纹理加载成功后释放图片

        return textureId;
    }

    public int initTextureRepeat(int drawableId)//textureId
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
        InputStream is = this.getResources().openRawResource(drawableId);
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

        //实际加载纹理
        GLUtils.texImage2D
                (
                        GLES30.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
                        0,                      //纹理的层次，0表示基本图像层，可以理解为直接贴图
                        bitmapTmp,              //纹理图像
                        0                      //纹理边框尺寸
                );
        bitmapTmp.recycle();          //纹理加载成功后释放图片

        return textureId;
    }
}

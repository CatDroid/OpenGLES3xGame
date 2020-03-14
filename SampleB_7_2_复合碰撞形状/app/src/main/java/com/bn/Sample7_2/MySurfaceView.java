package com.bn.Sample7_2;

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

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.util.ObjectArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CylinderShapeX;
import com.bulletphysics.collision.shapes.CylinderShapeZ;
import com.bulletphysics.collision.shapes.StaticPlaneShape;

class MySurfaceView extends GLSurfaceView
{

    DiscreteDynamicsWorld dynamicsWorld;            // 世界对象

    ArrayList<CubeCylinder> tca = new ArrayList<CubeCylinder>();
    ArrayList<CubeCylinder> tcaForAdd = new ArrayList<CubeCylinder>();

    CollisionShape planeShape;                      // 共用的平面形状
    CollisionShape[] csa = new CollisionShape[3];   // 圆柱、立方体组合

    Sample7_2_Activity activity;
    private SceneRenderer mRenderer;//场景渲染器

    public MySurfaceView(Context context)
    {

        super(context);
        activity = (Sample7_2_Activity) context;
        this.setEGLContextClientVersion(3);                     // 设置GLES版本为3.0

        //初始化物理世界
        initWorld();
        mRenderer = new SceneRenderer();                        // 创建场景渲染器
        setRenderer(mRenderer);                                 // 设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

    }

    // 初始化物理世界的方法
    public void initWorld()
    {

        // 创建碰撞检测配置信息对象
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        // 创建碰撞检测算法分配者对象，其功能为扫描所有的碰撞检测对，并确定适用的检测策略对应的算法
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);


        // 设置整个物理世界的边界信息
        Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
        Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
        int maxProxies = 1024;
        // 创建碰撞检测粗测阶段的加速算法对象
        AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);


        // 创建推动约束解决者对象
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();


        // 创建物理世界对象
        dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
        // 设置重力加速度
        dynamicsWorld.setGravity(new Vector3f(0, -10, 0));


        // 创建共用的立方体
        CollisionShape boxShape = new BoxShape(new Vector3f(Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE));
        // 创建共用的X圆柱  CylinderShape 就是CylinderShapeY 质心在原点 包围y轴
        //                CylinderShapeX 是包围x轴的  所以第一个参数是x轴 圆柱的长度  第二和第三 是椭圆的半径 y轴和z轴
        // cyShapeX 是 包围x轴的  高度的一半是 Constant.UNIT_SIZE * 1.8f  圆半径是 Constant.UNIT_SIZE / 2
        // cyShapeZ 是 包围z轴的  高度的一半是 Constant.UNIT_SIZE * 1.8f  圆半径是 Constant.UNIT_SIZE / 2
        CollisionShape cyShapeX = new CylinderShapeX(new Vector3f(Constant.UNIT_SIZE * 1.8f, Constant.UNIT_SIZE / 2, Constant.UNIT_SIZE / 2));
        // 创建共用的Z圆柱
        CollisionShape cyShapeZ = new CylinderShapeZ(new Vector3f(Constant.UNIT_SIZE / 2, Constant.UNIT_SIZE / 2, Constant.UNIT_SIZE * 1.8f));
        // 创建共用的形状数组
        csa[0] = boxShape;
        csa[1] = cyShapeX;
        csa[2] = cyShapeZ;


        // 创建共用的平面形状
        planeShape = new StaticPlaneShape(new Vector3f(0, 1, 0), 0);


    }

    //触摸事件回调方法
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                CubeCylinder tcTemp = new CubeCylinder
                        (
                                MySurfaceView.this,
                                Constant.UNIT_SIZE,
                                csa,
                                dynamicsWorld,
                                1,
                                0,
                                2,
                                4,
                                ShaderManager.getPrograms()
                        );
                //设置箱子的初始速度
                tcTemp.body.setLinearVelocity(new Vector3f(0, 2, -12));
                tcTemp.body.setAngularVelocity(new Vector3f(0, 0, 2));
                //将新立方体加入到列表中
                synchronized (tcaForAdd) {
                    tcaForAdd.add(tcTemp);
                }
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

    private class SceneRenderer implements GLSurfaceView.Renderer {
        int[] cubeTextureId = new int[2];//箱子面纹理
        int[] cyTextureId = new int[2];//圆柱纹理
        int floorTextureId;//地面纹理
        TexFloor floor;//纹理矩形1

        public void onDrawFrame(GL10 gl) {

            //清除颜色缓存于深度缓存
            GLES30.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            //绘制箱子
            synchronized (tca) {
                for (CubeCylinder tc : tca) {
                    MatrixState.pushMatrix();
                    tc.drawSelf(cubeTextureId, cyTextureId);
                    MatrixState.popMatrix();
                }
            }

            //绘制地板
            MatrixState.pushMatrix();
            floor.drawSelf(floorTextureId);
            MatrixState.popMatrix();
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置
            GLES30.glViewport(0, 0, width, height);
            //计算透视投影的比例
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);

            MatrixState.setCamera(
                    -1f,   //人眼位置的X
                    2f,    //人眼位置的Y
                    6.0f,   //人眼位置的Z
                    0,    //人眼球看的点X
                    2f,   //人眼球看的点Y
                    0,   //人眼球看的点Z
                    0,
                    1,
                    0);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色黑色RGBA
            GLES30.glClearColor(0, 0, 0, 0);
            //启用深度测试
            GLES30.glEnable(GL10.GL_DEPTH_TEST);
            //设置为打开背面剪裁
            GLES30.glEnable(GL10.GL_CULL_FACE);
            MatrixState.setInitStack();
            ShaderManager.loadCodeFromFile(activity.getResources());
            ShaderManager.compileShader();
            //初始化纹理
            floorTextureId = initTextureRepeat(R.drawable.f6);

            cubeTextureId[0] = initTexture(R.drawable.wood_bin2);
            cubeTextureId[1] = initTexture(R.drawable.wood_bin1);
            cyTextureId[0] = initTexture(R.drawable.cyh);
            cyTextureId[1] = initTexture(R.drawable.cy);

            // 创建地面矩形
            floor = new TexFloor(ShaderManager.getTextureShaderProgram(),
                    80 * Constant.UNIT_SIZE,
                    -Constant.UNIT_SIZE,  // 地面在y轴方向
                    planeShape,
                    dynamicsWorld);

            // 创建立方体
            int size = 2;
            float xStart = (-size / 2.0f + 0.5f) * (3.8f) * Constant.UNIT_SIZE;
            float yStart = 0.84f * Constant.UNIT_SIZE;
            float zStart = (-size / 2.0f + 0.5f) * (2 + 0.4f) * Constant.UNIT_SIZE - 4f;

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    for (int k = 0; k < size; k++) {
                        CubeCylinder tcTemp = new CubeCylinder
                                (
                                        MySurfaceView.this,
                                        Constant.UNIT_SIZE,
                                        csa,
                                        dynamicsWorld,
                                        1,
                                        xStart + i * (3.8f) * Constant.UNIT_SIZE,  // 世界坐标系中的位置,保存到每个rigid里面,渲染时候从rigid取出
                                        yStart + j * (3.64f) * Constant.UNIT_SIZE,
                                        zStart + k * (2 + 0.4f) * Constant.UNIT_SIZE,
                                        ShaderManager.getPrograms()
                                );
                        tca.add(tcTemp);
                        //使得立方体一开始是不激活的
                        tcTemp.body.forceActivationState(RigidBody.WANTS_DEACTIVATION);
                    }
                }
            }

            new Thread() {
                public void run() {
                    while (true) {
                        try {
                            synchronized (tcaForAdd) {
                                synchronized (tca) {
                                    for (CubeCylinder tc : tcaForAdd) {
                                        tca.add(tc);
                                    }
                                }
                                tcaForAdd.clear();
                            }
                            //模拟
                            dynamicsWorld.stepSimulation(1f / 60.f, 5);
                            Thread.sleep(20);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
    }
}

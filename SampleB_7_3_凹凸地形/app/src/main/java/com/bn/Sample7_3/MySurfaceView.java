package com.bn.Sample7_3;

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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;

import static com.bn.Sample7_3.Constant.*;

class MySurfaceView extends GLSurfaceView
{

    private SceneRenderer mRenderer;        // 场景渲染器
    DiscreteDynamicsWorld dynamicsWorld;    // 世界对象


    ArrayList<TexCube> tca = new ArrayList<TexCube>();
    ArrayList<TexCube> tcaForAdd = new ArrayList<TexCube>();
    CollisionShape boxShape;                // 共用的立方体

    Sample7_3_Activity activity;

    public MySurfaceView(Context context)
    {
        super(context);
        this.activity = (Sample7_3_Activity) context;
        this.setEGLContextClientVersion(3);
        Constant.initConstant(this.getResources());


        initWorld();                            // 初始化物理世界
        mRenderer = new SceneRenderer();        // 创建场景渲染器
        setRenderer(mRenderer);                 // 设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);// 设置渲染模式为主动渲染
    }

    //初始化物理世界的方法
    public void initWorld()
    {
        //创建碰撞检测配置信息对象
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        //创建碰撞检测算法分配者对象，其功能为扫描所有的碰撞检测对，并确定适用的检测策略对应的算法
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        //设置整个物理世界的边界信息
        Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
        Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
        int maxProxies = 1024;
        //创建碰撞检测粗测阶段的加速算法对象
        AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);
        //创建推动约束解决者对象
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
        //创建物理世界对象
        dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
        //设置重力加速度
        dynamicsWorld.setGravity(new Vector3f(0, -10, 0));
        // 创建共用的立方体
        boxShape = new BoxShape(new Vector3f(Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE));

    }

    private class SceneRenderer implements GLSurfaceView.Renderer {
        int[] cubeTextureId = new int[2];//箱子面纹理
        int floorTextureId;//地面纹理
        LandForm floor;//纹理矩形1

        public void onDrawFrame(GL10 gl) {
            //清除颜色缓存于深度缓存
            GLES30.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            //绘制箱子
            synchronized (tca)
            {
                for (TexCube tc : tca)
                {
                    MatrixState.pushMatrix();
                    tc.drawSelf(cubeTextureId);
                    MatrixState.popMatrix();
                }
            }

            //绘制地板
            MatrixState.pushMatrix();
            floor.drawSelf(floorTextureId);
            MatrixState.popMatrix();
        }

        public void onSurfaceChanged(GL10 gl, int width, int height)
        {
            //设置视窗大小及位置 
            GLES30.glViewport(0, 0, width, height);
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色黑色RGBA
            GLES30.glClearColor(0, 0, 0, 0);
            //启用深度测试
            GLES30.glEnable(GL10.GL_DEPTH_TEST);
            //设置为打开背面剪裁
            GLES30.glEnable(GL10.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();
            MatrixState.setCamera(
                    EYE_X,   //人眼位置的X
                    EYE_Y,    //人眼位置的Y
                    EYE_Z,   //人眼位置的Z
                    TARGET_X,    //人眼球看的点X
                    TARGET_Y,   //人眼球看的点Y
                    TARGET_Z,   //人眼球看的点Z
                    0,
                    1,
                    0);
            //初始化所用到的shader程序
            ShaderManager.loadCodeFromFile(activity.getResources());
            ShaderManager.compileShader();
            //初始化纹理
            cubeTextureId[0] = initTexture(R.drawable.wood_bin2);
            cubeTextureId[1] = initTexture(R.drawable.wood_bin1);
            floorTextureId = initTextureRepeat(R.drawable.floor);

            //创建地形
            floor = new LandForm(
                    MySurfaceView.this,
                    Constant.UNIT_SIZE,
                    -Constant.GT_UNIT_SIZE,
                    dynamicsWorld,
                    yArray,
                    yArray.length - 1,
                    yArray[0].length - 1,
                    ShaderManager.getTextureShaderProgram());

            //创建立方体       
            int size = 2;
            float xStart = (-size / 2.0f + 0.5f) * (2 + 0.4f) * Constant.GT_UNIT_SIZE;
            float yStart = 1.52f;
            float zStart = (-size / 2.0f + 0.5f) * (2 + 0.4f) * Constant.GT_UNIT_SIZE - 4f;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    for (int k = 0; k < size; k++) {
                        TexCube tcTemp = new TexCube       //创建纹理立方体
                                (
                                        MySurfaceView.this,        //MySurfaceView的引用
                                        Constant.GT_UNIT_SIZE,
                                        boxShape,
                                        dynamicsWorld,
                                        1,
                                        xStart + i * (2 + 0.4f) * Constant.GT_UNIT_SIZE,
                                        yStart + j * (2.02f) * Constant.GT_UNIT_SIZE,
                                        zStart + k * (2 + 0.4f) * Constant.GT_UNIT_SIZE,
                                        ShaderManager.getTextureShaderProgram()//着色器程序引用
                                );
                        tca.add(tcTemp);

                        // 使得立方体一开始是不激活的, 由于上面设置的物体位置是悬浮的 如果这里设置为Active 那么一开始立方体就会调到平面并碰撞
                        tcTemp.body.forceActivationState(RigidBody.WANTS_DEACTIVATION);
                    }
                }
            }

            new Thread() {
                public void run() {
                    while (true) {
                        try {
                            synchronized (tcaForAdd)        // 锁定新箱子所在集合
                            {
                                synchronized (tca)          // 锁定当前箱子的集合
                                {
                                    for (TexCube tc : tcaForAdd) {
                                        tca.add(tc);        // 向箱子集合中添加箱子
                                    }
                                }
                                tcaForAdd.clear();          // 将新箱子的集合清空
                            }
                                                            // 开始模拟
                            dynamicsWorld.stepSimulation(TIME_STEP, MAX_SUB_STEPS);
                                                            // 步进是16ms
                            Thread.sleep(20);           // 当前线程睡眠20毫秒
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();                                      // 启动线程
        }
    }


    //触摸事件回调方法
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction())
        {

            case MotionEvent.ACTION_DOWN:                   // 处理屏幕被按下的事件

                TexCube tcTemp = new TexCube                // 创建一个纹理立方体
                        (
                                this,                               // MySurfaceView的引用
                                Constant.UNIT_SIZE,                     // 尺寸
                                boxShape,                               // 碰撞形状
                                dynamicsWorld,                          // 物理世界
                                1,                                 // 刚体质量
                                0,                                   // 起始x坐标
                                2,                                   // 起始y坐标
                                4,                                   // 起始z坐标
                                ShaderManager.getTextureShaderProgram() // 着色器程序引用
                        );

                // 设置箱子的初始速度
                tcTemp.body.setLinearVelocity(new Vector3f(0, 2, -12));// 箱子直线运动的速度--Vx,Vy,Vz三个分量
                tcTemp.body.setAngularVelocity(new Vector3f(0, 0, 0)); // 箱子自身旋转的速度--绕箱子自身的x,y,x三轴旋转的速度

                // 将新立方体加入到列表中
                synchronized (tcaForAdd)// 锁定集合
                {
                    tcaForAdd.add(tcTemp);// 添加箱子
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
}

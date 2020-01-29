package com.bn.Sample5_10;

import java.io.IOException;
import java.io.InputStream;

import static com.bn.Sample5_10.Constant.*;

import android.opengl.GLSurfaceView;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MySurfaceView extends GLSurfaceView {
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;//角度缩放比例
    private SceneRenderer mRenderer;//场景渲染器        

    private float mPreviousX;//上次的触控位置X坐标
    private float mPreviousY;//上次的触控位置X坐标
    float xAngle = 0;
    float yAngle = 0;

    float[] mMVPMatrixMirror;   // 镜像摄像机的观察与投影组合矩阵
    int waterId;                // 水面自身的纹理id
    int textureIdNormal;        // 系统分配的纹理id
    TextureRect waterReflect;   // 用作水面的纹理矩形

    public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();    //创建场景渲染器
        setRenderer(mRenderer);                //设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;//计算触控点X位移
                float dy = y - mPreviousY;//计算触控点Y位移
                if (Math.abs(dx) <= 0.02f || Math.abs(dy) <= 0.02f) {
                    break;
                }
                xAngle += dx * TOUCH_SCALE_FACTOR;
                yAngle += dy * TOUCH_SCALE_FACTOR;
                if (xAngle < XANGLE_MIN) {//设置x轴旋转角度的最小值
                    xAngle = XANGLE_MIN;
                } else if (xAngle > XANGLE_MAX) {//设置x轴旋转角度的最大值
                    xAngle = XANGLE_MAX;
                }

                if (yAngle < YANGLE_MIN) {//设置y轴旋转角度的最小值
                    yAngle = YANGLE_MIN;
                } else if (yAngle > YANGLE_MAX) {//设置y轴旋转角度的最大值
                    yAngle = YANGLE_MAX;
                }
                //设置主摄像机和镜像摄像机的参数
                calculateMainAndMirrorCamera(xAngle, yAngle);
        }
        mPreviousX = x;//记录触控点X坐标
        mPreviousY = y;//记录触控点Y坐标
        return true;
    }

    private class SceneRenderer implements GLSurfaceView.Renderer {
        int waterReflectId;// 动态产生的水面倒影纹理Id
        int frameBufferId;//帧缓冲id
        int renderDepthBufferId;//渲染深度缓冲id
        //从指定的obj文件中加载对象
        LoadedObjectVertexNormalTexture house1;//房间绘制对象
        LoadedObjectVertexNormalTexture qiao;//房间绘制对象
        LoadedObjectVertexNormalTexture tong;//茶壶绘制对象
        LoadedObjectVertexNormalTexture tree0;//茶壶绘制对象
        LoadedObjectVertexNormalTexture skyBox;//天空盒
        LoadedObjectVertexNormalTexture table;//桌子
        LoadedObjectVertexNormalTexture woodPile;//木头
        LoadedObjectVertexNormalTexture mushRoom5;//蘑菇
        LoadedObjectVertexNormalTexture fb;//蘑菇
        LoadedObjectVertexNormalTexture flower;

        int house1Id;
        int qiaoId;
        int tongId;
        int tree0Id;
        int skyBoxId;
        int tableId;
        int woodPileId;
        int mushRoom5Id;
        int fbId;
        int flowerId;


        public void onDrawFrame(GL10 gl) {
            generateTextImage();
            if (!CONFIG_SHOW_REFLECTION)
            {
                DrawMirrorNegativeTexture();
            }

        }

        public void initFRBuffers() {

            int tia[] = new int[1];
            GLES30.glGenFramebuffers(tia.length, tia, 0);
            frameBufferId = tia[0];

            GLES30.glGenRenderbuffers(tia.length, tia, 0);
            renderDepthBufferId = tia[0];
            GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, renderDepthBufferId);
            GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER,
                    GLES30.GL_DEPTH_COMPONENT16, SCREEN_WIDTH, SCREEN_HEIGHT);

            int[] tempIds = new int[1];
            GLES30.glGenTextures(tempIds.length, tempIds, 0);
            waterReflectId = tempIds[0];
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, waterReflectId);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                    GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA,
                    SCREEN_WIDTH, SCREEN_HEIGHT, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);

            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, waterReflectId);
            GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, waterReflectId, 0);
            GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT, GLES30.GL_RENDERBUFFER, renderDepthBufferId);
        }

        // 第一次绘制，绘制场景，不绘制镜面
        private void generateTextImage()
        {

            GLES30.glViewport(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);


            if (CONFIG_SHOW_REFLECTION)
            {
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
            }
            else
            {
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId);
            }

            MatrixState.setCamera(mirrorCameraX, mirrorCameraY, mirrorCameraZ, targetX, targetY, targetZ, upX, upY, upZ);
            MatrixState.setProjectFrustum(left, right, bottom, top, near, far);

            mMVPMatrixMirror = MatrixState.getViewProjMatrix();

            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

            drawThings();
        }

        //第二次绘制，绘制场景，包括镜子
        private void DrawMirrorNegativeTexture() {

            GLES30.glViewport(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);//绑定系统默认的帧缓冲id
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

            MatrixState.setCamera(mainCameraX, mainCameraY, mainCameraZ, targetX, targetY, targetZ, upX, upY, upZ);
            MatrixState.setProjectFrustum(left, right, bottom, top, near, far);

            drawThings();

            /*
            *  1. 水面其实就是镜子  所以可以用镜像摄像机的实现 (纹理不用上下镜像,因为在投影贴图的时候会镜像)
            *  2. 由于水面有高低变化，所以每帧都要重新计算法向量，计算光照
            *  3. 使用法线纹理图的x,y(几乎接近0) 作为扰动(s,t) 采样镜像纹理图; 不用干扰也是可以的
            *
            * */
            // 最后绘制水面  这里没有像之前那样做模板测试等
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, 22);
            // waterReflectId 镜像虚拟摄像机的      镜像纹理图
            // waterId 水面本来的颜色              水面纹理图
            // textureIdNormal                  法向量纹理图
            waterReflect.drawSelf(waterReflectId, waterId, textureIdNormal, mMVPMatrixMirror);
            MatrixState.popMatrix();
        }

        // 绘制场景，不包括水面
        private void drawThings() {
            //绘制天空盒
            MatrixState.pushMatrix();
            skyBox.drawSelf(skyBoxId);
            MatrixState.popMatrix();

            //绘制房子
            MatrixState.pushMatrix();
            MatrixState.translate(0, 0, -66.5f);
            house1.drawSelf(house1Id);
            MatrixState.popMatrix();

            //绘制桥
            MatrixState.pushMatrix();
            MatrixState.translate(-25, 5, -15);
            qiao.drawSelf(qiaoId);
            MatrixState.popMatrix();


            //绘制桶
            MatrixState.pushMatrix();
            MatrixState.translate(15, -0.5f, 16);
            tong.drawSelf(tongId);
            MatrixState.popMatrix();

            //绘制树
            MatrixState.pushMatrix();
            MatrixState.translate(40, 0, -15);
            tree0.drawSelf(tree0Id);
            MatrixState.popMatrix();


            //绘制桌子
            MatrixState.pushMatrix();
            MatrixState.translate(-25, -0.5f, 5);
            table.drawSelf(tableId);
            MatrixState.popMatrix();

            //绘制木头
            MatrixState.pushMatrix();
            MatrixState.translate(23, -0.5f, 10);
            woodPile.drawSelf(woodPileId);
            MatrixState.popMatrix();

            //绘制蘑菇
            MatrixState.pushMatrix();
            MatrixState.translate(46, 0, -43);
            mushRoom5.drawSelf(mushRoom5Id);
            MatrixState.popMatrix();

            //绘制帆布
            MatrixState.pushMatrix();
            MatrixState.translate(-50, 0, -50);
            fb.drawSelf(fbId);
            MatrixState.popMatrix();


            //绘制花朵
            MatrixState.pushMatrix();
            MatrixState.translate(30, 0, -43);
            flower.drawSelf(flowerId);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(35, 0, -43);
            flower.drawSelf(flowerId);
            MatrixState.popMatrix();
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            SCREEN_WIDTH = width;
            SCREEN_HEIGHT = height;
            //设置视窗大小及位置
            GLES30.glViewport(0, 0, width, height);
            //计算GLSurfaceView的宽高比
            ratio = (float) SCREEN_WIDTH / SCREEN_HEIGHT;
            //初始化定向光方向
            MatrixState.setLightLocation(10, 350, 250);
            initFRBuffers();   //初始化帧缓冲
            initProject(1f);    //设置投影参数
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            // 设置屏幕背景色RGBA
            GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            // 打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            // 打开背面剪裁
            GLES30.glEnable(GLES30.GL_CULL_FACE);
            // 初始化变换矩阵
            MatrixState.setInitStack();

            // 加载要绘制的物体
            house1 = LoadUtil.loadFromFile("house1.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            qiao = LoadUtil.loadFromFile("qiao.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            tong = LoadUtil.loadFromFile("tong.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            tree0 = LoadUtil.loadFromFile("tree0.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            skyBox = LoadUtil.loadFromFile("skybox.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            table = LoadUtil.loadFromFile("table.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            woodPile = LoadUtil.loadFromFile("woodpile.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            mushRoom5 = LoadUtil.loadFromFile("mushroom5.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            fb = LoadUtil.loadFromFile("fb.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            flower = LoadUtil.loadFromFile("flower1.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            waterReflect = new TextureRect(MySurfaceView.this);

            // 加载纹理
            house1Id = initTexture(R.drawable.house1);
            qiaoId = initTexture(R.drawable.qiao);
            tongId = initTexture(R.drawable.stuff02);
            tree0Id = initTexture(R.drawable.tree0);
            skyBoxId = initTexture(R.drawable.sky);
            tableId = initTexture(R.drawable.tree0);
            woodPileId = initTexture(R.drawable.stuff01);
            mushRoom5Id = initTexture(R.drawable.vegetation01);
            waterId = initTexture(R.drawable.water);
            textureIdNormal = initTexture(R.drawable.resultnt);
            fbId = initTexture(R.drawable.stuff01);
            flowerId = initTexture(R.drawable.vegetation01);

            // 初始化主摄像机和镜像摄像机的参数
            calculateMainAndMirrorCamera(0, 15);

            UpdateThread ut = new UpdateThread(MySurfaceView.this);
            ut.start();
        }
    }

    public int initTexture(int drawableId)//textureId
    {
        int[] textures = new int[1];
        GLES30.glGenTextures(1, textures, 0);
        int textureId = textures[0];
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);


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

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0,
                        GLUtils.getInternalFormat(bitmapTmp),
                        bitmapTmp,
                        GLUtils.getType(bitmapTmp), 0);
        bitmapTmp.recycle();
        return textureId;
    }
}

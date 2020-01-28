package com.bn.Sample5_9_V1;

import java.io.IOException;
import java.io.InputStream;

import static com.bn.Sample5_9_V1.Constant.*;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.bn.Sample5_9_V1.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MySurfaceView extends GLSurfaceView
{

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;//角度缩放比例
    private SceneRenderer mRenderer;    // 场景渲染器
    int skyBoxTextureId;                // 系统分配的纹理id
    int teaPotTextureId;
    private float mPreviousX;           // 上次的触控位置X坐标
    float xAngle = 0;

    float[] mMVPMatrixMirror;           // 镜像摄像机的投影与观察组合矩阵

    public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3);                 // 设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();                    // 创建场景渲染器
        setRenderer(mRenderer);                             // 设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);// 设置渲染模式为主动渲染
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;//计算触控点X位移
                if (Math.abs(dx) <= 0.02f) {
                    break;
                }
                xAngle += dx * TOUCH_SCALE_FACTOR;
                if (xAngle < ANGLE_MIN) {
                    xAngle = ANGLE_MIN;
                } else if (xAngle > ANGLE_MAX) {
                    xAngle = ANGLE_MAX;
                }
                calculateMainAndMirrorCamera(xAngle);
        }
        mPreviousX = x;//记录触控点位置
        return true;
    }

    private class SceneRenderer implements GLSurfaceView.Renderer {


        LoadedObjectVertexNormalTexture lovo1;      // 房间绘制对象
        LoadedObjectVertexNormalTexture bed;        // 床绘制对象
        LoadedObjectVertexNormalTexture deng;       // 灯绘制对象
        LoadedObjectVertexNormalTexture book0;      // 书绘制对象
        LoadedObjectVertexNormalTexture chair;      // 椅子绘制对象
        LoadedObjectVertexNormalTexture book1;      // 书绘制对象
        LoadedObjectVertexNormalTexture border;     // 镜子边框绘制对象

        TextureRect mirror;                         // 用作镜子的纹理矩形对象

        int mirrorId;                               // 动态产生的镜像纹理Id
        int bedId;                                  // 从文件加载图片的纹理Id
        int dengId;
        int bookId0;
        int bookId1;
        int chairId;
        int borderId;

        int renderDepthBufferId;                    // 渲染深度缓冲id
        int frameBufferId;                          // 帧缓冲id

        public void onDrawFrame(GL10 gl) {
            generateTextImage();
            drawMirrorNegativeTexture();
        }

        private void initFRBuffers() {

            if (CONFIG_USING_CUSTOM_FBO)
            {

                int tia[]=new int[1];
                GLES30.glGenFramebuffers(tia.length, tia, 0);
                frameBufferId=tia[0];

                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId);

                GLES30.glGenRenderbuffers(tia.length, tia, 0);
                renderDepthBufferId = tia[0];
                GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, renderDepthBufferId);
                GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, GLES30.GL_DEPTH_COMPONENT16, SCREEN_WIDTH, SCREEN_HEIGHT);

                int[] tempIds = new int[1];
                GLES30.glGenTextures(tempIds.length,tempIds,0);
                mirrorId = tempIds[0];
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,mirrorId);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                        GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_LINEAR);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                        GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                        GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                        GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
                GLES30.glTexImage2D(
                        GLES30.GL_TEXTURE_2D,0, GLES30.GL_RGBA, SCREEN_WIDTH, SCREEN_HEIGHT,
                        0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);

                GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER,
                        GLES30.GL_COLOR_ATTACHMENT0,
                        GLES30.GL_TEXTURE_2D,
                        mirrorId,0);
                GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER,
                        GLES30.GL_DEPTH_ATTACHMENT,
                        GLES30.GL_RENDERBUFFER,
                        renderDepthBufferId);
            }
            else
            {
                int[] tempIds = new int[1];
                GLES30.glGenTextures(1, tempIds, 0);
                mirrorId = tempIds[0];

                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mirrorId);

                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
            }
        }

        // 第一次绘制，绘制场景，不绘制镜面, 渲染到Surface FBO=0
        private void generateTextImage() {
            //设置视口大小
            GLES30.glViewport(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT); // 实际上Surface的颜色缓冲区并没有这么大

            if (CONFIG_USING_CUSTOM_FBO) {
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferId);
            }

            //设置镜像摄像机的9参数
            MatrixState.setCamera(mirrorCameraX, mirrorCameraY, mirrorCameraZ, targetX, targetY, targetZ, upX, upY, upZ);
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(left, right, bottom, top, near, far);
            //获取镜像摄像机的观察与投影组合矩阵
            mMVPMatrixMirror = MatrixState.getViewProjMatrix(); // 后面投影映射 绘制镜面时候使用

            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            drawThings();

            // 从FBO复制颜色缓冲区到指定纹理
            if (CONFIG_USING_CUSTOM_FBO) {
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
            }else{
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mirrorId);
                GLES30.glCopyTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGB8, 0, 0,
                        SCREEN_WIDTH, SCREEN_HEIGHT, 0);
            }
        }

        // 第二次绘制，绘制场景，包括镜子
        public void drawMirrorNegativeTexture() {
            //设置视口大小
            GLES30.glViewport(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
            //清除深度缓冲与颜色缓冲
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            //设置主摄像机的9参数
            MatrixState.setCamera(mainCameraX, mainCameraY, mainCameraZ, targetX, targetY, targetZ, upX, upY, upZ);
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(left, right, bottom, top, near, far);

            drawThings();

            // 绘制镜子边框
            MatrixState.pushMatrix();
            MatrixState.translate(2.2f, 12, targetZ - 2);      // 尺寸是40*40
            border.drawSelf(borderId);   // mirror.obj  mirror.png
            MatrixState.popMatrix();

            // 绘制镜子
            MatrixState.pushMatrix();
            MatrixState.translate(2.0f, 12, targetZ - 1.7f);  // 镜子相对于墙和边框(矩形)前移一点  mirror.obj 30*30
            mirror.drawSelf(mirrorId, mMVPMatrixMirror);             // mirror 是 TextureRect mirror_vertex.glsl
            MatrixState.popMatrix();
        }

        //绘制场景，不包括镜子
        public void drawThings() {
            /*
                v  -37.0068 0.0000  -54.9883
                v  -37.0068 0.0000  35.0117
                v  52.9932  0.0000  -54.9883
                v  52.9932  0.0000  35.0117
                v  52.9932  90.0000 35.0117
                v  -37.0068 90.0000 35.0117
                v  52.9932  90.0000 -54.9883
                v  -37.0068 90.0000 -54.9883
            */
            //绘制房间
            MatrixState.pushMatrix();
            MatrixState.translate(0, -10, 7);   // 由于天空盒的中心不是在0,0,0 而是在 (10,0,-10)
            lovo1.drawSelf(skyBoxTextureId);            // 天空盒 房间的4个墙和上下 尺寸是90*90*90
            MatrixState.popMatrix();                    // 位移动后,中心点在世界坐标系 (10,-10,-3)

            MatrixState.pushMatrix();
            MatrixState.translate(40, 0, -12);
            MatrixState.rotate(180, 0, 1, 0);
            deng.drawSelf(dengId);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(14, -7, -18);
            book1.drawSelf(bookId1);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(22, 0, 8);
            MatrixState.rotate(180, 0, 1, 0);
            bed.drawSelf(bedId);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(-40, 30, 10);
            book0.drawSelf(bookId0);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(-40, 20, 20);
            book0.drawSelf(bookId0);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(-15, 0, -32);
            chair.drawSelf(chairId);
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            MatrixState.translate(40, 0, 34);
            MatrixState.rotate(180, 0, 1, 0);
            deng.drawSelf(dengId);
            MatrixState.popMatrix();

            //绘制镜子边框 绘制镜像 也绘制镜框也是可以的
//            MatrixState.pushMatrix();
//            MatrixState.translate(2.2f,12,targetZ-2);
//            border.drawSelf(borderId);
//            MatrixState.popMatrix();
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {

            GLES30.glViewport(0, 0, width, height);

            SCREEN_WIDTH = width;
            SCREEN_HEIGHT = height;
            ratio = (float) SCREEN_WIDTH / SCREEN_HEIGHT;

            MatrixState.setLightLocation(0, 10, 30);

            initFRBuffers();
            initProject(1f);    // 设置投影参数 (?? 没有影响)
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            GLES30.glEnable(GLES30.GL_CULL_FACE);

            MatrixState.setInitStack();

            //加载要绘制的物体
            lovo1 = LoadUtil.loadFromFile("skybox.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            bed = LoadUtil.loadFromFile("chuang.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            deng = LoadUtil.loadFromFile("deng.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            book0 = LoadUtil.loadFromFile("shu00.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            chair = LoadUtil.loadFromFile("yizi0.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            book1 = LoadUtil.loadFromFile("shu1.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            border = LoadUtil.loadFromFile("mirror.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            mirror = new TextureRect(MySurfaceView.this);

            skyBoxTextureId = initTexture(R.drawable.skybox1);
            bedId = initTexture(R.drawable.chuang3);  // R.drawable.chuang3  可以在3ds max UVW展开 UV编辑器看到贴图坐标
            dengId = initTexture(R.drawable.deng2);
            bookId0 = initTexture(R.drawable.shu0);
            bookId1 = initTexture(R.drawable.shu1);
            chairId = initTexture(R.drawable.yizi);
            borderId = initTexture(R.drawable.mirror);

            calculateMainAndMirrorCamera(xAngle);
        }
    }


    public int initTexture(int drawableId) {

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

        Log.e(TAG, String.format("internal format 0x%s type 0x%s",
                Integer.toHexString(GLUtils.getInternalFormat(bitmapTmp)),  // GL_LUMINANCE ??
                Integer.toHexString(GLUtils.getType(bitmapTmp)))            // GL_UNSIGNED_BYTE
        );
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0,
                GLUtils.getInternalFormat(bitmapTmp),
                bitmapTmp,
                GLUtils.getType(bitmapTmp), 0);

        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.e(TAG, "ERROR = " + GLUtils.getEGLErrorString(error));
        }

        bitmapTmp.recycle();
        return textureId;
    }
}

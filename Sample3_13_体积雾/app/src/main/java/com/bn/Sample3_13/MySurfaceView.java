package com.bn.Sample3_13;

import static com.bn.Sample3_13.Constant.*;
import static com.bn.Sample3_13.Sample3_13Activity.*;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;

public class MySurfaceView extends GLSurfaceView {
    static float direction = 0; // 视线方向
    static float cx = 0;        // 摄像机x坐标
    static float cz = 60;       // 摄像机z坐标

    static float tx = 0;        // 观察目标点x坐标
    static float tz = 0;        // 观察目标点z坐标
    static final float DEGREE_SPAN = (float) (3.0 / 180.0f * Math.PI);//摄像机每次转动的角度

    boolean flag = true;        // 线程循环的标志位
    float x;
    float y;
    float Offset = 20;
    SceneRenderer mRender;


    public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3);                     // 设置使用OPENGL ES3.0
        mRender = new SceneRenderer();                          // 创建场景渲染器
        setRenderer(mRender);                                   // 设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);   // 设置渲染模式为主动渲染
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getX();
        y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                flag = true;
                new Thread() {
                    public void run() {
                        while (flag) {
                            if (x > 0 && x < WIDTH / 2 && y > 0 && y < HEIGHT / 2) {//向前
                                cx = cx - (float) Math.sin(direction) * 1.0f;
                                cz = cz - (float) Math.cos(direction) * 1.0f;
                            } else if (x > WIDTH / 2 && x < WIDTH && y > 0 && y < HEIGHT / 2) {//向后
                                cx = cx + (float) Math.sin(direction) * 1.0f;
                                cz = cz + (float) Math.cos(direction) * 1.0f;
                            } else if (x > 0 && x < WIDTH / 2 && y > HEIGHT / 2 && y < HEIGHT) {
                                direction = direction + DEGREE_SPAN;
                            } else if (x > WIDTH / 2 && x < WIDTH && y > HEIGHT / 2 && y < HEIGHT) {
                                direction = direction - DEGREE_SPAN;
                            }
                            try {
                                Thread.sleep(100);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
                break;
            case MotionEvent.ACTION_UP:
                flag = false;
                break;
        }
        //设置新的观察目标点XZ坐标
        tx = (float) (cx - Math.sin(direction) * Offset);//观察目标点x坐标
        tz = (float) (cz - Math.cos(direction) * Offset);//观察目标点z坐标
        //设置新的摄像机位置
        MatrixState.setCamera(cx, CAMERA_Y, cz, tx, CAMERA_Y - 5, tz, 0, 1, 0);
        return true;
    }

    private class SceneRenderer implements GLSurfaceView.Renderer {

        Mountion mountion;
        int mountionId;
        int rockId;

        @Override
        public void onDrawFrame(GL10 gl) {

            // 清除深度缓冲与颜色缓冲
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

            // 渲染物体-山
            MatrixState.pushMatrix();
            mountion.drawSelf(mountionId, rockId);
            MatrixState.popMatrix();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {

            GLES30.glViewport(0, 0, width, height);

            float ratio = (float) width / height;
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 1000);
            MatrixState.setCamera(cx, CAMERA_Y, cz, tx, CAMERA_Y - 5, tz, 0, 1, 0);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            // 初始化状态 打开深度测试
            GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            MatrixState.setInitStack();

            // 灰度地形图/灰度图山脉
            yArray = loadLandforms(MySurfaceView.this.getResources(), R.drawable.land);
            mountion = new Mountion(MySurfaceView.this, yArray, yArray.length - 1, yArray[0].length - 1);

            // 初始化纹理
            mountionId = initTexture(R.drawable.grass);
            rockId = initTexture(R.drawable.rock);
        }
    }


    public int initTexture(int drawableId) {

        int[] textures = new int[1];
        GLES30.glGenTextures(1, textures, 0);
        int textureId = textures[0];
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
        //android.util.Log.e("TOM","GLES30 error " + GLES30.glGetError());
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER,
                GLES30.GL_LINEAR);                      // hhl fix  MAG不能使用MIPMAP
        //android.util.Log.e("TOM","GLES30 error " + GLES30.glGetError());
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,
                GLES30.GL_LINEAR_MIPMAP_NEAREST);        // 使用MipMap最近点纹理采样

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

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmapTmp, 0);
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
        bitmapTmp.recycle();

        return textureId;
    }
}
package com.bn.Sample6_7;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.bn.util.LoadUtil;
import com.bn.util.LoadedObjectVertexNormalTexture;
import com.bn.util.MatrixState;
import com.bn.util.Vector3f;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import static com.bn.Sample6_7.Constant.*;

public class MysurfaceView extends GLSurfaceView {

    private int mScreenWidth = 240;


    SceneRenderer mRenderer;
    LoadedObjectVertexNormalTexture ballBarObj; // 球门杆
    LoadedObjectVertexNormalTexture ballObj;    // 球

    int ballBarTextureId;
    int netTextureId;
    int ballTextureId;

    int cameraId = 2;





    public MysurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3);
        mRenderer = new SceneRenderer();
        this.setRenderer(mRenderer);
        this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getX() > mScreenWidth / 2) // 屏幕右边切换摄像机
            {
                cameraId = (cameraId + 1) % 3;
            } else                                // 屏幕左侧重置
            {
                synchronized (Constant.lockB) {
                    mRenderer.pc.initalize();
                }
            }
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRenderer.ct != null)
            mRenderer.ct.flag = false;
    }

    private class SceneRenderer implements GLSurfaceView.Renderer {
        Cloth net;
        ParticleControl pc;
        CalThread ct;
        Vector3f ballP;

        public void onDrawFrame(GL10 gl) {

            // 清除深度缓冲与颜色缓冲
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

            // 切换摄像机
            switch (cameraId)
            {
                case 0:
                    MatrixState.setCamera(0, 1, 6, 0, 1, 0, 0, 1, 0);// 设置摄像机前
                    break;
                case 1:
                    MatrixState.setCamera(0, 6, 0, 0, 0, 0, 0, 0, -1);// 设置摄像机上
                    break;
                case 2:
                    MatrixState.setCamera(6, 1, -1, 0, 1, -1, 0, 1, 0);// 设置摄像机右
                    break;
            }


            MatrixState.pushMatrix();

            FloatBuffer fbt = null;

            synchronized (Constant.lockA)
            {
                fbt = Constant.mVertexBufferForFlag;// 获取当前物理帧粒子数据
                ballP = Constant.ballP;
            }

            MatrixState.pushMatrix();
            MatrixState.translate(ballP.x, ballP.y, ballP.z); // 球中心的位置/球质点坐标
            ballObj.drawSelf(ballTextureId);                  // 球是不变形的
            MatrixState.popMatrix();

            MatrixState.pushMatrix();
            net.drawSelf(fbt, netTextureId);
            MatrixState.popMatrix();

            // 球门杆
            MatrixState.pushMatrix();
            ballBarObj.drawSelf(ballBarTextureId);
            MatrixState.popMatrix();

            MatrixState.popMatrix();
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            mScreenWidth = width;

            GLES30.glViewport(0, 0, width, height);
            float ratio = (float) width / height;
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 3, 100); //调用此方法计算产生透视投影矩阵
            MatrixState.setCamera(6, 1, 0, 0, 1, 0, 0, 1, 0);// 设置摄像机右
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config)
        {
            // 设置屏幕背景色RGBA
            GLES30.glClearColor(0, 0, 0, 1);
            // 打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            GLES30.glEnable(GLES30.GL_BLEND);
            // 设置混合因子c
            GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);

            MatrixState.setInitStack();

            net = new Cloth(MysurfaceView.this);
            pc = new ParticleControl();

            ballBarObj = LoadUtil.loadFromFile("ballbar.obj", MysurfaceView.this.getResources(), MysurfaceView.this);
            ballObj = LoadUtil.loadFromFile("ball.obj", MysurfaceView.this.getResources(), MysurfaceView.this);


            ballBarTextureId = initTexture(R.drawable.goal, MysurfaceView.this);
            netTextureId = initTexture(R.drawable.net, MysurfaceView.this);
            ballTextureId = initTexture(R.drawable.ball, MysurfaceView.this);


            ct = new CalThread(pc);
            ct.start();
        }

    }
}

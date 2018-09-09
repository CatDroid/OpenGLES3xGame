package com.bn.catcherFun;

import static com.bn.constant.SourceConstant.*;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;
import javax.xml.transform.Source;

import com.bn.MatrixState.MatrixState2D;
import com.bn.MatrixState.MatrixState3D;
import com.bn.constant.SourceConstant;
import com.bn.hand.R;
import com.bn.thread.SwitchThread;
import com.bn.util.manager.ShaderManager;
import com.bn.view.BNAbstractView;
import com.bn.view.GameView;
import com.bn.view.LoadView;
import com.bn.view.MainView;
import com.bn.view.MenuView;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.util.Arrays;

public class MySurfaceView extends GLSurfaceView {

    static private final String TAG = "MySurfaceView";

    public MainActivity activity;
    private SceneRenderer mRenderer;//场景渲染器
    public BNAbstractView currView;//当前界面
    public GameView gameView;//当前界面
    public boolean isInitOver = false;                        //资源是否初始化完毕
    public MainView mainView;
    public BNAbstractView collectionview;
    public MenuView menuview;
    public BNAbstractView YXJXView;//游戏教学界面
    public BNAbstractView GameAboutView;
    public BNAbstractView ScoreView;

    private SwitchThread mSwth;
    ;

    private static boolean isExit = false;

    public MySurfaceView(Context context) {
        super(context);
        activity = (MainActivity) context;
        this.setEGLContextClientVersion(3);//设置GLES版本为3.0  
        this.setEGLContextFactory(new DefaultContextFactoryInvoke(3) );
        mRenderer = new SceneRenderer();    //创建场景渲染器
        setRenderer(mRenderer);                //设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   

    }

    //触摸事件回调方法
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (currView == null) {
            return false;
        }
        return currView.onTouchEvent(e);


    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currView == gameView) {
                if (gameView.isMenu) {
                    currView = gameView;
                    gameView.isMenu = false; // GameView--MenuView 取消Overlay的MenuView
                } else {
                    if(!SourceConstant.musicOff){ // GameView-->MainView切换背景音乐
                        MainActivity.sound.playBackGroundMusic(activity, R.raw.nogame);
                    }
                    currView = mainView;
                }
            }else if (currView == mainView)
            {
                if (isSet) {
                    isSet = false;
                    currView = mainView;    //  MainView--MenuView 取消Overlay的MenuView
                } else {
                    exit();                 //  只有处于主界面时才可以按返回键返回桌面
                }
            }else if (currView == ScoreView) {
                currView = mainView;
            } else if (currView == YXJXView) {
                currView = mainView;
            } else if (currView == GameAboutView) {
                currView = mainView;
            } else if (currView == collectionview) { // 奖品收藏
                if (isSet) {                // 主界面--设置--奖品收藏  返回主界面，而且没有MenuView的Overlay
                    Log.i(TAG,"reset to MainView");
                    isCollection = false;
                    isSet = false;
                    currView = mainView;
                }else if (isCollection) {   // 游戏界面--菜单--奖品收藏 返回游戏界面，而且没有MenuView的Overlay
                    isCollection = false;
                    gameView.isMenu = false;
                    gameView.reData();
                    currView = gameView;
                    Log.i(TAG,"GameView's MenuView");
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this.getContext(), "再按一次退出游戏", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    isExit = false;
                    SourceConstant.musicOff = true;
                    SourceConstant.effectOff = true;
                    MainActivity.sound.mp.pause(); // 如果还不按第二次 那么退出按钮作为关闭按钮和背景音乐功能
                }// 如果2500ms没有再按退出，那么只是把按钮声音mute了，之后按按钮没有声音
            }, 2500);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private class DefaultContextFactoryInvoke implements EGLContextFactory {
        private int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
        private int mEGLContextClientVersion;
        public DefaultContextFactoryInvoke(int version){
            mEGLContextClientVersion = version ;
        }

        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig config) {
            int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, mEGLContextClientVersion,
                    EGL10.EGL_NONE };
            return egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT,
                    mEGLContextClientVersion != 0 ? attrib_list : null);
        }

        public void destroyContext(EGL10 egl, EGLDisplay display,
                                   EGLContext context) {
            if (!egl.eglDestroyContext(display, context)) {
                Log.e("DefaultContextFactory", "display:" + display + " context: " + context);
                Log.e("DefaultContextFactory", "eglError: " + egl.eglGetError());
            }
            // hhl 第一次创建eglSurface(onSurfaceChanged)之后才创建BNAbstractView这些渲染对象,
            //      而且创建之后 无论之后surface是否Lost了,都不释放,surface重建了也不创建(currView!=null)保证
            //      直到EGLContext被销毁了
            //      这里同时释放CPU上的线程

            gameView.lostContextOnGLThread();
            mainView.lostContextOnGLThread();
            collectionview.lostContextOnGLThread();
            menuview.lostContextOnGLThread();
            YXJXView.lostContextOnGLThread();
            GameAboutView.lostContextOnGLThread();
            ScoreView.lostContextOnGLThread();


            gameView = null; // hhl 删除所有的引用
            mainView = null;
            collectionview = null;
            menuview = null;
            YXJXView = null;
            GameAboutView = null;
            ScoreView = null;

            Special.lostContextOnGLThread();
            Special = null;

            currView = null;
        }
    }

    private class SceneRenderer implements GLSurfaceView.Renderer {
        public void onDrawFrame(GL10 gl) {
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            if (currView != null) {
                currView.drawView(gl);//绘制界面信息

				/*
                * currView不是真的View 只是都实现了BNAbstractView接口
				* BNAbstractView 定义了初始化(initView)  绘制(drawView)  触摸事件处理(onTouchEvent) 三个接口
				*
				* 最开始的BNAbstractView是LoadView(加载界面)
				*
				* 后面的BNAbstractView是MainView(主界面)
				*
				* MySurfaceView是唯一的View,而且控制了渲染线程
				*
				* */
            }

        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            // 在华为V10(1080(1088)x2160) 选择全屏显示后是 2040x1080  不全屏显示是 1920x1080
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);


            GLES30.glViewport
                    (
                            0,//(int)Constant.ssr.lucX,//x
                            0,//(int)Constant.ssr.lucY,//y
                            width,
                            height
                    );

            float ratio = (float) width / height;
            screenWidth = width;
            screenHeight = height;

            // 分别表示2D和3D的状态 主要区分是 2D是正交投影 3D是透视投影
            MatrixState3D.setInitStack();
            MatrixState3D.setProjectFrustum(-ratio, ratio, -1, 1, 1.5f, 100);
            MatrixState3D.setCamera(
                    EYE_X, EYE_Y, EYE_Z,
                    TARGET_X, TARGET_Y, TARGET_Z,
                    0, 1, 0);

            // 把MainVIew的所有按钮做成一个 2D物体 而且用正交投影
            // 缺点是 如果屏幕不是1920x1080就会导致左右或者上下超出屏幕以外
            MatrixState2D.setInitStack();
            MatrixState2D.setCamera(0, 0, 5, 0f, 0f, 0f, 0f, 1f, 0f); // 正交变换 没有影响
            MatrixState2D.setProjectOrtho(-ratio, ratio, -1, 1, 1, 100);
            MatrixState2D.setLightLocation(0, 50, 0); // 点光源 固定位置

            if (currView == null) { // 最开始的BNAbstractView: LoadView 负载加载 加载完毕后 这个BNAbstractView就会永远释放掉
                LoadView lv = new LoadView(MySurfaceView.this);

                if (!musicOff) {//创建音乐
                    MainActivity.sound.playBackGroundMusic(activity, R.raw.nogame);
                }

                currView = lv;
                lv = null;
            }

        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);


            float [] currMatrix = new float[16];
            Matrix.setIdentityM(currMatrix,0);
            Matrix.rotateM(currMatrix,0,90,0,0,1);// hhl 右手螺旋 右手拇指指向z轴正方向旋转
            Log.w(TAG,"str " + Arrays.toString(currMatrix));

            //设置为打开背面剪裁
            GLES30.glEnable(GL10.GL_CULL_FACE);

            // 加载所有shader和编译
            ShaderManager.loadCodeFromFile(activity.getResources());
            ShaderManager.compileShader();
        }

    }


    /*
        下面三个回调是SurfaceView的,在主线程上回调,GLSurfaceView.Render是其后在GLThread-XXX渲染线程上回调的

        surfaceDestroy会等待渲染线程 与EGLSurface解除关系 才会返回,但是EGLContext不一定会销毁

        ableToDraw 或者 readToDraw会返回false,所以不会跳出GL-Thread的内部while(true)循环来进行onDrawFrame和Egl.swap

        但还是会调用event.run()的

        GLSurfaceView ??? 如何知道GL-Thread退出???要自己实现EGLContextFactory ??


        MainActivity: [onCreate]
        MainActivity: [onStart]
        MainActivity: [onResume]
        MySurfaceView: [onAttachedToWindow] 1,main < 这时候View才Attach到Window
        MySurfaceView: [surfaceCreated]
        MySurfaceView: [surfaceChanged]

        MainActivity: [onPause]              <= 后台
        MySurfaceView:[surfaceDestroyed]
        MainActivity: [onStop]

        MainActivity: [onRestart]			<= 后台返回
        MainActivity: [onStart]
        MainActivity: [onResume]
        MySurfaceView: [surfaceCreated]
        MySurfaceView: [surfaceChanged]

        MainActivity: [onPause]             <= 后台
        MySurfaceView: [surfaceDestroyed]
        MainActivity: [onStop]
        MainActivity: [onDestroy]           <= 已在后台，然后到设置界面设置不保留活动 立刻被调用
        MySurfaceView: [onDetachedFromWindow] 1,main

      */

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.w(TAG, "[surfaceCreated] ");
        super.surfaceCreated(holder);
        if(mSwth != null){
            Log.e(TAG,"[surfaceCreated] again ??");
            mSwth.quitSync();
        }
        mSwth = new SwitchThread();
        mSwth.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.w(TAG, "[surfaceChanged] ");
        super.surfaceChanged(holder, format, w, h);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.w(TAG, "[surfaceDestroyed] ");
        super.surfaceDestroyed(holder);


        if (mSwth != null) {
            mSwth.quitSync();
            mSwth = null;
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        Log.w(TAG, "[onDetachedFromWindow] " + Thread.currentThread().getId() + "," + Thread.currentThread().getName());
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        Log.w(TAG, "[onAttachedToWindow] " + Thread.currentThread().getId() + "," + Thread.currentThread().getName());
        super.onAttachedToWindow();
    }

    public void destroy(){
        activity = null;
    }
}

package com.bn.Sample10_1a;
import java.io.IOException;
import java.io.InputStream;
import android.opengl.GLSurfaceView;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.os.Build;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import static com.bn.Sample10_1a.Constant.CONFIG_USING_ALPHA;

class MySurfaceView extends GLSurfaceView 
{
    private SceneRenderer mRenderer;//场景渲染器  
    public static int mScreenWidth = -1;
    public static int mScreenHeight = -1;

	//矩形的位置
	static float rectX;
	static float rectY;
	static int rectState = KeyThread.Stop;
	final static float moveSpan = 0.1f;
	private KeyThread keyThread;
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
    }
	
	//触摸事件回调方法
    @Override 
    public boolean onTouchEvent(MotionEvent e) 
    {
        /*  hl.he:

            Screen(SurfaceView)
            -------------------------------------
            |       |                   |       |
            |       |       up          |       |
            |  left |-------------------| right |
            |       |                   |       |
            |       |      down         |       |
            -------------------------------------

         */
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_DOWN:
            //Log.d("TOM", String.format("%f %f %d %d",x,y,Constant.mScreenWidth,Constant.mScreenHeight));
        	if(x< mScreenWidth /3.0f) {//按下屏幕左面1/3向左移
        		rectState = KeyThread.left;
        	}
        	else if(x> mScreenWidth *2/3.0f){//按下屏幕右面2/3向右移
        		rectState = KeyThread.right;
        	}
        	else {
            	if(y< mScreenHeight /2.0f) {   //按下屏幕上方向上移
            		rectState = KeyThread.up;
            	}
            	else {//按下屏幕下方向下移 
            		rectState = KeyThread.down;
            	}
        	}
        	break;
        case MotionEvent.ACTION_UP://抬起时停止移动
        	rectState = KeyThread.Stop;
        	break;
        }
        return true;
    }
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {
		int rectTexId;//纹理id
    	//从指定的obj文件中加载对象   hl.he 3DsMax中每个物体单独保存成一个文件
		LoadedObjectVertexNormalFace pm;
		LoadedObjectVertexNormalFace cft;
		LoadedObjectVertexNormalAverage qt;
		LoadedObjectVertexNormalAverage yh;
		LoadedObjectVertexNormalAverage ch;
        LoadedObjectVertexNormalAverage myteapot;// hl.he Add:自己用3dsMax生成的茶壶

		TextureRect rect;
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
		@SuppressLint("InlinedApi")
		public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

            /*
             * hl.he Note:
             *
             * 1. 搭建场景  五个基本物体 平面 圆环 茶壶 立方体 圆球 这些在3DsMax都直接有现成的
             *
             * 2. 深度缓冲  颜色越深 灰度值越小  代表深度越浅
             *
             * 3. 常用两种组合(A:Alpha s:Source R/G/B:Red/Green/Blue)
             *      a. 源因子 GL_SRC_ALPHA 目标因子 GL_ONE_MINUS_SRC_ALPHA 即 [As,As,As,As] [1-As,1-As,1-As,1-As]
             *          需要源片元是有透明通道的 实现透明
             *      b. (滤光镜效果因子组合)源因子 GL_SRC_COLOR 目标因子 GL_ONE_MINUS_SRC_COLOR 即 [Rs,Gs,Bs,As] [1-Rs,1-Bs,1-Bs,1-As]
             *          不要求源图片有alpha 实现透过有色玻璃看物体 要透明的话 只要源图片是R/G/B都是0也就是黑色
             *          这样实现 会使目标颜色/背景是浅色的 那么最终颜色比源颜色(纹理图片)要浅 比如背景是白色 那么最终颜色会非常浅色
             *    除此 还有使用常因子的 GL_CONSTANT_COLOR 和 GL_CONSTANT_ALPHA 需要通过glBendColor设置  这样实现整个物体同一个alpha透明
             *
             * 4. API 提供alpha和color一起和分开设置源因子和目标因子
             *          glBlendFunc
             *          glBlendSeparate
             *
             * 5. API 提供的混合方程式 glBlendEquation(int mode) 默认使用GL_FUNC_ADD = 源各个分量*对应源因子 + 目标各个分量*对应目标因子  计算后大于1.0截取为1.0
             *        也可以color和alpha通道使用不同的混合方程式 glBlendSeperateEquation
             *
             * 6. 示例中 滤镜是一个矩形物体 对应的纹理是中心圆带有alpha值 外边alpha是0(完全透明) 和 一个中心和外围都不带alpha(alpha=100)但中心是纯绿色外围是黑色(作为透明)
             *
             *
             */

            //MatrixState.pushMatrix();
            MatrixState.pushMatrix();
            MatrixState.rotate(25, 1, 0, 0);
            pm.drawSelf();//平面
            

            MatrixState.pushMatrix();
            MatrixState.scale(1.5f, 1.5f, 1.5f);   //缩放后面绘制的物体


            MatrixState.pushMatrix();//绘制长方体
            MatrixState.translate(-10f, 0f, 0);
            cft.drawSelf();
            MatrixState.popMatrix();   

            MatrixState.pushMatrix();//绘制球体
            MatrixState.translate(10f, 0f, 0);
            qt.drawSelf();
            MatrixState.popMatrix();  

            MatrixState.pushMatrix();//绘制圆环
            MatrixState.translate(0, 0, -10f);
            yh.drawSelf();
            MatrixState.popMatrix();  

//            MatrixState.pushMatrix();//绘制茶壶
//            MatrixState.translate(0, 0, 10f);
//            ch.drawSelf();
//            MatrixState.popMatrix();

            MatrixState.pushMatrix();//绘制茶壶
            MatrixState.translate(0f, 0, 10f);
            MatrixState.scale(0.5f,0.5f,0.5f);// hl.he Add 太大了
            myteapot.drawSelf();
            MatrixState.popMatrix();


            MatrixState.popMatrix();  // scale

            MatrixState.popMatrix();  // rotate
              

            MatrixState.pushMatrix();
            MatrixState.translate(rectX, rectY, 25f);
            GLES30.glEnable(GLES30.GL_BLEND);  //开启混合
            if(CONFIG_USING_ALPHA){
                GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA); //设置混合因子,其中第一个为源因子，第二个为目标因子
            }else{
                GLES30.glBlendFunc(GLES30.GL_SRC_COLOR, GLES30.GL_ONE_MINUS_SRC_COLOR);
            }

            rect.drawSelf(rectTexId);//绘制滤光镜纹理矩形  hhl: 这个纹理图片的aplha通道不是100 不开混合的话 就直接覆盖
            GLES30.glDisable(GLES30.GL_BLEND);//关闭混合

            MatrixState.popMatrix();

            //MatrixState.popMatrix();
        }  

        @SuppressLint("NewApi")
		public void onSurfaceChanged(GL10 gl, int width, int height) {

            // hl.he Add :
            mScreenHeight = height;
            mScreenWidth = width ;


        	GLES30.glViewport(0, 0, width, height); //设置视窗大小及位置

            float ratio = (float) width / height;//计算GLSurfaceView的宽高比
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);//调用此方法计算产生透视投影矩阵
            MatrixState.setCamera //设置camera位置
            (
            		0, 0,  50,  //  人眼位置
            		0, 0,  0,   //  人眼球看的点
            		0, 1,  0    //  up位置
            );
            MatrixState.setLightLocation(100, 100, 100);//初始化光源位置
            keyThread = new KeyThread(MySurfaceView.this);
            keyThread.start();
        }

        @SuppressLint("NewApi")
		@Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            GLES30.glClearColor(0.3f,0.3f,0.3f,1.0f);    

            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            GLES30.glEnable(GLES30.GL_CULL_FACE);

            MatrixState.setInitStack();         

            if(CONFIG_USING_ALPHA){
                rectTexId=initTexture(R.raw.lgq);  // 带有透明通道的图片 作为滤镜
            }else{
                rectTexId=initTexture(R.raw.lgq_no_alpha_black_background); // 没有透明通道 用黑色代表透明 源因子是 GL_SRC_COLOR
            }

            //加载要绘制的物体
            ch=LoadUtil.loadFromFileVertexOnlyAverage("ch.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            pm=LoadUtil.loadFromFileVertexOnlyFace("pm.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
    		cft=LoadUtil.loadFromFileVertexOnlyFace("cft.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
    		qt=LoadUtil.loadFromFileVertexOnlyAverage("qt.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
    		yh=LoadUtil.loadFromFileVertexOnlyAverage("yh.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
            myteapot=LoadUtil.loadFromFileVertexOnlyAverage("teapot.obj", MySurfaceView.this.getResources(),MySurfaceView.this);
    		rect = new TextureRect(MySurfaceView.this, 10, 10);    
        }  
    }

	
	@SuppressLint("NewApi")
	public int initTexture(int drawableId)
	{
		int[] textures = new int[1];
		GLES30.glGenTextures(1, textures, 0);
		int textureId=textures[0];    
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);

        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try 
        {
        	bitmapTmp = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmapTmp, 0);
        bitmapTmp.recycle();
        
        return textureId;
	}
	@Override
    public void onResume() {
    	super.onResume();
    	KeyThread.flag = true;
    	keyThread = new KeyThread(MySurfaceView.this);
        keyThread.start();
    }
	@Override
	public void onPause() {
		super.onPause();
		KeyThread.flag = false;
	}
}

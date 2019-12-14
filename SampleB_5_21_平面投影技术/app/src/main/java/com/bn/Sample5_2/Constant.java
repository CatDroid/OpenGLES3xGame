package com.bn.Sample5_2;

public class Constant {

    // 实际就是XOZ平面 pm.obj
    static public final float PLANE_NORMAL[] = {0.0f, 1.0f , 0.0f};

    // 可以看pm.obj实际平面时在 XOZ,这里 阴影实际平面 往法向量方向 移动了一点
    static public final float PLANE_POT[] = {0.0f, 0.1f, 0.0f} ;

    // 如果 投影平面 不与 实际平面 隔开一点 绘制阴影就会看不到全部, 或者可以 GLES30.glDisable(GLES30.GL_DEPTH_TEST);
    //static public final float PLANE_POT[] = {0.0f, 0.0f, 0.0f} ;
}

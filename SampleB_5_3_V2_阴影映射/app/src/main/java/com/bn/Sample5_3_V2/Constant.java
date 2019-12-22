package com.bn.Sample5_3_V2;

public class Constant
{
    public static float SCREEN_WIDTH;
    public static float SCREEN_HEIGHT;
    public static float SCREEN_RATIO;
    public static final int SHADOW_TEX_WIDTH = 4096;
    public static final int SHADOW_TEX_HEIGHT = 4096;
    public static final float S_MAX = 1.0f;
    public static final float T_MAX = 1.0f;
    public static final float MAX_DIFF = 10.0f;  // 距离差  如果两个物体之间要在24.0f才算是真的阴影

    public static final boolean SHOW_DISTORTION = true; // 由触摸控制最大的diff值
    public static final boolean AUTO_ANTI_DISTORTION = true ; // 由shader根据光线向量和法向量夹角来计算diff值
}
    
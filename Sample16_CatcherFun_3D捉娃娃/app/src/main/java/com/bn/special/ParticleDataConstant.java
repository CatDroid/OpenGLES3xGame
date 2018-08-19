package com.bn.special;

import android.opengl.GLES30;
     
public class ParticleDataConstant 
{
	//当前索引  
    public static int CURR_INDEX=5; 
    //起始颜色
    public static final float[][] START_COLOR=
	{
    	{0.7569f,0.2471f,0.1176f,1.0f},	//0-普通火焰
    	
    	{0.9882f,0.9765f,0.0118f,1.0f},	//黄色
    	{0.9882f,0.0196f,0.8863f,1.0f},	//粉红色
    	
    	
    	
    	{0.9804f,0.9804f,0.9804f,1.0f},//白色
    	{0.9882f,0.9765f,0.0118f,1.0f},//黄色
    	{0.9882f,0.9765f,0.0118f,1.0f},//黄色
	};
    
    //终止颜色
    public static final float[][] END_COLOR=
	{
    	{0.0f,0.0f,0.0f,0.0f},//0-普通火焰
    	{0.0f,0.0f,0.0f,0.0f},//黑色
    	{1.0f,0.8431f,0.0f,0.0f},//金色
    	
    	{0.9882f,0.0196f,0.8863f,0.0f},//粉红色
    	{0.1608f,0.9725f,0.2157f,0.0f},//绿色
    	{0.1608f,0.9725f,0.2157f,0.0f},//绿色
	};
    
    //源混合因子
    public static final int[] SRC_BLEND=
	{
    	GLES30.GL_SRC_ALPHA,
    	GLES30.GL_SRC_ALPHA,
    	GLES30.GL_SRC_ALPHA,
    	GLES30.GL_SRC_ALPHA,
    	GLES30.GL_SRC_ALPHA,
    	GLES30.GL_SRC_ALPHA,
	};
    
    //目标混合因子
    public static final int[] DST_BLEND=
	{
    	GLES30.GL_ONE,
    	GLES30.GL_ONE,
    	GLES30.GL_ONE,
    	GLES30.GL_ONE,
    	GLES30.GL_ONE,
    	GLES30.GL_ONE,
	};
    
    //混合方式
    public static final int[] BLEND_FUNC=
	{
    	GLES30.GL_FUNC_ADD,    				//0-普通火焰
    	GLES30.GL_FUNC_ADD,
    	GLES30.GL_FUNC_ADD,
    	GLES30.GL_FUNC_ADD,
    	GLES30.GL_FUNC_ADD,
    	GLES30.GL_FUNC_ADD,
	};
    
    //单个粒子半径
    public static final float[] RADIS=
    {
    	0.4f,		//0-普通火焰
    	
    	0.3f,
    	0.2f,
    	
    	0.2f,
    	0.15f,
    	0.6f,
    };
    
    //粒子最大生命期
    public static final float[] MAX_LIFE_SPAN=
    {
    	5.0f,		//0-普通火焰
    	
    	2f,
    	1.2f,
    	
    	4f,
    	4f,
    	4f,
    };
    
    //粒子生命周期步进
    public static final float[] LIFE_SPAN_STEP=
    {
    	0.2f,
    	
    	0.1f,
    	0.1f,
    	
    	0.03f,
    	0.03f,
    	0.01f,
    };
    
    //粒子发射的X左右范围
    public static final float[] X_RANGE=
	{
	    0.05f,		//0-普通火焰
	    
	    1f,		//0-普通火焰
    	0.3f,
    	
    	0.8f,
    	0.8f,
    	2.0f,
	};
    
    //粒子发射的Y上下范围
    public static final float[] Y_RANGE=
	{
	    1f,		//0-普通火焰
	    
	    0.8f,		//0-普通火焰d
	    0.8f,
	    
	    1.8f,
	    1.8f,
	    1.0f,
	};
    
    //每次喷发发射的数量
    public static final int[] GROUP_COUNT=
	{
    	10,
    	
    	1,
    	5,
    	
    	5,
    	4,
    	1,
	};
    
    //粒子Y方向升腾的速度
    public static final float[] VY=
	{
    	0.08f,
    	
    	0.02f,
    	0.02f,
    	
    	0.015f,
    	0.015f,
    	0.050f,
	};
    
    //粒子更新物理线程休息时间
    public static final int[] THREAD_SLEEP=
    {
    	15,
    	
    	16,
    	16,
    	
    	15,
    	15,
    	15,
    };
}

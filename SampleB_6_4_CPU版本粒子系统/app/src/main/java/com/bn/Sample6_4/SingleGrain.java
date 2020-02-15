package com.bn.Sample6_4;

// 存储粒子系统中的某个粒子物理信息的类
public class SingleGrain {
    private float vx;           // x轴速度分量
    private float vy;           // y轴速度分量
    private float vz;           // z轴速度分量
    public float timeSpan = 0;  // 粒子的累计运动时间

    public SingleGrain(float vx, float vy, float vz) // 初始化粒子在各个坐标轴上的速度
    {
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
    }

    public void drawSelf(GrainForDraw drawer)
    {

        MatrixState.pushMatrix();

        // 根据当前时间计算出粒子在X轴、Y轴、Z轴上的坐标
        float x = vx * timeSpan;                                            // x和y方向上做匀速运动
        float z = vz * timeSpan;
        float y = vy * timeSpan - 0.5f * timeSpan * timeSpan * 1.5f + 3.0f;

        // y方向做重力加速度运动
        // 初始化位置在y=3.0  重力加速度是1.5f timeSpan单位秒

        // 重力加速度不是9.8 因为这样比较快消失了

        MatrixState.translate(x, y, z);
        // 绘制粒子
        drawer.drawSelf();
        MatrixState.popMatrix();
    }
}
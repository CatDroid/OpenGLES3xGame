package com.bn.Sample6_4;

import java.util.ArrayList;

//代表粒子系统的类
public class GrainGroup {

    private GrainForDraw mDrawer;                                   // 用于绘制粒子的绘制者,所有粒子都是这个渲染的,program的封装

    private ArrayList<SingleGrain> mGrainList = new ArrayList<SingleGrain>();         // 所有粒子的列表

    private static final float SPEED_INIT = (float) (1.5f + 1.5f * Math.random());    // 所有粒子的初始速度大小一样,方向随机

    private static final float TIME_SPAN = 0.02f;                       // 粒子移动每一步的模拟时延，步进，时间间隔
//    private static final float TIME_SPAN = 0.01f;

    public GrainGroup(MySurfaceView mv)
    {
        mDrawer = new GrainForDraw(2, 1, 1, 1, mv);             // 创建粒子的绘制者

        for (int i = 0; i < 400; i++)                                               // 随机向列表中添加不同初速度的粒子
        {
            double elevation = 0.35f * Math.random() * Math.PI + Math.PI * 0.15f;   // 仰角   0.15 ~ 0.5 π 之间
            double direction = Math.random() * Math.PI * 2;                         // 方位角  0 ~ 2π 之间
            float vy = (float) (SPEED_INIT * Math.sin(elevation));                  // 分解出3个轴的初速度
            float vx = (float) (SPEED_INIT * Math.cos(elevation) * Math.cos(direction));
            float vz = (float) (SPEED_INIT * Math.cos(elevation) * Math.sin(direction));
            mGrainList.add(new SingleGrain(vx, vy, vz));                                    // 创建粒子对像并添加进粒子列表
        }
    }

    private long timeStamp = 0;                            // 用于计算的时间戳

    public void drawSelf() {

        long currTimeStamp = System.nanoTime() / 1000000;    //获取当前系统时间

        if (currTimeStamp - timeStamp > 10) {           // 若时间间隔大于10ms，则各个粒子前进一步

            for (SingleGrain sp : mGrainList) {         // 扫描粒子列表，并修改粒子的累计运动时间

                sp.timeSpan = sp.timeSpan + TIME_SPAN; // 真实时间的10ms 代表粒子生命周期过了0.02单位(秒)  如果超过10个单位 生命周期结束

                if (sp.timeSpan > 10) {         // 判断粒子的累计运动时间是否大于10
                    sp.timeSpan = 0;            // 将粒子的累计运动时间归零
                }
            }
            timeStamp = currTimeStamp;          // 更新用于计算的时间戳
        }
        else
        {
            // 由于屏幕渲染可能最大只有60fps 也就是 最快16ms刷新一次 所以应该不会执行到这里
            android.util.Log.e("TOM","Lost Update");
        }

        int size = mGrainList.size();
        for (int i = 0; i < size; i++)          // 循环扫描所有粒子的列表并绘制各个粒子
        {
            try
            {
                mGrainList.get(i).drawSelf(mDrawer);     // 根据时间进度,绘制粒子
            }
            catch (Exception e)
            {

            }
        }
    }
}

package com.bn.Sample7_2;

public class Cylinder {
    Circle bottomCircle;//底圆
    Circle topCircle;//顶圆
    CylinderSide cylinderSide;//侧面
    float h;

    MySurfaceView mv ;

    public Cylinder(float r, float h, int n, int mProgram, MySurfaceView mv)
    {

        topCircle = new Circle(r, n, new float[]{0, 1, 0}, mProgram);

        bottomCircle = new Circle(r, n, new float[]{0, -1, 0}, mProgram);

        cylinderSide = new CylinderSide(r, h, n, mProgram); // 侧面


        this.h = h;


        this.mv = mv ;



    }

    // 这个圆柱的 底部中心点在 模型坐标系的原点
    public void drawSelf(int topTexId, int BottomTexId, int sideTexId)
    {

        // 不放在构造函数, 是因为触摸动态生成的CubeClinder是在非渲染线程上 会导致获取不了uniform/attribute变量句柄

        bottomCircle.intShader(mv);

        topCircle.intShader(mv);

        cylinderSide.intShader(mv);


        //顶面 圆是在XOY平面
        MatrixState.pushMatrix();
        MatrixState.translate(0, h, 0);
        MatrixState.rotate(-90, 1, 0, 0);
        topCircle.drawSelf(topTexId);
        MatrixState.popMatrix();


        //底面
        MatrixState.pushMatrix();
        MatrixState.rotate(90, 1, 0, 0);
        MatrixState.rotate(45, 0, 0, 1); // 做这一步 就可以区分 圆柱的上面还是地面
        bottomCircle.drawSelf(BottomTexId);
        MatrixState.popMatrix();


        //侧面
        MatrixState.pushMatrix();
        cylinderSide.drawSelf(sideTexId);
        MatrixState.popMatrix();
    }
}

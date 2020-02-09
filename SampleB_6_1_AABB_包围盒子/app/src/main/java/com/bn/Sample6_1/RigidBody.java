package com.bn.Sample6_1;

import java.util.ArrayList;

public class RigidBody {                    // 刚体 不会形变和缩放 只会平移

    private LoadedObjectVertexNormal renderObject;  // 渲染物体
    private AABBBox collObject;                     // 渲染物体 对应的 碰撞AABB盒子

    private boolean isStatic;               // 是否静止的标志位 物体静止就不用计算和其他物体的碰撞,只有运动的物体需要检查碰撞
    private Vector3f currLocation;          // 位置三维变量
    private Vector3f currV;                 // 速度三维变量
    private final float V_UNIT = 0.02f;     // 阈值

    public RigidBody(
            LoadedObjectVertexNormal renderObject,
            boolean isStatic,
            Vector3f currLocation,
            Vector3f currV)
    {
        this.renderObject = renderObject;
        collObject = new AABBBox(renderObject.vertices);
        this.isStatic = isStatic;
        this.currLocation = currLocation;
        this.currV = currV;
    }

    public void drawSelf()
    {
        MatrixState.pushMatrix();   // 保护现场
        MatrixState.translate(currLocation.x, currLocation.y, currLocation.z); // 在单独的线程中更新位置和速度
        renderObject.drawSelf();    // 绘制物体
        MatrixState.popMatrix();    // 恢复现场
    }

    public void go(ArrayList<RigidBody> al) {

        // 静止不动的物体不检查碰撞
        if (isStatic) return;

        currLocation.add(currV); // 新位置 = 旧位置 + 速度

        // 检查场景中其他物体是否与本物体发生碰撞
        for (int i = 0; i < al.size(); i++) {

            RigidBody rb = al.get(i);

            // 碰撞不需要检查自己
            if (rb != this)
            {
                if (check(this, rb))// 检验碰撞
                {
                    // 哪个方向的有速度，该方向上的速度置反
                    this.currV.x = -this.currV.x;
                    this.currV.y = -this.currV.y;
                    this.currV.z = -this.currV.z;
                }
            }
        }
    }

    public boolean check(RigidBody ra, RigidBody rb)//true为撞上
    {
        // 每次都是根据各个物体移动后/平移,生成新的AABB盒子,来计算碰撞
        float[] over = calOverTotal(
                ra.collObject.getCurrAABBBox(ra.currLocation),
                rb.collObject.getCurrAABBBox(rb.currLocation));

        // 判断三个轴方向上是否重叠,发生碰撞
        return over[0] > V_UNIT && over[1] > V_UNIT && over[2] > V_UNIT;
    }


    // 分别计算三个轴上都发生了重叠,才算是碰撞
    public float[] calOverTotal(AABBBox a, AABBBox b) {
        float xOver = calOverOne(a.maxX, a.minX, b.maxX, b.minX);
        float yOver = calOverOne(a.maxY, a.minY, b.maxY, b.minY);
        float zOver = calOverOne(a.maxZ, a.minZ, b.maxZ, b.minZ);
        return new float[]{xOver, yOver, zOver};
    }

    // 先要判断两个物体哪个在左,左侧的max 如果大于右侧的min 那么就是重叠了
    public float calOverOne(float amax, float amin, float bmax, float bmin)
    {

        float leftMax = 0;

        float rightMin = 0;

        if (amin < bmin)    // a物体在b物体左侧
        {
            leftMax  = amax;
            rightMin = bmin;
        }
        else                // a物体在b物体右侧
        {
            leftMax  = bmax;
            rightMin = amin;
        }

        if (leftMax > rightMin) {
            return leftMax - rightMin;
        } else {
            return 0;
        }
    }
}

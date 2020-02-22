package com.bn.Sample6_7;

import static com.bn.Sample6_7.Constant.*;

import com.bn.util.Vector3f;

public class ParticleControl {

    private Particle particles[][] = new Particle[NUMROWS + 1][NUMCOLS + 1];// 粒子数组
    private Spring springs[] = new Spring[NUMSPTINGS];                    // 弹簧数组
    private float vertices[] = new float[NUMCOLS * NUMROWS * 2 * 3 * 3];            // 每个顶点xyz三个坐标
    private Vector3f temp = new Vector3f(0, 0, 0);                // 临时向量对象1
    private Vector3f temp2 = new Vector3f(0, 0, 0);                // 临时向量对象2
    private BallParticle bp = new BallParticle(0.3f);

    public ParticleControl()
    {
        initalize();
    }

    // 布料--旗帜--球网 按照卷绕方式 打包顶点坐标
    public float[] getVerties() {
        int count = 0;

        for (int r = 0; r < NUMROWS; r++) {
            for (int c = 0; c < NUMCOLS; c++) {
                vertices[count++] = particles[r][c].pvPosition.x;
                vertices[count++] = particles[r][c].pvPosition.y;
                vertices[count++] = particles[r][c].pvPosition.z;

                vertices[count++] = particles[r + 1][c].pvPosition.x;
                vertices[count++] = particles[r + 1][c].pvPosition.y;
                vertices[count++] = particles[r + 1][c].pvPosition.z;

                vertices[count++] = particles[r][c + 1].pvPosition.x;
                vertices[count++] = particles[r][c + 1].pvPosition.y;
                vertices[count++] = particles[r][c + 1].pvPosition.z;

                vertices[count++] = particles[r][c + 1].pvPosition.x;
                vertices[count++] = particles[r][c + 1].pvPosition.y;
                vertices[count++] = particles[r][c + 1].pvPosition.z;

                vertices[count++] = particles[r + 1][c].pvPosition.x;
                vertices[count++] = particles[r + 1][c].pvPosition.y;
                vertices[count++] = particles[r + 1][c].pvPosition.z;

                vertices[count++] = particles[r + 1][c + 1].pvPosition.x;
                vertices[count++] = particles[r + 1][c + 1].pvPosition.y;
                vertices[count++] = particles[r + 1][c + 1].pvPosition.z;
            }
        }
        return vertices;
    }

    public Vector3f getBall() {
        return bp.pvPosition;
    }

    // 初始化粒子系统数据
    public void initalize() {

        for (int r = 0; r <= NUMROWS; r++) {
            for (int c = 0; c <= NUMCOLS; c++) {
                particles[r][c] = new Particle();
                float f;
                f = 1;
                //设置质量,计算质量倒数
                particles[r][c].pfMass = f;
                particles[r][c].pfInvMass = 1 / particles[r][c].pfMass;
                //计算初始化位置
                particles[r][c].pvPosition.x = -CSTER * NUMCOLS / 2 + c * CSTER;
                particles[r][c].pvPosition.y = RSTER * NUMROWS - r * RSTER;
                particles[r][c].pvPosition.z = -r * 0.83f / 11;

                // 设置不动粒子 -- 设置为整个球网的边缘的质点

                if (r == 0 || c == 0 || r == NUMROWS || c == NUMCOLS) {
                    particles[r][c].bLocked = true;
                } else {
                    particles[r][c].bLocked = false;
                }
            }
        }

        bp.pfMass = 22f;
        bp.pfInvMass = 1 / bp.pfMass;
        bp.pvPosition.voluation(0, 1, 3);
        bp.pvVelocity.voluation(1, 0, -4f); // -5f 球的速度过快 可能会导致穿过去了
        bp.bLocked = false;


        // 初始化弹簧
        int count = 0;// 计数器
        for (int r = 0; r <= NUMROWS; r++)
        {

            for (int c = 0; c <= NUMCOLS; c++)
            {

                if (c < NUMCOLS)//初始化竖弹簧
                {
                    springs[count] = new Spring();
                    //第一个连接点
                    springs[count].p1.r = r;
                    springs[count].p1.c = c;
                    //第二个连接点
                    springs[count].p2.r = r;
                    springs[count].p2.c = c + 1;
                    //计算长度
                    temp.voluation(particles[r][c].pvPosition);
                    temp.sub(particles[r][c + 1].pvPosition);
                    springs[count].L = temp.module() + 0.01f;
                    count++;
                }

                if (r < NUMROWS)//初始化横弹簧
                {
                    springs[count] = new Spring();
                    //第一个连接点
                    springs[count].p1.r = r;
                    springs[count].p1.c = c;
                    //第二个连接点
                    springs[count].p2.r = r + 1;
                    springs[count].p2.c = c;
                    //计算长度
                    temp.voluation(particles[r][c].pvPosition);
                    temp.sub(particles[r + 1][c].pvPosition);
                    springs[count].L = temp.module() + 0.01f;
                    count++;
                }

                if (r < NUMROWS && c < NUMCOLS)//初始化左上右下弹簧
                {
                    springs[count] = new Spring();
                    springs[count].k = SPRING_SHEAR_CONSTANT;
                    //第一个连接点
                    springs[count].p1.r = r;
                    springs[count].p1.c = c;
                    //第二个连接点
                    springs[count].p2.r = r + 1;
                    springs[count].p2.c = c + 1;
                    //计算长度
                    temp.voluation(particles[r][c].pvPosition);
                    temp.sub(particles[r + 1][c + 1].pvPosition);
                    springs[count].L = temp.module() + 0.01f;
                    count++;
                }

                if (r < NUMROWS && c > 0)//初始化右上左下弹簧
                {
                    springs[count] = new Spring();
                    springs[count].k = SPRING_SHEAR_CONSTANT;
                    //第一个连接点
                    springs[count].p1.r = r;
                    springs[count].p1.c = c;
                    //第二个连接点
                    springs[count].p2.r = r + 1;
                    springs[count].p2.c = c - 1;
                    //计算长度
                    temp.voluation(particles[r][c].pvPosition);
                    temp.sub(particles[r + 1][c - 1].pvPosition);
                    springs[count].L = temp.module() + 0.01f;
                    count++;
                }
            }
        }

    }

    private void calcForces() {

        //将所有粒子受力至0
        for (int r = 0; r <= NUMROWS; r++)
        {
            for (int c = 0; c <= NUMCOLS; c++) {
                particles[r][c].pvForces.x = 0;
                particles[r][c].pvForces.y = 0;
                particles[r][c].pvForces.z = 0;
            }
        }

        bp.pvForces.voluation(0, 0, 0); // 球受到了重力
        bp.pvForces.y += GRAVITY * bp.pfMass;

        // 计算重力 空气阻力与风力
        for (int r = 0; r <= NUMROWS; r++)
        {
            for (int c = 0; c <= NUMCOLS; c++)
            {
                if (!particles[r][c].bLocked)
                {
                    // 重力
                    particles[r][c].pvForces.y += GRAVITY * particles[r][c].pfMass;

                    // 空气阻力 = 当前粒子速度反方向单位向量*速度大小平方*风阻参数
                    temp.voluation(particles[r][c].pvVelocity);
                    temp.normalize();
                    temp.scale(-particles[r][c].pvVelocity.moduleSq() * DRAGCOEFFICIENT);
                    particles[r][c].pvForces.add(temp);

                    // 风力 = 随机风向*随机风力
                    temp.voluation((float) (Math.random() * 1), 0, (float) (Math.random() * 0.4f));
                    temp.scale((float) (Math.random() * WindForce));
                    particles[r][c].pvForces.add(temp);
                }
            }
        }


        // 计算弹簧合力 = 弹力 + 阻尼力
        for (int i = 0; i < NUMSPTINGS; i++)
        {
            int r1 = (int) springs[i].p1.r;
            int c1 = (int) springs[i].p1.c;
            int r2 = (int) springs[i].p2.r;
            int c2 = (int) springs[i].p2.c;

            temp.voluation(particles[r1][c1].pvPosition);
            temp.sub(particles[r2][c2].pvPosition);         // 计算粒子间距离
            float pd = temp.module();

            temp2.voluation(particles[r1][c1].pvVelocity);
            temp2.sub(particles[r2][c2].pvVelocity);        // 计算速度差

            float L = springs[i].L;
            // 根据弹簧公式计算弹力
            float tanli = - springs[i].k * (pd - L);
            float diffSpeed = temp.dotProduct(temp2) / pd ;
            float zunili = - springs[i].d * diffSpeed;

            //float t = -(springs[i].k * (pd - L) + springs[i].d * (temp.dotProduct(temp2) / pd)) / pd;
//             float t = (tanli + zunili) / pd ; // z = -10 会穿越过去
            float t = (tanli + zunili);  // 如果用这个，球的初始速度为 z=-5f 就会穿过去了 bp.pvVelocity.voluation(1, 0, -5f);



            temp.scale(t);

            if (!particles[r1][c1].bLocked)
            {
                particles[r1][c1].pvForces.add(temp);
            }

            if (!particles[r2][c2].bLocked)
            {
                temp.scale(-1);
                particles[r2][c2].pvForces.add(temp);
            }
        }

        // 计算与球碰撞的相互作用力
        for (int r = 0; r <= NUMROWS; r++)
        {
            for (int c = 0; c <= NUMCOLS; c++)
            {
                if (!particles[r][c].bLocked)
                {
                    temp.voluation(particles[r][c].pvPosition);     // 将当前质点位置复制进临时向量
                    temp.sub(bp.pvPosition);                        // 减去足球质点位置
                    float fd = temp.moduleSq();                     // 获取足球质点与此质点距离的平方
                    if (fd < bp.rQ)                                 // 若距离平方小于足球半径平方
                    {
                        float u = (bp.ballR - (float) Math.sqrt(fd)) / bp.ballR; // 计算受力基本值
                        float f = u * 5000;                     // 受力基本值乘以系数获得力的大小
                        temp.normalize();                       // 得到力的方向向量的规格化版本
                        temp.scale(f);                          // 将向量的大小设置为力的大小
                        particles[r][c].pvForces.add(temp);     // 将此力加入质点合力

                        temp.scale(-1);                       // 将此力置反
                        bp.pvForces.add(temp);                  // 加入足球球质点合力
                    }
                }
            }
        }
    }


    // 只处理球质点和地面碰撞的
    private boolean checkForCollisions()
    {
        boolean state = false;                          // 是否有碰撞标志
        if ((bp.pvPosition.y <= COLLISIONTOLERANCE)     // 若足球质点低于地面
                && (bp.pvVelocity.y < 0f))              // 若足球质点的Y轴速度是向下
        {
            state = true;                               // 将碰撞标志设置为true
            bp.cn.x = 0.0f;                            // 记录碰撞面法向量的X分量
            bp.cn.y = 1.0f;                            // 记录碰撞面法向量的Y分量
            bp.cn.z = 0.0f;                            // 记录碰撞面法向量的Z分量
        }
        return state;                                   // 返回碰撞标志值
    }

    private void resolveCollisions()
    {
        temp.voluation(bp.cn);                      // 获取碰撞法向量
        temp.scale(temp.dotProduct(bp.pvVelocity)); // 求得法向量方向的速度分量

        temp2.voluation(bp.pvVelocity);             // 设置速度
        temp2.sub(temp);                            // 速度 -  法向量方向分量 = 切向分量

        temp.scale(-KRESTITUTION);                  // 法向量方向速度乘以反弹系数
        temp2.scale(FRICTIONFACTOR);                // 切向方向速度乘以动摩擦系数

        temp.add(temp2);                            // 切向分量 + 法向量分量 = 速度
        bp.pvVelocity.voluation(temp);              // 计算出新的速度
    }


    void stepSimulation(float dt)
    {
        calcForces();//物理计算

        for (int r = 0; r <= NUMROWS; r++)
        {
            for (int c = 0; c <= NUMCOLS; c++)
            {
                temp.voluation(particles[r][c].pvForces);
                temp.scale(particles[r][c].pfInvMass);  // 计算加速度
                particles[r][c].pvAcceleration.voluation(temp);
                temp.scale(dt);                         // 加速度乘进步时间
                particles[r][c].pvVelocity.add(temp);   // 计算新的速度
                temp.voluation(particles[r][c].pvVelocity);
                temp.scale(dt);
                particles[r][c].pvPosition.add(temp);   // 计算新的位置
            }
        }

        // 同样方法计算球的位置/球质点
        temp.voluation(bp.pvForces);
        temp.scale(bp.pfInvMass);
        bp.pvAcceleration.voluation(temp);
        temp.scale(dt);
        bp.pvVelocity.add(temp);
        temp.voluation(bp.pvVelocity);
        temp.scale(dt);
        bp.pvPosition.add(temp);


        if (isC)
        {
            if (checkForCollisions()) {
                resolveCollisions();
            }
        }
    }

}

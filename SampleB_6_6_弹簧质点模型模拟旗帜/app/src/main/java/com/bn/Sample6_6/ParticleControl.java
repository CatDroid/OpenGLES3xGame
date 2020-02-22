package com.bn.Sample6_6;

import static com.bn.Sample6_6.Constant.*;

import com.bn.util.Vector3f;


public class ParticleControl {
    Particle[][] particles = new Particle[NUMROWS + 1][NUMCOLS + 1]; // 粒子数组

    private Spring[] springs = new Spring[NUMSPTINGS];                      // 弹簧数组
    private float[] vertices  = new float[NUMCOLS * NUMROWS * 2 * 3 * 3];   // 每个顶点xyz三个坐标
    private Collision[] collisions = new Collision[NUMVERTICES * 2];        // 碰撞体数组
    private Vector3f temp = new Vector3f(0, 0, 0);                  // 临时向量对象1
    private Vector3f temp2 = new Vector3f(0, 0, 0);                 // 临时向量对象2

    public ParticleControl()
    {
        initalize();
    }

    public float[] getVerties() // 获取一帧数据
    {
        int count = 0;          // 顶点计数器
        for (int r = 0; r < NUMROWS; r++)
        {
            for (int c = 0; c < NUMCOLS; c++)
            {
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

    // 初始化弹簧质点系统
    public void initalize()
    {

        // 红旗的面积是  0.75f * 1.0f = 0.75 m^2
        final float RSTER = 0.75f / NUMROWS;	// 质点行间距 (纹理采样,归一化?顶点坐标)
        final float CSTER = 1.0f / NUMCOLS;	// 质点列间距

        // 初始化质点
        for (int r = 0; r <= NUMROWS; r++)//对质点网格行进行遍历
        {
            for (int c = 0; c <= NUMCOLS; c++)//对质点网格列进列遍历
            {
                // 创建质点对象
                particles[r][c] = new Particle();

                // 声明质点质量临时变量
                float f;
                if (((r == 0) && (c == 0)) || ((r == NUMROWS) && (c == NUMCOLS)))
                {
                    f = 1;  // 处在旗帜左上和右下角的质点质量为1
                }
                else if (((r == NUMROWS) && (c == 0)) || ((r == 0) && (c == NUMCOLS)))
                {
                    f = 2;  // 处在旗帜左下和右上角的质点质量为2
                }
                else if (
                            (
                                (r == 0)
                                //&&
                                //((c != 0) || (c != NUMCOLS))
                            )
                            ||
                            (   (r == NUMROWS)
                                //&&
                                //((c != 0) || (c != NUMCOLS)) // 出去了4个角落的质点
                            )
                        )
                {
                    f = 3;  // 处在旗帜上下边缘的质点质量为3
                }
                else
                {
                    f = 6;  // 其余质点的质量为6
                }

                f = f / 8;  // 统一将质点质量减小到前面所给值的1/8

                // 保存质点质量,计算质量倒数
                particles[r][c].pfMass = f;
                particles[r][c].pfInvMass = 1 / particles[r][c].pfMass;


                // 根据所在行列计算 质点初始位置坐标 XOY平面 z=0  x从旗杆半径开始,旗帜长度为1(CSTER)  旗帜高度为0.75(RSTER) y从上到下 从 Numbers/2 ~ -1Nubmers/2
                particles[r][c].pvPosition.x = FLAGPOLERADIUS + c * CSTER;
                particles[r][c].pvPosition.y = RSTER * ( r - NUMROWS / 2.0f );
                particles[r][c].pvPosition.z = 0;


                if ((r == 0 && c == 0) || (r == NUMROWS && c == 0)) // 若为左上角或左下角的质点
                {
                    particles[r][c].bLocked = true;                 // 将质点设置为锁定点
                }
                else                                                // 若不为左上角或左下角的质点
                {
                    particles[r][c].bLocked = false;                // 质点不被锁定
                }
            }
        }

        // 初始化弹簧  每个质点/格子有4根自己的弹簧 最低的一行只有一根横向弹簧 最右的一列 只有竖向弹簧
        int count = 0;                                      // 计数器
        for (int r = 0; r <= NUMROWS; r++)                  // 对质点网格行进行遍历
        {
            for (int c = 0; c <= NUMCOLS; c++)               // 对质点网格列进列遍历
            {

                if (c < NUMCOLS)                            // 初始化横弹簧 ---  除了最后一列 都有横向弹簧
                {
                    springs[count] = new Spring();          // 创建弹簧对象
                    springs[count].p1.r = r;                // 弹簧连接的第1个质点的行
                    springs[count].p1.c = c;                // 弹簧连接的第1个质点的列
                    springs[count].p2.r = r;                // 弹簧连接的第2个质点的行
                    springs[count].p2.c = c + 1;            // 弹簧连接的第2个质点的列

                    temp.assign(particles[r][c].pvPosition);     // 将第1个质点的位置设置进临时向量

                    temp.sub(particles[r][c + 1].pvPosition);       // 减去第2个质点的位置

                    springs[count].L = temp.module();               // 求出弹簧的原始长度

                    count++;                                        // 计数器加1
                }

                if (r < NUMROWS)                            // 初始化竖弹簧 ---  除了最后一行，都有竖向弹簧
                {
                    springs[count] = new Spring();          // 创建弹簧对象
                    springs[count].p1.r = r;                // 弹簧连接的第1个质点的行
                    springs[count].p1.c = c;                // 弹簧连接的第1个质点的列
                    springs[count].p2.r = r + 1;            // 弹簧连接的第2个质点的行
                    springs[count].p2.c = c;                // 弹簧连接的第2个质点的列

                    temp.assign(particles[r][c].pvPosition);     // 将第1个质点的位置设置进临时向量

                    temp.sub(particles[r + 1][c].pvPosition);       // 减去第2个质点的位置

                    springs[count].L = temp.module();               // 求出弹簧的原始长度

                    count++;                                        // 计数器加1
                }

                if (r < NUMROWS && c < NUMCOLS)                 // 初始化左上、右下斜弹簧 --- 除了最后一列和最后一行 都有左倾斜弹簧
                {
                    springs[count] = new Spring();              // 创建弹簧对象

                    springs[count].k = SPRING_SHEAR_CONSTANT;   // 设置其劲度系数

                    springs[count].p1.r = r;                    // 弹簧连接的第1个质点的行
                    springs[count].p1.c = c;                    // 弹簧连接的第1个质点的列
                    springs[count].p2.r = r + 1;                // 弹簧连接的第2个质点的行
                    springs[count].p2.c = c + 1;                // 弹簧连接的第2个质点的列

                    temp.assign(particles[r][c].pvPosition); // 将第1个质点的位置设置进临时向量

                    temp.sub(particles[r + 1][c + 1].pvPosition);//减去第2个质点的位置 (两个点连接的弹簧线是斜着的)

                    springs[count].L = temp.module();           // 求出弹簧的原始长度

                    count++;                                    // 计数器加1
                }

                if (r < NUMROWS && c > 0)                       // 初始化右上、左下斜弹簧--- 除了第一列和最后一行 都有右倾斜弹簧
                {
                    springs[count] = new Spring();              // 创建弹簧对象

                    springs[count].k = SPRING_SHEAR_CONSTANT;   // 设置其劲度系数

                    springs[count].p1.r = r;                    // 弹簧连接的第1个质点的行
                    springs[count].p1.c = c;                    // 弹簧连接的第1个质点的列
                    springs[count].p2.r = r + 1;                // 弹簧连接的第2个质点的行
                    springs[count].p2.c = c - 1;                // 弹簧连接的第2个质点的列

                    temp.assign(particles[r][c].pvPosition); // 将第1个质点的位置设置进临时向量

                    temp.sub(particles[r + 1][c - 1].pvPosition);//减去第2个质点的位置

                    springs[count].L = temp.module();           // 求出弹簧的原始长度

                    count++;                                    // 计数器加1
                }

            }
        }

        for (int i = 0; i < NUMVERTICES * 2; i++)                // 循环初始化碰撞数组 ????
        {
            collisions[i] = new Collision();                     // 创建碰撞信息对象
        }
    }

    // 计算质点受力的方法
    public void calcForces()
    {

        for (int r = 0; r <= NUMROWS; r++)              // 对质点网格行进行遍历
        {
            for (int c = 0; c <= NUMCOLS; c++)          // 对质点网格列进列遍历
            {
                particles[r][c].pvForces.x = 0;         // 将质点受力的X分量设置为0
                particles[r][c].pvForces.y = 0;         // 将质点受力的Y分量设置为0
                particles[r][c].pvForces.z = 0;         // 将质点受力的Z分量设置为0
            }
        }

        float fly_x = (float) (Math.random() * 1.0f);
        float fly_z =  (float) (Math.random() * 0.3f);
        float fly_power = (float) (Math.random() * WindForce); // 根据当前风力大小产生风力向量



        for (int r = 0; r <= NUMROWS; r++)              // 对质点网格行进行遍历
        {
            for (int c = 0; c <= NUMCOLS; c++)          // 对质点网格列进行遍历
            {
                if (!particles[r][c].bLocked)           // 若该质点没有被锁定
                {
                    particles[r][c].pvForces.y += GRAVITY * particles[r][c].pfMass;  //计算重力  G=mg  g=0.7m/s2 ?? 不是 9.8m/s2

                    // 计算空气阻力，其方向与质点速度方向相反，其大小与质点速度大小的平方成正比
                    temp.assign(particles[r][c].pvVelocity);// 将质点速度拷贝进临时向量 最开始的速度大小为0
                    temp.normalize();                       // 获取速度的单位向量
                    temp.scale(-particles[r][c].pvVelocity.moduleSq() * DRAGCOEFFICIENT); // - |v|^2 * (DRAGCOEFFICIENT)


                    particles[r][c].pvForces.add(temp);        // 将空气阻力加入总受力

                                                               // 产生一个与XOZ平面平行，方向随机的向量 XOZ的第一限象方向

//                    float x = (float)(Math.random() * 1.0f); // 这样计算会导致 z方向抖动不比较大!!
//                    float z = (float)Math.sqrt(1.0 - x*x);
//                    temp.assign(x,0, z);

                    // 全部质点用同一个风速
                    temp.assign(fly_x, 0, fly_z);
                    temp.normalize();
                    temp.scale(fly_power);

//                    temp.assign((float) (Math.random() * 1.0f), 0, (float) (Math.random() * 0.3f));
//                    temp.normalize();
//                    temp.scale((float) (Math.random() * WindForce));// 根据当前风力大小产生风力向量

                    particles[r][c].pvForces.add(temp);             // 将风力加入总受力
                }
            }
        }


        for (int i = 0; i < NUMSPTINGS; i++)            // 遍历所有弹簧计算弹簧弹力
        {
            int r1 = (int) springs[i].p1.r;             // 获取弹簧连接的第1个质点的行号
            int c1 = (int) springs[i].p1.c;             // 获取弹簧连接的第1个质点的列号
            int r2 = (int) springs[i].p2.r;             // 获取弹簧连接的第2个质点的行号
            int c2 = (int) springs[i].p2.c;             // 获取弹簧连接的第2个质点的列号

            temp.assign(particles[r1][c1].pvPosition);  // 将第一个质点位置复制进临时向量
            temp.sub(particles[r2][c2].pvPosition);     // 减去第2个质点的位置
            float pd = temp.module();                   // 计算出两个质点间的距离

            temp2.assign(particles[r1][c1].pvVelocity); // 将第一个质点速度复制进临时向量
            temp2.sub(particles[r2][c2].pvVelocity);    // 减去第2个质点的速度得到弹簧力方向  相对速度

            float L = springs[i].L;                     // 获取当前弹簧静止时的长度(也就是没有拉长或者压缩时候是没有弹簧力的)

                                                        // 根据弹簧力公式计算弹簧弹力与阻尼力之和的大小
            float v = (temp.dotProduct(temp2) / pd);    // 相对速度在弹簧方向上的分速度  实际就是求资点方向的单位向量 乘以 弹簧两个质点的相对速度


            float tanli =  - springs[i].k * (pd - L);   // 弹力大小
            float zunili=  - springs[i].d * v;          // 阻尼力大小

            float t =  (tanli  + zunili) ;

            temp.normalize();                                   // 弹簧力方向

            temp.scale(t);                                      // 弹簧力大小=阻力大小+弹力大小

            if (!particles[r1][c1].bLocked)                     // 若质点1未被锁定
            {
                particles[r1][c1].pvForces.add(temp);           // 将弹簧力加入总受力
            }

            if (!particles[r2][c2].bLocked)                     // 若质点2未被锁定
            {
                temp.scale(-1);                               // 计算弹簧力的反向力作为质点2所受弹簧力
                particles[r2][c2].pvForces.add(temp);            // 将弹簧力加入总受力
            }

        }
    }

    public boolean checkForCollisions()
    {
        int count = 0;

        // 返回值  如果state = true 有碰撞  = false 跟地面和旗杆都没有碰撞
        boolean state = false;

        // 清除上一轮碰撞信息 -1 代表没有碰撞
        for (int i = 0; i < collisions.length; i++)
        {
            collisions[i].r = -1;
        }

        // 检测质点与地面的碰撞
        for (int r = 0; r <= NUMROWS; r++)
        {
            for (int c = 0; c <= NUMCOLS; c++)
            {
                if (!particles[r][c].bLocked)
                {
                    // 质子是否与地面碰撞两个条件
                    // 1. y 低于地面的高度
                    // 2. y方向速度是负数(指向地面)
                    //
                    if ((particles[r][c].pvPosition.y <= COLLISIONTOLERANCE)
                            && (particles[r][c].pvVelocity.y < 0f))
                    {
                        state = true;
                        collisions[count].r = r;
                        collisions[count].c = c;
                        collisions[count].n.x = 0.0f;
                        collisions[count].n.y = 1.0f;
                        collisions[count].n.z = 0.0f;
                        count++;
                    }
                }
            }
        }


        // 检测质点与旗杆的碰撞
        for (int r = 0; r <= NUMROWS; r++)
        {
            for (int c = 0; c <= NUMCOLS; c++)
            {
                if (!particles[r][c].bLocked)
                {
                    // 获得此粒子位置距旗杆中线的距离
                    float fd = (particles[r][c].pvPosition.x) * (particles[r][c].pvPosition.x) +
                            (particles[r][c].pvPosition.z) * (particles[r][c].pvPosition.z);

                    // 在XOZ平面上 只要跟 向量(x,z) 夹角在-90到90之间的话  就是靠外运动
                    temp.assign(particles[r][c].pvPosition.x, 0, particles[r][c].pvPosition.z);

                    if ((fd <= FLAGPOLERADIUS_SQUARE) &&
                            (temp.dotProduct(particles[r][c].pvVelocity) < 0f)) // ??? > 0f
                    {
                        state = true;
                        collisions[count].r = r;
                        collisions[count].c = c;
                        collisions[count].n.assign(temp); // 碰撞面的法向量
                        collisions[count].n.normalize();
                        count++;
                    }
                }
            }
        }
        return state;
    }


    // 处理碰撞的方法 遍历所有碰撞 后 质点的速度
    public void resolveCollisions()
    {
        for (int i = 0; i < collisions.length; i++)     // 遍历碰撞信息对象数组
        {
            if (collisions[i].r != -1)                  // 若此碰撞信息对象有效
            {
                int r = collisions[i].r;                // 获取碰撞对应质点的行号
                int c = collisions[i].c;                // 获取碰撞对应质点的列号

                // 把速度分成 切向速度 + 法向速度
                // 求得法向量方向的速度分量
                temp.assign(collisions[i].n);           //  dot( -n , v ) * (-n)
                temp.scale(temp.dotProduct(particles[r][c].pvVelocity));


                temp2.assign(particles[r][c].pvVelocity);   // 将质点速度拷贝进临时向量2
                temp2.sub(temp);                            // 减去法向量方向速度分量得切向速度分量

                // 两个方向上分别做衰减
                temp.scale(-KRESTITUTION);                  // 法向量方向速度 乘以 负反弹系数
                temp2.scale(FRICTIONFACTOR);                // 切向方向速度   乘以 动摩擦系数

                temp.add(temp2);                            // 法向量方向速度加切向速度得到总速度

                particles[r][c].pvVelocity.assign(temp);    // 将总速度设置进质点速度属性
            }
        }
    }


    // 实现运动方程积分的方法
    public void stepSimulation(float dt)

    {
        // Step.1  调用calcForces方法计算各个质点的受力
        calcForces();

        // Step.2 计算新的位置
        for (int r = 0; r <= NUMROWS; r++)
        {
            for (int c = 0; c <= NUMCOLS; c++)
            {
                temp.assign(particles[r][c].pvForces);          // 将质点受合力复制进临时向量
                temp.scale(particles[r][c].pfInvMass);          // 计算质点总加速度  a = F / m

                particles[r][c].pvAcceleration.assign(temp);    //  将加速度记录进质点对象成员 ?? 啥作用 ??

                temp.scale(dt);                                 // 加速度乘以步进时间得到速度变化量

                particles[r][c].pvVelocity.add(temp);           // 原速度加上速度变化量得到新速度

                temp.assign(particles[r][c].pvVelocity);        //  将新速度复制进临时向量
                temp.scale(dt);                                 // 新速度乘以步进时间得到位移

                // 这里认为是以新的速度 运行了dt时间  相当于是匀速运动

                particles[r][c].pvPosition.add(temp);           // 原位置加上位移得到新位置

                if (particles[r][c].pvPosition.y < COLLISIONTOLERANCE)// 若新位置Y坐标低于地面
                {
                    particles[r][c].pvPosition.y = COLLISIONTOLERANCE;// 新位置Y坐标等于地面高度
                }
            }
        }

        // 检测碰撞 和 碰撞后速度方向和大小调整
        if (isC)
        {                                       // 若开启了碰撞检测
            if (checkForCollisions()) {         // 计算碰撞信息
                resolveCollisions();            // 处理碰撞
            }
        }
    }
}

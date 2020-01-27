package com.bn.Sample5_8;//声明包
/*
 * 球心在原点，半径为1的标准球
 * 注意：其变换成椭球后法向量也是对的
 */
public class Ball extends HitObject {// 球心在原点，半径为1的标准球

	// 初始化颜色与摄像机
	public Ball(Camera cam, Color3f color)
	{
		this.cam = cam;
		this.color = color;
	}

	// 重写的计算光线与物体碰撞点的方法 r 是世界坐标系下的光跟线
	@Override
	public boolean hit(Ray r,Intersection inter)
	{
		/*
		 * 求解光线S+ct与变换后物体的交点需要以下步骤：
		 * 1、求出逆变换光线S'+c't
		 * 2、求出逆变换光线与通用物体的碰撞时间t(需要大于0 并且取小的那个)
		 * 3、把碰撞时间t代入等式S+ct得到实际的交点坐标
		 */
		Ray genRay = new Ray();					// 变换后的光线(物体坐标系)
		xfrmRay(genRay, _getInvertMatrix(), r);	// 计算出变换后的光线

		double A,B,C;										// 计算交点的方程的3个系数
		A = Vector3.dot(genRay.dir, genRay.dir);			// 求出第一个系数A
		B = Vector3.dot(genRay.start, genRay.dir);			// 求出第一个系数B
		C = Vector3.dot(genRay.start, genRay.start)-1.0f;	// 求出第一个系数C

		double discrim = B*B-A*C;                        	// 求判别式的值
		if (discrim < 0.0)									// 若判别式值小于0则没有交点
		{
			return false;
		}

		int num = 0; 										// 目前的交点个数
		double discRoot = (float) Math.sqrt(discrim);		// 求判别式的平方根

		double t1 = (-B - discRoot)/A;						// 第一个交点的对应相交时间 必须是正数 大于浮点数精度0.00001
		if (t1 > 0.00001)
		{
			inter.hit[0].hitTime=t1;						// 记录交点的时间
			inter.hit[0].hitObject=this;					// 记录交点所属的物体
			inter.hit[0].isEntering=true;
			inter.hit[0].surface=0;			
			Point3 P = rayPos(r,t1);						// 交点坐标(代入 变换前的光线 S + c*t) 世界坐标系
			inter.hit[0].hitPoint.set(P);					// 设置交点的 顶点坐标/世界坐标系
			Point3 preP = xfrmPtoPreP(P);					// 世界坐标系转换物体坐标系 (对于球体来说 物体坐标系下 球表面点坐标就是法向量)
			inter.hit[0].hitNormal.set(preP);				// 设置交点的 顶点法线/物体坐标系(通过 法向量 左乘 逆转置矩阵)
			
			num = 1; 										// 交点数量
		}

		double t2 = (-B + discRoot)/A;						// 第二个交点的对应相交时间
		if (t2 > 0.00001)
		{
			inter.hit[num].hitTime=t2;						// 记录交点的时间
			inter.hit[num].hitObject=this;					// 记录交点所属的物体
			inter.hit[num].isEntering=true;
			inter.hit[num].surface=0;
			Point3 P = rayPos(r,t2);
			inter.hit[num].hitPoint.set(P);
			Point3 preP = xfrmPtoPreP(P);
			inter.hit[num].hitNormal.set(preP);
			
			num++;
		}
		inter.numHits = num;

		return (num > 0);						// 若有一个或一个以上的有效交点则返回true
	}


	// 服务于阴影探测器判断是否在阴影中的方法
	@Override
	public boolean hit(Ray r)
	{
		Ray genRay = new Ray();								// 变换后的光线
		xfrmRay(genRay, _getInvertMatrix(), r);				// 获取变换后的光线

		double A,B,C;										// 计算交点的方程的3个系数
		A = Vector3.dot(genRay.dir, genRay.dir);			// 求出第一个系数A
		B = Vector3.dot(genRay.start, genRay.dir);			// 求出第一个系数B
		C = Vector3.dot(genRay.start, genRay.start)-1.0f;	// 求出第一个系数C
		double discrim = B*B - A*C;                        	// 求判别式值
		if (discrim < 0.0) {								// 若判别式小于0则没有交点
			return false;
		}
		double discRoot = (float) Math.sqrt(discrim);		// 求判别式的平方根
		double t1 = (-B-discRoot)/A;						// 第一次相交的时间
															// 只接受从0到1之间的碰撞，因为在光源另外一侧不会产生阴影
		if (t1 < 0 || t1 > 1) {								// 若相交时间不在0～1内则不在阴影中
			return false;
		}
		return true;										// 否则在阴影中
	}
}

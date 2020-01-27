package com.bn.Sample5_8;

/*
 * 位于xoy平面中心，边长为2的正方形
 */
public class Square extends HitObject {

	public Square(Camera cam, Color3f color)
	{
		this.cam = cam;
		this.color = color;
	}

	@Override
	public boolean hit(Ray r,Intersection inter) {

		// 正方形 在模型坐标系下是 在XOY平面上的

		Ray genRay = new Ray();
		xfrmRay(genRay, _getInvertMatrix(), r);// r是世界坐标系下的光跟线 genRay是模型坐标系下的光跟线

		double denom = genRay.dir.z;	// 分母
		
		if(Math.abs(denom) < 0.0001)	// 光线和平面平行:无交点
		{
			return false;
		}

		double time = -genRay.start.z/denom ;	// 相交时间 直线和平面最多只有一个相交点 并且由于Square在XOY平面 相交点z=0
												// 			所以 Sz + Dz * t = 0
												//			t = -Sz / Dz
		if (time <= 0.0) 						// 交点落在视点后方
		{
			return false;
		}
		
		
		
		double hx = genRay.start.x + genRay.dir.x *time;	// 交点的x坐标
		double hy = genRay.start.y + genRay.dir.y *time;	// 交点的y坐标

		if (hx > 1.0 || hx < -1.0) {//x不在范围内
			return false;
		}
		if (hy > 1.0 || hy < -1.0) {//y不在范围内
			return false;
		}
		
		inter.numHits = 1;//有一个有效交点
		
		//将光线和物体的相交信息存入inter中
		inter.hit[0].hitTime = time;
		inter.hit[0].hitObject = this;
		inter.hit[0].isEntering = true;
		inter.hit[0].surface = 0;

		Point3 P = rayPos(r,time);					// 交点世界坐标系下的坐标(使用变换前的光线)
		inter.hit[0].hitPoint.set(P);				// 世界坐标系下的 顶点位置
		inter.hit[0].hitNormal.set(0,0,1);	// 模型坐标系的法向量

		return true;
	}

	// 只需要判断是否跟传入的光线 有碰撞
	@Override
	public boolean hit(Ray r) {

		// 获取变换后的光线（将r按逆变换矩阵变换后赋值给genRay）
		Ray genRay = new Ray();
		xfrmRay(genRay, _getInvertMatrix(), r);

		// 分母
		double denominator = genRay.dir.z;

		// 光线和平面平行:无交点
		if(Math.abs(denominator)<0.0001)
		{
			return false;
		}

		double time = -genRay.start.z/denominator;//相交时间

		if(time<0.0 ||time>1){
			return false;
		}
		
		double hx = genRay.start.x + genRay.dir.x*time;//交点的x坐标
		double hy = genRay.start.y + genRay.dir.y*time;//交点的y坐标
		if (hx > 1.0 || hx < -1.0) {
			return false;
		}
		if (hy > 1.0 || hy < -1.0) {
			return false;
		}
		return true;
	}
}

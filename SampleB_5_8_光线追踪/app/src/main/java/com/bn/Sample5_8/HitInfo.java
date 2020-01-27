package com.bn.Sample5_8;//声明包
//一个交点的信息
public class HitInfo{

	double hitTime;			// 相交时间
	HitObject hitObject;	// 相交的物体
	boolean isEntering;		// 光线是进入还是射出
	int surface;			// 相交于哪个表面
	Point3 hitPoint;		// 交点/顶点的坐标(世界坐标系)
	Vector3 hitNormal;		// 交点/顶点的法向量(世界坐标系)
	
	public HitInfo()
	{
		hitPoint = new Point3();		// 创建碰撞点对象
		hitNormal = new Vector3();		// 创建法向量对象
	}
	/*
	 * 根据传入的HitInfo对象，复制各项信息进入自身的成员变量
	 *
	 * 此方法可能会不对，复制问题可能出现
	 * 如果有解决不了的问题可以回来看
	 */
	public void set(HitInfo hit)
	{
		this.hitTime 	= hit.hitTime;
		this.hitObject 	= hit.hitObject;
		this.isEntering = hit.isEntering;
		this.surface	= hit.surface;
		this.hitPoint.set(hit.hitPoint);
		this.hitNormal.set(hit.hitNormal);
	}

	@Override
	public String toString()
	{
		return "hitTime"+hitTime+",hitPoint"+hitPoint;
	}
}

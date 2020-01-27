package com.bn.Sample5_8;//声明包

//光线类
public class Ray {

	Point3 start;//光线起始位置
	Vector3 dir;//光线方向
	public Ray(){
		//初始化光线起始位置及方向
		start = new Point3();
		dir = new Vector3();
	}
	//设置光线起始位置的方法
	public void setStart(Point3 start){
		this.start.x = start.x;
		this.start.y = start.y;
		this.start.z = start.z;
	}
	//设置光线发射方向的方法
	public void setDir(Vector3 dir){
		this.dir.x = dir.x;
		this.dir.y = dir.y;
		this.dir.z = dir.z;
	}
}

package com.bn.util;

/*
 * 代表3D空间中向量的类
 */
public class Vector3f {
	//向量的三个分量
	public float x;
	public float y;
	public float z;
	//构造器
	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Vector3f(Vector3f v) {
		this.x=v.x;
		this.y=v.y;
		this.z=v.z;
	}
	//将向量规格化的方法
	public void normalize(){
		float mod = module();
		//保证模不为零时再规格化向量
		if(mod != 0){
			x = x/mod;
			y = y/mod;
			z = z/mod;
		}
	}
	//求向量的模的方法
	public float module(){
		return (float) Math.sqrt(x*x + y*y + z*z);
	}
	//求向量的模平方
	public float moduleSq(){
		return (float) x*x + y*y + z*z;
	}
	//减法
	public void sub(Vector3f v){
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
	}
	//加法
	public void add(Vector3f v){
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
	}
	//缩放
	public void scale(float s){
		this.x *= s;
		this.y *= s;
		this.z *= s;
	}
	//点乘
	public float dotProduct(Vector3f v){
		return x*v.x+y*v.y+z*v.z;
	}
	//叉乘
	public Vector3f crossProduct(Vector3f v)
	{
		return new Vector3f(
				y*v.z-z*v.y,
				z*v.x-x*v.z,
				x*v.y-y*v.x
				);
	}
	//对该向量重新赋值
	public void voluation(float x,float y,float z)
	{
		this.x=x;
		this.y=y;
		this.z=z;
	}
	//对该向量重新赋值
	public void voluation(Vector3f v)
	{
		this.x=v.x;
		this.y=v.y;
		this.z=v.z;
	}
	
	


}

package com.bn.Sample6_6;

import com.bn.util.Vector3f;

public class Particle {
	float pfMass;									// 质点质量
	float pfInvMass;								// 质点质量的倒数，为方便计算
	Vector3f pvPosition;							// 质点位置
	Vector3f pvVelocity;							// 质点速度
	Vector3f pvAcceleration;						// 质点加速度
	Vector3f pvForces;								// 质点受合力
	boolean bLocked;								// 是否被锁定标志

	public Particle()
	{												// 构造器
		this.pvPosition = new Vector3f(0,0,0);			// 初始化位置
		this.pvVelocity = new Vector3f(0,0,0); 			// 初始化速度
		this.pvAcceleration = new Vector3f(0,0,0); 		// 初始化加速度
		this.pvForces = new Vector3f(0,0,0); 			// 初始化受力
	}
}

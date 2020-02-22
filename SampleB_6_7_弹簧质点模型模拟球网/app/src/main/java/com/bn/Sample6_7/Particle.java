package com.bn.Sample6_7;

import com.bn.util.Vector3f;

public class Particle {
	float pfMass;//粒子质量
	float pfInvMass;//粒子质量倒数，为方便计算
	Vector3f pvPosition;//粒子位置
	Vector3f pvVelocity;//粒子速度
	Vector3f pvAcceleration;//粒子加速度
	Vector3f pvForces;//粒子受力
	boolean bLocked;//是否被锁定
	
	public Particle()
	{
		this.pvPosition = new Vector3f(0,0,0);
		this.pvVelocity = new Vector3f(0,0,0);
		this.pvAcceleration = new Vector3f(0,0,0);
		this.pvForces = new Vector3f(0,0,0);
	}
	
}

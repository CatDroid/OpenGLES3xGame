package com.bn.Sample6_7;

import com.bn.util.Vector3f;

public class BallParticle extends Particle{				//继承自普通质点类
	float ballR;								//足球半径
	Vector3f cn;								//碰撞法向量
	float rQ;									//足球半径的平方
	public BallParticle(float r){						//构造器
		super();								//调用父类构造器
		cn = new Vector3f(0, 0, 0);				//初始化碰撞法向量
		ballR = r;								//初始化半径
		rQ = r*r;								//计算半径的平方
	}
}

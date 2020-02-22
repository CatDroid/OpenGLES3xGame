package com.bn.Sample6_7;

import com.bn.util.Vector3f;


public class Collision {
	int r;//碰撞粒子行列
	int c;
	Vector3f n;//计算线性冲量的碰撞法向量
	
	public Collision()
	{
		r = -1;
		c = -1;
		n = new Vector3f(0,0,0);
	}
}

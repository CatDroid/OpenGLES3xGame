package com.bn.Sample6_6;

import com.bn.util.Vector3f;

public class Collision {
	int r;												//碰撞质点所在行
	int c;												//碰撞质点所在列
	Vector3f n;										//碰撞法向量
	public Collision(){									//构造器
		r = -1; 										//初始化行数
		c = -1; 										//初始化列数
		n = new Vector3f(0,0,0); 							//初始化法向量
	}
}

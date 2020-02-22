package com.bn.Sample6_7;

import static com.bn.Sample6_7.Constant.*;
//弹簧
public class Spring {
	ParticleRet p1;//所连接1号粒子
	ParticleRet p2;//所连接2号粒子
	float k;//伸展的弹簧常数
	float d;//阻尼系数
	float L;//弹簧静止时的长度
	
	public Spring()
	{
		this.p1 = new ParticleRet();
		this.p2 = new ParticleRet();
		this.k = SPRING_TENSION_CONSTANT;
		this.d = SPRING_DAMPING_CONSTANT;
	}
}

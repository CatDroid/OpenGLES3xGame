package com.bn.Sample6_6;
import static com.bn.Sample6_6.Constant.*;
//弹簧
public class Spring {
	ParticleRet p1;								//	弹簧所连接的1号质点位置
	ParticleRet p2;								//	弹簧所连接的2号质点位置
	float k;									//	弹簧劲度系数（胡克系数）
	float d;									//	阻尼系数
	float L;									//	弹簧静止时的长度
	public Spring(){							//	构造器
		this.p1 = new ParticleRet();			// 	初始化弹簧连接的1号质点位置
		this.p2 = new ParticleRet();			// 	初始化弹簧连接的2号质点位置
		this.k = SPRING_TENSION_CONSTANT;		// 	初始化弹簧劲度系数
		this.d = SPRING_DAMPING_CONSTANT;		// 	初始化弹簧阻尼系数
	}
}

package com.bn;

import static com.bn.Constant.*;
import java.util.*;

public class Noise1DUtil {
	
	// 求乘方运算的方法
	public static int pow(int a, int b) {
		int result = 1;
		for (int i = 0; i < b; i++) {
			result = result * a;
		}
		return result;
	}

	// 生成  噪声控制点(整数点x) 幅度是amp 的方法
	public static double noise(int x, double amp) {
		x = (x << 13) ^ x;
		double result = ((1.0 - ((x * (x * x * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0) + 1) / 2.0;
		// result 返回的是0~1的浮点数 !!!!!!  论文没有+1 
		if(result > 1.0f || result < 0.0f ) {
			System.out.println(result);
		}
		return result * amp; // 幅度改成-amp~amp 
	}

	// 余弦插值函数
	public static double cosIn(double a, double b, double x) {
		double ft = x * Math.PI; 				// x 是 0~1
		double f = (1 - Math.cos(ft)) * 0.5; 	// cos(ft) 1~-1, -cos(ft) -1~1 1-cost(ft) 0~2 * 0.5 = 0~1
		return a * (1 - f) + b * f; 			// x 不是直接融合(线性插值) 而是先经过余弦映射
	}

	// 求出某一级倍频的数据
	public static double[][] calYJBP(int level) {
		
		double[][] result = null;

		
		// 计算出此级倍频的总段数   16 * 2^(level-1)
		int tc = COUNT * pow(BP, level - 1);   
		// 此级倍频的控制点值
		double[] cp = new double[tc + 1];
		// 此级倍频的振幅
		double amp = 1.0 / pow(BP, level - 1); //  1/(2^(level-1))  一级倍频  幅度最大是1 
		// 求出此级倍频的所有控制点
		for (int i = 0; i <= tc; i++) {
			cp[i] = noise(++X_CURR, amp);	  // X_CURR 为起始坐标 
		}
		
		
		// 此级倍频X步进 相当于控制点之间的距离
		double xSpan = X_SPAN / pow(BP, level - 1);
		// 此级倍频切分数 , 在每个步进 之间 要插值的数量 ，控制点越多，中间插值的就减少
		int qfs = pow(BP, 8 - level);
		// 此级倍频切分后的X步进  
		double xSpanQf = xSpan / qfs;  
		
		
		// 存放切分插值后点的坐标列表
		List<double[]> list = new ArrayList<double[]>();
		for (int i = 0; i < tc; i++) {
			// 取出这一段的两个控制点的值
			double a = cp[i];
			double b = cp[i + 1];
			
			// 将起始点添加进列表
			list.add(new double[] { i * xSpan, a }); // 注意记得这个：  坐标 , (控制点/插值后的)值
			
			// 循环插值求出后面的中间点
			for (int j = 1; j < qfs; j++) {
				// 插值用X
				double xIn = (1.0 / qfs) * j;
				// 求出此点的值加入列表   xSpan控制点之间的距离    xSpanQf两控制点之间插值点的距离
				list.add(new double[] { i * xSpan + j * xSpanQf , 
										cosIn(a, b, xIn)  // 做余弦插值 
									});
			}
		}
		// 注意记得这个：将最后一个点加入列表
		list.add(new double[] { COUNT, cp[tc] });

		
		// 转换成二维数组 
		result = new double[list.size()][];
		for (int i = 0; i < list.size(); i++) {
			result[i] = list.get(i); // result[i][0] result[i][1]
		}
		return result;
	}

	// 求出所有倍频的情况以及总情况
	public static double[][][] calSYBP() {
		double[][][] result = new double[PLS + 1][][];

		if(RESTART_FROM_BEGIN) {
			for (int i = 1; i <= PLS; i++)  
			{
				X_CURR = 0 ; 				// HHL 测试 如果每次都是从 X_CURR开始 
				result[i - 1] = calYJBP(i);	// result 第一个维度是倍频
			}
		}else {
			for (int i = 1; i <= PLS; i++) 
			{
				result[i - 1] = calYJBP(i);
			}
		}
		

		result[PLS] = new double[result[0].length][2];

		for (int i = 0; i < result[0].length; i++) { // 一共有多少个点   每个倍频的点都是一样多  只是倍频高的控制点多插值点少  2^P * 2^(8-P) = 2^8 = 64个点
			result[PLS][i][0] = result[0][i][0];
			double temp = 0;
			for (int j = 0; j < PLS; j++) { // 每个点 对应PLS个倍频的值 加起来
				temp += result[j][i][1];
			}
			result[PLS][i][1] = temp;
			//System.out.println(i + ":" + temp); // 因为  一级倍频  幅度最大是1 , 所以累加最大值是2
		}
		return result;
	}
}

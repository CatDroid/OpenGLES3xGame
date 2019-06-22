package com.bn;

//向量计算方法的封装类
public class VectorUtil {
	// 求两个向量的叉积方法
	public static float[] getCrossProduct(float x1, float y1, float z1, float x2, float y2, float z2) {
		// 求出两个矢量叉积矢量在x、y、z轴上的分量A、B、C
		float A = y1 * z2 - y2 * z1;
		float B = z1 * x2 - z2 * x1;
		float C = x1 * y2 - x2 * y1;
		return new float[] { A, B, C };// 返回结果向量
	}

	public static float[] vectorNormal(float[] vector) {// 向量规格化的方法
		float module = (float) Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);// 求向量的模
		return new float[] { vector[0] / module, vector[1] / module, vector[2] / module };
	}

	public static float mould(float[] vec) {// 求向量模的方法
		return (float) Math.sqrt(vec[0] * vec[0] + vec[1] * vec[1] + vec[2] * vec[2]);
	}
}

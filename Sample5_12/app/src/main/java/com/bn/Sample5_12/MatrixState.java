package com.bn.Sample5_12;// 声明包
import android.opengl.Matrix; //引入相关类
public class MatrixState {//存储系统矩阵状态的类
	private static float[] mProjMatrix = new float[16];//4x4矩阵 投影用
    private static float[] mVMatrix = new float[16];//摄像机位置朝向9参数矩阵
    private static float[] mMVPMatrix;//最终的总变换矩阵
	
	public static void setCamera(// 设置摄像机
			float cx, // 摄像机位置x
			float cy, // 摄像机位置y
			float cz, // 摄像机位置z
			float tx, // 摄像机目标点x
			float ty, // 摄像机目标点y
			float tz, // 摄像机目标点z
			float upx, // 摄像机UP向量X分量
			float upy, // 摄像机UP向量Y分量
			float upz // 摄像机UP向量Z分量
	) {
		Matrix.setLookAtM(mVMatrix, 0, cx, cy, cz, tx, ty, tz, upx, upy, upz);
	}	
	public static void setProjectFrustum(// 设置透视投影参数
			float left, // near面的left
			float right, // near面的right
			float bottom, // near面的bottom
			float top, // near面的top
			float near, // near面与视点的距离
			float far // far面与视点的距离
	) {
		Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
	}	
	public static void setProjectOrtho(// 设置正交投影参数
			float left, // near面的left
			float right, // near面的right
			float bottom, // near面的bottom
			float top, // near面的top
			float near, // near面与视点的距离
			float far // far面与视点的距离
	) {
		Matrix.orthoM(mProjMatrix, 0, left, right, bottom, top, near, far);
	}	
	public static float[] getFinalMatrix(float[] spec) {// 获取具体物体的总变换矩阵
		mMVPMatrix = new float[16];
		Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, spec, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
		return mMVPMatrix;
	}
}

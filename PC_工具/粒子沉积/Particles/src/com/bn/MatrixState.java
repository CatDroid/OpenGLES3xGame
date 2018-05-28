package com.bn;

public class MatrixState {
	private static float[] mProjMatrix = new float[16];
	private static float[] mVMatrix = new float[16];

	private static float[] currMatrix;

	public static int[] finalPosition(float[] p, float[] m, float sbw, float sbh) {
		float[] result = new float[4];

		Matrix.multiplyMV(result, 0, m, 0, new float[] { p[0], p[1], p[2], 1.0F }, 0);

		result[0] /= result[3];
		result[1] /= result[3];
		result[2] /= result[3];

		float x = result[0];
		float y = 1.0F - result[1];
		x = x * sbw / 2.0F + sbw / 2.0F;
		y = y * sbh / 2.0F;

		return new int[] { (int) x, (int) y };
	}

	public static void setInitStack() {
		currMatrix = new float[16];
		Matrix.setIdentityM(currMatrix, 0);
	}

	public static void setCamera(float cx, float cy, float cz, float tx, float ty, float tz, float upx, float upy,
			float upz) {
		Matrix.setLookAtM(

				mVMatrix, 0, cx, cy, cz, tx, ty, tz, upx, upy, upz);
	}

	public static void setProjectFrustum(float left, float right, float bottom, float top, float near, float far) {
		Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
	}

	public static float[] getFinalMatrix() {
		float[] mMVPMatrix = new float[16];
		Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
		return mMVPMatrix;
	}
}

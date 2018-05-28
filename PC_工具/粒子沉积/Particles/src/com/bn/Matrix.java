package com.bn;

public class Matrix {
	public static void multiplyMM(float[] result, int resultOffset, float[] mlIn, int lhsOffset, float[] mrIn,
			int rhsOffset) {
		double[] ml = new double[16];
		double[] mr = new double[16];
		for (int i = 0; i < 16; i++) {
			ml[i] = mlIn[i];
			mr[i] = mrIn[i];
		}

		result[(0 + resultOffset)] = ((float) (ml[(0 + lhsOffset)] * mr[(0 + rhsOffset)]
				+ ml[(4 + lhsOffset)] * mr[(1 + rhsOffset)] + ml[(8 + lhsOffset)] * mr[(2 + rhsOffset)]
				+ ml[(12 + lhsOffset)] * mr[(3 + rhsOffset)]));
		result[(1 + resultOffset)] = ((float) (ml[(1 + lhsOffset)] * mr[(0 + rhsOffset)]
				+ ml[(5 + lhsOffset)] * mr[(1 + rhsOffset)] + ml[(9 + lhsOffset)] * mr[(2 + rhsOffset)]
				+ ml[(13 + lhsOffset)] * mr[(3 + rhsOffset)]));
		result[(2 + resultOffset)] = ((float) (ml[(2 + lhsOffset)] * mr[(0 + rhsOffset)]
				+ ml[(6 + lhsOffset)] * mr[(1 + rhsOffset)] + ml[(10 + lhsOffset)] * mr[(2 + rhsOffset)]
				+ ml[(14 + lhsOffset)] * mr[(3 + rhsOffset)]));
		result[(3 + resultOffset)] = ((float) (ml[(3 + lhsOffset)] * mr[(0 + rhsOffset)]
				+ ml[(7 + lhsOffset)] * mr[(1 + rhsOffset)] + ml[(11 + lhsOffset)] * mr[(2 + rhsOffset)]
				+ ml[(15 + lhsOffset)] * mr[(3 + rhsOffset)]));

		result[(4 + resultOffset)] = ((float) (ml[(0 + lhsOffset)] * mr[(4 + rhsOffset)]
				+ ml[(4 + lhsOffset)] * mr[(5 + rhsOffset)] + ml[(8 + lhsOffset)] * mr[(6 + rhsOffset)]
				+ ml[(12 + lhsOffset)] * mr[(7 + rhsOffset)]));
		result[(5 + resultOffset)] = ((float) (ml[(1 + lhsOffset)] * mr[(4 + rhsOffset)]
				+ ml[(5 + lhsOffset)] * mr[(5 + rhsOffset)] + ml[(9 + lhsOffset)] * mr[(6 + rhsOffset)]
				+ ml[(13 + lhsOffset)] * mr[(7 + rhsOffset)]));
		result[(6 + resultOffset)] = ((float) (ml[(2 + lhsOffset)] * mr[(4 + rhsOffset)]
				+ ml[(6 + lhsOffset)] * mr[(5 + rhsOffset)] + ml[(10 + lhsOffset)] * mr[(6 + rhsOffset)]
				+ ml[(14 + lhsOffset)] * mr[(7 + rhsOffset)]));
		result[(7 + resultOffset)] = ((float) (ml[(3 + lhsOffset)] * mr[(4 + rhsOffset)]
				+ ml[(7 + lhsOffset)] * mr[(5 + rhsOffset)] + ml[(11 + lhsOffset)] * mr[(6 + rhsOffset)]
				+ ml[(15 + lhsOffset)] * mr[(7 + rhsOffset)]));

		result[(8 + resultOffset)] = ((float) (ml[(0 + lhsOffset)] * mr[(8 + rhsOffset)]
				+ ml[(4 + lhsOffset)] * mr[(9 + rhsOffset)] + ml[(8 + lhsOffset)] * mr[(10 + rhsOffset)]
				+ ml[(12 + lhsOffset)] * mr[(11 + rhsOffset)]));
		result[(9 + resultOffset)] = ((float) (ml[(1 + lhsOffset)] * mr[(8 + rhsOffset)]
				+ ml[(5 + lhsOffset)] * mr[(9 + rhsOffset)] + ml[(9 + lhsOffset)] * mr[(10 + rhsOffset)]
				+ ml[(13 + lhsOffset)] * mr[(11 + rhsOffset)]));
		result[(10 + resultOffset)] = ((float) (ml[(2 + lhsOffset)] * mr[(8 + rhsOffset)]
				+ ml[(6 + lhsOffset)] * mr[(9 + rhsOffset)] + ml[(10 + lhsOffset)] * mr[(10 + rhsOffset)]
				+ ml[(14 + lhsOffset)] * mr[(11 + rhsOffset)]));
		result[(11 + resultOffset)] = ((float) (ml[(3 + lhsOffset)] * mr[(8 + rhsOffset)]
				+ ml[(7 + lhsOffset)] * mr[(9 + rhsOffset)] + ml[(11 + lhsOffset)] * mr[(10 + rhsOffset)]
				+ ml[(15 + lhsOffset)] * mr[(11 + rhsOffset)]));

		result[(12 + resultOffset)] = ((float) (ml[(0 + lhsOffset)] * mr[(12 + rhsOffset)]
				+ ml[(4 + lhsOffset)] * mr[(13 + rhsOffset)] + ml[(8 + lhsOffset)] * mr[(14 + rhsOffset)]
				+ ml[(12 + lhsOffset)] * mr[(15 + rhsOffset)]));
		result[(13 + resultOffset)] = ((float) (ml[(1 + lhsOffset)] * mr[(12 + rhsOffset)]
				+ ml[(5 + lhsOffset)] * mr[(13 + rhsOffset)] + ml[(9 + lhsOffset)] * mr[(14 + rhsOffset)]
				+ ml[(13 + lhsOffset)] * mr[(15 + rhsOffset)]));
		result[(14 + resultOffset)] = ((float) (ml[(2 + lhsOffset)] * mr[(12 + rhsOffset)]
				+ ml[(6 + lhsOffset)] * mr[(13 + rhsOffset)] + ml[(10 + lhsOffset)] * mr[(14 + rhsOffset)]
				+ ml[(14 + lhsOffset)] * mr[(15 + rhsOffset)]));
		result[(15 + resultOffset)] = ((float) (ml[(3 + lhsOffset)] * mr[(12 + rhsOffset)]
				+ ml[(7 + lhsOffset)] * mr[(13 + rhsOffset)] + ml[(11 + lhsOffset)] * mr[(14 + rhsOffset)]
				+ ml[(15 + lhsOffset)] * mr[(15 + rhsOffset)]));
	}

	public static void multiplyMV(float[] resultVec, int resultVecOffset, float[] mlIn, int lhsMatOffset, float[] vrIn,
			int rhsVecOffset) {
		double[] ml = new double[16];
		double[] vr = new double[4];
		for (int i = 0; i < 16; i++) {
			ml[i] = mlIn[i];
		}
		vr[0] = vrIn[0];
		vr[1] = vrIn[1];
		vr[2] = vrIn[2];
		vr[3] = vrIn[3];

		resultVec[(0 + resultVecOffset)] = ((float) (ml[(0 + lhsMatOffset)] * vr[(0 + rhsVecOffset)]
				+ ml[(4 + lhsMatOffset)] * vr[(1 + rhsVecOffset)] + ml[(8 + lhsMatOffset)] * vr[(2 + rhsVecOffset)]
				+ ml[(12 + lhsMatOffset)] * vr[(3 + rhsVecOffset)]));
		resultVec[(1 + resultVecOffset)] = ((float) (ml[(1 + lhsMatOffset)] * vr[(0 + rhsVecOffset)]
				+ ml[(5 + lhsMatOffset)] * vr[(1 + rhsVecOffset)] + ml[(9 + lhsMatOffset)] * vr[(2 + rhsVecOffset)]
				+ ml[(13 + lhsMatOffset)] * vr[(3 + rhsVecOffset)]));
		resultVec[(2 + resultVecOffset)] = ((float) (ml[(2 + lhsMatOffset)] * vr[(0 + rhsVecOffset)]
				+ ml[(6 + lhsMatOffset)] * vr[(1 + rhsVecOffset)] + ml[(10 + lhsMatOffset)] * vr[(2 + rhsVecOffset)]
				+ ml[(14 + lhsMatOffset)] * vr[(3 + rhsVecOffset)]));
		resultVec[(3 + resultVecOffset)] = ((float) (ml[(3 + lhsMatOffset)] * vr[(0 + rhsVecOffset)]
				+ ml[(7 + lhsMatOffset)] * vr[(1 + rhsVecOffset)] + ml[(11 + lhsMatOffset)] * vr[(2 + rhsVecOffset)]
				+ ml[(15 + lhsMatOffset)] * vr[(3 + rhsVecOffset)]));
	}

	public static void setIdentityM(float[] sm, int smOffset) {
		for (int i = 0; i < 16; i++) {
			sm[i] = 0.0F;
		}

		sm[0] = 1.0F;
		sm[5] = 1.0F;
		sm[10] = 1.0F;
		sm[15] = 1.0F;
	}

	public static void translateM(float[] m, int mOffset, float x, float y, float z) {
		for (int i = 0; i < 4; i++) {
			int mi = mOffset + i;
			m[(12 + mi)] += m[mi] * x + m[(4 + mi)] * y + m[(8 + mi)] * z;
		}
	}

	public static float length(float x, float y, float z) {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public static void setRotateM(float[] rm, int rmOffset, float a, float x, float y, float z) {
		rm[(rmOffset + 3)] = 0.0F;
		rm[(rmOffset + 7)] = 0.0F;
		rm[(rmOffset + 11)] = 0.0F;
		rm[(rmOffset + 12)] = 0.0F;
		rm[(rmOffset + 13)] = 0.0F;
		rm[(rmOffset + 14)] = 0.0F;
		rm[(rmOffset + 15)] = 1.0F;
		a *= 0.017453292F;
		float s = (float) Math.sin(a);
		float c = (float) Math.cos(a);
		if ((1.0F == x) && (0.0F == y) && (0.0F == z)) {
			rm[(rmOffset + 5)] = c;
			rm[(rmOffset + 10)] = c;
			rm[(rmOffset + 6)] = s;
			rm[(rmOffset + 9)] = (-s);
			rm[(rmOffset + 1)] = 0.0F;
			rm[(rmOffset + 2)] = 0.0F;
			rm[(rmOffset + 4)] = 0.0F;
			rm[(rmOffset + 8)] = 0.0F;
			rm[(rmOffset + 0)] = 1.0F;
		} else if ((0.0F == x) && (1.0F == y) && (0.0F == z)) {
			rm[(rmOffset + 0)] = c;
			rm[(rmOffset + 10)] = c;
			rm[(rmOffset + 8)] = s;
			rm[(rmOffset + 2)] = (-s);
			rm[(rmOffset + 1)] = 0.0F;
			rm[(rmOffset + 4)] = 0.0F;
			rm[(rmOffset + 6)] = 0.0F;
			rm[(rmOffset + 9)] = 0.0F;
			rm[(rmOffset + 5)] = 1.0F;
		} else if ((0.0F == x) && (0.0F == y) && (1.0F == z)) {
			rm[(rmOffset + 0)] = c;
			rm[(rmOffset + 5)] = c;
			rm[(rmOffset + 1)] = s;
			rm[(rmOffset + 4)] = (-s);
			rm[(rmOffset + 2)] = 0.0F;
			rm[(rmOffset + 6)] = 0.0F;
			rm[(rmOffset + 8)] = 0.0F;
			rm[(rmOffset + 9)] = 0.0F;
			rm[(rmOffset + 10)] = 1.0F;
		} else {
			float len = length(x, y, z);
			if (1.0F != len) {
				float recipLen = 1.0F / len;
				x *= recipLen;
				y *= recipLen;
				z *= recipLen;
			}
			float nc = 1.0F - c;
			float xy = x * y;
			float yz = y * z;
			float zx = z * x;
			float xs = x * s;
			float ys = y * s;
			float zs = z * s;
			rm[(rmOffset + 0)] = (x * x * nc + c);
			rm[(rmOffset + 4)] = (xy * nc - zs);
			rm[(rmOffset + 8)] = (zx * nc + ys);
			rm[(rmOffset + 1)] = (xy * nc + zs);
			rm[(rmOffset + 5)] = (y * y * nc + c);
			rm[(rmOffset + 9)] = (yz * nc - xs);
			rm[(rmOffset + 2)] = (zx * nc - ys);
			rm[(rmOffset + 6)] = (yz * nc + xs);
			rm[(rmOffset + 10)] = (z * z * nc + c);
		}
	}

	public static void rotateM(float[] m, int mOffset, float a, float x, float y, float z) {
		float[] rm = new float[16];
		setRotateM(rm, 0, a, x, y, z);
		float[] rem = new float[16];
		multiplyMM(rem, 0, m, 0, rm, 0);
		for (int i = 0; i < 16; i++) {
			m[i] = rem[i];
		}
	}

	public static void frustumM(float[] m, int offset, float left, float right, float bottom, float top, float near,
			float far) {
		if (left == right) {
			throw new IllegalArgumentException("left == right");
		}
		if (top == bottom) {
			throw new IllegalArgumentException("top == bottom");
		}
		if (near == far) {
			throw new IllegalArgumentException("near == far");
		}
		if (near <= 0.0F) {
			throw new IllegalArgumentException("near <= 0.0f");
		}
		if (far <= 0.0F) {
			throw new IllegalArgumentException("far <= 0.0f");
		}
		float r_width = 1.0F / (right - left);
		float r_height = 1.0F / (top - bottom);
		float r_depth = 1.0F / (near - far);
		float x = 2.0F * (near * r_width);
		float y = 2.0F * (near * r_height);
		float A = 2.0F * ((right + left) * r_width);
		float B = (top + bottom) * r_height;
		float C = (far + near) * r_depth;
		float D = 2.0F * (far * near * r_depth);
		m[(offset + 0)] = x;
		m[(offset + 5)] = y;
		m[(offset + 8)] = A;
		m[(offset + 9)] = B;
		m[(offset + 10)] = C;
		m[(offset + 14)] = D;
		m[(offset + 11)] = -1.0F;
		m[(offset + 1)] = 0.0F;
		m[(offset + 2)] = 0.0F;
		m[(offset + 3)] = 0.0F;
		m[(offset + 4)] = 0.0F;
		m[(offset + 6)] = 0.0F;
		m[(offset + 7)] = 0.0F;
		m[(offset + 12)] = 0.0F;
		m[(offset + 13)] = 0.0F;
		m[(offset + 15)] = 0.0F;
	}

	public static void setLookAtM(float[] rm, int rmOffset, float eyeX, float eyeY, float eyeZ, float centerX,
			float centerY, float centerZ, float upX, float upY, float upZ) {
		float fx = centerX - eyeX;
		float fy = centerY - eyeY;
		float fz = centerZ - eyeZ;
		float rlf = 1.0F / length(fx, fy, fz);
		fx *= rlf;
		fy *= rlf;
		fz *= rlf;
		float sx = fy * upZ - fz * upY;
		float sy = fz * upX - fx * upZ;
		float sz = fx * upY - fy * upX;
		float rls = 1.0F / length(sx, sy, sz);
		sx *= rls;
		sy *= rls;
		sz *= rls;
		float ux = sy * fz - sz * fy;
		float uy = sz * fx - sx * fz;
		float uz = sx * fy - sy * fx;
		rm[(rmOffset + 0)] = sx;
		rm[(rmOffset + 1)] = ux;
		rm[(rmOffset + 2)] = (-fx);
		rm[(rmOffset + 3)] = 0.0F;
		rm[(rmOffset + 4)] = sy;
		rm[(rmOffset + 5)] = uy;
		rm[(rmOffset + 6)] = (-fy);
		rm[(rmOffset + 7)] = 0.0F;
		rm[(rmOffset + 8)] = sz;
		rm[(rmOffset + 9)] = uz;
		rm[(rmOffset + 10)] = (-fz);
		rm[(rmOffset + 11)] = 0.0F;
		rm[(rmOffset + 12)] = 0.0F;
		rm[(rmOffset + 13)] = 0.0F;
		rm[(rmOffset + 14)] = 0.0F;
		rm[(rmOffset + 15)] = 1.0F;
		translateM(rm, rmOffset, -eyeX, -eyeY, -eyeZ);
	}
}

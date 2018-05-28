package com.bn;

import java.util.ArrayList;
import java.util.Collections;

public class LiZiChenJiUtil {
	public static void genCJ(float[][] result, int cx, int cy, int count, int span, int gdyzIn, boolean ssfxjh,
			boolean sfSmms, int[][] smwz) {
		ArrayList[] knPosition = new ArrayList[span];
		for (int k = 1; k <= span; k++) {
			knPosition[(k - 1)] = new ArrayList();
			int powerSpan = k * k;
			for (int i = -k; i <= k; i++) {
				for (int j = -k; j <= k; j++) {
					if (((i != 0) || (j != 0)) && (i * i + j * j <= powerSpan)) {
						knPosition[(k - 1)].add(new int[] { i, j });
					}
				}
			}
		}

		int width = result.length;
		int height = result[0].length;

		for (int i = 0; i < count; i++) {

			int currX = cx;
			int currY = cy;

			if (sfSmms) {
				currX = smwz[(i % smwz.length)][0];
				currY = smwz[(i % smwz.length)][1];
			}

			float currHeight = result[currX][currY];

			int currSpan = (int) Math.ceil(span * Math.random());

			int gdyz = (int) Math.ceil(gdyzIn / 2.0D * Math.random() + gdyzIn / 2.0D);

			ArrayList<int[]> knwz = knPosition[(currSpan - 1)];

			if (ssfxjh) {
				Collections.shuffle(knwz);
			}

			for (int[] wz : knwz) {
				int j = wz[0];
				int k = wz[1];

				if ((currX + j >= 0) && (currX + j < width) && (currY + k >= 0) && (currY + k < height)) {

					float tempHeight = result[(currX + j)][(currY + k)];

					if (currHeight - tempHeight > gdyz) {

						currX += j;
						currY += k;

						break;
					}
				}
			}

			result[currX][currY] += 1.0F;
		}
	}
}

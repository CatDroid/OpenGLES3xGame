package com.bn;

import static com.bn.Constant.*;
import java.awt.image.*;

public class Noise3DUtil {

	public static int GLOBAL_SIZE = 64;

	// 求乘方运算的方法
	public static int pow(int a, int b) {
		int result = 1;
		for (int i = 0; i < b; i++) {
			result = result * a;
		}
		return result;
	}

	// 生成噪声控制点的方法
	public static double noise(int x, int y, int z, double amp) {
		int n = x + y * 57 + z * 3249; // 与一维的区别，先根据(x,y,z)生成一个数n
		n = (n << 13) ^ n;
		double result = ((1.0 - ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0) + 1) / 2.0;
		// result 返回的是0~1的浮点数 !!!!!!  论文没有+1  也没有/2  应该是在这种情况 ( 与一维的区别)
		if(result > 1.0f || result < 0.0f ) {
			System.out.println(result);
		}
		return result * amp;
	}

	// 生成某一级倍频的数据
	public static int[][][] calYJBP(int level) {
		// 求出此级倍频的实际大小
		int size = pow(2, level);
		// 此级倍频的像素颜色数据
		int[][][] result = new int[size][size][size];

		// 求出此级的振幅
		double amp = 255;

		// 循环生成每一个位置的像素值
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				for (int k = 0; k < size; k++) {
					double val = noise(++X_CURR, ++Y_CURR, ++Y_CURR, amp); // 返回0~1  0~255
					int gray = (int) val;
					result[i][j][k] = gray;
				}
			}
		}

		int[][][] resultHelp = new int[GLOBAL_SIZE][GLOBAL_SIZE][GLOBAL_SIZE];// 要把size*size*size放大到GLOBAL_SIZE*GLOBAL_SIZE*GLOBAL_SIZE
		// 求出缩放比  scale 就类似间隔 
		int scale = GLOBAL_SIZE / size; 
		
		/*
		 *   0 1 2 
		 *   3 4 5
		 *   6 7 8 
		 *   
		 *   如果是3x3放到成9x9    9/3 = 3 = scale = 步进/间隔
		 *   
		 *   0 0 0 1 1 1 2 2 2   
		 *   0 0 0 1 1 1 2 2 2
		 *   0 0 0 1 1 1 2 2 2
		 *   3 3 3 4 4 4 5 5 5
		 *   3 3 3 4 4 4 5 5 5 
		 *   3 3 3 4 4 4 5 5 5
		 *   6 6 6 7 7 7 8 8 8  
		 *   6 6 6 7 7 7 8 8 8  
		 *   6 6 6 7 7 7 8 8 8  
		 * */
		for (int i = 0; i < GLOBAL_SIZE; i++) {
			for (int j = 0; j < GLOBAL_SIZE; j++) {
				for (int k = 0; k < GLOBAL_SIZE; k++) {
					resultHelp[i][j][k] = result[i / scale][j / scale][k / scale];
				}
			}
		}

		// 相当于平滑插值
		int[][][] resultFinal = new int[GLOBAL_SIZE][GLOBAL_SIZE][GLOBAL_SIZE];
		for (int i = 0; i < GLOBAL_SIZE; i++) {
			for (int j = 0; j < GLOBAL_SIZE; j++) {
				for (int k = 0; k < GLOBAL_SIZE; k++) {// (i,j,k)每个像素
					float count = 0;
					int temp = 0;
					int half = scale / 2;
					for (int a = -half; a <= half; a++) {
						for (int b = -half; b <= half; b++) {
							for (int c = -half; c <= half; c++) {
								int indexX = i + a;
								int indexY = j + b;
								int indexZ = k + c; // (i,j,k) 周围的元素 立方体  half 到 -half  的总和 
								if (indexX >= 0 && indexX < GLOBAL_SIZE && indexY >= 0 && indexY < GLOBAL_SIZE
										&& indexZ >= 0 && indexZ < GLOBAL_SIZE) {
									temp = temp + resultHelp[indexX][indexY][indexZ];
									count++;
								}
							}
						}
					}

					resultFinal[i][j][k] = (int) (temp / count); // (i,j,k) 周围的元素 立方体  half 到 -half  的总和 的平均值 
					//System.out.println("" + resultFinal[i][j][k] );
				}
			}
		}

		return resultFinal;
	}

	// RGBA各自一个倍频 (R 是 倍频1(2*2->64*64)  G是倍频2(4*4->64*64)  B是倍频3(8*8->64*64)  A是倍频4((16*16->64*64)) )
	// 4个倍频 都是等幅度的  而且固定只会产生4个倍频 
	public static int[][][][] calSYBP_BPFL() {
		final int PLS4 = 4;

		int[][][][] resultFinalL = new int[GLOBAL_SIZE][GLOBAL_SIZE][GLOBAL_SIZE][PLS4];

		for (int l = 1; l <= PLS4; l++) {
			int[][][] resulttemp = calYJBP(l);
			for (int i = 0; i < GLOBAL_SIZE; i++) {
				for (int j = 0; j < GLOBAL_SIZE; j++) {
					for (int k = 0; k < GLOBAL_SIZE; k++) {
						resultFinalL[i][j][k][l - 1] = resulttemp[i][j][k]; // 最后一维放的是 当前像素的该倍频级别的值 
					}
				}
			}
		}

		// 创建512x512的结果图像  把最后叠加的 3D柏林  张开成一张张的2D图像
		D3Each = new BufferedImage[GLOBAL_SIZE];
		for (int d = 0; d < GLOBAL_SIZE; d++) {
			D3Each[d] = new BufferedImage(GLOBAL_SIZE, GLOBAL_SIZE, BufferedImage.TYPE_INT_ARGB);
			for (int i = 0; i < GLOBAL_SIZE; i++) {
				for (int j = 0; j < GLOBAL_SIZE; j++) {
					int red = resultFinalL[d][i][j][0];
					int green = resultFinalL[d][i][j][1];
					int blue = resultFinalL[d][i][j][2];
					int alpha = resultFinalL[d][i][j][3]; // 4个倍频级别 分别用R G B A 表示

					int cResult = 0x00000000;
					cResult += alpha << 24;
					cResult += red << 16;
					cResult += green << 8;
					cResult += blue;
					D3Each[d].setRGB(i, j, cResult);
				}
			}
		}

		return resultFinalL;
	}

	// 合成统一灰度
	public static int[][][] calSYBP() {
		int[][][] resultFinal = new int[GLOBAL_SIZE][GLOBAL_SIZE][GLOBAL_SIZE];

		// resultFinal=calYJBP(5);

		for (int l = 1; l <= PLS; l++) { // 生成PLS(不只是4个倍频)的噪声 
			int[][][] resulttemp = calYJBP(l);
			for (int i = 0; i < GLOBAL_SIZE; i++) {
				for (int j = 0; j < GLOBAL_SIZE; j++) {
					for (int k = 0; k < GLOBAL_SIZE; k++) {
						resultFinal[i][j][k] = resultFinal[i][j][k] * 2 + resulttemp[i][j][k];
					}// *2 之前的噪声  倍频1的噪声幅度就是2^(PLS-1)
				}
			}
		}

		int max = 0; // 最大的幅度
		for (int i = 0; i < GLOBAL_SIZE; i++) {
			for (int j = 0; j < GLOBAL_SIZE; j++) {
				for (int k = 0; k < GLOBAL_SIZE; k++) {
					if (max < resultFinal[i][j][k]) {
						max = resultFinal[i][j][k];
					}
				}
			}
		}

		for (int i = 0; i < GLOBAL_SIZE; i++) {
			for (int j = 0; j < GLOBAL_SIZE; j++) {
				for (int k = 0; k < GLOBAL_SIZE; k++) {
					resultFinal[i][j][k] = resultFinal[i][j][k] * 256 / max;
				} //  /max 归一化    *256 转成 0~255
			}
		}

		// 创建512x512的结果图像  跟 calSYBP_BPFL 转到一张张的2D图片
		D3Each = new BufferedImage[GLOBAL_SIZE];

		for (int d = 0; d < GLOBAL_SIZE; d++) {
			D3Each[d] = new BufferedImage(GLOBAL_SIZE, GLOBAL_SIZE, BufferedImage.TYPE_INT_ARGB);
			for (int i = 0; i < GLOBAL_SIZE; i++) {
				for (int j = 0; j < GLOBAL_SIZE; j++) {
					int gray = resultFinal[d][i][j];
					int cResult = 0x00000000;
					cResult += 255 << 24;
					cResult += gray << 16;
					cResult += gray << 8; // 3个通道都是一样的 
					cResult += gray;
					D3Each[d].setRGB(i, j, cResult);
				}
			}
		}
		return resultFinal;
	}
}

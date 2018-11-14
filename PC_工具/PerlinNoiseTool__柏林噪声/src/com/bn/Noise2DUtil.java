package com.bn;

import static com.bn.Constant.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;

public class Noise2DUtil {
	// 求乘方运算的方法
	public static int pow(int a, int b) {
		int result = 1;
		for (int i = 0; i < b; i++) {
			result = result * a;
		}
		return result;
	}

	// 生成噪声控制点的方法
	public static double noise(int x, int y, double amp) {
		int n = x + y * 57;  // 与一维的区别，先根据(x,y)生成一个数n
		n = (n << 13) ^ n;
		double result = ((1.0 - ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0) + 1) / 2.0;
		// result 返回的是0~1的浮点数 !!!!!!  论文没有+1 
		if(result > 1.0f || result < 0.0f ) {
			System.out.println(result);
		}
		return result * amp; // 幅度改成-amp~amp 
	}

	// 生成某一级倍频的图
	public static BufferedImage calYJBP(int level) {
		
		// 求出此级倍频的实际大小 总段数(宽和高) 每次倍频 生成 (16x16),(16x2^1,16x2^1),(16x2^2,16x2^2)..等分辨率 最后按双三次插值 得到 512x512的图 
		int size = COUNT * pow(2, level - 1);
		
		// 创建此级倍频的实际图像  java.awt.image.BufferedImage
		BufferedImage temp = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		
		
		// 求出此级的振幅
		double amp = 255.0 / pow(2, level);
		if (Constant.ZQDBD_FLAG) { // 设置这个标记 所有倍频都按照 255的幅度(用来显示)，所以在后面多个倍频相加的时候，就要做衰减
			amp = 255;
		}

		// 循环生成每一个像素值
		if(CONFIG_ROW_COLUMN) {
			for (int i = 0 ,controlX = X_CURR ; i < size; i++) {
				++controlX;
				for (int j = 0, controlY = Y_CURR ; j < size; j++) {
					double val = noise(controlX , ++controlY  , amp); // (X_CURR,Y_CURR) 起始值
					int gray = (int) val;
					int cResult = 0x00000000;
					cResult += 255 << 24;
					cResult += gray << 16;
					cResult += gray << 8;
					cResult += gray;     // R = G = B = gray 三个通道颜色都一样
					temp.setRGB(j, i, cResult);
				}
			}
		}else {
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					double val = noise(++X_CURR, ++Y_CURR, amp); // (X_CURR,Y_CURR) 起始值
					int gray = (int) val;
					int cResult = 0x00000000;
					cResult += 255 << 24;
					cResult += gray << 16;
					cResult += gray << 8;
					cResult += gray;     // R = G = B = gray 三个通道颜色都一样
					temp.setRGB(j, i, cResult);
				}
			}
		}
	

		
		// 创建512x512的结果图像
		BufferedImage result = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
		int scale = 512 / size;				// 求出缩放比
		Graphics g = result.getGraphics();	// 获取画笔
		Graphics2D g2d = (Graphics2D) g;
		// 设置插值  双三次插值(Bicubic interpolation) 比双线性插值更平滑的图像边缘
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		// 创建缩放对象  java.awt.image.AffineTransform 
		// 2D 仿射变换，它执行从 2D 坐标到其他 2D 坐标的线性映射，保留了线的"直线性"和"平行性"。
		// 可以使用一系列平移 (translation)、缩放 (scale)、翻转 (flip)、旋转 (rotation) 和错切 (shear) 来构造仿射变换
		AffineTransform at = new AffineTransform();
		at.scale(scale, scale);
		

		g2d.drawImage(temp, at, null); // 把 这个级别的分辨率(size*size) 经过插值，放到到512x512 

		return result; // 每次返回都是 由该级倍频(size*size)放到到512*512的图像
	}

	public static BufferedImage[] calSYBP() {
		BufferedImage[] result = new BufferedImage[PLS + 1]; // 最后一个是叠加的

		if(RESTART_FROM_BEGIN) { 
			for (int i = 1; i <= PLS; i++) {
				 X_CURR = 0;
				 Y_CURR = 0;
				result[i - 1] = calYJBP(i); // 0 ~ PLS-1 各个倍频 得到的512*512图像
			}
		}else {
			for (int i = 1; i <= PLS; i++) {
				result[i - 1] = calYJBP(i); // 0 ~ PLS-1 各个倍频 得到的512*512图像
			}	
		}
	

		// 创建512x512的结果图像  合成的 放到返回result的最后 
		result[PLS] = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);

		for (int i = 0; i < 512; i++) {
			for (int j = 0; j < 512; j++) {
				int gray = 0;
				for (int k = 1; k <= PLS; k++) { // 当前像素位置的 各个倍频 
					int color = result[k - 1].getRGB(i, j);
					// 拆分出RGB三个色彩通道的值
					int r = (color >> 16) & 0xff;
					int g = (color >> 8) & 0xff;
					int b = (color) & 0xff;
					int grayTemp = 0;
					if (Constant.ZQDBD_FLAG) { // 如果为True增加对比度 也就是每级倍频图幅度都是256 没有做衰减 所以这里就要处理衰减后再相加
						grayTemp = (int) ((r + g + b) / 3.0 / pow(2, k)); 
					} else {
						grayTemp = (r + g + b) / 3;
					}
					gray += grayTemp;
				}
				int cResult = 0x00000000;
				cResult += 255 << 24;
				cResult += gray << 16;
				cResult += gray << 8;
				cResult += gray;
				result[PLS].setRGB(i, j, cResult);
			}
		}

		return result;
	}
}

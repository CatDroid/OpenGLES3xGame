package com.bn;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class Constant {
	static float[][] heightOrign;
	static float[][] heightForDraw;
	static float MAX_HEIGHT = 20.0F;
	static float[] vertexPeer;
	static float GZ_SIZE = 2.0F;
	static int[][] smwz;

	static Image exportPic(ImageObserver observer) {
		int width = heightForDraw.length;
		int height = heightForDraw[0].length;
		BufferedImage sourceBuf = new BufferedImage(width, height, 2);
		float max = 0.0F;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (heightForDraw[i][j] > max) {
					max = heightForDraw[i][j];
				}
			}
		}
		if (max != 0.0F) {
			float ratio = 255.0F / max;
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					int gray = (int) (heightForDraw[i][j] * ratio);
					int cResult = 0;
					cResult += -16777216;
					cResult += (gray << 16);
					cResult += (gray << 8);
					cResult += gray;
					sourceBuf.setRGB(i, j, cResult);
				}
			}
		} else {
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					int cResult = 0;
					cResult += -16777216;
					sourceBuf.setRGB(i, j, cResult);
				}
			}
		}
		return sourceBuf;
	}

	static Image loadPic(String path, ImageObserver observer) {
		Image image = null;
		javax.swing.ImageIcon ii = new javax.swing.ImageIcon(path);
		image = ii.getImage();
		int width = image.getWidth(observer);
		int height = image.getHeight(observer);

		BufferedImage sourceBuf = new BufferedImage(width, height, 2);
		java.awt.Graphics graph = sourceBuf.getGraphics();
		graph.drawImage(ii.getImage(), 0, 0, java.awt.Color.white, null);

		heightOrign = new float[width][height];
		heightForDraw = new float[width][height];

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int color = sourceBuf.getRGB(i, j);

				int r = color >> 16 & 0xFF;
				int g = color >> 8 & 0xFF;
				int b = color & 0xFF;
				int gray = (r + g + b) / 3;
				heightOrign[i][j] = gray;
				heightForDraw[i][j] = (heightOrign[i][j] * MAX_HEIGHT / 255.0F);
			}
		}

		genVertexPeerForGray();

		return image;
	}

	static void genVertexPeerForGray() {
		int width = heightForDraw.length;
		int height = heightForDraw[0].length;
		float startX = -GZ_SIZE * width / 2.0F;
		float startZ = -GZ_SIZE * height / 2.0F;

		vertexPeer = new float[30 * (width - 1) * (height - 1)];
		int index = 0;
		for (int i = 0; i < width - 1; i++) {
			for (int j = 0; j < height - 1; j++) {

				float x0 = startX + i * GZ_SIZE;
				float y0 = heightForDraw[i][j];
				float z0 = startZ + j * GZ_SIZE;

				float x1 = x0;
				float y1 = heightForDraw[i][(j + 1)];
				float z1 = z0 + GZ_SIZE;

				float x2 = x0 + GZ_SIZE;
				float y2 = heightForDraw[(i + 1)][(j + 1)];
				float z2 = z0 + GZ_SIZE;

				float x3 = x0 + GZ_SIZE;
				float y3 = heightForDraw[(i + 1)][j];
				float z3 = z0;

				vertexPeer[(index++)] = x0;
				vertexPeer[(index++)] = y0;
				vertexPeer[(index++)] = z0;
				vertexPeer[(index++)] = x1;
				vertexPeer[(index++)] = y1;
				vertexPeer[(index++)] = z1;

				vertexPeer[(index++)] = x1;
				vertexPeer[(index++)] = y1;
				vertexPeer[(index++)] = z1;
				vertexPeer[(index++)] = x2;
				vertexPeer[(index++)] = y2;
				vertexPeer[(index++)] = z2;

				vertexPeer[(index++)] = x2;
				vertexPeer[(index++)] = y2;
				vertexPeer[(index++)] = z2;
				vertexPeer[(index++)] = x3;
				vertexPeer[(index++)] = y3;
				vertexPeer[(index++)] = z3;

				vertexPeer[(index++)] = x3;
				vertexPeer[(index++)] = y3;
				vertexPeer[(index++)] = z3;
				vertexPeer[(index++)] = x0;
				vertexPeer[(index++)] = y0;
				vertexPeer[(index++)] = z0;

				vertexPeer[(index++)] = x0;
				vertexPeer[(index++)] = y0;
				vertexPeer[(index++)] = z0;
				vertexPeer[(index++)] = x2;
				vertexPeer[(index++)] = y2;
				vertexPeer[(index++)] = z2;
			}
		}
	}

	static void genVertexPeerForLZCJ() {
		int width = heightForDraw.length;
		int height = heightForDraw[0].length;
		float startX = -GZ_SIZE * width / 2.0F;
		float startZ = -GZ_SIZE * height / 2.0F;

		float max = 0.0F;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (heightForDraw[i][j] > max) {
					max = heightForDraw[i][j];
				}
			}
		}
		float ratio = 1.0F;
		if (max != 0.0F) {
			ratio = MAX_HEIGHT / max;
		}

		vertexPeer = new float[30 * (width - 1) * (height - 1)];
		int index = 0;
		for (int i = 0; i < width - 1; i++) {
			for (int j = 0; j < height - 1; j++) {

				float x0 = startX + i * GZ_SIZE;
				float y0 = heightForDraw[i][j] * ratio;
				float z0 = startZ + j * GZ_SIZE;

				float x1 = x0;
				float y1 = heightForDraw[i][(j + 1)] * ratio;
				float z1 = z0 + GZ_SIZE;

				float x2 = x0 + GZ_SIZE;
				float y2 = heightForDraw[(i + 1)][(j + 1)] * ratio;
				float z2 = z0 + GZ_SIZE;

				float x3 = x0 + GZ_SIZE;
				float y3 = heightForDraw[(i + 1)][j] * ratio;
				float z3 = z0;

				vertexPeer[(index++)] = x0;
				vertexPeer[(index++)] = y0;
				vertexPeer[(index++)] = z0;
				vertexPeer[(index++)] = x1;
				vertexPeer[(index++)] = y1;
				vertexPeer[(index++)] = z1;

				vertexPeer[(index++)] = x1;
				vertexPeer[(index++)] = y1;
				vertexPeer[(index++)] = z1;
				vertexPeer[(index++)] = x2;
				vertexPeer[(index++)] = y2;
				vertexPeer[(index++)] = z2;

				vertexPeer[(index++)] = x2;
				vertexPeer[(index++)] = y2;
				vertexPeer[(index++)] = z2;
				vertexPeer[(index++)] = x3;
				vertexPeer[(index++)] = y3;
				vertexPeer[(index++)] = z3;

				vertexPeer[(index++)] = x3;
				vertexPeer[(index++)] = y3;
				vertexPeer[(index++)] = z3;
				vertexPeer[(index++)] = x0;
				vertexPeer[(index++)] = y0;
				vertexPeer[(index++)] = z0;

				vertexPeer[(index++)] = x0;
				vertexPeer[(index++)] = y0;
				vertexPeer[(index++)] = z0;
				vertexPeer[(index++)] = x2;
				vertexPeer[(index++)] = y2;
				vertexPeer[(index++)] = z2;
			}
		}
	}
}

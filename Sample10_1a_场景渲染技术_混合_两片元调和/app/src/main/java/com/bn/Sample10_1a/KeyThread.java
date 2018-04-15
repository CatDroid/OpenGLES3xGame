package com.bn.Sample10_1a;

public class KeyThread extends Thread {

	MySurfaceView mv;
	public static boolean flag = true;
	// 表示按钮状态的常量
	public static final int Stop = 0;
	public static final int up = 1;
	public static final int down = 2;
	public static final int left = 3;
	public static final int right = 4;

	public KeyThread(MySurfaceView mv) {
		this.mv = mv;
	}

	public void run() {
		while (flag) {
			if (MySurfaceView.rectState == up) {// 上
				MySurfaceView.rectY += MySurfaceView.moveSpan;
			}
			else if (MySurfaceView.rectState == down) {// 下
				MySurfaceView.rectY -= MySurfaceView.moveSpan;
			}
			else if (MySurfaceView.rectState == left) {// 左
				MySurfaceView.rectX -= MySurfaceView.moveSpan;
			}
			else if (MySurfaceView.rectState == right) {// 右
				MySurfaceView.rectX += MySurfaceView.moveSpan;
			}
			try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}

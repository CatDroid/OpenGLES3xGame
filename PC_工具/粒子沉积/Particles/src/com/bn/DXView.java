package com.bn;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;

public class DXView extends JPanel {
	final int width = 1000;
	final int height = 600;
	float distance = 170.0F;
	float direction = 0.0F;
	float angle = 30.0F;

	int preX;

	int preY;

	public void cameraConfig() {
		double angleHD = Math.toRadians(this.direction);
		float[] cup = { -(float) Math.sin(angleHD), 0.0F, -(float) Math.cos(angleHD), 1.0F };
		float[] cLocation = { 0.0F, this.distance, 0.0F, 1.0F };

		float[] zhou = { -cup[2], 0.0F, cup[0] };

		float[] helpM = new float[16];
		Matrix.setIdentityM(helpM, 0);
		Matrix.rotateM(helpM, 0, this.angle, zhou[0], zhou[1], zhou[2]);
		float[] cupr = new float[4];
		float[] cLocationr = new float[4];
		Matrix.multiplyMV(cupr, 0, helpM, 0, cup, 0);
		Matrix.multiplyMV(cLocationr, 0, helpM, 0, cLocation, 0);
		MatrixState.setCamera(

				cLocationr[0], cLocationr[1], cLocationr[2], 0.0F, 0.0F, 0.0F, cupr[0], cupr[1], cupr[2]);
	}

	public DXView() {
		setPreferredSize(new java.awt.Dimension(1000, 600));
		float ratio = 1.6666666F;

		MatrixState.setInitStack();
		MatrixState.setProjectFrustum(-ratio, ratio, -1.0F, 1.0F, 2.0F, 1000.0F);
		cameraConfig();

		addMouseMotionListener(

				new MouseMotionListener() {
					public void mouseMoved(MouseEvent e) {
					}

					public void mouseDragged(MouseEvent e) {
						int x = e.getX();
						int y = e.getY();
						int xc = x - DXView.this.preX;
						int yc = y - DXView.this.preY;

						DXView.this.direction += 1.0F * xc;
						float tempAngle = DXView.this.angle + 1.0F * yc;

						if ((tempAngle < 90.0F) && (tempAngle > 0.0F)) {
							DXView.this.angle = tempAngle;
						}
						DXView.this.cameraConfig();
						DXView.this.repaint();

						DXView.this.preX = x;
						DXView.this.preY = y;
					}

				});
		addMouseListener(

				new java.awt.event.MouseListener() {
					public void mouseClicked(MouseEvent e) {
					}

					public void mouseEntered(MouseEvent e) {
					}

					public void mouseExited(MouseEvent e) {
					}

					public void mousePressed(MouseEvent e) {
						DXView.this.preX = e.getX();
						DXView.this.preY = e.getY();
					}

					public void mouseReleased(MouseEvent e) {
					}
				});
	}

	public void paint(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, 2000, 1200);

		if (Constant.vertexPeer == null) {
			return;
		}
		g.setColor(Color.green);
		Graphics2D g2d = (Graphics2D) g;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		float[] finalMatrix = MatrixState.getFinalMatrix();
		for (int i = 0; i < Constant.vertexPeer.length / 6; i++) {
			int k = i * 6;
			float[] PA = { Constant.vertexPeer[k], Constant.vertexPeer[(k + 1)], Constant.vertexPeer[(k + 2)] };
			float[] PB = { Constant.vertexPeer[(k + 3)], Constant.vertexPeer[(k + 4)], Constant.vertexPeer[(k + 5)] };

			// TOM ADD ++
			if( Constant.vertexPeer[(k + 1)] <= 0.0f  && Constant.vertexPeer[(k + 4)] <= 0.0f ) {
				g.setColor(Color.green);
			}else {
				g.setColor(Color.red);
			}
			
			// TOM ADD -- 
			
			int[] fPA = MatrixState.finalPosition(PA, finalMatrix, 1000.0F, 600.0F);
			int[] fPB = MatrixState.finalPosition(PB, finalMatrix, 1000.0F, 600.0F);

			g.drawLine(fPA[0], fPA[1], fPB[0], fPB[1]);
		}
	}
}

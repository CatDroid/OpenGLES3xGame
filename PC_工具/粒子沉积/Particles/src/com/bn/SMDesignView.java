package com.bn;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;

public class SMDesignView extends JPanel {
	ShanMaiFrame father;
	int width;
	int height;
	final int span = 20;

	public SMDesignView(ShanMaiFrame father) {
		this.father = father;
		this.width = Constant.heightForDraw.length;
		this.height = Constant.heightForDraw[0].length;
		setPreferredSize(new Dimension(this.width * 21, this.height * 21));
		initMouseListener();
	}

	public void paint(Graphics g) {
		g.setColor(Color.green);
		g.fillRect(0, 0, this.width * 21, this.height * 21);
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				if (Constant.smwz[i][j] == 0) {
					g.setColor(Color.white);
				} else {
					g.setColor(Color.black);
				}
				g.fillRect(i * 21, j * 21, 20, 20);
			}
		}
	}

	public void initMouseListener() {
		addMouseMotionListener(

				new MouseMotionListener() {

					public void mouseDragged(MouseEvent e) {
						int col = e.getX() / 21;
						int row = e.getY() / 21;
						Constant.smwz[col][row] = (SMDesignView.this.father.jrbTC.isSelected() ? 1 : 0);
						SMDesignView.this.repaint();
					}

					public void mouseMoved(MouseEvent e) {
					}
				});
	}
}

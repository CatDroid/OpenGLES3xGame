package com.bn;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

public class HDTView extends JPanel {
	Image ii;

	public void setImage(Image ii) {
		this.ii = ii;
		setPreferredSize(new Dimension(ii.getHeight(this), ii.getWidth(this)));
		repaint();
	}

	public void paint(Graphics g) {
		g.clearRect(0, 0, 500, 500);

		if (this.ii != null) {
			g.drawImage(this.ii, 0, 0, this);
		}
	}
}

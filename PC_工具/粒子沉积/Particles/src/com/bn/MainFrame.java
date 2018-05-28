package com.bn;

import javax.swing.JSplitPane;

public class MainFrame extends javax.swing.JFrame {
	DXView dxv = new DXView();
	javax.swing.JScrollPane jspDxv = new javax.swing.JScrollPane(this.dxv);
	HDTView hdv = new HDTView();
	javax.swing.JScrollPane jspHdv = new javax.swing.JScrollPane(this.hdv);
	DesignView dv = new DesignView(this);

	JSplitPane jspy = new JSplitPane(0, this.jspDxv, this.jspHdv);
	JSplitPane jspz = new JSplitPane(1, this.dv, this.jspy);

	public MainFrame() {
		setTitle("粒子沉积地形灰度图生成工具");

		this.jspy.setDividerLocation(620);
		this.jspy.setDividerSize(4);

		this.jspz.setDividerLocation(280);
		this.jspz.setDividerSize(4);

		add(this.jspz);

		setBounds(10, 10, 1300, 750);
		setVisible(true);
		setDefaultCloseOperation(3);
	}

	public static void main(String[] args) {
		new MainFrame();
	}
}

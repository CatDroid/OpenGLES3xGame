package com.bn;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

public class ShanMaiFrame extends JFrame {
	SMDesignView smdv = new SMDesignView(this);
	JScrollPane jsp = new JScrollPane(this.smdv);
	JPanel jpMode = new JPanel();
	JRadioButton jrbTC = new JRadioButton("填充");
	JRadioButton jrbCC = new JRadioButton("擦除");

	public ShanMaiFrame() {
		setTitle("山脉设计");
		this.jpMode.add(this.jrbTC);
		this.jpMode.add(this.jrbCC);
		this.jrbTC.setSelected(true);
		ButtonGroup bg = new ButtonGroup();
		bg.add(this.jrbTC);
		bg.add(this.jrbCC);
		add(this.jpMode, "North");

		add(this.jsp, "Center");

		setBounds(10, 10, 500, 500);
		setVisible(true);
		setDefaultCloseOperation(2);

		this.smdv.requestFocus();
	}
}

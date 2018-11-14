package com.bn;
import java.awt.*;
import javax.swing.*;

public class DisplayPanel extends JPanel
{
	private static final long serialVersionUID = 1573489782439613476L;
	MainFrame father;
	JTabbedPane jtb=new JTabbedPane();
	OneDDisplay xd=new OneDDisplay();
	JScrollPane jsp1D=new JScrollPane(xd);
	TwoDDisplay td=new TwoDDisplay();
	JScrollPane jsp2D=new JScrollPane(td);
	ThreeDDisplay td3=new ThreeDDisplay();
	JScrollPane jsp3D=new JScrollPane(td3);
	public DisplayPanel(MainFrame father)
	{
		this.father=father;
		this.setLayout(new BorderLayout());		
		this.add(jtb);
		
		jtb.add("1D噪声", jsp1D);
		jtb.add("2D噪声", jsp2D);
		jtb.add("3D噪声", jsp3D);
	}
}

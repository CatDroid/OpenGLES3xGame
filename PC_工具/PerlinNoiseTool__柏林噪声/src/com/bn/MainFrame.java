package com.bn;

import javax.swing.*;


public class MainFrame extends JFrame
{
	private static final long serialVersionUID = 5859961213603642666L;
	
	ControlPanel cp=new ControlPanel(this);
	DisplayPanel dp=new DisplayPanel(this);
	
	
	// 主面板 
	JSplitPane jsp=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,cp,dp);
	
	public MainFrame()
	{
		this.setTitle("Perlin噪声生成工具");
		
		this.add(jsp);
		jsp.setDividerLocation(250);
		jsp.setDividerSize(4);
		
		this.setBounds(10,100,1100,800);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	// 程序入口函数 
	public static void main(String args[])
	{
		new MainFrame();
	}
}

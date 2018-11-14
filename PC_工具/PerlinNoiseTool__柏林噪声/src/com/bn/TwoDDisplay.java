package com.bn;
import java.awt.*;
import javax.swing.*;

public class TwoDDisplay extends JPanel
{
	private static final long serialVersionUID = -2109681415253211585L;

	public Image[] data;
	
	
	public TwoDDisplay()
	{
		this.setPreferredSize(new Dimension(550,8000));
	}
	
	public void paint(Graphics g)
	{
		//空白大小
		final int span=5;
		//清屏
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 1000,8000);
		
		
		if(data==null) return;
		
		//获取子图的数量
		int count=data.length;
		
		//循环绘制每一级图像
		for(int i=0;i<count;i++)
		{
			g.drawImage(data[i],span,512*i+span*(i+1),this);
		}
	}
	
	public void refresh(Image[] data)
	{
		this.data=data;
		this.repaint();
	}
}

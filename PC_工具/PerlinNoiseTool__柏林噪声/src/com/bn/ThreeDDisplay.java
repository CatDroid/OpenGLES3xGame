package com.bn;
import java.awt.*;
import javax.swing.*;

public class ThreeDDisplay extends JPanel
{
	private static final long serialVersionUID = -2109681415253211585L;

	public Image[] data;
	
	
	public ThreeDDisplay()
	{
		this.setPreferredSize(new Dimension(560,5000));
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
		int cols=560/(Noise3DUtil.GLOBAL_SIZE+span);
		int k=0;
		//循环绘制每一级图像
		outer:for(int i=0;i<count;i++)
		{
			for(int j=0;j<cols;j++)
			{
				g.drawImage(data[k],j*(Noise3DUtil.GLOBAL_SIZE+span)+span,Noise3DUtil.GLOBAL_SIZE*i+span*(i+1),this);
				k++;
				if(k==data.length)
				{
					break outer;
				}
			}
		}
	}
	
	public void refresh(Image[] data)
	{
		this.data=data;
		this.repaint();
	}
}

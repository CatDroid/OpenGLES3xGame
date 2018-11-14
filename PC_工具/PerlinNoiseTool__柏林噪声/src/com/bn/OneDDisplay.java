package com.bn;
import java.awt.*;
import javax.swing.*;
import static com.bn.Constant.*;

public class OneDDisplay extends JPanel
{
	private static final long serialVersionUID = -2109681415253211585L;

	public double[][][] lineData;
	
	
	public OneDDisplay()
	{
		this.setPreferredSize(new Dimension(550,900));
	}
	
	@Override 
	public void paint(Graphics g)
	{
		//空白大小
		final int span=5;
		//清屏
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 1000,1000);
		
		
		if(lineData==null) return;
		
		//获取子图的数量
		int count=lineData.length;
		//计算绘制用X间距
		double xSpan=X_ARRANGE/lineData[0].length;
		
		//循环绘制每一级图像
		for(int i=0;i<count;i++)
		{
			//清除这一级的图像
			g.setColor(Color.WHITE);
			if(i==count-1)
			{				
				g.fillRect(span, span*(i+1)+(int)BASE_AMPLITUDE*i, (int)X_ARRANGE, (int)BASE_AMPLITUDE+100);
			}
			else
			{
				g.fillRect(span, span*(i+1)+(int)BASE_AMPLITUDE*i, (int)X_ARRANGE, (int)BASE_AMPLITUDE);
			}
			
			
			//取出一个子图的点
			double[][] points=lineData[i];
			//计算此子图的Y起始值
			int yStart=span*(i+1)+(int)BASE_AMPLITUDE*i;
			
			for(int j=0;j<points.length-1;j++)
			{				
				int x1=(int)(xSpan*j)+span;
				int y1=(int)(points[j][1]*BASE_AMPLITUDE*(i!=(count-1)?i+1:1))+yStart;
				int x2=(int)(xSpan*j+xSpan)+span;
				int y2=(int)(points[j+1][1]*BASE_AMPLITUDE*(i!=(count-1)?i+1:1))+yStart;
				
				g.setColor(Color.BLACK); 
				g.drawLine(x1,y1, x2, y2);
			}
		}
	}
	
	
	// 更新数据 用于刷新界面
	public void refresh(double[][][] lineData)
	{
		this.lineData=lineData;
		this.repaint(); // 导致JPanel回到paint方法 渲染 
	}
}

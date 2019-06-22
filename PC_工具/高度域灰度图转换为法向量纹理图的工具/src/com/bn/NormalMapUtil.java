package com.bn;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.color.*;
import java.io.*;
import javax.imageio.ImageIO;

public class NormalMapUtil extends JFrame {
	// 创建显示源图像的标签，并将其放置到滚动窗格中
	JLabel jls = new JLabel();
	JScrollPane jspz = new JScrollPane(jls);
	// 创建显示目标图像的标签，并将其放置到滚动窗格中
	JLabel jlt = new JLabel();
	JScrollPane jspy = new JScrollPane(jlt);
	// 创建分割窗格，并设置各子窗格中显示的控件
	JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jspz, jspy);
	// 创建文件选择器
	JFileChooser jfc;

	// 定义一个图标引用
	ImageIcon ii;

	public NormalMapUtil() {
		String path = new File("a").getAbsolutePath();
		path = path.substring(0, path.length() - 2);
		jfc = new JFileChooser(path);

		// 加载选择的图片到图标对象中
		ii = this.chooserFile();
		// 将图片设置到源标签中
		jls.setIcon(ii);
		// 设置两个标签的水平、垂直对齐方式
		jls.setVerticalAlignment(JLabel.CENTER);
		jls.setHorizontalAlignment(JLabel.CENTER);
		jlt.setVerticalAlignment(JLabel.CENTER);
		jlt.setHorizontalAlignment(JLabel.CENTER);

		// 设置分隔条的宽度以及初始位置
		jsp.setDividerLocation(500);
		jsp.setDividerSize(4);
		// 将分割窗格添加到窗体中
		this.add(jsp);

		// 设置窗体的标题、大小位置以及可见性
		this.setTitle("高度域灰度图转换成法向量纹理图工具");
		this.setBounds(0, 0, 1000, 500);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Image iTemp = process();
		// 将处理后的图片设置到目标标签中
		jlt.setIcon(new ImageIcon(iTemp));

		try {
			FileOutputStream os = new FileOutputStream("resultnt.jpg");
			System.out.println(((RenderedImage) iTemp).getColorModel());
			ImageIO.write((RenderedImage) iTemp, "JPEG", os);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 加载选中图片的方法
	public ImageIcon chooserFile() {
		// 弹出文件选择器
		int i = jfc.showOpenDialog(this);
		// 获取选择文件的路径
		String dir = (jfc.getSelectedFile() != null) ? (jfc.getSelectedFile().getPath()) : null;
		if (dir != null && !dir.equals("")) {
			// 按指定的路径加载图片到图标对象中并返回
			return new ImageIcon(dir);
		}
		return null;
	}

	// 从源高度图生成凹凸贴图的方法
	public Image process() {
		
		// 获取待处理图像的宽度与高度
		int width = ii.getImage().getWidth(null);
		int height = ii.getImage().getHeight(null);
		
		// 创建两个BufferedImage对象分别用来放置待处理图像与处理后的图像
		BufferedImage sourceBuf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		BufferedImage targetBuf = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		
		// 将待处理图像绘制加载到源BufferedImage对像中
		Graphics graph = sourceBuf.getGraphics();
		graph.drawImage(ii.getImage(), 0, 0, Color.white, null);
		
		// 对待处理图中像素的行循环
		for (int i = 0; i < height; i++) {
			
			// 对待处理图中像素的列循环
			for (int j = 0; j < width; j++) {
				
				// 获取指定位置处的像素
				int color = sourceBuf.getRGB(j, i);
				
				// 拆分出RGB三个色彩通道的值
				int r = (color >> 16) & 0xff;
				int g = (color >> 8) & 0xff;
				int b = (color) & 0xff;
				float c = (r + g + b) / 3.0f / 255.0f;// 求出折算后此像素的高度
				if (i == 0 || j == width - 1) {// 若为最左侧一列或最上面一行的像素不用计算
					targetBuf.setRGB(j, i, 0xFF8080FF);
					continue;
				}
				
				// 取出正上方像素的值并折算成高度
				int colorUp = sourceBuf.getRGB(j, i - 1);
				int rUp = (colorUp >> 16) & 0xff;
				int gUp = (colorUp >> 8) & 0xff;
				int bUp = (colorUp) & 0xff;
				float cUp = (rUp + gUp + bUp) / 3.0f / 255.0f;
				
				// 取出正右侧像素的值并折算成高度
				int colorRight = sourceBuf.getRGB(j + 1, i);
				int rRight = (colorRight >> 16) & 0xff;
				int gRight = (colorRight >> 8) & 0xff;
				int bRight = (colorRight) & 0xff;
				float cRight = (rRight + gRight + bRight) / 3.0f / 255.0f;
				
				// 计算出两个差分向量
//				float[] vec1 = { 1, 0, cUp - c };
//				float[] vec2 = { 0, 1, cRight - c }; 	// 凸的效果 
				
				float[] vec1 = { 1, 0, c - cUp }; 		// 凹的效果 
				float[] vec2 = { 0, 1, c - cRight  };	
				
				// ??? *4是把凹凸感更加明显??
				float ratio = 4 ; 
				
				// 将差分向量叉积得到结果向量  HHL 因为cUP-c cRight-c可能比较小 导致叉积的结果接近0,0,1 
				float[] vResult = VectorUtil.getCrossProduct(
						vec1[0], vec1[1], vec1[2] * ratio,  
						vec2[0], vec2[1], vec2[2] * ratio);
				vResult = VectorUtil.vectorNormal(vResult);
				
				// 将结果向量各分量值折算到0-255的范围内
				int cResultRed = (int) (vResult[0] * 128) + 128;
				int cResultGreen = (int) (vResult[1] * 128) + 128;
				int cResultBlue = (int) (vResult[2] * 128) + 128;
				cResultRed = (cResultRed > 255) ? 255 : cResultRed;
				cResultGreen = (cResultGreen > 255) ? 255 : cResultGreen;
				cResultBlue = (cResultBlue > 255) ? 255 : cResultBlue;
				
				// 将结果向量送入像素
				int cResult = 0xFF000000;
				cResult += cResultRed << 16;
				cResult += cResultGreen << 8;
				cResult += cResultBlue;
				targetBuf.setRGB(j, i, cResult);
			}
		}
		
		// 返回结果
		return targetBuf;
	}

	public static void main(String[] args) {
		// 创建Sample29_8窗体对象
		new NormalMapUtil();
	}
}

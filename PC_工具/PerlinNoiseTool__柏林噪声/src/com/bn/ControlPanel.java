package com.bn;
import static com.bn.Constant.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.RenderedImage;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ControlPanel extends JPanel
{
	private static final long serialVersionUID = 5989765494914231933L;
	MainFrame father;
	
	JCheckBox jcb=new JCheckBox("2D噪声各分频是否增强对比度");
	JCheckBox jcbHC=new JCheckBox("3D噪声是否RGBA各一个倍频");
	
	JLabel jlwd=new JLabel("噪声维度");
	JLabel jlPLS=new JLabel("噪声频率数");
	JLabel jl3DSize=new JLabel("3D噪声尺寸");
	JComboBox jcb1d2d3d=new JComboBox(new String[]{"1D柏林噪声","2D柏林噪声","3D柏林噪声"});
	JComboBox jcb3DSize=new JComboBox(new String[]{"64X64X64","32X32X32"});
	Integer value = new Integer(5); 
	Integer min = new Integer(1);
	Integer max = new Integer(6); 
	Integer step = new Integer(1); 
	SpinnerNumberModel jsPLSmodel = new SpinnerNumberModel(value, min, max, step); 
	JSpinner jsPLS=new JSpinner(jsPLSmodel);
	
	JButton jbGen=new JButton("生成噪声图像及数据");
	
	public ControlPanel(MainFrame fatherIn)
	{		
		this.father=fatherIn;
		this.setLayout(null);
		
		jlwd.setBounds(10,10,60,20);
		this.add(jlwd);
		
		jcb1d2d3d.setBounds(70,10,130,20);
		this.add(jcb1d2d3d);
		jcb1d2d3d.addItemListener
		(
			new ItemListener()
			{
				@Override
				public void itemStateChanged(ItemEvent e) 
				{
					switch(jcb1d2d3d.getSelectedIndex())
					{
						case 0:
							jcb.setEnabled(false);
							jcbHC.setEnabled(false);
							jcb3DSize.setEnabled(false);
						break;
						case 1:
							jcb.setEnabled(true);
							jcbHC.setEnabled(false);
							jcb3DSize.setEnabled(false);
						break;	
						case 2:
							jcb.setEnabled(false);
							jcbHC.setEnabled(true);
							jcb3DSize.setEnabled(true);
						break;							
					}
				}				
			}
		);

		
		
		jlPLS.setBounds(10,40,70,20);
		this.add(jlPLS);
		jsPLS.setBounds(80,40,120,20);
		this.add(jsPLS);
		jsPLS.addChangeListener
		(
			new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e) 
				{
					Constant.PLS=(Integer) jsPLS.getValue();
				}				
			}
		);
		
		jcb.setSelected(true);
		jcb.setBounds(5,70,200,20);
		this.add(jcb);
		jcb.addChangeListener
		(
			new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent arg0) 
				{
					Constant.ZQDBD_FLAG=jcb.isSelected();
				}				
			}
		);
		
		jcbHC.setBounds(5,100,200,20);
		this.add(jcbHC);
		
		jcb.setEnabled(false);
		jcbHC.setEnabled(false);
		jcb3DSize.setEnabled(false);
		
		jl3DSize.setBounds(10,130,70,20);
		this.add(jl3DSize);
		jcb3DSize.setBounds(80,130,120,20);
		this.add(jcb3DSize);
		jcb3DSize.addItemListener
		(
			new ItemListener()
			{
				@Override
				public void itemStateChanged(ItemEvent e) 
				{
					switch(jcb3DSize.getSelectedIndex())
					{
						case 0:
							Noise3DUtil.GLOBAL_SIZE=64;
						break;
						case 1:
							Noise3DUtil.GLOBAL_SIZE=32;
						break;
					}
				}				
			}
		);
		
		jbGen.setBounds(10,160,160,20);
		this.add(jbGen);
		
		jbGen.addActionListener
		(
			new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					setControlDisabled();					
					switch(jcb1d2d3d.getSelectedIndex())
					{
						case 0:
							do1DStuff();
						break;
						case 1:
							do2DStuff();
						break;	
						case 2:
							do3DStuff();
						break;							
					}			
				}				
			}
		);
	}
	
	public void setControlDisabled()
	{
		jcb.setEnabled(false);
		jcbHC.setEnabled(false);
		jcb1d2d3d.setEnabled(false);
		jsPLS.setEnabled(false);
		jbGen.setEnabled(false);
		jcb3DSize.setEnabled(false);
	}
	
	public void setControlEnabled()
	{
		jcb1d2d3d.setEnabled(true);
		jsPLS.setEnabled(true);
		jbGen.setEnabled(true);
		switch(jcb1d2d3d.getSelectedIndex())
		{
			case 0:
				jcb.setEnabled(false);
				jcbHC.setEnabled(false);
				jcb3DSize.setEnabled(false);
			break;
			case 1:
				jcb.setEnabled(true);
				jcbHC.setEnabled(false);
				jcb3DSize.setEnabled(false);
			break;	
			case 2:
				jcb.setEnabled(false);
				jcbHC.setEnabled(true);
				jcb3DSize.setEnabled(true);
			break;							
		}		
	}

	public void do1DStuff()
	{
		new Thread()
		{
			public void run()
			{
				X_CURR= (int) (100000*Math.random());

				father.dp.jtb.setSelectedIndex(0);
				father.dp.xd.refresh(Noise1DUtil.calSYBP());				
				setControlEnabled();
			}
		}.start();		
	}
	
	public void do2DStuff()
	{
		new Thread()
		{
			public void run()
			{
				X_CURR=(int) (100000*Math.random());
				Y_CURR=(int) (100000*Math.random());	
			
				father.dp.jtb.setSelectedIndex(1);
				Image[] data=Noise2DUtil.calSYBP();
				father.dp.td.refresh(data);
				try
				{
					int count=0;
					File file=new File("pic/noise"+count+".png");
					while(file.exists())
					{
						count++;
						file=new File("pic/noise"+count+".png");
					}
					
					FileOutputStream os = new FileOutputStream(file);
		      		ImageIO.write((RenderedImage)data[PLS], "PNG", os);
		      		os.flush();
		      		os.close();
				}
				catch(Exception ea)
				{
					ea.printStackTrace();
				}
				setControlEnabled();
			}
		}.start();	
	}

	public void do3DStuff()
	{
		new Thread()
		{
			public void run()
			{
				X_CURR=(int) (100000*Math.random());
				Y_CURR=(int) (100000*Math.random());
				Z_CURR=(int) (100000*Math.random());
				father.dp.jtb.setSelectedIndex(2);
				
				if(jcbHC.isSelected())
				{
					int[][][][] data=Noise3DUtil.calSYBP_BPFL();
					father.dp.td3.refresh(Constant.D3Each);
					try 
					{
						ExportUtil3DTexture.Export_BPFL(data);
					} 
					catch (IOException e1) 
					{
						e1.printStackTrace();
					}
				}
				else
				{
					int[][][] data=Noise3DUtil.calSYBP();
					father.dp.td3.refresh(Constant.D3Each);
					try 
					{
						ExportUtil3DTexture.Export(data);
					} 
					catch (IOException e1) 
					{
						e1.printStackTrace();
					}
				}
				setControlEnabled();
			}
		}.start();	
	}
}

package com.bn.bezier;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class BezierExMain extends JFrame
{
   public static void main(String args[])
   {
	   new BezierExMain();
   }
   
   int lineWidth=1;//绘制线宽
   
   MyPanel mp;
   JLabel jlfd=new JLabel("分段");   
   JSpinner jspfd;
   JLabel jlbj=new JLabel("步进");   
   JSpinner jspbj;
   JButton jbClear=new JButton("清空");
   JButton jbFlush=new JButton("删除尾");
   JButton jbLeft=new JButton("<-左移");
   JButton jbRight=new JButton("右移->");
   JButton jbUp=new JButton("上移");
   JButton jbDown=new JButton("下移");
   JTextArea jta=new  JTextArea();
   JScrollPane jsp=new JScrollPane(jta);
   JCheckBox jcb =new JCheckBox("显示控制点");
   JLabel jlLineWidth=new JLabel("    绘制线宽");
   JSlider jsLineWidth=new JSlider(1,3);
   public BezierExMain()
   {
	   this.setTitle("Bezier曲线工具");
	   this.setLayout(null);
	   
	   mp=new MyPanel(this);
	   mp.setBounds(10,10,400,Constant.HEIGHT);
	   this.add(mp);
	   
	   jcb.setBounds(415,10,90,20);
	   this.add(jcb);
	   jcb.setSelected(true);
	   jcb.addChangeListener
	   (
		   new ChangeListener()
		   {
			@Override
			public void stateChanged(ChangeEvent arg0) 
			{
				Constant.XSKZD=jcb.isSelected();
				mp.repaint();
			}			   
		   }
	   );
	   
	   jlfd.setBounds(420,40,50,20);
	   this.add(jlfd);	  	   
	   
	   Integer value = new Integer(20); 
	   Integer min = new Integer(0);
	   Integer max = new Integer(100); 
	   Integer step = new Integer(1); 
	   SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step); 
	   jspfd=new JSpinner(model);
	   jspfd.setBounds(450,40,50,20);
	   this.add(jspfd);
	   jspfd.addChangeListener
	   (
         new ChangeListener()
         {
			@Override
			public void stateChanged(ChangeEvent arg0) 
			{
				Constant.FD=(Integer)jspfd.getValue();
				mp.repaint();
			}        	 
         }
	   );
	   
	   jbClear.setBounds(420,70,80,20);
	   this.add(jbClear);
	   jbClear.addActionListener
	   (
		    new ActionListener()
		    {
				@Override
				public void actionPerformed(ActionEvent arg0) 
				{
					BezierUtil.al.clear();
					mp.repaint();
				}		    	
		    }
	   );
	   
	   jbFlush.setBounds(420,100,80,20);
	   this.add(jbFlush);
	   jbFlush.addActionListener
	   (
		    new ActionListener()
		    {
				@Override
				public void actionPerformed(ActionEvent arg0) 
				{
					BezierUtil.al.remove(BezierUtil.al.size()-1);
					mp.repaint();
				}		    	
		    }
	   );
	   
	   jlbj.setBounds(420,130,50,20);
	   this.add(jlbj);
	   value = new Integer(1); 
	   min = new Integer(1);
	   max = new Integer(20); 
	   step = new Integer(1);
	   model = new SpinnerNumberModel(value, min, max, step); 
	   jspbj=new JSpinner(model);
	   jspbj.setBounds(450,130,50,20);
	   this.add(jspbj);
	   jspbj.addChangeListener
	   (
         new ChangeListener()
         {
			@Override
			public void stateChanged(ChangeEvent arg0) 
			{
				Constant.ZYBJ=(Integer)jspbj.getValue();
				mp.repaint();
			}        	 
         }
	   );
	   
	   jbLeft.setBounds(420,160,80,20);
	   this.add(jbLeft);
	   jbLeft.addActionListener
	   (
		    new ActionListener()
		    {
				@Override
				public void actionPerformed(ActionEvent arg0) 
				{
					for(BNPosition bnp:BezierUtil.al)
					{
						bnp.x-=Constant.ZYBJ;
					}
					mp.repaint();
				}		    	
		    }
	   );
	   
	   jbRight.setBounds(420,190,80,20);
	   this.add(jbRight);
	   jbRight.addActionListener
	   (
		    new ActionListener()
		    {
				@Override
				public void actionPerformed(ActionEvent arg0) 
				{
					for(BNPosition bnp:BezierUtil.al)
					{
						bnp.x+=Constant.ZYBJ;
					}
					mp.repaint();
				}		    	
		    }
	   );
	   
	   jbUp.setBounds(420,220,80,20);
	   this.add(jbUp);
	   jbUp.addActionListener
	   (
		    new ActionListener()
		    {
				@Override
				public void actionPerformed(ActionEvent arg0) 
				{
					for(BNPosition bnp:BezierUtil.al)
					{
						bnp.y+=Constant.ZYBJ;
					}
					mp.repaint();
				}		    	
		    }
	   );
	   
	   jbDown.setBounds(420,250,80,20);
	   this.add(jbDown);
	   jbDown.addActionListener
	   (
		    new ActionListener()
		    {
				@Override
				public void actionPerformed(ActionEvent arg0) 
				{
					for(BNPosition bnp:BezierUtil.al)
					{
						bnp.y-=Constant.ZYBJ;
					}
					mp.repaint();
				}		    	
		    }
	   );
	   
	   jlLineWidth.setBounds(420,280,80,20);
	   this.add(jlLineWidth);
	   
	   jsLineWidth.setBounds(420,300,80,20);
	   this.add(jsLineWidth);
	   jsLineWidth.setValue(1);
	   jsLineWidth.addChangeListener
	   (
			 new ChangeListener()
			 {
				@Override
				public void stateChanged(ChangeEvent e) 
				{
					lineWidth=jsLineWidth.getValue();
					mp.repaint();
				}				 
			 }
	   ); 
	   
	   jsp.setBounds(510,10,210,360);
	   this.add(jsp);
	   
	   this.setBounds(10,10,730,400);
	   this.setVisible(true);
	   this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  
	   mp.requestFocus();
   }
}

package com.bn;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;

public class DesignView extends javax.swing.JPanel {
	MainFrame father;

	public DesignView(MainFrame father) {
		this.father = father;
		initComponent();
	}

	JButton jbLoadPic = new JButton("加载灰度图");
	JLabel jlDistance = new JLabel("观察距离");
	javax.swing.JSlider jsDistance = new javax.swing.JSlider(60, 300);
	JLabel jlWidth = new JLabel("宽度");
	JLabel jlHeight = new JLabel("高度");
	JTextField jtfWidth = new JTextField("32");
	JTextField jtfHeight = new JTextField("32");
	JButton jbNew = new JButton("新建");
	JLabel jlLZS = new JLabel("粒子数量");
	JSpinner jspLZS = new JSpinner(new javax.swing.SpinnerNumberModel(800, 0, 1000, 5));
	JLabel jlGDYZ = new JLabel("高度阈值");
	JSpinner jspGDYZ = new JSpinner(new javax.swing.SpinnerNumberModel(2, 1, 12, 1));
	JLabel jlCenter = new JLabel("沉积中心点XY");
	JTextField jtfCenterX = new JTextField("16");
	JTextField jtfCenterY = new JTextField("16");
	JCheckBox jcbFXJH = new JCheckBox("搜索方向是否均衡");
	JLabel jlSpan = new JLabel("搜索步进范围");
	JSpinner jspSpan = new JSpinner(new javax.swing.SpinnerNumberModel(1, 1, 8, 1));
	JCheckBox jcbSJSM = new JCheckBox("是否使用山脉模式");
	JButton jbSMWZ = new JButton("设计山脉");
	JButton jbZXCJ = new JButton("执行沉积");
	JButton jbDCHDT = new JButton("导出灰度图");
	JButton jbDCCCode = new JButton("导出C代码");

	public void initComponent() {
		setLayout(null);
		this.jbLoadPic.setBounds(10, 10, 100, 20);
		add(this.jbLoadPic);
		this.jbLoadPic.addActionListener(

				new java.awt.event.ActionListener() {

					public void actionPerformed(ActionEvent arg0) {
						javax.swing.JFileChooser jfc = new javax.swing.JFileChooser("pic");
						jfc.showOpenDialog(DesignView.this);
						String path = jfc.getSelectedFile().getAbsolutePath();
						Image image = Constant.loadPic(path, DesignView.this);
						DesignView.this.jtfWidth.setText(""+image.getWidth(null));
						DesignView.this.jtfHeight.setText(""+image.getHeight(null));
						DesignView.this.jtfCenterX.setText(""+image.getWidth(null) / 2);
						DesignView.this.jtfCenterY.setText(""+image.getHeight(null) / 2);

						int[][] smwzTemp = Constant.smwz;
						Constant.smwz = new int[image.getWidth(null)][image.getHeight(null)];
						if (smwzTemp != null) {
							for (int i = 0; i < smwzTemp.length; i++) {
								for (int j = 0; j < smwzTemp[0].length; j++) {
									if ((i < Constant.smwz.length) && (j < Constant.smwz[0].length)) {

										Constant.smwz[i][j] = smwzTemp[i][j];
									}
								}
							}
						}
						DesignView.this.father.hdv.setImage(image);
						DesignView.this.father.dxv.repaint();
					}

				});
		this.jlDistance.setBounds(130, 10, 60, 20);
		add(this.jlDistance);

		this.jsDistance.setBounds(180, 10, 100, 20);
		this.jsDistance.setValue(170);
		add(this.jsDistance);
		this.jsDistance.addChangeListener(

				new javax.swing.event.ChangeListener() {

					public void stateChanged(javax.swing.event.ChangeEvent arg0) {
						DesignView.this.father.dxv.distance = DesignView.this.jsDistance.getValue();
						DesignView.this.father.dxv.cameraConfig();
						DesignView.this.father.dxv.repaint();
					}

				});
		this.jlWidth.setBounds(10, 40, 30, 20);
		add(this.jlWidth);
		this.jtfWidth.setBounds(40, 40, 40, 20);
		add(this.jtfWidth);
		this.jlHeight.setBounds(90, 40, 30, 20);
		add(this.jlHeight);
		this.jtfHeight.setBounds(120, 40, 40, 20);
		add(this.jtfHeight);
		this.jbNew.setBounds(170, 40, 60, 20);
		add(this.jbNew);
		this.jbNew.addActionListener(

				new java.awt.event.ActionListener() {

					public void actionPerformed(ActionEvent arg0) {
						int width = 0;
						int height = 0;

						try {
							width = Integer.parseInt(DesignView.this.jtfWidth.getText());
							height = Integer.parseInt(DesignView.this.jtfHeight.getText());
						} catch (Exception e) {
							JOptionPane.showMessageDialog(

									DesignView.this, "必须输入整数！", "提示", 0);
						}

						if ((width <= 0) || (height <= 0)) {
							JOptionPane.showMessageDialog(

									DesignView.this, "行数、列数必须大于0！", "提示", 0);

							return;
						}

						int[][] smwzTemp = Constant.smwz;
						Constant.smwz = new int[width][height];
						if (smwzTemp != null) {
							for (int i = 0; i < smwzTemp.length; i++) {
								for (int j = 0; j < smwzTemp[0].length; j++) {
									if ((i < Constant.smwz.length) && (j < Constant.smwz[0].length)) {
										Constant.smwz[i][j] = smwzTemp[i][j];
									}
								}
							}
						}

						DesignView.this.jtfCenterX.setText( Integer.toString( width / 2) );
						DesignView.this.jtfCenterY.setText( Integer.toString( height/ 2) );

						Constant.heightForDraw = new float[width][height];
						Constant.genVertexPeerForLZCJ();
						DesignView.this.father.dxv.repaint();

						Image image = Constant.exportPic(DesignView.this);
						DesignView.this.father.hdv.setImage(image);
						DesignView.this.father.hdv.repaint();
					}

				});
		this.jlLZS.setBounds(10, 70, 60, 20);
		add(this.jlLZS);

		this.jspLZS.setBounds(65, 70, 60, 20);
		add(this.jspLZS);

		this.jlGDYZ.setBounds(135, 70, 80, 20);
		add(this.jlGDYZ);

		this.jspGDYZ.setBounds(190, 70, 40, 20);
		add(this.jspGDYZ);

		this.jlCenter.setBounds(10, 100, 80, 20);
		add(this.jlCenter);
		this.jtfCenterX.setBounds(94, 100, 22, 20);
		add(this.jtfCenterX);
		this.jtfCenterY.setBounds(118, 100, 22, 20);
		add(this.jtfCenterY);

		this.jcbFXJH.setBounds(140, 100, 140, 20);
		this.jcbFXJH.setSelected(true);
		add(this.jcbFXJH);

		this.jlSpan.setBounds(10, 130, 100, 20);
		add(this.jlSpan);
		this.jspSpan.setBounds(90, 130, 75, 20);
		add(this.jspSpan);

		this.jcbSJSM.setBounds(5, 160, 130, 20);
		add(this.jcbSJSM);

		this.jbSMWZ.setBounds(135, 160, 90, 20);
		add(this.jbSMWZ);
		this.jbSMWZ.addActionListener(

				new java.awt.event.ActionListener() {

					public void actionPerformed(ActionEvent arg0) {
						if (Constant.smwz == null) {
							JOptionPane.showMessageDialog(

									DesignView.this, "请首先新建或加载！", "提示", 0);

							return;
						}

						new ShanMaiFrame();
					}

				});
		this.jbZXCJ.setBounds(10, 190, 100, 20);
		add(this.jbZXCJ);
		this.jbZXCJ.addActionListener(

				new java.awt.event.ActionListener() {

					public void actionPerformed(ActionEvent arg0) {
						if (Constant.heightForDraw == null) {
							JOptionPane.showMessageDialog(

									DesignView.this, "请首先新建或加载！", "提示", 0);

							return;
						}

						int centerX = 0;
						int centerY = 0;
						int span = 0;
						int gdyz = 0;
						try {
							centerX = Integer.parseInt(DesignView.this.jtfCenterX.getText());
							centerY = Integer.parseInt(DesignView.this.jtfCenterY.getText());
							span = Integer.parseInt(DesignView.this.jspSpan.getValue().toString());
							gdyz = Integer.parseInt(DesignView.this.jspGDYZ.getValue().toString());
						} catch (Exception e) {
							JOptionPane.showMessageDialog(

									DesignView.this, "必须输入整数！", "提示", 0);
						}

						if ((centerX < 0) || (centerY < 0)) {
							JOptionPane.showMessageDialog(

									DesignView.this, "行数、列数必须大于等于0！", "提示", 0);

							return;
						}

						if ((centerX >= Constant.heightForDraw.length)
								|| (centerY >= Constant.heightForDraw[0].length)) {
							JOptionPane.showMessageDialog(

									DesignView.this, "行数、列数在有效范围内！", "提示", 0);

							return;
						}

						java.util.ArrayList<int[]> smwzAl = new java.util.ArrayList();
						for (int i = 0; i < Constant.smwz.length; i++) {
							for (int j = 0; j < Constant.smwz[0].length; j++) {
								if (Constant.smwz[i][j] == 1) {
									smwzAl.add(new int[] { i, j });
								}
							}
						}

						LiZiChenJiUtil.genCJ(

								Constant.heightForDraw, centerX, centerY,
								((Integer) DesignView.this.jspLZS.getValue()).intValue(), span, gdyz,
								DesignView.this.jcbFXJH.isSelected(), DesignView.this.jcbSJSM.isSelected(),
								(int[][]) smwzAl.toArray(new int[0][0]));

						Constant.genVertexPeerForLZCJ();
						DesignView.this.father.dxv.repaint();

						Image image = Constant.exportPic(DesignView.this);
						DesignView.this.father.hdv.setImage(image);
						DesignView.this.father.hdv.repaint();
					}

				});
		this.jbDCHDT.setBounds(10, 220, 100, 20);
		add(this.jbDCHDT);
		this.jbDCHDT.addActionListener(

				new java.awt.event.ActionListener() {

					public void actionPerformed(ActionEvent arg0) {
						if (DesignView.this.father.hdv.ii == null) {
							JOptionPane.showMessageDialog(

									DesignView.this, "请首先新建或加载！", "提示", 0);

							return;
						}

						if (!(DesignView.this.father.hdv.ii instanceof java.awt.image.BufferedImage)) {
							JOptionPane.showMessageDialog(

									DesignView.this, "没有操作，不需要导出！", "提示", 0);

							return;
						}

						java.io.File f = new java.io.File("pic/out.png");
						try {
							javax.imageio.ImageIO.write((java.awt.image.BufferedImage) DesignView.this.father.hdv.ii,
									"PNG", f);
							JOptionPane.showMessageDialog(

									DesignView.this, "导出成功！", "提示", 1);

						} catch (IOException e) {
							JOptionPane.showMessageDialog(

									DesignView.this, "导出失败" + e.toString() + "！", "提示", 0);
						}

					}

				});
		this.jbDCCCode.setBounds(10, 250, 100, 20);
		add(this.jbDCCCode);
		this.jbDCCCode.addActionListener(

				new java.awt.event.ActionListener() {

					public void actionPerformed(ActionEvent arg0) {
						if (Constant.heightForDraw == null) {
							JOptionPane.showMessageDialog(

									DesignView.this, "没有数据可供导出！", "提示", 0);

							return;
						}

						int width = Constant.heightForDraw.length;
						int height = Constant.heightForDraw[0].length;

						float max = 0.0F;
						for (int i = 0; i < width; i++) {
							for (int j = 0; j < height; j++) {
								if (Constant.heightForDraw[i][j] > max) {
									max = Constant.heightForDraw[i][j];
								}
							}
						}

						if (max != 0.0F) {
							StringBuilder sb = new StringBuilder("const float mHigh[" + width + "][" + height + "]=\n");
							sb.append("{\n");

							float ratio = 255.0F / max;
							for (int i = 0; i < width; i++) {
								for (int j = 0; j < height; j++) {
									int gray = (int) (Constant.heightForDraw[i][j] * ratio);
									sb.append(gray + ",");
								}
								sb.append("\n");
							}
							sb.append("};");
							String code = sb.toString();
							java.io.File f = new java.io.File("code/terri.c");
							try {
								java.io.FileWriter fw = new java.io.FileWriter(f);
								fw.write(code);
								fw.close();
								JOptionPane.showMessageDialog(

										DesignView.this, "导出成功！", "提示", 1);

							} catch (IOException e) {
								JOptionPane.showMessageDialog(

										DesignView.this, "导出失败" + e.toString() + "！", "提示", 0);
							}

						} else {
							JOptionPane.showMessageDialog(

									DesignView.this, "没有数据可供导出！", "提示", 0);
						}
					}
				});
	}
}

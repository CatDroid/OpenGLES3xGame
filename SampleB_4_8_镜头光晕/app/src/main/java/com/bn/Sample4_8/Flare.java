package com.bn.Sample4_8;//声明包

import static com.bn.Sample4_8.Constant.DIS_MAX;
import static com.bn.Sample4_8.Constant.SCALE_MAX;
import static com.bn.Sample4_8.Constant.SCALE_MIN;

import java.util.ArrayList;//引用列表

public class Flare{

	// 纹理数组
	public int[] textures;
	// 存放光晕元素的列表
	public ArrayList<SingleFlare> sFl = new ArrayList<SingleFlare>();


	public Flare(int[] textures)
	{
		this.textures = textures; 	// 初始化纹理数组
		initFlare();				// 初始化光晕元素
	}

	// 初始化光晕元素对象的方法
	public void initFlare()
	{
		// 以下创建并向列表中添加了13个镜头光晕元素对象
		sFl.add(new SingleFlare(textures[1],5.4f,-1.0f, 	new float[]{1.0f, 1.0f, 1.0f, 1.0f}));
		sFl.add(new SingleFlare(textures[1],0.4f,-0.8f,  	new float[]{0.7f, 0.5f, 0.0f, 0.02f}));
		sFl.add(new SingleFlare(textures[1],0.04f,-0.7f,	new float[]{1.0f, 0.0f, 0.0f, 0.07f}));
		sFl.add(new SingleFlare(textures[0],0.4f,-0.5f,		new float[]{1.0f, 1.0f, 0.0f, 0.05f}));
		sFl.add(new SingleFlare(textures[2],1.22f,-0.4f,	new float[]{1.0f, 1.0f, 0.0f, 0.05f}));
		sFl.add(new SingleFlare(textures[0],0.4f,-0.3f,		new float[]{1.0f, 0.5f, 0.0f, 1.0f}));
		sFl.add(new SingleFlare(textures[1],0.4f,-0.1f,		new float[]{1.0f, 1.0f, 0.5f, 0.05f}));

		sFl.add(new SingleFlare(textures[0],0.4f,0.2f,		new float[]{1.0f, 0.0f, 0.0f, 1.0f}));
		sFl.add(new SingleFlare(textures[1],0.8f,0.3f,		new float[]{1.0f, 1.0f, 0.6f, 1.0f}));
		sFl.add(new SingleFlare(textures[0],0.6f,0.4f,		new float[]{1.0f, 0.7f, 0.0f, 0.03f}));
		sFl.add(new SingleFlare(textures[2],0.6f,0.7f,		new float[]{1.0f, 0.5f, 0.0f, 0.02f}));
		sFl.add(new SingleFlare(textures[2],1.28f,1.0f,		new float[]{1.0f, 0.7f, 0.0f, 0.02f}));
		sFl.add(new SingleFlare(textures[2],3.20f,1.3f,		new float[]{1.0f, 0.0f, 0.0f, 0.05f})); // 只显示部分
	}


	// (lx,ly) 太阳在屏幕空间的位置,左上角-radio~radio -1~1
	public void update(float lx,float ly)
	{

		// 太阳 到 屏幕空间的原点 之间的距离
		float currDis = (float)Math.sqrt( lx*lx + ly*ly );

		// 根据太阳/光源 与 原点的距离 计算整体的缩放比例
		// DIS_MAX 屏幕左上角到屏幕中心的距离值
		// 太阳距离屏幕原点 距离越小, 缩放比例越大
		// 距离为0, 缩放比例是SCALE_MAX
		// 距离为DIS_MAX, 缩放比例是SCALE_MIN
		float currScale = SCALE_MIN + (SCALE_MAX - SCALE_MIN) * (1 - currDis/DIS_MAX);

		// 循环遍历所有光晕元素对象
		for(SingleFlare ss:sFl)
		{
			ss.px = -ss.distance * lx;			// 计算该光晕元素的绘制位置x坐标 ss.distance 这个光晕 与 光源在屏幕上 的距离
												// 光源在屏幕空间的坐标 与 原点 连接线上 的 距离

			ss.py = -ss.distance * ly;			// 计算该光晕元素的绘制位置y坐标

			ss.displaySize =ss.originSize * currScale;			//计算变换后的尺寸
		}
	}
}

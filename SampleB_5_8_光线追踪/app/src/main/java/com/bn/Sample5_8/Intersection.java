package com.bn.Sample5_8;//声明包

// 保存光线每与某个物体 的 交叉点 的类
public class Intersection
{

	int numHits;						// 有正相交时间的相交物体的数目
	HitInfo[] hit = new HitInfo[8];		// 交点列表数组
	
	public Intersection()
	{
		for(int i=0; i<8; i++){			// 初始化交点列表数组
			hit[i] = new HitInfo();
		}
	}

	public void set(Intersection inter)	// 将传入Intersection对象的信息复制到自身
	{
		for(int i=0; i<8; i++)
		{
			this.hit[i].set(inter.hit[i]);
		}
		
		this.numHits = inter.numHits;
	}
}

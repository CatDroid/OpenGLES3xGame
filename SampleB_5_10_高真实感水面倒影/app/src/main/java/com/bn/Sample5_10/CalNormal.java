package com.bn.Sample5_10;
import android.annotation.SuppressLint;
import java.util.HashMap;
import java.util.HashSet;

public class CalNormal
{

	// vertexes 为索引法时的顶点坐标数据数组
	// indices  为索引数组
	@SuppressLint("UseSparseArrays")
	public static float[] calNormal(float[] vertexes, int[] indices) {

		// 平均前各个索引对应的点的法向量集合Map
		// 此HashMap的key为点的索引， value为点所在的各个面的法向量的集合

		HashMap<Integer, HashSet<Normal>> hmn = new HashMap<Integer, HashSet<Normal>>();

		float[] answer = new float[vertexes.length];

		// 每3个索引 构成一个三角形
		for (int i = 0; i < indices.length / 3; i++)
		{

			// 顶点索引数组，3个索引，构成一个面
			int[] index = new int[] {
					indices[i * 3 + 0],
					indices[i * 3 + 1],
					indices[i * 3 + 2] };

			// 3个顶点的3个坐标
			float x0 = vertexes[index[0] * 3 + 0];
			float y0 = vertexes[index[0] * 3 + 1];
			float z0 = vertexes[index[0] * 3 + 2];

			float x1 = vertexes[index[1] * 3 + 0];
			float y1 = vertexes[index[1] * 3 + 1];
			float z1 = vertexes[index[1] * 3 + 2];

			float x2 = vertexes[index[2] * 3 + 0];
			float y2 = vertexes[index[2] * 3 + 1];
			float z2 = vertexes[index[2] * 3 + 2];

			// 通过三角形面两个边向量0-1，0-2求叉积得到此面的法向量
			// 求0号点到1号点的向量
			float vxa = x1 - x0;
			float vya = y1 - y0;
			float vza = z1 - z0;
			// 求0号点到2号点的向量
			float vxb = x2 - x0;
			float vyb = y2 - y0;
			float vzb = z2 - z0;

			// 叉积 求三角面的法向量
			float[] tempNormal=LoadUtil.getCrossProduct(vxa, vya, vza, vxb,vyb, vzb);

			// 归一化
			float[] vNormal = LoadUtil.vectorNormal(tempNormal);

			for (int tempInxex : index) 
			{
				// 记录每个索引点的法向量，并将其添加进该索引对应的集合hsn中
				// 获取当前索引对应点的法向量集合
				HashSet<Normal> hsn = hmn.get(tempInxex);
				// 若集合不存在则创建
				if (hsn == null)
				{
					hsn = new HashSet<Normal>();
				}
				// 将此点的法向量添加到集合中
				// 由于Normal类重写了equals方法，因此同样的法向量不会重复出现在此点
				// 对应的法向量集合中
				hsn.add(new Normal(vNormal[0], vNormal[1], vNormal[2]));
				// 将集合放进HsahMap中
				hmn.put(tempInxex, hsn);
			}
		}

		// 生成法向量数组
		int c = 0;
		for (int i = 0; i < vertexes.length/3; i++) {
			// 根据当前点的索引从Map中取出一个法向量的集合
			HashSet<Normal> hsn = hmn.get(i);
			// 求出平均法向量
			float[] tn = Normal.getAverage(hsn);
			// 将计算出的平均法向量存放到法向量数组中
			answer[c++] = tn[0];
			answer[c++] = tn[1];
			answer[c++] = tn[2];
		}
		return answer;
	}
}

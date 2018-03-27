package com.bn.Sample8_6;//包声明

import java.util.ArrayList;//相关类的引入

public class UtilTools {


	// hhl  C60是具有60个顶点和32个面，其中12个为正五边形，20个为正六边形
	public UtilTools() {

	}//构造器

	float bHalf = 0;//黄金长方形的宽
	float r = 0;//足球碳的半径
	int count = 0;
	//大小，黄金长方形的长的一半，分段数
	   public ResultData initVertexData(float scale, float aHalf, int n) {
		ResultData Rda = new ResultData();//创建Rda(结果数据)对象

		aHalf *= scale;					// 黄金长方形的长
		bHalf = aHalf * 0.618034f;		// 黄金长方形的宽
		r = (float) Math.sqrt(aHalf * aHalf + bHalf * bHalf);// 足球碳分子的半径

		ArrayList<Float> alVertix20 = new ArrayList<Float>();		// 构成二十面体的顶点坐标列表
		ArrayList<Integer> alFaceIndex20 = new ArrayList<Integer>();	// 正二十面体组织成面的顶点的索引值列表

		alVertix20.add(0f);		alVertix20.add(aHalf);	alVertix20.add(-bHalf);// 正20面体顶点
		alVertix20.add(0f);		alVertix20.add(aHalf);	alVertix20.add(bHalf);
		alVertix20.add(aHalf);	alVertix20.add(bHalf);	alVertix20.add(0f);
		alVertix20.add(bHalf);	alVertix20.add(0f);		alVertix20.add(-aHalf);
		alVertix20.add(-bHalf);	alVertix20.add(0f);		alVertix20.add(-aHalf);
		alVertix20.add(-aHalf);	alVertix20.add(bHalf);	alVertix20.add(0f);

		alVertix20.add(-bHalf);	alVertix20.add(0f);		alVertix20.add(aHalf);
		alVertix20.add(bHalf);	alVertix20.add(0f);		alVertix20.add(aHalf);
		alVertix20.add(aHalf);	alVertix20.add(-bHalf);	alVertix20.add(0f);
		alVertix20.add(0f);		alVertix20.add(-aHalf);	alVertix20.add(-bHalf);
		alVertix20.add(-aHalf);	alVertix20.add(-bHalf);	alVertix20.add(0f);
		alVertix20.add(0f);		alVertix20.add(-aHalf);	alVertix20.add(bHalf);
		// 正20面体索引！
		alFaceIndex20.add(0);	alFaceIndex20.add(1);	alFaceIndex20.add(2);// 1号三角形面坐标索引
		alFaceIndex20.add(0); 	alFaceIndex20.add(2); 	alFaceIndex20.add(3);// 2号三角形面坐标索引
		alFaceIndex20.add(0);	alFaceIndex20.add(3);	alFaceIndex20.add(4);// 3号三角形面坐标索引
		alFaceIndex20.add(0); 	alFaceIndex20.add(4); 	alFaceIndex20.add(5);// 4号三角形面坐标索引
		alFaceIndex20.add(0);	alFaceIndex20.add(5);	alFaceIndex20.add(1);// 5号三角形面坐标索引

		alFaceIndex20.add(1);	alFaceIndex20.add(6);	alFaceIndex20.add(7);// 6号三角形面坐标索引
		alFaceIndex20.add(1); 	alFaceIndex20.add(7); 	alFaceIndex20.add(2);// 7号三角形面坐标索引
		alFaceIndex20.add(2);	alFaceIndex20.add(7);	alFaceIndex20.add(8);// 8号三角形面坐标索引
		alFaceIndex20.add(2);	alFaceIndex20.add(8);	alFaceIndex20.add(3);// 9号三角形面坐标索引
		alFaceIndex20.add(3); 	alFaceIndex20.add(8); 	alFaceIndex20.add(9);// 10号三角形面坐标索引
		 
		alFaceIndex20.add(3);	alFaceIndex20.add(9);	alFaceIndex20.add(4);// 11号三角形面坐标索引
		alFaceIndex20.add(4); 	alFaceIndex20.add(9); 	alFaceIndex20.add(10);// 12号三角形面坐标索引
		alFaceIndex20.add(4);	alFaceIndex20.add(10);	alFaceIndex20.add(5);// 13号三角形面坐标索引
		alFaceIndex20.add(5);	alFaceIndex20.add(10);	alFaceIndex20.add(6);// 14号三角形面坐标索引
		alFaceIndex20.add(5); 	alFaceIndex20.add(6); 	alFaceIndex20.add(1);// 15号三角形面坐标索引
		
		alFaceIndex20.add(6);	alFaceIndex20.add(11);	alFaceIndex20.add(7); // 16号三角形面坐标索引
		alFaceIndex20.add(7); 	alFaceIndex20.add(11); 	alFaceIndex20.add(8); // 17号三角形面坐标索引
		alFaceIndex20.add(8);	alFaceIndex20.add(11);	alFaceIndex20.add(9); // 18号三角形面坐标索引
		alFaceIndex20.add(9);	alFaceIndex20.add(11);	alFaceIndex20.add(10);// 19号三角形面坐标索引
		alFaceIndex20.add(10); 	alFaceIndex20.add(11); 	alFaceIndex20.add(6); // 20号三角形面坐标索引


		float[] vertices20 = VectorUtil.cullVertex(alVertix20, alFaceIndex20);	// 构成正二十面体的三角形顶点坐标


		ArrayList<float[]> AlCAtomicPosition = new ArrayList<float[]>();		// 存放碳原子的坐标列表
		ArrayList<float[]> AlChemicalBondPoints = new ArrayList<float[]>();		// 存放代表化学键的边的端点坐标列表

		for (int k = 0; k < vertices20.length; k += 9){//对正二十面体中每个正三角形循环
			float[] v1 = new float[] { vertices20[k + 0], vertices20[k + 1], vertices20[k + 2] };
			float[] v2 = new float[] { vertices20[k + 3], vertices20[k + 4], vertices20[k + 5] };
			float[] v3 = new float[] { vertices20[k + 6], vertices20[k + 7], vertices20[k + 8] };
			//计算出每个正三角形每条边上的两个顶点所在大圆圆弧上的三等分点坐标
			for (int i = 1; i < n; i++) { // n = 3   所以只获取 1和2个等分点 也就是三等分后的中间两个顶点
				float[] vi1 = VectorUtil.devideBall(r, v1, v2, n, i);//第1大圆圆弧上的第i三等分点坐标
				vi1 = VectorUtil.normalizeVector(vi1);//规格化vi1
				float[] vi2 = VectorUtil.devideBall(r, v1, v3, n, i);//第2大圆圆弧上的第i三等分点坐标
				vi2 = VectorUtil.normalizeVector(vi2);//规格化vi2
				float[] vi3 = VectorUtil.devideBall(r, v2, v3, n, i);//第3大圆圆弧上的第i三等分点坐标
				vi3 = VectorUtil.normalizeVector(vi3);//规格化vi3
				AlCAtomicPosition.add(vi1); // hhl ArrayList中每一个元素都是float[]/三维数组
				AlCAtomicPosition.add(vi2);
				AlCAtomicPosition.add(vi3);
			}
			/* hhl 新增了6个元素 每个元素是个三维数组float[]
				      v1
				     0  1
				    3    4
				  v2  2 5  v3

			 */

			//将求得的6个三等分点的坐标依次两两组合起来
			AlChemicalBondPoints.add(AlCAtomicPosition.get(count * 6));	 //1号顶点的坐标
			AlChemicalBondPoints.add(AlCAtomicPosition.get(count * 6 + 1));//2号顶点的坐标
			
			AlChemicalBondPoints.add(AlCAtomicPosition.get(count * 6 + 1)); //2号顶点的坐标
			AlChemicalBondPoints.add(AlCAtomicPosition.get(count * 6 + 4)); //5号顶点的坐标
			
			AlChemicalBondPoints.add(AlCAtomicPosition.get(count * 6 + 4)); //5号顶点的坐标
			AlChemicalBondPoints.add(AlCAtomicPosition.get(count * 6 + 5)); //6号顶点的坐标
			
			AlChemicalBondPoints.add(AlCAtomicPosition.get(count * 6 + 5)); //6号顶点的坐标
			AlChemicalBondPoints.add(AlCAtomicPosition.get(count * 6 + 2)); //3号顶点的坐标
			
			AlChemicalBondPoints.add(AlCAtomicPosition.get(count * 6 + 2)); //3号顶点的坐标
			AlChemicalBondPoints.add(AlCAtomicPosition.get(count * 6 + 3)); //4号顶点的坐标
			
			AlChemicalBondPoints.add(AlCAtomicPosition.get(count * 6 + 3)); //4号顶点的坐标
			AlChemicalBondPoints.add(AlCAtomicPosition.get(count * 6)); 	//1号顶点的坐标
			count+=1;// 六边形的个数!!   hhl: 不用画五边形!! 只要把六边形全部画出来,五边形就有了
		}


		//创建代表化学键的边的端点坐标数组，ChemicalBondPoints中每个一维数据存放两个顶点的信息
		Rda.ChemicalBondPoints = new float[AlChemicalBondPoints.size() / 2][6];
		//把AlChemicalBondPoints中的数据转存到Rda.ChemicalBondPoints中
		// hhl Rda.ChemicalBondPoints 是个二维数组  第一维代表每个化学键  第二位代表这个化学键的起始和终止坐标
		for (int i = 0; i < Rda.ChemicalBondPoints.length; i++) {
			//把AlChemicalBondPoints中的第2*i个点的坐标转存到Rda.ChemicalBondPoints中
			for (int j = 0; j < 3; j++) {
				Rda.ChemicalBondPoints[i][j] =r * AlChemicalBondPoints.get(2 * i)[j];
			}
			//把AlChemicalBondPoints中的第2*i+1个点的坐标转存到Rda.ChemicalBondPoints中
			for (int k= 3; k <6; k++) {
				Rda.ChemicalBondPoints[i][k] = r *AlChemicalBondPoints.get(2 * i + 1)[k - 3];
			}
		}



		//创建碳原子位置的坐标数组Rda.CAtomicPosition
		// hhl Rda.CAtomicPosition 第一维代表每个顶点(碳原子) 第二维代表 每个顶点的坐标
		Rda.CAtomicPosition = new float[AlCAtomicPosition.size()][3];
		for (int i = 0; i < AlCAtomicPosition.size(); i++) {
			for (int j = 0; j < 3; j++) {
				//把AlCAtomicPosition中的坐标转存到Rda.CAtomicPosition中
				Rda.CAtomicPosition[i][j] = r * AlCAtomicPosition.get(i)[j];
			}
		}
		return Rda;
	}
}

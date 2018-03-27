package com.bn.zqt.util;//包声明

public class ZQTEdgeUtil 
{
	public static float STANDARD_EDGE_LENGTH=2;//原始圆柱长度
	public static float[] ZHU_VECTOR_NORMAL={1,0,0}; 	//原始圆柱方向向量
	
	public static float[] calTranslateRotateScale(float[] ab)
	{		
		//拆分出目标边对应圆柱AB两端点的坐标
		float xa=ab[0];
		float ya=ab[1];
		float za=ab[2];
		float xb=ab[3];
		float yb=ab[4];
		float zb=ab[5];
		//计算A点到B点的向量
		float[] abVector={xb-xa,yb-ya,zb-za};
		//规格化AB向量
		float[] normalAB=VectorUtil.vectorNormal(abVector);
		//AB向量叉乘原始圆柱向量
		float[] normalABCrossZhu=VectorUtil.vectorNormal
		(
			VectorUtil.getCrossProduct
		    (
		    	normalAB[0],normalAB[1],normalAB[2],
		    	ZHU_VECTOR_NORMAL[0], ZHU_VECTOR_NORMAL[1], ZHU_VECTOR_NORMAL[2]
		    )
		);
		//求AB向量与原始圆柱向量的夹角
		float angle=(float)Math.toDegrees(VectorUtil.angle(normalAB, ZHU_VECTOR_NORMAL));
		
		float xABZ=(xa+xb)/2;//求AB向量中点
		float yABZ=(ya+yb)/2;//求AB向量中点
		float zABZ=(za+zb)/2;//求AB向量中点
		//求长度缩放值
		float scale=VectorUtil.mould(abVector)/STANDARD_EDGE_LENGTH;//求长度缩放值		
		final float angleThold=0.8f;	//旋转角阈值	
		//旋转角为0度或180度时则不需要旋转
		if(Math.abs(angle)>angleThold&&Math.abs(angle)<180-angleThold){			
			return new float[]{
				xABZ,yABZ,zABZ,
				-angle,   // hhl ?? 旋转角
				normalABCrossZhu[0],normalABCrossZhu[1],normalABCrossZhu[2], // 旋转轴
				scale,1,1
			};
		}
		else{			
			return new float[]{
				xABZ,yABZ,zABZ,//平移信息
				0,0,0,1,//旋转信息
				scale,1,1//缩放信息
			};
		}
	}
}

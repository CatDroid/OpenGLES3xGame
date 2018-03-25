package com.bn.Sample8_5;//包声明

import android.util.Log;

import java.util.ArrayList;//相关类的引入

//计算三角形法向量的工具类
public class VectorUtil {
	//向量规格化的方法
	public static float[] normalizeVector(float [] vec){
		float mod=module(vec);
		return new float[]{vec[0]/mod, vec[1]/mod, vec[2]/mod};//返回规格化后的向量
	}
	//求向量的模的方法
	public static float module(float [] vec){
		return (float) Math.sqrt(vec[0]*vec[0]+vec[1]*vec[1]+vec[2]*vec[2]);
	}
	//两个向量叉乘的方法
	public static float[] crossTwoVectors(float[] a, float[] b)
	{
		float x=a[1]*b[2]-a[2]*b[1];
		float y=a[2]*b[0]-a[0]*b[2];
		float z=a[0]*b[1]-a[1]*b[0];
		return new float[]{x, y, z};//返回叉乘结果
	}
	
	public static float dotTwoVectors(float[] a, float[] b)
	{//两个向量点积的方法
		return a[0]*b[0]+a[1]*b[1]+a[2]*b[2];//返回点积结果
	}

	//根据顶点编号生成卷绕后顶点纹理坐标数组的方法
	public static float[] cullTexCoor(
			ArrayList<Float> alST,
			ArrayList<Integer> alTexIndex
			)
	{
		float[] textures=new float[alTexIndex.size()*2];//结果纹理坐标数组
		
		int stCount=0;//纹理坐标计数器
		for(int i:alTexIndex){//对顶点编号列表进行循环
			textures[stCount++]=alST.get(2*i);//将当前编号顶点的S坐标值存入最终数组
			textures[stCount++]=alST.get(2*i+1);//将当前编号顶点的T坐标值存入最终数组
		}
		return textures;//返回结果纹理坐标数组
	}
	//根据顶点编号生成卷绕后顶点坐标数组的方法
	public static float[] cullVertex(
			ArrayList<Float> alv,
			ArrayList<Integer> alFaceIndex
			)
	{
		float[] vertices=new float[alFaceIndex.size()*3];//存放卷绕后顶点坐标值的数组
		
		int vCount=0;//顶点计数器
		for(int i:alFaceIndex){//对顶点编号列表进行循环
			vertices[vCount++]=alv.get(3*i);//将当前编号顶点的X坐标值存入最终数组
			vertices[vCount++]=alv.get(3*i+1);//将当前编号顶点的Y坐标值存入最终数组
			vertices[vCount++]=alv.get(3*i+2);//将当前编号顶点的Z坐标值存入最终数组
		}
		return vertices;//返回结果顶点坐标数组
	}
	// 计算圆弧的n等分点坐标的方法
	// r为半径，start为从球心到圆弧起始点的向量 end为从球心到圆弧终点的向量 n为切分的总份数
	// i 为所求点对应的份数编号
	public static float[] devideBall(
			float r, 		//	球的半径
			float[] start, 	//	指向圆弧起点的向量
			float[] end, 	//	指向圆弧终点的向量
			int n, 			//	圆弧分的份数
			int i 			//	求第i份在圆弧上的坐标（i为0和n时分别代表起点和终点坐标）
			)
	{
		if(n==0){
			if( Math.abs(start[0]-end[0]) > 0.00001
					||  Math.abs(start[1]-end[1]) > 0.00001
					||   Math.abs(start[2]-end[2]) > 0.00001  )
				Log.w("TOM", "diff ?");
			return start ;// 如果n为零，返回起点坐标
		}else{
			if(i == 0 ) { // hhl add 几乎和下面运算得到的一样 最多差距 0.000002
				return start;
			}else if(i == n){
				return end;
			}
		}

		/* 
		 * 先求出所求向量的规格化向量，再乘以半径r即可
		 * s0*x+s1*y+s2*z=cos(angle1)//根据所求向量和起点向量夹角为angle1---1式
		 * e0*x+e1*y+e2*z=cos(angle2)//根据所求向量和终点向量夹角为angle2---2式
		 * x*x+y*y+z*z=1//所球向量的规格化向量模为1---3式
		 * x*n0+y*n1+z*n2=0//所球向量与法向量垂直---4式
		 * 算法为：将1、2两式用换元法得出x=a1+b1*z，y=a2+b2*z的形式，
		 * 将其代入4式求出z，再求出x、y，最后将向量(x,y,z)乘以r即为所求坐标。
		 * 1式和2式是将3式代入得到的，因此已经用上了。
		 * 由于叉乘的结果做了分母，因此起点、终点、球心三点不能共线
		 * 注意结果是将劣弧等分
		 */
		//先将指向起点和终点的向量规格化
		float[] s=VectorUtil.normalizeVector(start);//先将指向起点和终点的向量规格化
		float[] e=VectorUtil.normalizeVector(end);

//		if( Math.abs(module(start) - r ) >= 0.0000001  ){
//			Log.d("TOM"," module " + module(start) + " r = " + r );
//		}

//		if(n==0){	//如果n为零，返回起点坐标
//			return new float[]{s[0]*r, s[1]*r, s[2]*r};
//		}

		//求两个向量的夹角
		double angrad=Math.acos(VectorUtil.dotTwoVectors(s, e));//起点终点向量夹角
		double angrad1=angrad*i/n;//所求向量和起点向量的夹角
		double angrad2=angrad-angrad1;//所求向量和终点向量的夹角
		
		float[] normal=VectorUtil.crossTwoVectors(s, e);//求与s、e向量正交的向量

		//if( module(normal) != 1 ){  // 两个单位向量的叉积 不一定是单位向量 但一定是正交的向量 垂直平面
		//	Log.w("TOM", "normal is not 1 " + module(normal));
		//}

		/*
			hhl 三个方程是： (x,y,z) 未知数 单位向量
				与s向量的点积 是 cos夹角  			  	----> 	(x,y,z) .dot (s0,s1,s2) = 1*1*cos(angrad1)
				与e向量的点积 是 cos夹角				---->	(x,y,z) .dot (e0,e1,e2) = 1*1*cos(angrad2)
				与s和e正交的向量 一定是跟 (x,y,z) 垂直 	---->  	normal  .dot (x,y,z)  = 0					-- 限制了一定要在s,e所在平面


		 */
		double matrix[][]={								// 用doolittle分解算法解n元一次线性方程组所需的系数矩阵
				{s[0],s[1],s[2],Math.cos(angrad1)},
				{e[0],e[1],e[2],Math.cos(angrad2)},
				{normal[0],normal[1],normal[2],0}  
		};
		double result[] = MyMathUtil.doolittle(matrix); // 解n元一次线性方程组


//		if(i==0){
//			double diffx = Math.abs( result[0]*r -start[0]);
//			double diffy = Math.abs( result[1]*r -start[1]);
//			double diffz = Math.abs( result[2]*r -start[2]);
//			if( diffx > 0.000001 || diffy > 0.000001 || diffz > 0.000001  )
//				Log.w("TOM", String.format("start %f %f %f --- %f %f %f ", result[0]*r, result[1]*r,result[2]*r, start[0],start[1],start[2] ) );
//			return start;
//		}else if(i==n){
//			double diffx = Math.abs( result[0]*r -end[0]);
//			double diffy = Math.abs( result[1]*r -end[1]);
//			double diffz = Math.abs( result[2]*r -end[2]);
//			if( diffx > 0.000001 || diffy > 0.000001 || diffz > 0.000001  )
//				Log.w("TOM", String.format("end %f %f %f --- %f %f %f ",  result[0]*r, result[1]*r,result[2]*r, end[0],end[1],end[2] ) );
//			return end;
//		}

		//求规格化向量xyz的值
		float x=(float) result[0];			//	得到从球心到所求'点向量的规格化版本'
		float y=(float) result[1];
		float z=(float) result[2];
		return new float[]{x*r, y*r, z*r};	//	得到所求'点的坐标'并返回
	}


	// 计算线段的n等分点坐标的方法
	// start为线段起点坐标，end为线段终点坐标，n为切分的总份数
	// i为所求点对应的份数编号
	public static float[] devideLine(
			float[] start, 			// 线段起点坐标
			float[] end, 			// 线段终点坐标
			int n, 					// 线段分的份数
			int i 					// 求第i份在线段上的坐标（i为0和n时分别代表起点和终点坐标）
			)
	{
		if(n==0){					// 如果n为零，返回起点坐标
			return start;
		}else if( n == 1 ){ // hhl add
			if( i == 0 ){
				return start;
			}else if( i == 1 ){
				return end;
			}
		}

		float[] ab=new float[]{end[0]-start[0], end[1]-start[1], end[2]-start[2]};	// 求起点到终点的向量
		float vecRatio=i/(float)n;	// 求向量比例
		float[] ac=new float[]{ab[0]*vecRatio, ab[1]*vecRatio, ab[2]*vecRatio};		// 求起点到所求点的向量

		float x=start[0]+ac[0];		// 得到所求点坐标
		float y=start[1]+ac[1];
		float z=start[2]+ac[2];
		return new float[]{x, y, z};// 返回线段所求点坐标
	}
}

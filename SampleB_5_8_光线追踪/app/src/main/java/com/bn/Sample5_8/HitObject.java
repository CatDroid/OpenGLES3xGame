package com.bn.Sample5_8;//声明包
import android.opengl.Matrix;//引入相关类

public abstract class HitObject {

	Color3f color;				// 物体的颜色

	private float[] myMatrix;	// 模型变换矩阵  从 模型坐标系 到 世界坐标系

	Camera cam;					// 摄像机

	public abstract boolean hit(Ray ray,Intersection inter);	// 求交点的方法
	public abstract boolean hit(Ray ray);						// 只判断是否相交的方法


	public Color3f getColor() {//获取物体颜色的方法
		return color;
	}

	 // 返回光线在t时刻的位置的方法( r 是变换前的光线 t 是时间点 返回是物体和跟踪光线的相交点/世界坐标系)
	public Point3 rayPos(Ray r,double t)
	{
		return cam.getEye().addVec(r.dir.multiConst((float)t));	//eye+dir*t得到当前点的坐标
	}


	// 获取变换后的光线(原来光线r 按逆变换矩阵变换后invf 赋值给genRay (左乘变换矩阵的逆矩阵))
	// 用于计算光线是否跟物体表面相交
	public void xfrmRay(Ray genRay, float[] invf, Ray r)
	{
		// 求变换后的start
		float[] genStart = new float[4];
		Matrix.multiplyMV(genStart, 0, invf, 0, r.start.toQici4(), 0);
		genRay.start.set(genStart);// 只取齐次坐标和前3个分量作为start
		
		// 求变换后的dir
		float[] genDir = new float[4];
		Matrix.multiplyMV(genDir, 0, invf, 0, r.dir.toQici4(), 0);
		genRay.dir.set(genDir);		// 只取齐次坐标和前3个分量作为dir
	}

	// 获取变换后的法向量,这里只是根据传入的逆转置矩阵计算法线( 法向量normal 左乘 变换矩阵的逆转置矩阵 getInvertTransposeMatrix()  )
	public void xfrmNormal(Vector3 genNormal, Vector3 normal)
	{
		float[] tmpNormal = new float[4];	// 用于存储变换后法向量的数组
		Matrix.multiplyMV(	tmpNormal, 0,
							_getInvertTransposeMatrix(), 0,
							normal.toQici4(), 0);
		genNormal.set(tmpNormal);			// 只取齐次坐标和前3个分量作为normal
	}

	// 把给定世界坐标系上的坐标点/有向向量 转换成 物体坐标系
	public Point3 xfrmPtoPreP(Point3 P){

		float[] inverM = _getInvertMatrix();	// 获取逆变换矩阵
		float[] preP = new float[4];		// 存储变换前点坐标的数组
		Matrix.multiplyMV(preP, 0, inverM, 0, P.toQici4(), 0);
											// 求变换前的点
		return new Point3(preP);			// 变换前的点就是变换之前的法向量
	}


	// 初始化变换矩阵的方法
	public void initMyMatrix() {
		myMatrix = new float[16];
	    Matrix.setIdentityM(myMatrix, 0);	    
	}
	
	// 获取对象的变换矩阵的方法
	public float[] getMatrix(){		
		return myMatrix;
	}

	// 逆矩阵
	public float[] _getInvertMatrix(){
//		float[] invM = new float[16];//用于存储结果矩阵的数组
//		Matrix.invertM(invM, 0, myMatrix, 0);//求逆矩阵
//		return invM;
        return myRevertMatrix;
	}


	// 逆转置矩阵
	public float[] _getInvertTransposeMatrix(){
//        float[] invTranspM = new float[16];//用于存储结果矩阵的数组
//        Matrix.transposeM(invTranspM, 0, myMatrix, 0);//求转置矩阵
//        Matrix.invertM(invTranspM, 0, invTranspM, 0);//求逆转置矩阵
//        return invTranspM;
        return myTransposeRevertMatrix;
	}


	//// 仿射变换

	// 设置沿x、y、z轴移动
	public void translate(float x, float y, float z)
	{
		Matrix.translateM(myMatrix, 0, x, y, z);
	}

	// 设置指定轴旋转
	public void rotate(float angle, float x, float y, float z)
	{
		Matrix.rotateM(myMatrix, 0, angle, x, y, z);
	}

	// 设置沿x、y、z轴缩放
	public void scale(float x, float y, float z)
	{
		Matrix.scaleM(myMatrix, 0, x, y, z);
	}

	private float[] myRevertMatrix = new float[16];
	private float[] myTransposeRevertMatrix  = new float[16];

	// 计算一次逆矩阵和逆转置矩阵
	public void calcRevert()
    {
        Matrix.invertM(myRevertMatrix, 0, myMatrix, 0);
        Matrix.transposeM(myTransposeRevertMatrix, 0, myRevertMatrix, 0);//求转置矩阵
    }
}
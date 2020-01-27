package com.bn.Sample5_8;//声明包
import java.util.ArrayList;
import java.util.List;

import static com.bn.Sample5_8.Constant.*;
//场景类
public class Scene {
	Camera cam;//摄像机
	Light light;//光源
	Ray feeler = new Ray();//阴影探测器
	List<HitObject> hitObjects;//物体列表

	// 场景中的物体
	private Ball ball1;		// 红色球
	private Ball ball2;		// 蓝色球
	private Square sqare;	// 矩形平面
	
	public Scene(Camera cam, Light light)
	{
		this.cam   = cam;	// 初始化摄像机
		this.light = light;	// 初始化光源

		hitObjects = new ArrayList<HitObject>();			// 初始化物体列表

		ball1 = new Ball(cam, new Color3f(BALL1_COLOR));	// 创建一个红色的球
		ball2 = new Ball(cam, new Color3f(BALL2_COLOR));	// 创建一个蓝色的球
		sqare = new Square(cam, new Color3f(PLANE_COLOR));	// 创建一个绿色的平面
		
								// 将物体加入场景中
		hitObjects.add(ball1);	// 将红色球加入物体列表
		hitObjects.add(ball2);	// 将蓝色球加入物体列表
		hitObjects.add(sqare);	// 将绿色矩形平面加入物体列表
		
	}
	
	// 对场景中的物体进行变换，以摆放到位
	public void transform()
	{

		// 为所有物体初始化变换矩阵
		for(HitObject pObj:hitObjects){
			pObj.initMyMatrix();
		}
		
		// 旋转矩形平面 (模型坐标系下是 XOY 平面)
		sqare.rotate(-90, 1, 0, 0);
		sqare.scale(PLANE_WIDTH/2.0f, PLANE_HEIGHT/2.0f, 1);//缩放矩形平面
		sqare.calcRevert(); // 计算逆和逆转置矩阵
		
		// 设置球1的变换
		ball1.translate(-CENTER_DIS, R, 0);//平移红色球
		ball1.scale(R, R, R);//缩放红色球
		ball1.calcRevert();
		
		// 设置球2的变换
		ball2.translate(CENTER_DIS, R, 0);//平移蓝色球
		ball2.scale(R, R, R);//缩放蓝色球
		ball2.calcRevert();
	}
	
	/*
	 * 返回光线对应的像素各信息,
	 * 
	 * 返回值：
	 * -1表示没有交点，
	 * 0表示有交点，且最佳碰撞点不在阴影中
	 * 1表示有交点，且最佳碰撞点在阴影中
	 */
	public int shade(//进行光线跟踪计算的方法
			Ray ray, //光线
			Color3f color, //第一交点的颜色
			Point3 vetex, //第一交点的位置
			Vector3 normal//第一交点的法向量
	){
		Intersection best = new Intersection();	// 用于保存到目前为止最近的交点
		getFirstHit(ray, best);					// 计算出第一交点
		if(best.numHits==0){
			return -1;							// 这个追踪的光线没有碰到场景任何物体,也就是可以直接使用大背景颜色，也就不用渲染
		}

		// 如果有物体与光线相交返回碰撞点的各信息
		color.set(best.hit[0].hitObject.getColor());// 交点处物体的颜色
		vetex.set(best.hit[0].hitPoint);			// 交点的位置

		// 通过逆转置变换，求变换之后的法向量
		Vector3 preN = best.hit[0].hitNormal;					// 变换前的法向量
		best.hit[0].hitObject.xfrmNormal(normal, preN);			// 从模型坐标系的法线 通过逆转置矩阵 求变换后的法线
		
		// 取出交点坐标
		Point3 hitPoint = best.hit[0].hitPoint;
		// 阴影探测器的起点为：将交点朝人眼方向移动一个微小的距离 ray是摄像头光线追踪直线 交点沿着摄像头跟踪光线往近处走一点
		feeler.start.set(hitPoint.minus(ray.dir.multiConst(MNIMUM)));
		//feeler.start.set(hitPoint); // 也会产生自身阴影??
		// 阴影探测器的方向，从碰撞点指向光源
		feeler.dir = light.pos.minus(hitPoint);
		if(isInShadow(feeler)){				//	交点在阴影中返回1
			return 1;						//  有交点，且最佳碰撞点在阴影中
		}
		return 0;// 交点不在阴影中返回0
	}
	
	public void getFirstHit(Ray ray, Intersection best){//计算出此条光线与物体的最近交点

		Intersection inter = new Intersection();//创建交点列表对象

		best.numHits=0;//初始时交点数为0
		/*
		 * 此处检测光线与每个物体是否相交，
		 * 与每个物体相交的信息都会存储在best中。
		 * 由于光线与单个物体相交时，
		 * 总会将光线与该物体的最近相交点保存在best.hit[0]中(由每个物体的hit方法决定)，
		 * 因此只要将所有物体的“最近点”信息做比较，并将最终结果存入best.hit[0]中，
		 * 即可得出光线与所有物体的最近的交点信息
		 */
		for(HitObject pObj:hitObjects){	// 遍历场景中的每一个物体

			if(!pObj.hit(ray, inter)){	// 光线是否与当前物体相交
				continue;				// 无交点则检测下一个物体
			}

			// 若best中还没有交点信息，或best中的交点不是最近点
			if(best.numHits==0 ||
					inter.hit[0].hitTime<best.hit[0].hitTime){
				/*
				 * 注意这里一定是复制一份，而不能直接给其引用，
				 * 否则里面的值一变会导致best的值也变！
				 */
				best.set(inter);// 将当前交点信息传入best
			}
		}
	}

	// 检测交点是否在阴影中的方法，入口参数为阴影探测器对应光线
	public boolean isInShadow(Ray feeler)
	{
		for(HitObject pObj:hitObjects){	// 遍历所有物体
			if(pObj.hit(feeler)){		// 若光线被任何物体挡住，则在阴影中
				return true;
			}
		}
		return false;					// 否则不在阴影中
	}
}
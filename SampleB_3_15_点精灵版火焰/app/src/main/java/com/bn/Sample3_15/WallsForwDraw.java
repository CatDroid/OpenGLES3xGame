package com.bn.Sample3_15;

public class WallsForwDraw {

	private Wall wall; // hhl 是一个矩形绘制 作为墙壁 带有环境光和散射光 法向量 没有镜面光

	WallsForwDraw(MySurfaceView mv) {
		wall=new Wall(mv,ParticleDataConstant.wallsLength);
	}

	// hhl 6个墙壁 分别位于 x=-wallsLength~wallsLength z=-wallsLength~wallsLength y=0~2*wallsLength
	void drawSelf()
	{
		//绘制第一面墙-底
		MatrixState.pushMatrix();
		wall.drawSelf(ParticleDataConstant.walls[0]);
		MatrixState.popMatrix();
		
		//绘制第二面墙-上
		MatrixState.pushMatrix();
		MatrixState.translate(0,2*ParticleDataConstant.wallsLength, 0);
		MatrixState.rotate(180,1,0,0);
		wall.drawSelf(ParticleDataConstant.walls[1]);
		MatrixState.popMatrix();
		
		//绘制第三面墙-右  hhl: 注意 虽然关闭了CULL_FACE 但是墙绘制时有根据关照，所以法向量的方向很重要，不对的话可能是黑色的
		MatrixState.pushMatrix();
		MatrixState.translate(ParticleDataConstant.wallsLength, ParticleDataConstant.wallsLength ,0);
		MatrixState.rotate(90, 0, 0, 1); // 这个翻转会影响法向量
		MatrixState.rotate(-90, 0, 1, 0); // hhl 贴图也是有方向的
		wall.drawSelf(ParticleDataConstant.walls[2]);
		MatrixState.popMatrix();
		
		//绘制第四面墙-左
		MatrixState.pushMatrix();
		MatrixState.translate(-ParticleDataConstant.wallsLength, ParticleDataConstant.wallsLength, 0);
		MatrixState.rotate(-90, 0, 0, 1);
		MatrixState.rotate(90, 0, 1, 0);
		wall.drawSelf(ParticleDataConstant.walls[3]);
		MatrixState.popMatrix();

		//绘制第五面墙-前
		MatrixState.pushMatrix();
		MatrixState.translate(0, ParticleDataConstant.wallsLength,ParticleDataConstant.wallsLength);
		MatrixState.rotate(-90, 1, 0, 0);
		MatrixState.rotate(180, 0, 1, 0);
		wall.drawSelf(ParticleDataConstant.walls[4]);
		MatrixState.popMatrix();

		//绘制第六面墙-后
		MatrixState.pushMatrix();
		MatrixState.translate(0, ParticleDataConstant.wallsLength,-ParticleDataConstant.wallsLength);
		MatrixState.rotate(90, 1, 0, 0);
		wall.drawSelf(ParticleDataConstant.walls[5]);
		MatrixState.popMatrix();
	}

}

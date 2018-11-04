#version 300 es
uniform mat4 uMVPMatrix; //总变换矩阵
in vec3 aPosition;  //顶点位置
in vec2 aTexCoor;    //顶点纹理坐标
out vec2 vTextureCoord;  //用于传递给片元着色器的变量
uniform float ratio;// 当前整体扭动角度因子 hhl  就是每单位长度 旋转的弧度
void main()
{       
	float pi = 3.1415926; //圆周率
	float centerX=0.0;//中心点的X坐标
	float centerY=-5.0;//中心点的Y坐标  hhl 中心点其实是 triangle_edgeLength/2/Math.cos(Math.PI/6) = 8.0f/2/cos30 = 4.6 应该在(0,-4.6,0)
	float currX = aPosition.x;//当前点的x坐标 hhl 物体的中心点不在物体坐标系的原点
	float currY = aPosition.y;//当前点的y坐标
	float spanX = currX - centerX;//当前x偏移量
	float spanY = currY - centerY;//当前y偏移量
	float currRadius = sqrt(spanX * spanX + spanY * spanY);//计算距离
	float currRadians;//当前点与x轴正方向的夹角
	if(spanX != 0.0){ // 一般情况
	    currRadians = atan(spanY , spanX);
	    // atan 这个反正切 范围是-pi到pi，考虑到点位于哪个限象，跟C++的atan2一样
//	    if(spanX < 0.0 ){
//	         currRadians += pi;
//	    }
	}
	else {// 避免在90或者-90度的时候 atan计算无穷大
		currRadians = spanY > 0.0 ? pi/2.0 : 3.0*pi/2.0; 
	}
	float resultRadians = currRadians + ratio*currRadius;//计算出扭曲后的角度  hhl  ratio=最大旋转角/最大半径
	float resultX = centerX + currRadius * cos(resultRadians);//计算结果点的x坐标
	float resultY = centerY + currRadius * sin(resultRadians);//计算结果点的y坐标
	//构造结果点，并根据总变换矩阵计算此次绘制此顶点的位置
    gl_Position = uMVPMatrix * vec4(resultX,resultY,0.0,1); 
    vTextureCoord = aTexCoor;//将接收的纹理坐标传递给片元着色器
}      
                     
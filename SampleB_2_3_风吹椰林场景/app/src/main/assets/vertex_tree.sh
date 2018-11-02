#version 300 es
//这里是树干节点的顶点着色器
//这里的所有的偏转角都是相对于Z轴正方向来说的，逆时针旋转的
uniform mat4 uMVPMatrix; //总变换矩阵
uniform float bend_R;//这里指的是树的弯曲半径
uniform float direction_degree;//用角度表示的风向，沿Z轴正方向逆时针旋转
in vec3 aPosition;  //顶点位置
in vec2 aTexCoor;    //顶点纹理坐标
out vec2 vTextureCoord;  //用于传递给片元着色器的纹理坐标
void main()     
{      
   	//计算当前的弧度
	float curr_radian=aPosition.y/bend_R;
	//计算当前点变换后的Y坐标
	float result_height=bend_R*sin(curr_radian);
	//计算当前点的增加的长度
	float increase=bend_R-bend_R*cos(curr_radian);
	//计算当前点最后的x坐标
	float result_X=aPosition.x+increase*sin(radians(direction_degree));
	//计算当前点最后的z坐标
	float result_Z=aPosition.z+increase*cos(radians(direction_degree));
	//最后结果顶点的坐标
	vec4 result_point=vec4(result_X,result_height,result_Z,1.0);
    gl_Position = uMVPMatrix * result_point; //根据总变换矩阵计算此次绘制此顶点位置
   	vTextureCoord = aTexCoor;//将接收的纹理坐标传递给片元着色器
}



                      
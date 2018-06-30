#version 300 es
uniform mat4 uMVPMatrix; //总变换矩阵
in vec3 aPosition;  //顶点位置
in vec3 aNormal;    //顶点法向量

void main()     
{ 
	vec3 position=aPosition;//获取此顶点位置

	// hhl 将法向量 物体坐标空间 转化到 标准设备空间x,y,z(-1,1) ?????
	vec4 ydskj      =   uMVPMatrix*vec4(0,0,0,1);
	vec4 fxldskj    =   uMVPMatrix*vec4(aNormal.xyz,1.0); // 法向量的齐次坐标
	vec2 skjNormal  =   fxldskj.xy - ydskj.xy;  // hhl ??? 只用x y ??? 后面把z坐标直接设置为1.0

	skjNormal=normalize(skjNormal);

	vec4 finalPosition=uMVPMatrix * vec4(position.xyz,1);
	finalPosition=finalPosition/finalPosition.w; // hhl MVP之后的顶点坐标是齐次坐标，要做透视除法，才能得到最后标准设备空间的坐标

   	// gl_Position =finalPosition+vec4(skjNormal.xy,1.0,1.0)*0.01;//根据总变换矩阵计算此次绘制此顶点位置  扩展的长度是0.01

   	gl_Position = vec4(finalPosition.xyz + vec3(skjNormal.xy,1.0)*0.01,1.0); // 管线不用做透视除法，因为w=1
}                    
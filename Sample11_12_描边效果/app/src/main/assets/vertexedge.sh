#version 300 es
uniform mat4 uMVPMatrix; //总变换矩阵
in vec3 aPosition;  //顶点位置
in vec3 aNormal;    //顶点法向量

void main()     
{ 
	vec3 position=aPosition;        // 获取此顶点位置
	position.xyz += aNormal*0.4;    // 进行顶点挤出
	
   	gl_Position = uMVPMatrix * vec4(position.xyz,1);//根据总变换矩阵计算此次绘制此顶点位置  
}                    
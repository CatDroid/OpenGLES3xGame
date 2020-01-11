#version 300 es
precision mediump float;
uniform mat4 uMMatrix;      //变换矩阵
uniform vec3 uLightLocation;//光源位置
uniform vec3 uSkyColor;	    //天空颜色
uniform vec3 uGroundColor;  //大地颜色
in vec3 vNormal;            //接收从顶点着色器过来的顶点法向量
in vec3 vPosition;          //接收从顶点着色器过来的顶点位置

out vec4 fragColor;         //输出到的片元颜色

void main()                         
{
	// 计算变换后的法向量
	vec3 normalTarget=vPosition+normalize(vNormal);
  	vec3 newNormal=(uMMatrix*vec4(normalTarget,1)).xyz-(uMMatrix*vec4(vPosition,1)).xyz;

  	// 计算从表面点到光源位置的向量
  	vec3 position=(uMMatrix*vec4(vPosition,1)).xyz;
	vec3 lightVec=normalize(uLightLocation-position);   // 光源向量


	float costheta=dot(newNormal,lightVec);             // 计算法向量和表面点到光源位置的向量的点乘
	float a=costheta*0.5+0.5;                           // 计算a值
	vec3 vColor=mix(uGroundColor,uSkyColor,a);          // 计算此片元的光照颜色值RGB-即漫反射颜色
	
	fragColor=vec4(vColor,1.0);//此片元的最终颜色
}
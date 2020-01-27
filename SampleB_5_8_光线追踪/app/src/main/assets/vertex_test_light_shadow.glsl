#version 300 es
uniform mat4 uMVPMatrix; //总变换矩阵
uniform int isShadow;//阴影绘制标志
uniform vec3 uLightLocation;	//光源位置
uniform mat4 uMMatrix; //变换矩阵
uniform mat4 uMProjCameraMatrix; //投影、摄像机组合矩阵

in vec3 aPosition;  //顶点位置
in vec3 aNormal;    //法向量

out vec3 vPosition;//用于传递给片元着色器的顶点位置
out vec3 vNormal;//用于传递给片元着色器的顶点法向量

void main()     
{
   if(isShadow==1)
   {//绘制本影，计算阴影顶点位置
      vec3 A=vec3(0.0,0.0,0.0);//投影平面上任一点坐标
      vec3 n=vec3(0.0,1.0,0.0);//投影平面法向量
      vec3 S=uLightLocation; //光源位置
      vec3 V=(uMMatrix*vec4(aPosition,1)).xyz;  //经过平移和旋转变换后的点的坐标    
      vec3 VL=S+(V-S)*(dot(n,(A-S))/dot(n,(V-S)));//求得的投影点坐标
      gl_Position = uMProjCameraMatrix*vec4(VL,1); //根据总变换矩阵计算此次绘制此顶点位置   
   }
   else
   {
	  gl_Position = uMVPMatrix * vec4(aPosition,1); //根据总变换矩阵计算此次绘制此顶点位置
   }
   //将顶点的位置传给片元着色器
   vPosition = aPosition; 
   //将顶点的法向量传给片元着色器
   vNormal = aNormal;
}
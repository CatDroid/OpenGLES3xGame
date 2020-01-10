#version 300 es
//真正带光照绘制的顶点着色器
uniform mat4 uMMatrix; //变换矩阵
uniform vec3 uLightLocation;	//光源位置
uniform mat4 uMProjCameraMatrix; //投影、摄像机组合矩阵
in vec3 aPosition;  //顶点位置

void main()     
{ 
      //绘制本影，计算阴影顶点位置
      vec3 A=vec3(0.0,0.1,0.0);//投影平面上任一点坐标
      vec3 n=vec3(0.0,1.0,0.0);//投影平面法向量
      vec3 S=uLightLocation; //光源位置     
      vec3 V=(uMMatrix*vec4(aPosition,1)).xyz;  //经过平移和旋转变换后的点的坐标    
      vec3 VL=S+(V-S)*(dot(n,(A-S))/dot(n,(V-S)));//求得的投影点坐标
      gl_Position = uMProjCameraMatrix*vec4(VL,1); //根据总变换矩阵计算此次绘制此顶点位置   
}                      
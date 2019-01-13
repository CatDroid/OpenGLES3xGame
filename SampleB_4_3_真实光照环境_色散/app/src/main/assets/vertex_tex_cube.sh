#version 300 es
uniform mat4 uMVPMatrix; 		//总变换矩阵
uniform mat4 uMMatrix; 			//变换矩阵
uniform vec3 uCamera;			//摄像机位置
in vec3 aPosition;  		//顶点位置
in vec3 aNormal;    		//顶点法向量
out vec3 eyeVary;			//用于传递给片元着色器的视线向量
out vec3 newNormalVary;		//用于传递给片元着色器的变换后法向量
void main()  { 
  gl_Position = uMVPMatrix * vec4(aPosition,1); //根据总变换矩阵计算此次绘制此顶点位置    
   //计算变换后的法向量并规格化
  vec3 normalTarget=aPosition+aNormal;
  vec3 newNormal=(uMMatrix*vec4(normalTarget,1)).xyz-(uMMatrix*vec4(aPosition,1)).xyz;
  newNormal=normalize(newNormal);  
  newNormalVary=newNormal;//将变换后的法向量传递给片元着色器
  //计算从观察点到摄像机的向量(视线向量)
  vec3 eye = normalize(uCamera-(uMMatrix*vec4(aPosition,1)).xyz);
  eyeVary=eye;//将视线向量传递给片元着色器
} 

#version 300 es
precision mediump float;
uniform samplerCube sTexture;//纹理内容数据
in vec3 eyeVary;		//接收从顶点着色器过来的视线向量
in vec3 newNormalVary;	//接收从顶点着色器过来的变换后法向量
out vec4 fragColor;//输出到的片元颜色
vec4 zs(					//根据法向量、视线向量及斯涅尔定律计算立方图纹理采样的方法
  in float zsl				//折射系数
){  
  vec3 vTextureCoord=refract(-eyeVary,newNormalVary,zsl);//根据斯涅尔定律计算折射后的视线向量
  vec4 finalColor=texture(sTexture, vTextureCoord);     
  return finalColor;
}
void main(){//主函数   折射系数 = 入射方的折射率  /   折射方的折射率  = sin(折射角) / sin(入射角)
   fragColor=zs(0.94); //以折射系数0.94调用zs方法完成片元颜色的计算
}    

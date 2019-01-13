#version 300 es
precision mediump float;
uniform samplerCube sTexture;//纹理内容数据
in vec3 eyeVary;		//接收从顶点着色器过来的视线向量
in vec3 newNormalVary;	//接收从顶点着色器过来的变换后法向量
out vec4 fragColor;     //输出到的片元颜色

vec4 zs(					// 根据法向量、视线向量及斯涅尔定律计算立方图纹理采样的方法
  in float zsl				// 折射率
){  
  vec3 vTextureCoord=refract(-eyeVary,newNormalVary,zsl);   // 根据斯涅尔定律计算折射后的视线向量
  vec4 finalColor=texture(sTexture, vTextureCoord);         // 进行立方图纹理采样
  return finalColor;
}
void main(){
   vec4 finalColor=vec4(0.0,0.0,0.0,0.0);

    /*
    shader中只需要对同一个材质，r，g，b三个通道设置不同的折射率，

    同一个视线向量(入射角)，r/g/b不同通道由于折射率不同，
    在立方体贴图上的，采样位置就不一样，采样到的颜色也只取对应的r/g/b

    */

   // 由于有色散RGB三个色彩通道单独计算折射
   finalColor.r=zs(0.97).r;     // 计算红色通道的采样结果
   finalColor.g=zs(0.955).g;    // 计算绿色通道的采样结果
   finalColor.b=zs(0.94).b;     // 计算蓝色通道的采样结果
   fragColor=finalColor;        // 将最终片元颜色传递给管线
}    

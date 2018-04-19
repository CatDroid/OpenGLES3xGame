#version 300 es
precision mediump float;
in vec2 vTextureCoord;//接收从顶点着色器过来的参数
in vec4 ambient;
in vec4 diffuse;
in vec4 specular;
uniform sampler2D sTexture;//纹理内容数据
out vec4 fragColor;//输出到的片元颜色
void main()                         
{  
  //给此片元从纹理中采样出颜色值            
  vec4 finalColor = texture(sTexture, vTextureCoord); 
  //根据颜色值计算透明度
  finalColor.a=(finalColor.r+finalColor.g+finalColor.b)/3.0;
  //计算光照因素
  finalColor=finalColor*ambient+finalColor*specular+finalColor*diffuse;
  //给此片元颜色值 
  fragColor = finalColor;
}              
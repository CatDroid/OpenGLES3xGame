#version 300 es
precision mediump float;//给出浮点默认精度
in vec2 vTextureCoord;//接收从顶点着色器过来的参数
in vec4 vAmbient;
in vec4 vDiffuse;
in vec4 vSpecular;
out vec4 fragColor;
uniform sampler2D sTexture;//纹理内容数据
void main()                         
{  //月球着色器的main方法
  //给此片元从纹理中采样出颜色值            
  vec4 finalColor = texture(sTexture, vTextureCoord); 
  //给此片元颜色值 
  fragColor = finalColor*vAmbient+finalColor*vSpecular+finalColor*vDiffuse;
}              
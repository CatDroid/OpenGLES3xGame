#version 300 es
precision mediump float;
uniform float ColorCS;//颜色变换的参数
in vec2 vTextureCoord;//接收从顶点着色器过来的参数
uniform sampler2D sTexture;//纹理内容数据
uniform sampler2D adpmTexCoor;
out vec4 fragColor;
void main()                         
{  
  vec4 switchColor;
  vec4 finalColor;
  float bl=ColorCS/100.0;
  switchColor = texture(sTexture, vTextureCoord);
  finalColor  =vec4(switchColor.r,switchColor.g,switchColor.b,bl);
  //给此片元颜色值
 fragColor = finalColor;  
}   
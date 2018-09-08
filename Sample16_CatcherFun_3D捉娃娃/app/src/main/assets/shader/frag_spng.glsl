#version 300 es
precision mediump float;
uniform float CLStep;//颜色变换的参数
in vec2 vTextureCoord; //接收从顶点着色器过来的参数
uniform sampler2D sTexture;//纹理内容数据
out vec4 fragColor;
void main()                         
{    
   vec4 finalColor; 
   vec4 switchcolor;
   float f = CLStep/100.0; // CLStep=[5~30]  5%~30%   1.0+1.5*f  1.075~1.45
   //给此片元从纹理中采样出颜色值            
   switchcolor= texture(sTexture, vTextureCoord)*(1.0+1.5*f);  // CLpng.java SwitchThread.java
   finalColor=vec4(switchcolor.r,switchcolor.g,switchcolor.b,switchcolor.a*f);// 动态修改整体的透明度
   fragColor=finalColor;
}              
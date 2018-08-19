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
   float f=CLStep/100.0;     
   //给此片元从纹理中采样出颜色值            
   switchcolor= texture(sTexture, vTextureCoord)*(1.0+1.5*f); 
   finalColor=vec4(switchcolor.r,switchcolor.g,switchcolor.b,switchcolor.a*f);
   fragColor=finalColor;
}              
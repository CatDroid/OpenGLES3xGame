#version 300 es
precision highp float;
uniform sampler2D sTexture;//纹理内容数据
//接收从顶点着色器过来的参数
in vec4 ambient;
in vec4 diffuse;
in vec4 specular;
in vec2 vTextureCoord;
layout(location=0) out vec4 fragColor0;
layout(location=1) out float fragColor1;
void main()
{
   //将计算出的颜色给此片元
   vec4 finalColor=texture(sTexture, vTextureCoord);
   //给此片元颜色值
   fragColor0 = finalColor*ambient+finalColor*specular+finalColor*diffuse;
   fragColor1 = gl_FragCoord.z;
}
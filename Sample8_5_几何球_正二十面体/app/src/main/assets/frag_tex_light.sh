#version 300 es
precision mediump float;
uniform sampler2D sTexture;//纹理内容数据
in vec2 vTextureCoord; //接收从顶点着色器过来的参数
in vec4 vambient;
in vec4 vdiffuse;
in vec4 vspecular;
out vec4 fragColor;//输出的片元颜色
void main()                         
{
   //将计算出的颜色给此片元
   vec4 finalColor=texture(sTexture, vTextureCoord);
   //给此片元颜色值 
   fragColor = finalColor*vambient+finalColor*vspecular+finalColor*vdiffuse;//给此片元颜色值
}              
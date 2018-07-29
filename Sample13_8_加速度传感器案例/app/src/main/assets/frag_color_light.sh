#version 300 es
precision mediump float;
in  vec4 vaaColor; //接收从顶点着色器过来的参数
in vec4 vambient;
in vec4 vdiffuse;
in vec4 vspecular;
out vec4 fragColor;//输出到的片元颜色
void main()                         
{
   //将颜色给此片元
	vec4 finalColor = vaaColor;
   //给此片元颜色值 
   fragColor = finalColor*vambient+finalColor*vspecular+finalColor*vdiffuse;//给此片元颜色值
}              
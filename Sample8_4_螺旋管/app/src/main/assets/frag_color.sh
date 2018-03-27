#version 300 es
precision mediump float;
in  vec4 vaColor; //接收从顶点着色器过来的参数
out vec4 fragColor;//输出的片元颜色
void main()                         
{
	vec4 finalColor =vaColor;
	fragColor = finalColor;//给此片元颜色值
}              
#version 300 es
precision mediump float;
in vec4 aaColor; //接收从顶点着色器过来的参数
out vec4 fragColor;//输出到的片元颜色
void main()                         
{                       
   fragColor = aaColor;//给此片元颜色值
}              
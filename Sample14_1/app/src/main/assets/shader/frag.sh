#version 300 es
precision mediump float;
in vec4 DestinationColor; //接收从顶点着色器过来的参数
out vec4 fragColor;
void main()
{
    fragColor = DestinationColor; //给此片元从顶点着色器传过来的颜色值
}
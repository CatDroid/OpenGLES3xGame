#version 300 es
precision mediump float;
in  vec4 vColor; //接收从顶点着色器过来的参数
in vec3 vPosition;//接收从顶点着色器过来的顶点位置
layout (location = 0) out vec4 fragColor;    //顶点颜色
void main() {  
   fragColor = vColor;//给此片元颜色值
}
#version 300 es
precision mediump float;
uniform vec3 uColor;//粒子颜色
out vec4 fragColor;//输出的片元颜色
void main()                         
{
  //给此片元颜色值 
  fragColor = vec4(uColor,1.0);
}              
#version 300 es
precision mediump float;
in vec4 ambient;
in vec4 diffuse;
in vec4 specular;
out vec4 fragColor;
void main()                         
{
   vec4 mColor=vec4(0.763,0.657,0.614,0);
   fragColor = mColor*ambient+mColor*diffuse+mColor*specular;//给此片元颜色值
}   
#version 300 es
precision mediump float;
in vec4 ambient;
in vec4 diffuse;
in vec4 specular;
out vec4 fragColor;//输出到的片元颜色
void main()                         
{    
   vec4 finalColor=vec4(1.0,1.0,1.0,1.0);
   fragColor = finalColor*ambient+finalColor*specular+finalColor*diffuse;//给此片元颜色值
   
}   
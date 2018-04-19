#version 300 es
precision mediump float;
in vec4 ambient;
in vec4 diffuse;
in vec4 specular;
out vec4 fragColor;
void main()                         
{//绘制球本身，纹理从球纹理采样
	vec4 finalColor=vec4(1.0,1.0,1.0,0.0);//物体颜色
	fragColor = finalColor*ambient+finalColor*specular+finalColor*diffuse;//给此片元颜色值   
}              
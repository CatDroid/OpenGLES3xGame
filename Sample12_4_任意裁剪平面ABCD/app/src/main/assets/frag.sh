#version 300 es
precision mediump float;
//接收从顶点着色器过来的参数
in vec4 ambient;
in vec4 diffuse;
in vec4 specular;
in float u_clipDist;

out vec4 fragColor;//输出到的片元颜色
void main()                         
{    
	 if(u_clipDist < 0.0) discard; // hhl 增加discard

   //将计算出的颜色给此片元
   vec4 finalColor=vec4(0.95,0.95,0.95,1.0);   
   fragColor = finalColor*ambient+finalColor*specular+finalColor*diffuse;//给此片元颜色值
}   
#version 300 es
precision mediump float;	//给出浮点默认精度
out vec4 fragColor;//传入渲染管线的片元颜色
void main()                         
{//星空着色器的main方法
  //给此片元赋颜色值
  fragColor = vec4(1.0,1.0,1.0,1.0);
}                 
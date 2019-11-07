#version 300 es
precision highp float;
in vec4 vPosition;  //接收从顶点着色器过来的参数
out float fragColor;//输出到的片元颜色
uniform highp vec3 uLightLocation;	//光源位置
void main()                         
{   
   float dis=distance(vPosition.xyz,uLightLocation);   
   fragColor=dis;  
}    
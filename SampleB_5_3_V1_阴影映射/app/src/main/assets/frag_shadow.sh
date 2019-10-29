#version 300 es
precision highp float;//给出默认的浮点精度
in vec4 vPosition;  //接收从顶点着色器过来的顶点位置
out float fragColor;//输出到的片元颜色
uniform highp vec3 uLightLocation;//光源位置
void main()                         
{   
   float dis=distance(vPosition.xyz,uLightLocation);//计算被照射片元到光源的距离
   fragColor=dis;//给此片元最终颜色值 
}    
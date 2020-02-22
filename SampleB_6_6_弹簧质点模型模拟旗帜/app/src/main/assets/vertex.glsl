#version 300 es
uniform mat4 uMVPMatrix; //总变换矩阵
in vec3 aPosition;  //顶点位置
//attribute vec4 aColor;    //顶点颜色
in vec2 aTexCoor;    //顶点纹理坐标

//varying  vec4 vColor;  //用于传递给片元着色器的变量
out vec2 vTextureCoord;	//传给着色器的纹理坐标

void main()     
{                            		
   gl_Position = uMVPMatrix * vec4(aPosition,1); //根据总变换矩阵计算此次绘制此顶点位置
   //vColor = aColor;//将接收的颜色传递给片元着色器 
   vTextureCoord=aTexCoor;  
}                      
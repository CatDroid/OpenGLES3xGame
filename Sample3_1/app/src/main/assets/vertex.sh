#version 300 es
uniform mat4 uMVPMatrix; // 总变换矩阵
layout (location = 0) in vec3 aPosition;  // 顶点位置
layout (location = 1) in vec4 aColor;    // 顶点颜色
out  vec4 vColor;  // 用于传递给片元着色器的变量

void main()
{
   // 顶点着色器 也可以用 采样器 sampler2D    A*B  也可以用 [B^t * A^t]^t  由于 vec4 可以是列向量也可以是行向量 所以vec=B^t=B
   gl_Position = vec4(aPosition,1) * uMVPMatrix  ;  //  根据总变换矩阵计算此次绘制此顶点位置
   vColor = aColor;     //  将接收的颜色传递给片元着色器
}
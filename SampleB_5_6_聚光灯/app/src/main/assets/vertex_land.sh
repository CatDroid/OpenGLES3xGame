#version 300 es

// 真正带光照绘制的顶点着色器

uniform mat4 uMVPMatrix;    // 总变换矩阵
in vec3 aPosition;          // 顶点位置
in vec3 aNormal;            // 顶点法向量
out vec3 VaPosition;
out vec3 VaNormal;
void main()     
{
   gl_Position = uMVPMatrix * vec4(aPosition,1); // 根据总变换矩阵计算此次绘制此顶点位置
   VaPosition = aPosition;
   VaNormal = aNormal;
}                      
#version 300 es
uniform mat4 uMVPMatrix;            // 总变换矩阵
uniform mat4 uMMatrix;              // 变换矩阵
uniform float uUsingRGBATexture ;   // 使用RGBA纹理中r通道传递距离值 归一化到-1到1 同时不存在超过[-1,1]范围的
in vec3 aPosition;                  // 顶点位置
out vec4 vPosition;                 // 用于传递给片元着色器的变量

void main()
{

   gl_Position = uMVPMatrix * vec4(aPosition,1.0);    // 根据总变换矩阵计算此次绘制此顶点位置

   if (uUsingRGBATexture == 1.0)
   {
        vPosition = vec4(aPosition,1.0);                // 顶点坐标 (物体坐标系)
   }
   else
   {
        vPosition = uMMatrix * vec4(aPosition,1.0);     // 顶点坐标 (世界坐标系)
   }
}                      
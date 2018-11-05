#version 300 es
uniform mat4 uMVPMatrix;    // 总变换矩阵
in vec3 aPosition;          // 顶点位置
in vec2 aLongLat;           // 顶点经纬度
out vec2 mcLongLat;         // 用于传递给片元着色器的顶点经纬度
void main()     {
   gl_Position = uMVPMatrix * vec4(aPosition,1); // 根据总变换矩阵计算此次绘制此顶点位置
   mcLongLat=aLongLat;      // 将顶点的经纬度传给片元着色器
}                      
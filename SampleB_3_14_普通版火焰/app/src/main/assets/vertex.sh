#version 300 es
uniform mat4 uMVPMatrix;    // 总变换矩阵
uniform float maxLifeSpan;  // 最大允许生命期
in vec4 aPosition;      // 从渲染管线接收的顶点位置属性
in vec2 aTexCoor;       // 从渲染管线接收的纹理坐标
out vec2 vTextureCoord; // 用于传递给片元着色器的纹理坐标

out vec4 vPosition;     // 用于传递给片元着色器的顶点位置属性
out float sjFactor;     // 用于传递给片元着色器的总衰减因子/剩余生命占比

void main(){
   gl_Position = uMVPMatrix * vec4(aPosition.x, aPosition.y, 0.0, 1 );  // 根据总变换矩阵计算此次绘制此顶点位置 粒子只是位于XOY平面
   vTextureCoord = aTexCoor;                                            // 将接收的纹理坐标传递给片元着色器
   vPosition=vec4(aPosition.x,aPosition.y,0.0,aPosition.w);             // 计算顶点位置属性，并将其传递给片元着色器 vPosition.w用来判断当前粒子所有是否激活/否则所有片元透明无色
   sjFactor= (maxLifeSpan-aPosition.w) / maxLifeSpan;                   // 计算总衰减因子/剩余生命占比，并将其传递给片元着色器
}                      
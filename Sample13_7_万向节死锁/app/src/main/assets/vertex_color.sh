#version 300 es
uniform mat4 uMVPMatrix; //总变换矩阵
in vec3 aPosition;  //从渲染管线接收的顶点位置
out vec3 vPosition;  //用于传递给片元着色器的顶点位置
void main() {                            		
   gl_Position = uMVPMatrix * vec4(aPosition,1); //根据总变换矩阵计算此次绘制此顶点的位置
   vPosition=aPosition;//将顶点位置传递给片元着色器
}
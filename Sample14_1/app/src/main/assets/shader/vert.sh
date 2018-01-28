#version 300 es
uniform mat4 Modelview;//总变换矩阵
in vec3 Position;//顶点位置
in vec4 SourceColor;//顶点颜色
out vec4 DestinationColor;//用于传给片元着色器
void main()
{
    DestinationColor = SourceColor;//将接收的颜色数据传递给片元着色器
    gl_Position = Modelview * vec4(Position,1);//根据总变换矩阵计算此次绘制此顶点位置
}
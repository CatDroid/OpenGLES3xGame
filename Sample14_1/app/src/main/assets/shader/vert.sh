#version 300 es
uniform mat4 Modelview;//�ܱ任����
in vec3 Position;//����λ��
in vec4 SourceColor;//������ɫ
out vec4 DestinationColor;//���ڴ���ƬԪ��ɫ��
void main()
{
    DestinationColor = SourceColor;//�����յ���ɫ���ݴ��ݸ�ƬԪ��ɫ��
    gl_Position = Modelview * vec4(Position,1);//�����ܱ任�������˴λ��ƴ˶���λ��
}
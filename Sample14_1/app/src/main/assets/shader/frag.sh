#version 300 es
precision mediump float;
in vec4 DestinationColor; //���մӶ�����ɫ�������Ĳ���
out vec4 fragColor;
void main()
{
    fragColor = DestinationColor; //����ƬԪ�Ӷ�����ɫ������������ɫֵ
}
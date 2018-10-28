#version 300 es
precision mediump float;
uniform sampler2D sTexture;//纹理内容数据
in vec2 vTextureCoord;
out vec4 fragColor;
void main()
{
	fragColor=texture(sTexture, vTextureCoord);
}
#version 300 es
precision mediump float;
in vec2 vTextureCoord;
uniform sampler2D sTexture;//纹理内容数据
out vec4 fragColor;

void main()
{
	fragColor=texture(sTexture, vTextureCoord);
}
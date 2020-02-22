#version 300 es
precision mediump float;
//varying  vec4 vColor; //接收从顶点着色器过来的参数
in vec2 vTextureCoord;//接收从顶点着色器过来的参数
uniform sampler2D sTexture;//纹理内容数据
out vec4 fragColor;//输出的片元颜色
void main()                         
{          
	//vec4 finalColor = vColor;      
	vec4 finalColor =  texture(sTexture, vTextureCoord);    
	fragColor = finalColor;
}
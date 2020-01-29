#version 300 es
precision mediump float;

// 纹理内容数据
uniform sampler2D sTexture;

// 接收从顶点着色器过来的参数
in vec4 ambient;
in vec4 diffuse;
in vec4 specular;
in vec2 vTextureCoord;
// 给此片元颜色值
out vec4 fragColor;

void main()                         
{    

   vec4 finalColor=texture(sTexture, vTextureCoord);    

   fragColor = finalColor*ambient+finalColor*specular+finalColor*diffuse;

}   
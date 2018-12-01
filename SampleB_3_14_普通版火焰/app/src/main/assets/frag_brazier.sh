#version 300 es
precision mediump float;

uniform sampler2D sTexture;//纹理内容数据

in vec4 ambient;        // 接收从顶点着色器过来的参数
in vec4 diffuse;
in vec2 vTextureCoord;
out vec4 fragColor;     // 将计算出的颜色给此片元

void main(){
   vec4 finalColor=texture(sTexture, vTextureCoord);
   fragColor = finalColor*ambient + finalColor*diffuse;
}
#version 300 es
precision mediump float;
in vec2 vTextureCoord;//从顶点着色器传递过来的纹理坐标
uniform sampler2D sTexture1;//纹理内容数据1(仇磊照片)
uniform sampler2D sTexture2;//纹理内容数据2(夏学良照片)
uniform float uT;//混合比例因子
out vec4 fFragColor;//输出的片元颜色
void main() {           
    vec4 color1 = texture(sTexture1, vTextureCoord); 	 //从纹理1中采样出颜色值1 
    vec4 color2 = texture(sTexture2, vTextureCoord); 	//从纹理2中采样出颜色值2
    fFragColor = color1*(1.0-uT) + color2*uT;//按比例混合两个颜色值
}              
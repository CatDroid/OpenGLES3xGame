#version 300 es
precision mediump float;//设置默认精度
uniform sampler2D sTexture;//纹理内容数据
in vec4 ambient;//接收从顶点着色器过来的环境光最终强度
in vec4 diffuse;//接收从顶点着色器过来的散射光最终强度
in vec4 specular;//接收从顶点着色器过来的镜面光最终强度
in vec2 vTextureCoord;//接收从顶点着色器过来的纹理坐标
out vec4 fragColor;

void main()                         
{
   //将计算出的颜色给此片元
   vec4 finalColor=texture(sTexture, vTextureCoord);    
   //计算片元的最终颜色值
   fragColor = finalColor*ambient+finalColor*specular+finalColor*diffuse;
}   
#version 300 es
precision mediump float;
uniform sampler2D sTexture;//纹理内容数据
in vec2 vTextureCoord;//接收从顶点着色器过来的纹理坐标数据
out vec4 fragColor;//输出的片元颜色
void main()                         
{    
   //将计算出的颜色给此片元
   vec4 finalColor=texture(sTexture, vTextureCoord);
   //最终片元的颜色
   fragColor = finalColor;

}
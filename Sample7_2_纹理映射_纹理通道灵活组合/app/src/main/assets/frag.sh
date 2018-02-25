#version 300 es
precision mediump float;
uniform sampler2D sTexture;//纹理内容数据
in vec2 vTextureCoord; //接收从顶点着色器过来的参数
out vec4 fragColor;

void main()
{
   //进行纹理采样
   vec4 texColor = texture(sTexture, vTextureCoord);
   // 由于 纹理的绿色通道  映射到  采样器红色通道， 但是纹理的绿色通道 不变 还是映射到 采样器的绿色通道
   // 所以 采样器的绿色通道和红色通道 的值一样 !!
   //if ( texColor.r == texColor.g )
   //     fragColor = vec4(1,0,0,0);
   //else
        fragColor = texColor ;
}
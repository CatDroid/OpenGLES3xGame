#version 300 es
precision mediump float;
in vec2 vTextureCoord;//接收从顶点着色器过来的参数
uniform sampler2D sTexture;//纹理内容数据
layout(location=0) out vec4 fragColor0;
layout(location=1) out float fragColor1;
void main()                         
{           
   //给此片元从纹理中采样出颜色值            
   fragColor0=texture(sTexture,vTextureCoord); 
   fragColor1=gl_FragCoord.z; 
}  
#version 300 es
precision mediump float;
in vec2 vTextureCoord; //接收从顶点着色器过来的参数
out vec4 fragColor;
uniform sampler2D sTexture;//纹理内容数据
void main()                         
{           
   //进行纹理采样    
   fragColor = texture(sTexture, vTextureCoord); 
}              
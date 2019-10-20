#version 300 es
precision mediump float;
in vec2 vTextureCoord; //接收从顶点着色器过来的参数
uniform sampler2D sTexture;//纹理内容数据

out vec4 fragColor;
void main()                         
{           
   //给此片元从纹理中采样出颜色值    
   vec4 color=texture(sTexture, vTextureCoord);
   color.a=0.0; // 关闭了颜色混合 0.0和1.0没有区别，后续其他物体渲染的时候，一般使用的是src的alpha
   fragColor=color;  
}              
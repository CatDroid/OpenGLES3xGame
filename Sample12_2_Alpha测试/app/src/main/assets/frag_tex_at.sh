#version 300 es
precision mediump float;
in vec2 vTextureCoord; //接收从顶点着色器过来的参数
uniform sampler2D sTexture;//纹理内容数据

out vec4 fragColor;//输出到的片元颜色
void main() { 
   vec4 bcolor = texture(sTexture, vTextureCoord);//给此片元从纹理中采样出颜色值 
   if(bcolor.a<0.6) { // 如果0.0就是完全透明 就抛弃这个片元
   		discard;
   } else {
      fragColor=bcolor;
}}
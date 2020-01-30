#version 300 es
precision mediump float;							//给出默认的浮点精度
in vec2 vTextureCoord; 						//接收从顶点着色器过来的纹理坐标
uniform sampler2D sTextureGrass;					//纹理内容数据(草皮)
layout(location=0) out vec4 fragColor0;
layout(location=1) out float fragColor1;
void main(){
   vec4 gColor=texture(sTextureGrass, vTextureCoord); 	//从草皮纹理中采样出颜色
   fragColor0 = gColor; //给此片元最终颜色值    
   fragColor1 = gl_FragCoord.z;
}
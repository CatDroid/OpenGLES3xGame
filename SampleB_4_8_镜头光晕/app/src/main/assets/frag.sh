#version 300 es
precision mediump float;//给出默认的浮点精度
uniform vec4 color;		//实际颜色RGBA值
uniform sampler2D sTexture;//纹理内容数据
in vec2 vTextureCoord; //接收从顶点着色器过来的纹理坐标
out vec4 fragColor;//输出到片元的颜色
void main()                         
{ //主函数 
	vec4 finalColor=texture(sTexture, vTextureCoord);// 进行纹理采样
	finalColor=finalColor*color;    // 顶点颜色与实际颜色结合-实现弱着色
   	fragColor =finalColor;          // 片元的最终颜色
}
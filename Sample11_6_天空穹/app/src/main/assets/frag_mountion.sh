#version 300 es
precision mediump float;
in vec2 vTextureCoord; //接收从顶点着色器过来的参数
uniform sampler2D sTexture;//纹理内容数据
in float currY;
uniform sampler2D sTextureGrass;//纹理内容数据（草皮）
uniform sampler2D sTextureRock;//纹理内容数据（岩石）
uniform float landStartY;//陆地起始Y
uniform float landYSpan;//陆地Y偏移量

out vec4 fragColor;//输出到的片元颜色
void main()
{           
   	   float min=0.25;
	   float max=0.7;
	   
	   float currYRatio=(currY-landStartY)/landYSpan;
	   
	   vec4 gColor=texture(sTextureGrass, vTextureCoord); 
	   vec4 rColor=texture(sTextureRock, vTextureCoord); 
	   
	   vec4 finalColor;
	   
	   if(currYRatio<min)
	   {
	      finalColor=gColor;
	   }
	   else if(currYRatio>max)
	   {
	      finalColor=rColor;
	   }
	   else
	   {
	      float rockBL=(currYRatio-min)/(max-min);
	      finalColor=rockBL*rColor+(1.0-rockBL)*gColor;
	   }
	   //给此片元从纹理中采样出颜色值            
	   fragColor = finalColor; 
}              
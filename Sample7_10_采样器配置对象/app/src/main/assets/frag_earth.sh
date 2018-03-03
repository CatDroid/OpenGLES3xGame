#version 300 es      
precision mediump float;
in vec2 vTextureCoord;//接收从顶点着色器过来的参数
in vec4 vAmbient;			//接收从顶点着色器过来环境光最终强度
in vec4 vDiffuse;			//接收从顶点着色器过来散射光最终强度
in vec4 vSpecular;			//接收从顶点着色器过来镜面反射光最终强度
out vec4 fragColor;			//传递到渲染管线的片元颜色
uniform sampler2D sTextureDay;	//白天纹理的内容数据
uniform sampler2D sTextureNight;//黑夜纹理的内容数据
void main()                         
{  //地球着色器的main方法
	vec4 finalColorDay;  		//从白天纹理中采样出颜色值
	vec4 finalColorNight;   	//从夜晚纹理中采样出颜色值
  
  finalColorDay= texture(sTextureDay, vTextureCoord);//采样出白天纹理的颜色值
  finalColorDay = finalColorDay*vAmbient+finalColorDay*vSpecular+finalColorDay*vDiffuse;
  finalColorNight = texture(sTextureNight, vTextureCoord);  //采样出夜晚纹理的颜色值
  finalColorNight = finalColorNight*vec4(0.5,0.5,0.5,1.0);//计算出的该片元夜晚颜色值
  
  if(vDiffuse.x>0.21)
  {//当散射光分量大于0.21时
    fragColor=finalColorDay;     //采用白天纹理 
  } 
  else if(vDiffuse.x<0.05)
  {     //当散射光分量小于0.05时
     fragColor=finalColorNight;//采用夜间纹理
  }
  else
  {//当环境光分量大于0.05小于0.21时，为白天夜间纹理的过渡阶段
     float t=(vDiffuse.x-0.05)/0.16;//计算白天纹理应占纹理过渡阶段的百分比
     fragColor=t*finalColorDay+(1.0-t)*finalColorNight;//计算白天黑夜过渡阶段的颜色值
  }  
}              
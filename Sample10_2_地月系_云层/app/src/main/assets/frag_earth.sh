#version 300 es
//地球着色器
precision mediump float;
in vec2 vTextureCoord;//接收从顶点着色器过来的参数
in vec4 vAmbient;
in vec4 vDiffuse;
in vec4 vSpecular;
uniform sampler2D sTextureDay;//纹理内容数据
uniform sampler2D sTextureNight;//纹理内容数据
out vec4 fragColor;//输出到的片元颜色
void main()                         
{  
  //给此片元从纹理中采样出颜色值   
  vec4 finalColorDay;   
  vec4 finalColorNight;   
  
  finalColorDay= texture(sTextureDay, vTextureCoord);
  finalColorDay = finalColorDay*vAmbient+finalColorDay*vSpecular+finalColorDay*vDiffuse;
  finalColorNight = texture(sTextureNight, vTextureCoord); 
  finalColorNight = finalColorNight*vec4(0.5,0.5,0.5,1.0);
  
  if(vDiffuse.x>0.21)
  {
    fragColor=finalColorDay;    
  } 
  else if(vDiffuse.x<0.05)
  {     
     fragColor=finalColorNight;
  }
  else
  {
     float t=(vDiffuse.x-0.05)/0.16;
     fragColor=t*finalColorDay+(1.0-t)*finalColorNight;
  }  
}              
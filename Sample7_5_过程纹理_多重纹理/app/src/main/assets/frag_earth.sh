#version 300 es      
precision mediump float;	//给出浮点默认精度
in vec2 vTextureCoord;//接收从顶点着色器过来的参数
in vec4 vAmbient;			//接收从顶点着色器过来环境光最终强度
in vec4 vDiffuse;			//接收从顶点着色器过来散射光最终强度
in vec4 vSpecular;			//接收从顶点着色器过来镜面反射光最终强度

out vec4 fragColor;			    //传递到渲染管线的片元颜色
uniform sampler2D sTextureDay;	//白天纹理的内容数据
uniform sampler2D sTextureNight;//黑夜纹理的内容数据

void main()                         
{  //地球着色器的main方法
	vec4 finalColorDay;  		//从白天纹理中采样出颜色值
	vec4 finalColorNight;   	//从夜晚纹理中采样出颜色值

  
  finalColorDay= texture(sTextureDay, vTextureCoord);       //采样出白天纹理的颜色值
  finalColorDay = finalColorDay*vAmbient + finalColorDay*vSpecular +  finalColorDay*vDiffuse;


  finalColorNight = texture(sTextureNight, vTextureCoord);  //采样出夜晚纹理的颜色值
  finalColorNight = finalColorNight*vec4(0.5,0.5,0.5,1.0);//计算出的该片元夜晚颜色值
  
  if(vDiffuse.x>0.21)           // 当散射光分量大于0.21时  Mark.4 vertex_earth.sh中 散射光的各个分量的初始强度都是1.0 散射光模型是 max(0,cos<法向量，光源>)*初始强度
  {
    fragColor=finalColorDay;    // 采用白天纹理
  } 
  else if(vDiffuse.x<0.05)      // 当散射光分量小于0.05时  Mark.5 特别是 入射光与法向量 夹角大于90度 cos值为负数 结果Max为0  )
  {
     fragColor=finalColorNight; // 采用夜间纹理
  }
  else                          // 当散射光分量 大于0.05小于0.21时，为白天夜间纹理的过渡阶段
  {                             //                      Mark.6 法向量与光源线夹角  白天边界acos(0.21)->77.87度   黑夜边界acos(0.05)->87.13度

     float t=(vDiffuse.x-0.05)/0.16;                    //计算白天纹理应占纹理过渡阶段的百分比  0.16 = 0.21 - 0.05
     fragColor=t*finalColorDay+(1.0-t)*finalColorNight; //计算白天黑夜过渡阶段的颜色值
  }  
}

/*
Mark.1 片元着色器 使用多个sampler2D对同一物体渲染 就是多重纹理

Mark.2 片元着色器 使用多个sampler2D渲染时候 计算 (1-%)*a + %*a 就是过程纹理

Mark.3 地球 根据 散射光最终强度 决定使用白天纹理还是黑夜纹理 还是在过渡中的过程纹理 (1-%)*白天 + %*黑夜

*/
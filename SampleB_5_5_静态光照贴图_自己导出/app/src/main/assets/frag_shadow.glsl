#version 300 es
precision mediump float;				//给出默认的浮点精度
uniform highp int isShadow;			//阴影绘制标志
uniform sampler2D sTexture;//纹理内容数据
in vec4 diffuse;					//从顶点着色器传递过来的散射光最终强度
in vec4 specular;				//从顶点着色器传递过来的镜面光最终强度
in vec2 vTextureCoord;//从顶点着色器传递过来的纹理坐标数据
out vec4 fragColor;//输出到的片元颜色
void main() { 
   	if(isShadow==0){						//绘制物体本身
	    //根据纹理坐标采样出片元颜色值
		vec4 finalColor=texture(sTexture, vTextureCoord);
   		//给此片元最终颜色值
   		fragColor =finalColor*specular+finalColor*diffuse;
   	}else{								//绘制阴影
	   fragColor = vec4(0.18,0.2,0.2,0.0);//片元最终颜色为阴影的颜色
   	}
}

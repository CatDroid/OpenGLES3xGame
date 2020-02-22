#version 300 es
precision mediump float;
in vec2 vTextureCoord;//接收从顶点着色器过来的参数
uniform highp int isShadow;			//阴影绘制标志
in vec4 ambient;  				//从顶点着色器传递过来的环境光最终强度
in vec4 diffuse;					//从顶点着色器传递过来的散射光最终强度
in vec4 specular;				//从顶点着色器传递过来的镜面光最终强度
uniform sampler2D sTexture;//纹理内容数据
out vec4 fragColor;//输出的片元颜色
void main()                         
{  
     if(isShadow==0){						//绘制物体本身
	    vec4 finalColor=texture(sTexture, vTextureCoord); //物体本身的颜色
	    //综合三个通道光的最终强度及片元的颜色计算出最终片元的颜色并传递给管线
	    fragColor = finalColor*ambient+finalColor*specular+finalColor*diffuse;
   	}else{								//绘制阴影
	   fragColor = vec4(0.2,0.2,0.2,0.0);//片元最终颜色为阴影的颜色
   	}
}       
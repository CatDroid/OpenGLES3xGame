#version 300 es
precision mediump float;
in vec4 ambient;							//从顶点着色器传递过来的环境光最终强度
in vec4 diffuse;								//从顶点着色器传递过来的散射光最终强度
in vec4 specular;							//从顶点着色器传递过来的镜面光最终强度
in float vFogFactor;							//从顶点着色器传递过来的雾化因子
out vec4 fragColor;//输出到的片元颜色
void main()                         
{
	vec4 objectColor=vec4(0.95,0.95,0.95,1.0);//物体颜色	hhl 物体颜色应该用纹理  雾的颜色应该uniform传入
	vec4 fogColor = vec4(0.97,0.76,0.03,1.0);//雾的颜色	
 	if(vFogFactor != 0.0){//如果雾因子为0，不必计算光照
		objectColor = objectColor*ambient+objectColor*specular+objectColor*diffuse;//计算光照之后物体颜色
		//fragColor = objectColor*vFogFactor + fogColor*(1.0-vFogFactor);//物体颜色和雾颜色插值计算最终颜色
		fragColor = mix(fogColor,objectColor,vFogFactor ); // mix是 x*(1-a)+y*a 而不是 x*a + y*(1-a)
	}else{
 	    fragColor=fogColor;
 	}
}
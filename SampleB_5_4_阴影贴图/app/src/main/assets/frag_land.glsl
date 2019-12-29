#version 300 es
precision mediump float;	
uniform sampler2D sTexture;		//纹理内容数据
in vec4 ambient;			//接收从顶点着色器传递过来的环境光参数
in vec4 diffuse;				//接收从顶点着色器传递过来的散射光参数
in vec4 specular;			//接收从顶点着色器传递过来的镜面光参数
in vec4 vPosition;  			//接收从顶点着色器传递过来的片元位置
uniform highp mat4 uMVPMatrixGY; //光源位置处虚拟摄像机观察及投影组合矩阵 
out vec4 fragColor;
void main()
{

   vec4 gytyPosition = uMVPMatrixGY * vec4(vPosition.xyz,1);
   gytyPosition = gytyPosition/gytyPosition.w;
   float s = gytyPosition.s*0.5 + 0.5;
   float t = gytyPosition.t*0.5 + 0.5;

//   if( t > 1.0)
//   {
//        fragColor = vec4(1., 0., 0., 1.);
//        return ;
//   }

   vec4 finalcolor = vec4(0.8,0.8,0.8,1.0);
   vec4 colorA = finalcolor*ambient+finalcolor*specular+finalcolor*diffuse;
   vec4 colorB = vec4(0.1,0.1,0.1,0.0);
   if(s >= 0.0 && s <= 1.0 && t >= 0.0 && t <= 1.0)
   {
		vec4 projColor = texture(sTexture, vec2(s,t));
		float a = step(0.9999, projColor.r); // 本来阴影图是没有过渡的 纹理使用线性采样的话,就会有过渡
        float b = step(0.0001, projColor.r);
        float c = 1.0 - sign(a);
		fragColor = a*colorA
		            + (1.0 - b)*colorB
		            + b*c*mix(colorB, colorA, smoothstep(0.0, 1.0, projColor.r));
   }
   else
   {

        fragColor = colorA;
   }
}     

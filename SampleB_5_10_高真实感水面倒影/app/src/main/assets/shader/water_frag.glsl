#version 300 es
precision mediump float;
uniform highp mat4 uMVPMatrixMirror;    // 镜像摄像机观察及投影组合矩阵
uniform sampler2D sTextureDY;           // 倒影纹理内容数据
uniform sampler2D sTextureWater;        // 水面自己纹理内容数据
uniform sampler2D sTextureNormal;		// 纹理内容数据（法线）
uniform vec3 uCamera;				    // 摄像机位置
uniform vec3 uLightLocation;		    // 光源位置
in mat4 vMMatrix; 				        // 变换矩阵

in vec4 vPosition;
in vec2 vTextureCoord;                  // 接收从顶点着色器过来的参数
in vec3 fNormal;    			        // 接收从顶点着色器传递过来的法向量
in vec3 mvPosition;  				    // 接收从顶点着色器传递过来的顶点位置

out vec4 fragColor;


void pointLight(					    // 定位光光照计算的方法
  in vec3 normal,				        // 法向量
  inout vec4 ambient,			        // 环境光最终强度
  inout vec4 diffuse,				    // 散射光最终强度
  inout vec4 specular,			        // 镜面光最终强度
  in vec3 lightLocation,			    // 光源位置
  in vec4 lightAmbient,			        // 环境光强度
  in vec4 lightDiffuse,			        // 散射光强度
  in vec4 lightSpecular			        // 镜面光强度
){
	ambient=lightAmbient;			//直接得出环境光的最终强度  
  	vec3 normalTarget=mvPosition+normal;	//计算变换后的法向量
  	vec3 newNormal=(vMMatrix*vec4(normalTarget,1)).xyz-(vMMatrix*vec4(mvPosition,1)).xyz;
  	newNormal=normalize(newNormal); 	//对法向量规格化
  	//计算从表面点到摄像机的向量
  	vec3 eye= normalize(uCamera-(vMMatrix*vec4(mvPosition,1)).xyz);  
  	//计算从表面点到光源位置的向量vp
  	vec3 vp= normalize(lightLocation-(vMMatrix*vec4(mvPosition,1)).xyz);  
  	vp=normalize(vp);//格式化vp
  	vec3 halfVector=normalize(vp+eye);	//求视线与光线的半向量
  
	float nDotViewPosition=max(0.0,dot(newNormal,vp)); 	//求法向量与vp的点积与0的最大值
	diffuse=lightDiffuse*nDotViewPosition;				//计算散射光的最终强度
	
	float nDotViewHalfVector=dot(newNormal,halfVector);	//法线与半向量的点积   
	float shininess=50.0;								//粗糙度，越小越光滑  50
	float powerFactor=max(0.0,pow(nDotViewHalfVector,shininess)); //镜面反射光强度因子  
	
	specular=lightSpecular*powerFactor;    			//计算镜面光的最终强度
}
void main()                         
{    
	// 将片元的位置投影到镜像摄像机的近平面上
   vec4 gytyPosition = uMVPMatrixMirror * vec4(vPosition.xyz,1);
   gytyPosition = gytyPosition/gytyPosition.w;	    // 进行透视除法
   float s = (gytyPosition.s + 1.0)/2.0;			// 将投影后的坐标换算为纹理坐标
   float t = (gytyPosition.t + 1.0)/2.0;
   
   vec4 ambient,diffuse,specular;	                // 用来接收三个通道最终强度的变量
   pointLight(  normalize(fNormal),
                ambient, diffuse,specular,
                uLightLocation,
                vec4(0.9,0.9,0.9,1.0),
                vec4(0.1,0.1,0.1,1.0),
                vec4(0.9,0.9,0.9,1.0));
   	
   vec4 normalColor = texture(sTextureNormal, vTextureCoord); // 从法线纹理图中读出值

   vec3 cNormal = vec3(                             // 将值恢复到-1～+1范围
                2.0 * (normalColor.r-0.5),
                2.0 * (normalColor.g-0.5),
                2.0 * (normalColor.b-0.5) );        // cNormal.z (RGB B=1 蓝色 法线) 大部分都是 1

   cNormal = normalize(cNormal);                    // 将扰动结果向量规格化
   
   const float mPerturbationAmt = 0.02;			    // 扰动系数控制扭曲程度
   s = s * (1.0 + mPerturbationAmt * cNormal.x);	// 计算扰动后的纹理坐标S
   t = t * (1.0 + mPerturbationAmt * cNormal.y);	// 计算扰动后的纹理坐标T
   
   // 进行倒影纹理采样
   vec4 dyColor = texture(sTextureDY, vec2(s,t));

   // 进行水自身纹理采样
   vec4 waterColor = texture(sTextureWater,vTextureCoord);

   // 混合倒影与水自身得到此片元的颜色值 ，倒影纹理占70%，水面自身纹理占30%
   vec4 dyAndWaterColor = mix(waterColor, dyColor, 0.7);

   // 综合3个通道光的最终强度以及混合得到的颜色值计算出最终的颜色并传递给渲染管线
   fragColor = dyAndWaterColor*ambient + dyAndWaterColor*specular + dyAndWaterColor*diffuse;
}   
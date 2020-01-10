#version 300 es
uniform int isShadow;//阴影绘制标志
uniform mat4 uMVPMatrix; //总变换矩阵
uniform mat4 uMMatrix; //基本变换矩阵
uniform mat4 uMProjCameraMatrix; //投影、摄像机组合矩阵
uniform vec3 uLightLocation;	//光源位置
uniform vec3 uCamera;	//摄像机位置
in vec3 aPosition;  //顶点位置
in vec3 aNormal;    //顶点法向量
in vec2 aTexCoor;    //顶点纹理坐标
out vec4 diffuse;//传递给片元着色器的散射光最终强度
out vec4 specular; //传递给片元着色器的镜面光最终强度
out vec2 vTextureCoord; //传递给片元着色器的纹理坐标数据

void pointLight(
  in vec3 normal,
  inout vec4 diffuse,
  inout vec4 specular,
  in vec3 lightLocation,
  in vec4 lightDiffuse,
  in vec4 lightSpecular
){  
  vec3 normalTarget = aPosition + normal;
  vec3 newNormal = (uMMatrix*vec4(normalTarget,1)).xyz - (uMMatrix*vec4(aPosition,1)).xyz;
  newNormal = normalize(newNormal);

  vec3 eye = normalize(uCamera - (uMMatrix*vec4(aPosition,1)).xyz);

  vec3 vp = normalize(lightLocation - (uMMatrix*vec4(aPosition,1)).xyz);

  vec3 halfVector = normalize(vp + eye);	                // 求视线与光线的半向量
  float shininess = 50.0;				                    // 粗糙度，越小越光滑
  float nDotViewPosition = max(0.0, (dot(newNormal,vp) + 1.0)/2.0); // 求法向量与vp的点积与0的最大值
  diffuse = lightDiffuse * nDotViewPosition;				// 计算散射光的最终强度
  float nDotViewHalfVector = dot(newNormal,halfVector) ;	// 法线与半向量的点积
  float powerFactor = max(0.0, pow(nDotViewHalfVector,shininess)); 	// 镜面反射光强度因子
  specular = lightSpecular*powerFactor;    			        // 计算镜面光的最终强度
}

void main()     
{
   if(isShadow==1)// 绘制本影，计算阴影顶点位置
   {
      vec3 A=vec3(0.0,0.5,0.0); // 投影平面上任一点坐标  0.5 避免深度冲突
      vec3 n=vec3(0.0,1.0,0.0); // 投影平面法向量
      vec3 S=uLightLocation;    // 光源位置
      vec3 V=(uMMatrix*vec4(aPosition,1)).xyz;      // 经过平移和旋转变换后的点的坐标
      vec3 VL=S+(V-S)*(dot(n,(A-S))/dot(n,(V-S)));  // 求得的投影点坐标
      gl_Position = uMProjCameraMatrix*vec4(VL,1);  // 根据总变换矩阵计算此次绘制此顶点位置
   }
   else
   {
	  gl_Position = uMVPMatrix * vec4(aPosition,1); // 根据总变换矩阵计算此次绘制此顶点位置
   }
   vec4 diffuseTemp, specularTemp;                  // 散射光、镜面光的临时变量

   pointLight(  normalize(aNormal),                 // 计算光照
                diffuseTemp,
                specularTemp,
                uLightLocation,
                vec4(1.0,1.0,1.0,1.0),              // 漫反射强度
                vec4(0.3,0.3,0.3,1.0)
               );
   
   diffuse = diffuseTemp;
   specular = specularTemp;
   vTextureCoord = aTexCoor;//将接收的纹理坐标传递给片元着色器
}                      
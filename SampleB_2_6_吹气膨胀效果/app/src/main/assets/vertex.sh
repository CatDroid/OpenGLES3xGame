#version 300 es
uniform float uFatFactor;//接收的吹气膨胀系数
uniform mat4 uMVPMatrix; //总变换矩阵
uniform mat4 uMMatrix; //变换矩阵
uniform vec3 uLightLocation;	//光源位置
uniform vec3 uCamera;	//摄像机位置
in vec3 aPosition;  //顶点位置
in vec3 aNormal;    //顶点法向量
in vec2 aTexCoor;    //顶点纹理坐标
out vec4 ambient;//用于传递给片元着色器的变量的环境光最终强度
out vec4 diffuse;//用于传递给片元着色器的变量的散射光最终强度
out vec4 specular;//用于传递给片元着色器的变量的镜面光最终强度
out vec2 vTextureCoord;//用于传递给片元着色器的变量的纹理坐标
void pointLight(					//定位光光照计算的方法
  in vec3 normal,				//法向量
  inout vec4 ambient,			//环境光最终强度
  inout vec4 diffuse,				//散射光最终强度
  inout vec4 specular,			//镜面光最终强度
  in vec3 lightLocation,			//光源位置
  in vec4 lightAmbient,			//光源环境光强度
  in vec4 lightDiffuse,			//光源散射光强度
  in vec4 lightSpecular			//光源镜面光强度
){
  ambient=lightAmbient;			//直接得出环境光的最终强度  
  vec3 normalTarget=aPosition+normal;	//计算变换后的法向量
  vec3 newNormal=(uMMatrix*vec4(normalTarget,1)).xyz-(uMMatrix*vec4(aPosition,1)).xyz;
  newNormal=normalize(newNormal); 	//对法向量规格化
  //计算从表面点到摄像机的向量
  vec3 eye= normalize(uCamera-(uMMatrix*vec4(aPosition,1)).xyz);  
  //计算从表面点到光源位置的向量vp
  vec3 vp= normalize(lightLocation-(uMMatrix*vec4(aPosition,1)).xyz);  
  vp=normalize(vp);//格式化vp
  vec3 halfVector=normalize(vp+eye);	//求视线与光线的半向量    
  float shininess=50.0;				//粗糙度，越小越光滑
  float nDotViewPosition=max(0.0,dot(newNormal,vp)); 	//求法向量与vp的点积与0的最大值
  diffuse=lightDiffuse*nDotViewPosition;				//计算散射光的最终强度
  float nDotViewHalfVector=dot(newNormal,halfVector);	//法线与半向量的点积 
  float powerFactor=max(0.0,pow(nDotViewHalfVector,shininess)); 	//镜面反射光强度因子
  specular=lightSpecular*powerFactor;    			//计算镜面光的最终强度
}
void main()     
{ 
	//根据总变换矩阵计算此次绘制此顶点的位置，在计算时将顶点位置沿着法向量方向移动一定的距离
   gl_Position = uMVPMatrix * vec4(aPosition+aNormal*uFatFactor,1); 
   vec4 ambientTemp, diffuseTemp, specularTemp;   //环境光、散射光、镜面反射光的临时变量      
   pointLight(
                normalize(aNormal),
                ambientTemp,
                diffuseTemp,
                specularTemp,
                uLightLocation,
                vec4(0.15,0.15,0.15,1.0),
                vec4(0.9,0.9,0.9,1.0),
                vec4(0.4,0.4,0.4,1.0));

   ambient=ambientTemp;//将环境光最终强度传给片元着色器
   diffuse=diffuseTemp;//将散射光最终强度传给片元着色器
   specular=specularTemp;//将镜面光最终强度传给片元着色器
   vTextureCoord = aTexCoor;//将接收的纹理坐标传递给片元着色器
}                      
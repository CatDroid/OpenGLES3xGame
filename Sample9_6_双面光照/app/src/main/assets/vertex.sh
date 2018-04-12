#version 300 es
uniform mat4 uMVPMatrix; //总变换矩阵
uniform mat4 uMMatrix; //变换矩阵
uniform vec3 uLightLocation;	//光源位置
uniform vec3 uCamera;	//摄像机位置
in vec3 aPosition;  //顶点位置
in vec3 aNormal;    //顶点法向量
//用于传递给片元着色器的变量
out vec4 ambientZM;//用于传递给片元着色器的正面环境光最终强度
out vec4 diffuseZM;//用于传递给片元着色器的正面散射光最终强度
out vec4 specularZM;//用于传递给片元着色器的正面镜面反射光最终强度
out vec4 ambientFM;//用于传递给片元着色器的反面环境光最终强度
out vec4 diffuseFM;//用于传递给片元着色器的反面散射光最终强度
out vec4 specularFM;//用于传递给片元着色器的反面镜面反射光最终强度

//定位光光照计算的方法
void pointLight(					//定位光光照计算的方法
  in vec3 normal,				//法向量
  inout vec4 ambient,			//环境光最终强度
  inout vec4 diffuse,				//散射光最终强度
  inout vec4 specular,			//镜面光最终强度
  in vec3 lightLocation,			//光源位置
  in vec4 lightAmbient,			//环境光强度
  in vec4 lightDiffuse,			//散射光强度
  in vec4 lightSpecular			//镜面光强度
){
  ambient=lightAmbient;			//直接得出环境光的最终强度

  vec3 newNormal= mat3(uMMatrix)*normal;//计算变换后的法向量
  newNormal=normalize(newNormal);

  vec3 eye= normalize(uCamera-(uMMatrix*vec4(aPosition,1)).xyz);  //计算从表面点到摄像机的向量
  vec3 vp= normalize(lightLocation-(uMMatrix*vec4(aPosition,1)).xyz);  //计算从表面点到光源位置的向量vp


  float shininess=50.0;				                    //粗糙度，越小越光滑
  float nDotViewPosition=max(0.0,dot(newNormal,vp)); 	//求法向量与vp的点积与0的最大值
  diffuse=lightDiffuse*nDotViewPosition;				//计算散射光的最终强度

   vec3 halfVector=normalize(vp+eye);	                //求视线与光线的半向量
  float nDotViewHalfVector=dot(newNormal,halfVector);	//法线与半向量的点积 
  float powerFactor=max(0.0,pow(nDotViewHalfVector,shininess)); 	//镜面反射光强度因子
  specular=lightSpecular*powerFactor;    			                //计算镜面光的最终强度
}

void main()     
{//主函数 
   gl_Position = uMVPMatrix * vec4(aPosition,1); //根据总变换矩阵计算此次绘制此顶点位置  
   
   vec4 ambientTemp, diffuseTemp, specularTemp;   //存放环境光、散射光、镜面反射光的临时变量   

   // hhl 其实就是计算正反两面的法向量下 两面的最终光线   然后在片元着色器中判断片元是正面还是反面 反面就用反法向量的关照  由于还有深度测试 所以会有遮挡的!

   //进行正面光照计算
   pointLight(normalize(aNormal),ambientTemp,diffuseTemp,specularTemp,uLightLocation,vec4(0.1,0.1,0.1,1.0),vec4(0.7,0.7,0.7,1.0),vec4(0.3,0.3,0.3,1.0));
   ambientZM=ambientTemp;//将正面环境光最终强度传给片元着色
   diffuseZM=diffuseTemp;//将正面散射光最终强度传给片元着色
   specularZM=specularTemp;//将正面镜面反射光最终强度传给片元着色


   //进行反面光照计算
   pointLight(normalize(-aNormal),ambientTemp,diffuseTemp,specularTemp,uLightLocation,vec4(0.1,0.1,0.1,1.0),vec4(0.7,0.7,0.7,1.0),vec4(0.3,0.3,0.3,1.0));
   ambientFM=ambientTemp;//将反面环境光最终强度传给片元着色器
   diffuseFM=diffuseTemp;//将反面散射光最终强度传给片元着色器
   specularFM=specularTemp;//将反面镜面反射光最终强度传给片元着色器
}                      
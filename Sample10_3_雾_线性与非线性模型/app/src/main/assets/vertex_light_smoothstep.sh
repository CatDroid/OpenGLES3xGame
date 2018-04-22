#version 300 es
uniform mat4 uMVPMatrix; 								//总变换矩阵
uniform mat4 uMMatrix; 									//变换矩阵
uniform vec3 uLightLocation;								//光源位置
uniform vec3 uCamera;									//摄像机位置
in vec3 aPosition;  								//顶点位置
in vec3 aNormal;    								//顶点法向量
out vec4 ambient;						//用于传递给片元着色器的环境光最终强度
out vec4 diffuse;							//用于传递给片元着色器的散射光最终强度
out vec4 specular; 						//用于传递给片元着色器的镜面光最终强度
out float vFogFactor; 					//用于传递给片元着色器的雾化因子

//定位光光照计算的方法
void pointLight(
  in vec3 normal,
  inout vec4 ambient,
  inout vec4 diffuse,
  inout vec4 specular,
  in vec3 lightLocation,
  in vec4 lightAmbient,
  in vec4 lightDiffuse,
  in vec4 lightSpecular
){
    ambient=lightAmbient;			        // 直接得出环境光的最终强度


    vec3 newNormal = mat3(uMMatrix)*normal ;// 对法向量规格化
    newNormal=normalize(newNormal);



    vec3 eye= normalize(uCamera-(uMMatrix*vec4(aPosition,1)).xyz);//计算从表面点到摄像机的向量eye
    vec3 vp= normalize(lightLocation-(uMMatrix*vec4(aPosition,1)).xyz);//计算从表面点到光源位置的向量vp


    float nDotViewPosition=max(0.0,dot(newNormal,vp)); 	//求法向量与vp的点积与0的最大值
    diffuse=lightDiffuse*nDotViewPosition;				//计算散射光的最终强度

    vec3 halfVector=normalize(vp+eye);	                // 求视线与光线的半向量
    float shininess=50.0;				                // 粗糙度，越小越光滑
    float nDotViewHalfVector=dot(newNormal,halfVector);	// 法线与半向量的点积
    float powerFactor=max(0.0,pow(nDotViewHalfVector,shininess)); 	// 镜面反射光强度因子
    specular=lightSpecular*powerFactor;    			    // 计算镜面光的最终强度
}

//计算雾因子的方法  线性模型
float computeFogFactor(){
   float tmpFactor;
   float fogDistance = length(uCamera-(uMMatrix*vec4(aPosition,1)).xyz); // 顶点到摄像机的距离  hhl 可以跟关照模型共用计算'表面点到摄像机的向量'
   const float end = 450.0;         // 雾结束位置  hhl 应该用Unifom传入
   const float start = 350.0;       // 雾开始位置
   tmpFactor = max(min((end- fogDistance)/(end-start),1.0),0.0);//用雾公式计算雾因子   	
   return tmpFactor;
}
void main()     
{
   gl_Position = uMVPMatrix * vec4(aPosition,1); //根据总变换矩阵计算此次绘制此顶点位置   
   pointLight(
       normalize(aNormal),      // 法向量  hhl 如果外面传入就标准化了,这里就不用了
       ambient,diffuse,specular,// 环境光最终强度  散射光最终强度   镜面光最终强度
       uLightLocation,          // 光源位置
       vec4(0.4,0.4,0.4,1.0),vec4(0.7,0.7,0.7,1.0),vec4(0.3,0.3,0.3,1.0)
                                // 环境光强度 散射光强度 镜面光强度
       );

   // vFogFactor = computeFogFactor();  // 计算雾因子 线性模型
   vFogFactor = 1.0 - smoothstep(         // hhl smoothstep函数名字全部小写,
                    350.0,
                    450.0,
                    distance(uCamera, (uMMatrix*vec4(aPosition,1)).xyz)  // 摄像机与表面点向量的距离
                    ); // hhl smoothStep 计算后要用 1.0 -  而且不能用 1 - (不能用整数)
}
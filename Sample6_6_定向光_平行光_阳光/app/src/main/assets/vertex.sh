#version 300 es
uniform mat4 uMVPMatrix; //总变换矩阵
uniform mat4 uMMatrix; //变换矩阵
uniform vec3 uLightDirection;	//定向光方向
uniform vec3 uCamera;	//摄像机位置
in vec3 aPosition;  //顶点位置
in vec3 aNormal;    //法向量
out vec3 vPosition;			//用于传递给片元着色器的顶点位置
out vec4 vAmbient;			//用于传递给片元着色器的环境光最终强度
out vec4 vDiffuse;			//用于传递给片元着色器的散射光最终强度
out vec4 vSpecular;			//用于传递给片元着色器的镜面光最终强度
void directionalLight(			//定向光光照计算的方法
  in vec3 normal,				//法向量
  inout vec4 ambient,			//环境光最终强度
  inout vec4 diffuse,				//散射光最终强度
  inout vec4 specular,			//镜面光最终强度
  in vec3 lightDirection,			//定向光方向
  in vec4 lightAmbient,			//环境光强度
  in vec4 lightDiffuse,			//散射光强度
  in vec4 lightSpecular			//镜面光强度
){

  // Step.1 计算变换后的法向量
  //vec3 normalTarget=aPosition+normal;
  //vec3 newNormal=(uMMatrix*vec4(normalTarget,1)).xyz-(uMMatrix*vec4(aPosition,1)).xyz;
  vec3 newNormal = mat3(uMMatrix)*normal ;// hhl. 重新修改 法向量  使用 模型变换矩阵 作为 法向量变换矩阵
  newNormal=normalize(newNormal);


  // Step.2 表面点到摄像机的向量
  vec3 eye= normalize(uCamera-(uMMatrix*vec4(aPosition,1)).xyz);

  // Step.3 表面点到光源点的向量(定向光只需规范化向量)
  vec3 vp= normalize(lightDirection); // hhl. 定向光 区别就是: 不需要再计算 照射点到光源点 的向量


  // Step.4 直接得出环境光的最终强度
  ambient=lightAmbient;

  // Step.5 计算散射光的最终强度
  float nDotViewPosition=max(0.0,dot(newNormal,vp)); 	//求法向量与vp的点积与0的最大值
  diffuse=lightDiffuse*nDotViewPosition;

  // Step.6  计算镜面光的最终强度
  vec3 halfVector=normalize(vp+eye);	                            // 求视线与光线的半向量
  float shininess=50.0;				                                // 粗糙度，越小越光滑
  float nDotViewHalfVector=dot(newNormal,halfVector);	            // 法线与半向量的点积
  float powerFactor=max(0.0,pow(nDotViewHalfVector,shininess)); 	// 镜面反射光强度因子
  specular=lightSpecular*powerFactor;
}
void main(){ 
   gl_Position = uMVPMatrix * vec4(aPosition,1);                    // 根据总变换矩阵计算此次绘制此顶点位置
   vec4 ambientTemp,diffuseTemp,specularTemp;	                    // 用来接收三个通道最终强度的变量

   directionalLight(
    normalize(aNormal),
    ambientTemp,diffuseTemp,specularTemp,
    uLightDirection,
    vec4(0.15,0.15,0.15,1.0),vec4(0.8,0.8,0.8,1.0),vec4(0.7,0.7,0.7,1.0) // hhl.三个通道的光初始强度都写死在shader

    );


   vAmbient=ambientTemp; 		//将环境光最终强度传给片元着色器
   vDiffuse=diffuseTemp; 		//将散射光最终强度传给片元着色器
   vSpecular=specularTemp; 		//将镜面光最终强度传给片元着色器     
   vPosition = aPosition;       //将顶点的位置传给片元着色器
}

/*
Mark.1 当前模型(镜面光 散射光 环境光) 跟光源的距离 没有关系

Mark.2 两单位向量点乘就是夹角的余弦值

Mark.3 定向光 方向向量 就是 照射点到很远的光源点的方向向量是固定的 所以不需要计算光源

Mark.4 定向光和定位光，都要 照射点到光源点的向量 这个向量其实就是方向向量 只需要其方向 不需要长度，单位向量，因为最后只是为了计算半向量和两单位向量点乘就是余弦值，所以定向光只需要传入一个光方向向量


*/
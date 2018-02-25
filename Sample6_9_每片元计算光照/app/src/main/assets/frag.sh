#version 300 es
precision mediump float;//给出默认浮点精度
uniform float uR;//球的半径
uniform vec3 uLightLocation;//光源位置
uniform mat4 uMMatrix; //变换矩阵
uniform vec3 uCamera;	//摄像机位置
in vec3 vPosition;//接收从顶点着色器过来的顶点位置
in vec3 vNormal;//接收从顶点着色器传递过来的法向量
out vec4 fragColor;
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
  vec3 normalTarget=vPosition+normal;	//计算变换后的法向量
  vec3 newNormal=(uMMatrix*vec4(normalTarget,1)).xyz-(uMMatrix*vec4(vPosition,1)).xyz;
  newNormal=normalize(newNormal); 	//对法向量规格化
  //计算从表面点到摄像机的向量
  vec3 eye= normalize(uCamera-(uMMatrix*vec4(vPosition,1)).xyz);  
  //计算从表面点到光源位置的向量vp
  vec3 vp= normalize(lightLocation-(uMMatrix*vec4(vPosition,1)).xyz);  
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
   vec3 color;
   float n = 8.0;//一个坐标分量分的总份数
   float span = 2.0*uR/n;//每一份的长度
   //每一维在立方体内的行列数
   int i = int((vPosition.x + uR)/span);
   int j = int((vPosition.y + uR)/span);
   int k = int((vPosition.z + uR)/span);
   //计算当点应位于白色块还是黑色块中
   int whichColor = int(mod(float(i+j+k),2.0));
   if(whichColor == 1) {//奇数时为红色
   		color = vec3(0.678,0.231,0.129);//红色
   }
   else {//偶数时为白色
   		color = vec3(1.0,1.0,1.0);//白色
   }
   //最终颜色
   vec4 finalColor=vec4(color,0);
   
   
   vec4 ambient,diffuse,specular;    //用来接收三个通道最终强度的变量 
   
   pointLight(normalize(vNormal),ambient,diffuse,specular,uLightLocation,//计算定位光各通道强度 
   vec4(0.15,0.15,0.15,1.0),vec4(0.8,0.8,0.8,1.0),vec4(0.7,0.7,0.7,1.0));  
   
   //综合三个通道光的最终强度及片元的颜色计算出最终片元的颜色并传递给管线
   fragColor=finalColor*ambient + finalColor*diffuse + finalColor*specular;
}     
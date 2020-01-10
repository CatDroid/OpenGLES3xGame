#version 300 es
precision mediump float;//给出默认的浮点精度
uniform mat4 uMMatrix; //基本变换矩阵
uniform vec3 uLightLocation;	//光源位置
uniform vec3 uCamera;	//摄像机位置	
uniform vec3 light;//聚光灯的方向向量
in vec3 VaPosition;//接收从顶点着色器过来的顶点位置
in vec3 VaNormal;//接收从顶点着色器传递过来的法向量
out vec4 fragColor;//输出的片元颜色
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
  vec3 normalTarget=VaPosition+normal;	//计算变换后的法向量
  vec3 newNormal=(uMMatrix*vec4(normalTarget,1)).xyz-(uMMatrix*vec4(VaPosition,1)).xyz;
  newNormal=normalize(newNormal); 	//对法向量规格化
  //计算从表面点到摄像机的向量
  vec3 eye= normalize(uCamera-(uMMatrix*vec4(VaPosition,1)).xyz);  
  //计算从表面点到光源位置的向量vp
  vec3 vp= normalize(lightLocation-(uMMatrix*vec4(VaPosition,1)).xyz);  
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
   	vec4 ambient, diffuse, specular;
   	vec3 dis=VaPosition-uLightLocation; // 顶点与光源位置的向量
   	float l1 = length(light);           // 聚光灯的方向向量的模
   	float l2 = length(dis);             // 顶点与光源位置之间向量的模
   	float cos_angle=dot(dis,light)/(l1*l2); // 计算向量dis和向量light的夹角余弦值

    pointLight(normalize(VaNormal),
                ambient,
                diffuse,
                specular,
                uLightLocation,
                vec4(0.1,0.1,0.1,1.0),
                vec4(0.7,0.7,0.7,1.0),
                vec4(0.3,0.3,0.3,1.0));

    // 物体本身的颜色
    vec4 finalcolor=vec4(0.8,0.8,0.8,1.0);

    // 计算物体光照下的颜色
    vec4 colorA = finalcolor*ambient + finalcolor*specular + finalcolor*diffuse;

    // 物体在非光照区的颜色
    vec4 colorB=vec4(0.1,0.1,0.1,1.0);

	if(cos_angle>0.98)  // 若在全光照区 // 0.98 = 11度  0.95 = 18度
   	{
   	  	finalcolor=colorA;
   	}
   	else if(cos_angle>0.95&&cos_angle<=0.98) //若在过渡区
   	{

        float media=(cos_angle-0.95)/0.03; // 定义过度变量值
       	finalcolor= colorA * media + colorB *(1.0-media);
   	}
   	else // 若在阴影区
   	{
   		finalcolor=colorB;
   	}	
   	fragColor =finalcolor;
}     

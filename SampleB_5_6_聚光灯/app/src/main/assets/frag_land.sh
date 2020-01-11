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
   	vec3 dis=(uMMatrix*vec4(VaPosition,1)).xyz -uLightLocation; // 顶点与光源位置的向量

   	// 计算向量dis和向量light的夹角余弦值

    //float l1 = length(light);                 // 聚光灯的方向向量的模
    //float l2 = length(dis);                   // 顶点与光源位置之间向量的模
    //float cos_angle=dot(dis,light)/(l1*l2);   // 会算出是0 如果向量中的x,y,z比较大
    //float l12 = l1*l2 ;                       // isinf(l12) --> false
    //if (l1 > 150.0 && l2 > 150. )
    //{
    //    if(  l12 > 30000. )
    //    {
    //       fragColor = vec4(1.,1.,0.,1.);
    //
    //    }else
    //    {
    //        fragColor = vec4(1.,1.,1.,1.);
    //    }
    //    return ;
    //}


   	//highp float l1 = length(light);          // 或者整个着色器声明为 precision highp float
   	//highp float l2 = length(dis);
    //highp float l12 = l1*l2 ;
    ////if (l12 > 16384. )                      // GPU的半精度 范围在2^-14~2^14 比IEEE754的要少
    ////{
    ////    fragColor = vec4(0.,1.,1.,1.);
    ////    return ;
    ////}
    //float re = dot(dis,light);
    //float cos_angle= re / l12 ;


   	float l1 = length(light);                     // 聚光灯的方向向量的模
   	float l2 = length(dis);                       // 顶点与光源位置之间向量的模
    float cos_angle = dot(dis,light)/ l1 / l2;    // 这样也避免算出来是0

    //  光源位置是 50,40,30  平面的大小尺寸是  242,239  放在场景中 x= -121,121  y=0 z= -119,119
    //  光源目标 -50.0, -40.0, -48.0  light 向量  -100,-80,-78   模l1 149  l1*l2 模相乘 34,270‬  GPU在16384就溢出了
    //  顶点坐标 -121,  0      ,-119  dis 向量    -171,-40,-149  模l2 230  light*dist 点积 31,922‬    0.931
    // lowp float变量用10位表示,medium float用16位表示,而highp用32位来表示


   	if (isnan(cos_angle)) {
        fragColor = vec4(0.,1.,1.,1.);  // 天蓝色
        return ;
   	}

   	if (isinf(cos_angle) ){
        fragColor = vec4(1.,1.,1.,1.);  // 白色
   	    return ;
   	}


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

        float media =(cos_angle-0.95)/0.03; // 定义过度变量值
       	finalcolor= colorA * media + colorB *(1.0-media);
   	}
   	else if(cos_angle > 0.) // 若在阴影区
   	{
   		finalcolor = colorB;
   	}
   	else if (cos_angle < 0.) // 夹角超过了90度
   	{
        finalcolor = vec4(1.,0.,0.,1.); // 红色
   	}
   	else if (cos_angle == 0.) // 刚好是90度夹角
   	{
        finalcolor = vec4(0.,1.,0.,1.); // 绿色
   	}



   	fragColor = finalcolor;
}     

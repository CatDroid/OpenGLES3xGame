#version 300 es

in vec3 aPosition;  //顶点位置
in vec3 aNormal;    //顶点法向量
in vec2 aTexCoor;    //顶点纹理坐标
//用于传递给片元着色器的变量
out vec4 ambient;
out vec4 diffuse;
out vec4 specular;
out vec2 vTextureCoord;  
uniform MyDataBlock
{
	vec3 uLightLocation;	//光源位置    offset = 0
	vec3 uCamera;			//摄像机位置  offset = 12 如果加上layout (std140) 就是offset = 16
} mb;

// 不加也可以的
layout (std140,column_major) uniform MyDataBlock2
{
    // bool uResult ;
    mat4 uMVPMatrix;    //总变换矩阵  如果有bool 这个offset是16字节 也即是对齐按照vec4
    mat4 uMMatrix;      //基本变换矩阵
} mb2;

layout (std140) uniform TestBlock
{
    bool uResult1; // offset = 0
    bool uResult2; // offset = 4 个字节  相当于 sizeof(GLfloat)
} mb3;

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
  vec3 normalTarget=aPosition+normal;	//计算变换后的法向量
  vec3 newNormal=(mb2.uMMatrix*vec4(normalTarget,1)).xyz-(mb2.uMMatrix*vec4(aPosition,1)).xyz;
  newNormal=normalize(newNormal); 	//对法向量规格化
  //计算从表面点到摄像机的向量
  vec3 eye= normalize(mb.uCamera-(mb2.uMMatrix*vec4(aPosition,1)).xyz);
  //计算从表面点到光源位置的向量vp
  vec3 vp= normalize(lightLocation-(mb2.uMMatrix*vec4(aPosition,1)).xyz);
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
   gl_Position = mb2.uMVPMatrix * vec4(aPosition,1); //根据总变换矩阵计算此次绘制此顶点位置
   
   vec4 ambientTemp, diffuseTemp, specularTemp;   //存放环境光、散射光、镜面反射光的临时变量      
   pointLight(normalize(aNormal),ambientTemp,diffuseTemp,specularTemp,mb.uLightLocation,vec4(0.15,0.15,0.15,1.0),vec4(0.9,0.9,0.9,1.0),vec4(0.4,0.4,0.4,1.0));
   
   ambient=ambientTemp;
   diffuse=diffuseTemp;
   specular=specularTemp;
   vTextureCoord = aTexCoor;//将接收的纹理坐标传递给片元着色器
}                      
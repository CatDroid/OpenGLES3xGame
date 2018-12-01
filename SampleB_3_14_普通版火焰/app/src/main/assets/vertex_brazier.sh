#version 300 es
uniform mat4 uMVPMatrix;            // 总变换矩阵
uniform mat4 uMMatrix;              // 变换矩阵
uniform vec3 uLightLocation;        // 光源位置
uniform vec3 uCamera;	            // 摄像机位置
in vec3 aPosition;                  // 顶点位置
in vec3 aNormal;                    // 顶点法向量
in vec2 aTexCoor;                   // 顶点纹理坐标

out vec4 ambient;                   // 用于传递给片元着色器的变量
out vec4 diffuse;
out vec2 vTextureCoord;


void pointLight(				 // 定位光光照计算的方法
  in vec3 normal,				 // 法向量
  inout vec4 ambient,			 // 环境光最终强度
  inout vec4 diffuse,			 // 散射光最终强度
  in vec3 lightLocation,		 // 光源位置
  in vec4 lightAmbient,			 // 环境光强度
  in vec4 lightDiffuse		     // 散射光强度
){
  ambient=lightAmbient;			 // 直接得出环境光的最终强度

  vec3 newNormal= normalize(mat3(uMMatrix)*normal);     // 计算变换后的法向量
  //vec3 normalTarget=aPosition+normal;
  //vec3 newNormal=(uMMatrix*vec4(normalTarget,1)).xyz-(uMMatrix*vec4(aPosition,1)).xyz;
  //newNormal=normalize(newNormal);
  vec3 vPostion = (uMMatrix*vec4(aPosition,1)).xyz;     // 表面点在世界坐标的位置

  vec3 vp= normalize(lightLocation- vPostion );         // 计算从表面点到光源位置的向量vp
  float nDotViewPosition=max(0.0,dot(newNormal,vp)); 	// 求法向量与vp的点积与0的最大值
  diffuse=lightDiffuse*nDotViewPosition;				// 计算散射光的最终强度
}
void main()     
{                            		
   gl_Position = uMVPMatrix * vec4(aPosition,1);// 表面点标准设置空间的位置

   vec4 ambientTemp, diffuseTemp;   // 存放环境光、散射光的临时变量
   pointLight(normalize(aNormal),ambientTemp,diffuseTemp,uLightLocation,vec4(0.05,0.05,0.05,1.0),vec4(0.5,0.5,0.5,1.0));
   ambient=ambientTemp;             // 将接收的环境光传给片元着色器
   diffuse=diffuseTemp;             // 将接收的散射光强度传给片元着色器

   vTextureCoord = aTexCoor;        // 将接收的纹理坐标传递给片元着色器
}                      
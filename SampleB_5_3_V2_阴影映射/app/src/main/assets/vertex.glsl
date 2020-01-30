#version 300 es
uniform mat4 uMVPMatrix;        // 总变换矩阵
uniform mat4 uMMatrix;          // 变换矩阵
uniform mat4 uMVPMatrixGY;      // 总变换矩阵(光源)
uniform vec3 uLightLocation;    // 光源位置
uniform vec3 uCamera;           // 摄像机位置

in vec3 aPosition;      // 顶点位置
in vec3 aNormal;        // 顶点法向量
out vec4 ambient;       // 用于传递给片元着色器的环境光最终强度
out vec4 diffuse;       // 用于传递给片元着色器的散射光最终强度
out vec4 specular;      // 用于传递给片元着色器镜面光最终强度
out vec4 vPosition;     // 用于传递给片元着色器的顶点位置(世界坐标系)
out vec3 worldNormal;   // 用于自动计算避免投影失真的偏移量
out vec3 vModelPosition ;

//定位光光照计算的方法
void pointLight(                // 定位光光照计算的方法
  in vec3 normal,				// 法向量
  inout vec4 ambient,			// 环境光最终强度
  inout vec4 diffuse,           // 散射光最终强度
  inout vec4 specular,			// 镜面光最终强度
  in vec3 lightLocation,        // 光源位置
  in vec4 lightAmbient,			// 环境光强度
  in vec4 lightDiffuse,			// 散射光强度
  in vec4 lightSpecular			// 镜面光强度
)
{
    ambient = lightAmbient;     // 直接得出环境光的最终强度

    vec3 newNormal =normalize( mat3(uMMatrix) * aNormal);
    vec3 worldPos = (uMMatrix * vec4(aPosition,1)).xyz;

    worldNormal = newNormal ;

    // 计算从表面点到摄像机的向量
    vec3 eye = normalize(uCamera - worldPos);

    // 计算从表面点到光源位置的向量vp
    vec3 vp = normalize(lightLocation - (uMMatrix*vec4(aPosition,1)).xyz);
    float nDotViewPosition = max(0.0, dot(newNormal,vp)); 	// 求法向量与vp的点积与0的最大值
    diffuse = lightDiffuse * nDotViewPosition;				// 计算散射光的最终强度

    float shininess=50.0;				    // 粗糙度，越小越光滑
    vec3 halfVector = normalize(vp + eye);	// 求视线与光线的半向量
    float nDotViewHalfVector = dot(newNormal, halfVector);	            // 法线与半向量的点积
    float powerFactor = max(0.0, pow(nDotViewHalfVector,shininess)); 	// 镜面反射光强度因子
    specular = lightSpecular * powerFactor;    			                // 计算镜面光的最终强度
}
void main()     
{ 
    gl_Position = uMVPMatrix * vec4(aPosition,1.0);      // 根据总变换矩阵计算此次绘制此顶点的位置
    pointLight(
        normalize(aNormal),
        ambient,
        diffuse,
        specular,
        uLightLocation,
        vec4(0.15,0.15,0.15,1.0),
        vec4(0.7,0.7,0.7,1.0),
        vec4(0.3,0.3,0.3,1.0));                         // 计算光照各个通道的强度


    vPosition = uMMatrix * vec4(aPosition,1.0);       // 将变换后的顶点位置传递给片元着色器

    vModelPosition = aPosition ;
}                      
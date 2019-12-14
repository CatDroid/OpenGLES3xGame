#version 300 es
precision mediump float;
uniform int isShadow;					// 阴影绘制标志
uniform mat4 uMVPMatrix; 				// 总变换矩阵
uniform mat4 uMMatrix; 					// 变换矩阵
uniform mat4 uMProjCameraMatrix; 		// 投影、摄像机组合矩阵
uniform vec3 uLightLocation;			// 光源位置
uniform vec3 uCamera;					// 摄像机位置

uniform vec3 uPlaneNormal ;             // 投影接收体平面的法线
uniform vec3 uPlanePot ;                // 投影平面上的一点   // 平面法线+平面上的一点=确定一个平面

in vec3 aPosition;  					// 顶点位置
in vec3 aNormal;    					// 顶点法向量
out vec4 ambient;						// 用于传递给片元着色器的环境光最终强度
out vec4 diffuse; 						// 用于传递给片元着色器的散射光最终强度
out vec4 specular;	 					// 用于传递给片元着色器的镜面光最终强度
// 定位光光照计算的方法
void pointLight(					// 定位光光照计算的方法
  in vec3 normal,				    // 法向量
  inout vec4 ambient,			    // 环境光最终强度
  inout vec4 diffuse,				// 散射光最终强度
  inout vec4 specular,			    // 镜面光最终强度
  in vec3 lightLocation,			// 光源位置
  in vec4 lightAmbient,			    // 环境光强度
  in vec4 lightDiffuse,			    // 散射光强度
  in vec4 lightSpecular			    // 镜面光强度
)
{
    ambient = lightAmbient;			    // 直接得出环境光的最终强度

    vec3 normalTarget=aPosition+normal;	// 计算变换后的法向量
    vec3 newNormal=(uMMatrix*vec4(normalTarget,1)).xyz-(uMMatrix*vec4(aPosition,1)).xyz;
    newNormal=normalize(newNormal);

    // 计算从表面点到摄像机的向量
    vec3 eye= normalize(uCamera-(uMMatrix*vec4(aPosition,1)).xyz);
    // 计算从表面点到光源位置的向量vp
    vec3 vp= normalize(lightLocation-(uMMatrix*vec4(aPosition,1)).xyz);

    // 求视线与光线的半向量
    vec3 halfVector=normalize(vp+eye);

    float nDotViewPosition=max(0.0,dot(newNormal,vp)); 	// 求法向量与vp的点积与0的最大值
    diffuse=lightDiffuse*nDotViewPosition;				// 计算散射光的最终强度

    float shininess = 50.0;				                            // 粗糙度，越小越光滑
    float nDotViewHalfVector = dot(newNormal,halfVector);	            // 法线与半向量的点积
    float powerFactor = max(0.0, pow(nDotViewHalfVector,shininess)); 	// 镜面反射光强度因子
    specular = lightSpecular*powerFactor;    			                // 计算镜面光的最终强度
}

void main(){
    if (isShadow == 1) {						        // 标志位为1，则绘制阴影
        vec3 A = uPlanePot; 			             // 绘制阴影平面上任意一点的坐标 (接收阴影的平面是在XOZ平面上 pm.obj )
        vec3 n = uPlaneNormal;                       // 绘制阴影平面的法向量
        vec3 S = uLightLocation; 				            // 光源位置
        vec3 V = (uMMatrix*vec4(aPosition,1)).xyz;        // 经过平移和旋转变换后的点的坐标
        vec3 VL= S + (V-S)*(dot(n,(A-S))/dot(n, (V-S)));    // 顶点沿光线投影到需要绘制阴影的平面上点的坐标
        gl_Position = uMProjCameraMatrix*vec4(VL,1); 	// 根据组合矩阵计算此次绘制此顶点位置
    } else {   							                // 根据总变换矩阵计算此次绘制此顶点位置
        gl_Position = uMVPMatrix * vec4(aPosition,1);
    }
    pointLight(normalize(aNormal),ambient,diffuse,specular,uLightLocation,
    vec4(0.4,0.4,0.4,1.0),vec4(0.7,0.7,0.7,1.0),vec4(0.3,0.3,0.3,1.0));//计算光照
}



                   
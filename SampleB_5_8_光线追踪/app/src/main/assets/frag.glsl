#version 300 es
precision mediump float;    // 给出默认的浮点精度

uniform vec3 uColor;        // 基本块的颜色

uniform vec3 uPosition;     // 基本块对应的第一碰撞点位置
uniform vec3 uNormal;       // 基本块对应的第一碰撞点法向量 并且这个基本块(小矩形)的4个顶点的(颜色,法线,顶点坐标)是一样
                            // 在shader里面绘制的不是 物体，而是在cpu端计算好的 近平面!
uniform vec3 uLightLocation;// 光源位置
uniform vec3 uCamera;	    // 摄像机位置

uniform int isShadow;       // 阴影绘制标志
out vec4 fragColor;         // 输出到的片元颜色

void pointLight             // 定位光光照计算的方法
(
  inout vec4 ambient,       // 环境光分量
  inout vec4 diffuse,       // 散射光分量
  inout vec4 specular,      // 镜面反射光分量
  in vec4 lightAmbient,     // 光的环境光分量
  in vec4 lightDiffuse,     // 光的散射光分量
  in vec4 lightSpecular     // 光的镜面反射光分量
)
{
  ambient=lightAmbient;			//直接得出环境光的最终强度

  vec3 newNormal=normalize(uNormal);
  //计算从表面点到摄像机的矢量
  vec3 eye= normalize(uCamera-uPosition);  
  //计算从表面点到光源位置的矢量
  vec3 vp = normalize(uLightLocation-uPosition);
  vec3 halfVector=normalize(vp+eye);	                // 求视线与光线的半向量
  float shininess=50.0;				                    // 粗糙度，越小越光滑
  float nDotViewPosition=max(0.0,dot(newNormal,vp)); 	// 求法向量与vp的点积与0的最大值
  diffuse=lightDiffuse*nDotViewPosition;				// 计算散射光的最终强度
  float nDotViewHalfVector=dot(newNormal,halfVector);	// 法线与半向量的点积
  float powerFactor=max(0.0,pow(nDotViewHalfVector,shininess)); // 镜面反射光强度因子
  specular=lightSpecular*powerFactor;    			            // 计算镜面光的最终强度
}

void main()
{
   // 基本颜色
   vec4 baseColor = vec4(uColor,1.0);
   if(isShadow == 0)
   {
        // 若不在阴影中则需要计算光照
   		vec4 ambient, diffuse, specular;
   		pointLight(
   		        ambient,
   		        diffuse,
   		        specular,
   				vec4(0.15,0.15,0.15,1.0),
   				vec4(0.9,0.9,0.9,1.0),
   				vec4(0.7,0.7,0.7,1.0));
   		// 综合3个通道光的最终强度及片元的颜色计算出最终片元的颜色并传递给管线
   		fragColor=baseColor*ambient + baseColor*diffuse + baseColor*specular;
   }
   else // 如果在阴影中只计算环境光
   {
   		fragColor=baseColor*vec4(0.15,0.15,0.15,1.0);
   }
}
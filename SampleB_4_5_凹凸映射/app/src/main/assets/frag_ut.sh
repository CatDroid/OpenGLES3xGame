#version 300 es
precision mediump float;		//给出默认浮点精度
in vec2 vTextureCoord;			//接收从顶点着色器传递过来的纹理坐标
in vec3 fNormal;    			//接收从顶点着色器传递过来的法向量
in vec3 ftNormal;   				//接收从顶点着色器传递过来的切向量
in vec3 vPosition;  				//接收从顶点着色器传递过来的顶点位置
uniform sampler2D sTextureWg;		//纹理内容数据（外观）
uniform sampler2D sTextureNormal;		//纹理内容数据（法线）
uniform mat4 uMMatrix; 				//变换矩阵
uniform vec3 uCamera;				//摄像机位置
uniform vec3 uLightLocationSun;		//光源位置
out vec4 fragColor;//输出的片元颜色

void pointLight(					//定位光光照计算的方法
  in vec3 normal,				//扰动后法向量
  out vec4 ambient,				//最终环境光强度
  out vec4 diffuse,				//最终散射光强度
  out vec4 specular,				//最终镜面光强度  
  in vec3 vp,					//变换到标准法向量所属坐标系的表面点到光源位置的向量
  in vec3 eye,					//变换到标准法向量所属坐标系的视线向量
  in vec4 lightAmbient,			//环境光强度
  in vec4 lightDiffuse,			//散射光强度
  in vec4 lightSpecular			//镜面光强度
){   
  ambient=lightAmbient; 						//直接得出环境光的最终强度  
  vec3 halfVector=normalize(vp+eye);			//求视线与光线的半向量    
  float shininess=50.0;						//粗糙度，越小越光滑
  float nDotViewPosition=max(0.0,dot(normal,vp)); 	//求法向量与vp的点积与0的最大值
  diffuse=lightDiffuse*nDotViewPosition;			//计算散射光的最终强度
  float nDotViewHalfVector=dot(normal,halfVector);	//法向量与半向量的点积 
  float powerFactor=max(0.0,pow(nDotViewHalfVector,shininess)); 	//镜面反射光强度因子
  specular=lightSpecular*powerFactor;    		//计算镜面光的最终强度	
}

// OpenGL ES2.0  GLSL 1.0 not built-in transpose
// OpenGL ES3.0     transpose  inverse  determinant outerProduct 都是内置
//highp mat3 transpose(in highp mat3 inMatrix) {
//    highp vec3 i0 = inMatrix[0];
//    highp vec3 i1 = inMatrix[1];
//    highp vec3 i2 = inMatrix[2];
//
//
//    highp mat3 outMatrix = mat3(
//                 vec3(i0.x, i1.x, i2.x),
//                 vec3(i0.y, i1.y, i2.y),
//                 vec3(i0.z, i1.z, i2.z)
//                 );
//
//    return outMatrix;
//}

void main(){

    // 从法线纹理图中读出值 将值恢复到-1～+1范围
    vec4 normalColor = texture(sTextureNormal, vTextureCoord);
    //vec3 cNormal=vec3(2.0*(normalColor.r-0.5),2.0*(normalColor.g-0.5),2.0*(normalColor.b-0.5));
    vec3 cNormal= (normalColor*2.0 - 1.0).rgb;
    cNormal=normalize(cNormal);   //将扰动结果向量规格化


    // 变换后的法向量 过圆心旋转,xyz同时缩放等是正交矩阵 所以同样是世界变换矩阵
    //vec3 normalTarget=vPosition+fNormal;
    //vec3 newNormal=(uMMatrix*vec4(normalTarget,1)).xyz-(uMMatrix*vec4(vPosition,1)).xyz;
    //newNormal=normalize(newNormal);
    vec3 newNormal =normalize(mat3(uMMatrix)*fNormal);

    // 变换后的切向量 切向量的变换矩阵就是世界变换矩阵
    //vec3 tangentTarget=vPosition+ftNormal;
    //vec3 newTangent=(uMMatrix*vec4(tangentTarget,1)).xyz-(uMMatrix*vec4(vPosition,1)).xyz;
    //newTangent=normalize(newTangent);
    vec3 newTangent = normalize(mat3(uMMatrix)*ftNormal);

    // 副法向量
    vec3 binormal=normalize(cross(newTangent,newNormal));

    // 变换后的片元位置
    vec3 newPosition=(uMMatrix*vec4(vPosition,1)).xyz;

    // 用切向量、副法向量、法向量搭建变换矩阵，此矩阵用于将向量
    // 从实际坐标系变换到标准法向量所属坐标系
    // 这里需要inverse求逆矩阵 否则左右两个茶壶的光照方向看起来就会不一样
//   mat3 rotation=mat3(newTangent,binormal,newNormal); // 原来demo用这个 结果是错误的!


    //mat3 rotation=inverse(mat3(newTangent,binormal,newNormal));
     mat3 rotation=inverse(mat3(binormal,newTangent,newNormal));   // 副法和法调换 ?? 有轻微效果不一样??
     // x轴--切线   y轴--副法线  z轴--法线(确定的)
     // 因为NormalMapUtil工具导出来的时候，使用z轴代表法线

    //mat3 rotation=inverse(mat3(newTangent,newNormal,binormal));   // 如果把法线定义改为y轴:修改-1/2处



    //  单位方向向量可以直接乘以变换矩阵 得到变换后的坐标系上描述的单位方向向量
    //  变换 vp向量
    vec3 vp = normalize(rotation*normalize(uLightLocationSun-newPosition));
    //  求出从表面点到摄像机的视线向量进行变换
    vec3 eye= normalize(rotation*normalize(uCamera-newPosition));


   vec4 ambient,diffuse,specular;                                 // 用来接收三个通道最终强度的变量
   pointLight(
                cNormal,
                //cNormal.xzy,                                    // 如果把法线定义改为y轴:修改-2/2处
                ambient,diffuse,specular,
                vp,eye,
   	            vec4(0.05,0.05,0.05,1.0),
   	            vec4(1.0,1.0,1.0,1.0),
   	            vec4(0.3,0.3,0.3,1.0));

   vec4 finalColor=texture(sTextureWg, vTextureCoord);	          // 根据纹理坐标采样出片元颜色值

   // 综合三个通道光的最终强度及片元的颜色计算出最终片元的颜色并传递给渲染管线
   fragColor = finalColor*ambient+finalColor*specular+finalColor*diffuse;
}        

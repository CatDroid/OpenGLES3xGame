#version 300 es
uniform mat4 uMVPMatrix; 	//总变换矩阵
uniform mat4 uMMatrix; 		//变换矩阵
uniform vec3 uLightLocation;	//光源位置
uniform vec3 uCamera;		//摄像机位置
in vec3 aPosition;  	//顶点位置
in vec3 aNormal;   	//法向量
out vec3 vPosition;		//用于传递给片元着色器的顶点位置
out vec4 vSpecular;		//用于传递给片元着色器的镜面光最终强度
void pointLight(				//定位光光照计算的方法
  in vec3 normal,			//法向量
  inout vec4 specular,		//镜面光最终强度
  in vec3 lightLocation,		//光源位置
  in vec4 lightSpecular		//镜面光强度
){ 
  //vec3 normalTarget=aPosition+normal; 	//计算变换后的法向量
  //vec3 newNormal=(uMMatrix*vec4(normalTarget,1)).xyz-(uMMatrix*vec4(aPosition,1)).xyz;
  mat3 mmatrix = mat3(uMMatrix);
  vec3 newNormal= mmatrix * normal ; // 直接使用模型变换矩阵 作为法向量变换矩阵
  newNormal=normalize(newNormal);  	//对法向量规格化
  //计算从表面点到摄像机的向量
  vec3 eye= normalize(uCamera-(uMMatrix*vec4(aPosition,1)).xyz);  
  //计算从表面点到光源位置的向量vp
  vec3 vp= normalize(lightLocation-(uMMatrix*vec4(aPosition,1)).xyz);  
  vp=normalize(vp);//格式化vp
  vec3 halfVector=normalize(vp+eye);	//求视线与光线的”半向量“  光源向量与观察者向量的规范化 之后 求和   刚好就是光源和观察者的中间线
  float shininess=15.0;				//粗糙度，越小越光滑
  float nDotViewHalfVector=dot(newNormal,halfVector);			//法线与半向量的点积 
  float powerFactor=max(0.0,pow(nDotViewHalfVector,shininess)); 	//镜面反射光强度因子   shader函数: max(a,b)  pow(a,b) a^b
  specular=lightSpecular*powerFactor;    //最终的镜面光强度
}
void main()  {                        		
   gl_Position = uMVPMatrix * vec4(aPosition,1); //根据总变换矩阵计算此次绘制此顶点的位置      
   vec4 specularTemp=vec4(0.0,0.0,0.0,0.0);   
   pointLight(normalize(aNormal), specularTemp, uLightLocation, vec4(0.7,0.7,0.7,1.0));//计算镜面光  
   vSpecular=specularTemp;	//将最终镜面光强度传给片元着色器   
   vPosition = aPosition; 		//将顶点的位置传给片元着色器
} 

/*

Mark.1 镜面光照射结果 = 材质的反射系数 * 镜面光最终强度     镜面光最终强度 = 镜面光强度 * max( 0 ,  cos(半向量与法向量夹角)^粗糙度 )
        材质的反射系数 就是 照射处 物体本来的颜色
        半向量 观察点与光源点 跟 照射点 之间的中间线


Mark.2 cos值  可以通过夹角的两个向量 的 点积  shader的dot函数

Mark.3 两个向量中平均中间角线  可以通过两个向量的规范化的和 得到的向量 就在中间角线 上

Mark.4 在只有位移 xyz一致缩放 旋转 等变换的情况下  可以用 去掉位移的 模型变换矩阵(3*3) 作为 法向量变换矩阵

Mark.5 观察者位置 就是 九参数视图变换矩阵 中的摄像机坐标

*/
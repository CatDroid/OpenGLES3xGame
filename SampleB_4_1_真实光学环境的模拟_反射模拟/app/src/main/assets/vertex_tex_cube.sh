#version 300 es

uniform mat4 uMVPMatrix; 	// 总变换矩阵
uniform mat4 uMMatrix; 		// 变换矩阵
uniform vec3 uCamera;		// 摄像机位置

in vec3 aPosition;  	    // 顶点位置
in vec3 aNormal;    	    // 顶点法向量

out vec3 vTextureCoord;     // 用于传递给片元着色器的立方图采样向量


// 使用shader中reflect计算反射向量，作为单位方向向量，采样立方体贴图
void main() {

    // 根据总变换矩阵计算此次绘制此顶点位置
    gl_Position = uMVPMatrix * vec4(aPosition,1);


    // 计算变换后的法向量并规格化
    //vec3 normalTarget=aPosition+aNormal;
    //vec3 newNormal=(uMMatrix*vec4(normalTarget,1)).xyz-(uMMatrix*vec4(aPosition,1)).xyz;
    //newNormal=normalize(newNormal);

    vec3 newNormal=normalize(mat3(uMMatrix)*aNormal.xyz );

    // 计算从表面点到摄像机的向量(视线向量)  !!! 注意这里 取负号 改为指向表面点(入射向量)
    vec3 eye= - normalize(uCamera-(uMMatrix*vec4(aPosition,1)).xyz);


    // 计算视线向量的反射向量(从入射点指向外部)，并传递给片元着色器
    vTextureCoord = reflect(eye,newNormal);
}        

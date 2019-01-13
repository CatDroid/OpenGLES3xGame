#version 300 es
precision mediump float;
uniform samplerCube sTexture;// 纹理内容数据
in vec3 eyeVary;		// 接收从顶点着色器过来的视线向量
in vec3 newNormalVary;	// 接收从顶点着色器过来的变换后法向量
out vec4 fragColor;     // 输出到的片元颜色

// 计算折反射纹理采样颜色的方法
vec4 zfs( in float zsl ){   //折射率

    vec3 vTextureCoord;	    //用于进行立方图纹理采样的向量

    vec4 finalColor;  		// 最终颜色

    const float maxH=0.7;	    // 入射角余弦值若大于此值则仅计算折射
    const float minH=0.2;	    // 入射角余弦值若小于此值则仅计算反射
    float sizeH=maxH-minH;      // 混合时余弦值的跨度 余弦值越大 代表入射角也小

    float testValue=abs(dot(eyeVary,newNormalVary));	// 计算视线向量与法向量的余弦值

    if(testValue>maxH)  {							    // 余弦值大于maxH仅折射

        vTextureCoord=refract(-eyeVary,newNormalVary,zsl);
        finalColor=texture(sTexture, vTextureCoord);

    }else if(testValue<=maxH&&testValue>=minH) {        // 余弦值在minH～maxH范围内反射、折射融合

        vec4 finalColorZS;		//若是折射的采样结果
        vec4 finalColorFS;		//若是反射的采样结果

        vTextureCoord=reflect(-eyeVary,newNormalVary);
        finalColorFS=texture(sTexture, vTextureCoord);  	    // 反射的计算结果

        vTextureCoord=refract(-eyeVary,newNormalVary,zsl);
        finalColorZS=texture(sTexture, vTextureCoord);  	    // 折射的计算结果

        float ratio=(testValue-minH)/sizeH;					    // 融合比例
        finalColor=finalColorZS*ratio+(1.0-ratio)*finalColorFS;	// 折反射结果线性融合

    }else{										                // 余弦值小于minH仅反射

        vTextureCoord=reflect(-eyeVary,newNormalVary);
        finalColor=texture(sTexture, vTextureCoord);
    }
    return finalColor;									// 返回最终结果
}

void main(){
   vec4 finalColor=vec4(0.0,0.0,0.0,0.0);
   // 由于有色散RGB三个色彩通道单独计算折反射
   finalColor.r=zfs(0.97).r;  		// 计算红色通道
   finalColor.g=zfs(0.955).g;  		// 计算绿色通道
   finalColor.b=zfs(0.94).b;  		// 计算蓝色通道
   fragColor=finalColor; 		    // 将最终的片元颜色传递给管线
} 

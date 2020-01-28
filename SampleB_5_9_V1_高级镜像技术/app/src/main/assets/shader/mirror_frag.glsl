#version 300 es
precision mediump float;                // 指定浮点默认精度
uniform highp mat4 uMVPMatrixMirror;    // 镜像摄像机观察及投影组合矩阵
uniform sampler2D sTexture;             // 纹理内容数据
in vec4 vPosition;                      // 接收来自顶点着色器的片元位置坐标
out vec4 fragColor;                     // 传递到渲染管线的片元颜色
void main()                         
{    
    vec4 gytyPosition=uMVPMatrixMirror * vec4(vPosition.xyz,1); 	// 将片元的位置投影到镜像摄像机的近平面上
    gytyPosition=gytyPosition/gytyPosition.w;	                    // 进行透视除法

    float s = (gytyPosition.s + 1.0)/2.0;                           // 将投影后的坐标换算为纹理坐标
    float t = (gytyPosition.t + 1.0)/2.0;
  

    if( s >= 0.0 && s <= 1.0 && t >= 0.0 && t <= 1.0)
    {
      	vec4 finalColor=texture(sTexture, vec2(s,t));
   		fragColor = finalColor;
    }
    else
    {
   		fragColor=vec4(0.0,0.0,0.0,1.0);                            // 计算最终的片元颜色值 -- 黑色
    }
}   
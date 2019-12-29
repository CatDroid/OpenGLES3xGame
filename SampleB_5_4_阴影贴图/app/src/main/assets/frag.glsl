#version 300 es
precision mediump float;	    // 设置默认精度

uniform highp int isShadow;	    // 是否绘制平面阴影

uniform sampler2D sTexture;	    // 阴影贴图纹理内容数据

in vec4 ambient;			    // 接收从顶点着色器传递过来的环境光参数
in vec4 diffuse;				// 接收从顶点着色器传递过来的散射光参数
in vec4 specular;			    // 接收从顶点着色器传递过来的镜面光参数

in vec4 vPosition;  			// 世界坐标系上的顶点坐标
uniform highp mat4 uMVPMatrixGY;// 光源虚拟摄像机-观察及投影组合矩阵

out vec4 fragColor;

void main(){

    if(isShadow==0)
    {
        //将片元的位置投影到光源处虚拟摄像机的近平面上
        vec4 gytyPosition = uMVPMatrixGY * vec4(vPosition.xyz,1);
        gytyPosition = gytyPosition/gytyPosition.w;	    // 进行透视除法
        float s=gytyPosition.s + 0.5;					// 将投影后的坐标换算为纹理坐标
        float t=gytyPosition.t + 0.5;
        vec4 finalColor=vec4(0.8,0.8,0.8,1.0); 		    // 物体本身的颜色

        // 计算在亮处的片元颜色，此时环境光、散射光、镜面光三个通道都有
        vec4 colorA=finalColor*ambient+finalColor*specular+finalColor*diffuse;
        // 计算在阴影中的片元颜色，此时仅有环境光、散射光，而且散射光减弱为原来的30%
        vec4 colorB=finalColor*ambient+finalColor*diffuse*0.3;


        if(s>=0.0&&s<=1.0&&t>=0.0&&t<=1.0)                      // 若纹理坐标在合法范围内则考虑投影贴图
        {
            vec4 projColor=texture(sTexture, vec2(s,t));        // 对投影纹理图进行采样
            float a=step(0.9999,projColor.r);                   // 如果r>=0.9999，则a=1
            float b=step(0.0001,projColor.r);                   // 如果r<0.0001，则b=0
            float c=1.0-sign(a);                                // 如果a=0,则c=1.如果a=1，则c=0 也可以是(1.0-a)
            fragColor =
                    a*colorA +
                    (1.0-b)*colorB +
                    b*c*mix(colorB,colorA,smoothstep(0.0,1.0,projColor.r)); // 计算最终片元颜色
        }
        else
        {

           fragColor= colorB;   // 计算最终片元颜色
        }
    }
    else // 绘制平面影音
    {

       fragColor = vec4(0.1,0.1,0.1,0.0);
    }
}       



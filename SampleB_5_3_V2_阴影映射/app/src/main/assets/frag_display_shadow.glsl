#version 300 es

precision highp float;      // 仅仅贴纹理的着色器
in vec2 vTextureCoord;      // 接收从顶点着色器过来的参数
uniform sampler2D sTexture; // 纹理内容数据
out vec4 fragColor;         // 输出到的片元颜色
uniform float usingTextureDepth ;

void main()                         
{           

    if ( usingTextureDepth == 1.0 )
    {
        float depthValue =  texture(sTexture,vTextureCoord).r  ;

//        没有<0也没有>1 大部分在0.95~1.0 整个场景都距离光源比较远的位置
//        if (depthValue > 0.98 && depthValue < 1.0 )
//        {
//           fragColor = vec4(1.0, 0. , 0. ,1.0);
//           return ;
//        }
        depthValue = pow(depthValue,4.0); // 提高0.5~1.0范围的对比度
        fragColor = vec4(depthValue, depthValue , depthValue ,1.0);

    }
    else if ( usingTextureDepth == 2.0)
    {

        float depthValue =  texture(sTexture,vTextureCoord).r  ; // -1 ~ 1
        //if (depthValue < 0.8) // 大部分在 0.8 ~ 1.0
        //{
        //    fragColor = vec4(1.0, 0.0 , 0.0 ,1.0);
        //    return ;
        //}
        float depth2 = (depthValue + 1.0) * 0.5 ;
        float depth3 = pow(depth2, 5.0);
        fragColor = vec4(depth3, depth3 , depth3 ,1.0);
    }
    else
    {
        // 给此片元从纹理中采样出颜色值，为了使不同的距离值显示出来灰度不同除以100，
        // 使距离值的对应颜色值在0～1之间，否则值大于1的话看起来都是白色了
        // sTexture是 R16F
        float depthValue = texture(sTexture, (vTextureCoord*0.5 + vec2(0.25))).r ;

        // 大部分在20 ~ 100之间
//        if (depthValue > 20. && depthValue < 100.)
//        {
//            fragColor = vec4(1.0, 0. , 0. ,1.0);
//            return ;
//        }

        depthValue = depthValue / 100.0 ;
        fragColor = vec4(depthValue, depthValue, depthValue,1.0);

    }


}              
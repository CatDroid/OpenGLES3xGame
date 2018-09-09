#version 300 es
precision mediump float;
in float vxPosition;
in vec2 vTextureCoord; //接收从顶点着色器过来的参数
uniform sampler2D sTexture;//纹理内容数据
uniform float xPosition;
out vec4 fragColor;
void main()                         
{         
//     float ff=xPosition-90.0;
//     float fPosition = vxPosition+0.72 ; // ????
//     if(fPosition >= 0.0 && fPosition < ff/625.0){
//         fragColor = vec4(1.0,0.0,0.0,0.5);
//     }else if( fPosition >= ff/625.0 &&  fPosition <= 1.44){ // 900/625 ??
//         fragColor = texture(sTexture, vTextureCoord);
//
//     }


    float ff=xPosition ; // 以左上角为原点的当前进度位置 vertxt_load2d.glsl传递的要是 aPosition.x
    // invert fromScreenXToNearX
    float fPosition = vxPosition * (1920.0/2.0) + (1080.0/2.0) ;//  转成左上角为原点的
//    if( fPosition < 0.0){
//        fragColor = vec4(0.0,1.0,0.0,1.0);
//    } // 用于调试

    if(fPosition >= 0.0 && fPosition <= ff){
        fragColor = vec4(1.0,0.0,0.0,0.5);
        // 如果渲染的位置 小于 当前的进度 就是红色的,红色当前进度条,fPosition表示0~fPosition是当前的进度
    }else if( fPosition > ff &&  fPosition <= 900.){ // 0--当前进度ff--900
        // 透明图 直接显示 粉红色背景进度条
        fragColor = texture(sTexture, vTextureCoord);
        //fragColor = vec4(0.0,0.0,0.0,0.0); // 直接这样也同样效果
    }
}              
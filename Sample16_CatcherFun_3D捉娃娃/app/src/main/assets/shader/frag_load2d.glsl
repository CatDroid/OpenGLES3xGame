#version 300 es
precision mediump float;
in float vxPosition;
in vec2 vTextureCoord; //接收从顶点着色器过来的参数
uniform sampler2D sTexture;//纹理内容数据
uniform float xPosition;
out vec4 fragColor;
void main()                         
{         
     float ff=xPosition-90.0; // 因为LoadView会加上90 90+0*20 ~ 90+41*20  loadPosition = 90 + initIndex * 20f;

     // 540, 1680, 900, 20,
     float fPosition = vxPosition+0.72 ;
     if(fPosition>= 0.0 && fPosition < ff/625.0){  // 625 *2 = 1250
         fragColor = vec4(1.0,0.0,0.0,0.5);   // 红色当前进度条  fPosition表示0~fPosition是当前的进度
     }else if( fPosition >= ff/625.0 &&  fPosition <= 1.44){ // 625.0*1.44 = 900 因为LoadView把BN2DObject设置为宽是900
         fragColor = texture(sTexture, vTextureCoord); // 透明图 直接显示背景进度条
     }
    
}              
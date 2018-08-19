#version 300 es
precision mediump float;
uniform vec4 startColor;//起始颜色
uniform vec4 endColor;//终止颜色
uniform float sjFactor;//衰减因子
uniform float bj;//半径
uniform sampler2D sTexture;//纹理内容数据
in vec2 vTextureCoord; //接收从顶点着色器过来的参数
in vec3 vPosition;
out vec4 fragColor;
void main()                         
{               
    vec4 colorTL = texture(sTexture, vTextureCoord); 
    vec4 colorT;
    float disT=distance(vPosition,vec3(0.0,0.0,0.0));
    float tampFactor=(1.0-disT/bj)*sjFactor;
    vec4 factor4=vec4(tampFactor,tampFactor,tampFactor,tampFactor);
    colorT=clamp(factor4,endColor,startColor);
    colorT=colorT*colorTL.a;  
    fragColor=colorT;
}
#version 300 es
precision mediump float;
uniform vec4 startColor;//起始颜色
uniform vec4 endColor;//终止颜色
uniform float sjFactor;//衰减因子
uniform float bj;//半径
uniform sampler2D sTexture;//纹理内容数据
in vec2 vTextureCoord; //接收从顶点着色器过来的参数
in vec3 vPosition; // 在物体坐标系中的位置
out vec4 fragColor;
void main()                         
{               


    float disT = distance( vPosition, vec3(0.0,0.0,0.0) ); // 物体表面点 距离 物体坐标系原点的距离
    float tampFactor =  ( 1.0 - disT / bj ) * sjFactor; // disT/bj=当前位置/半径=归一化  越往圆心 值越大
    vec4 factor4 = vec4( tampFactor, tampFactor, tampFactor, tampFactor ); // 作为颜色
//    vec4 colorT = clamp(factor4, endColor, startColor); // 在指定范围内的颜色
    vec4 colorT = mix(endColor, startColor, factor4); // endColor*(1-factor4) + startColor*factor4 线性插值 混合

    vec4 colorTL = texture(sTexture, vTextureCoord);
    colorT = colorT * colorTL.a;
    // 读取对应纹理图的颜色,只读取alpha值,为了显示形状
    // 如果不用这个,那么会是一个圆圈
    // 如果把disT/bj与中心距离的因素也去掉，就是矩形
    // fire.png star.png star2.png

    fragColor = colorT ;

/*
    mix 函数:

    minval > maxval
    Mali-G72        先判断最大 后判断最小 if(val>maxval) {val = maxval; return;} else if(val<minval){val = minval ; return;}
    Adreno (TM) 530 总是等于maxval  val = maxval

    colorT = clamp(vec4(0.5,0.7,0.0,1.0) ,
        vec4(0.8,0.2,0.0,1.0),
        vec4(0.4,0.6,0.0,1.0)); // 每个成分单独clamp
    if(colorT.g == 0.6){
        fragColor = vec4(0.,0.,1.,1.);
    }else{
        fragColor = vec4(0.,1.,0.,1.);
    }
*/
}
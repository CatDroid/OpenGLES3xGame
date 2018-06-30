#version 300 es   
precision mediump float;//给出默认的浮点精度
uniform sampler2D sTexture;//纹理内容数据
in float vEdge;//描边系数
in vec2 vTextureCoord;//纹理坐标
out vec4 fragColor;//输出到的片元颜色

void main()                         
{
    //从纹理中采样出颜色值
    vec4 finalColor=texture(sTexture, vTextureCoord);

    const vec4 edgeColor=vec4(1.0,1.0,0.0,0.0);// 描边的颜色

    // vEdge 是 镜头指向物体表面点方向 与 物体表面点上的法向量 夹角 余弦值

    // vEdge>0.2--return0  arccos0.2 =78度
    // 余弦值越大角度越小，余弦值超过0.2，也就是与人眼睛夹角低过78度，就不是边界
    // 计算此片元是否进行描边的因子
    float mbFactor=step(0.2,vEdge); // mbFactor=1 不是边界


    // 如果不为边缘像素用纹理采样颜色，如果为边缘像素用描边颜色
    //fragColor=(1.0-mbFactor)*edgeColor+mbFactor*finalColor; //  mbFactor要么是1要么是0
    fragColor = mix(edgeColor,finalColor,mbFactor );
}
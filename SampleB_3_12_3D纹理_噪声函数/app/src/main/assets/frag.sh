#version 300 es
precision mediump float;
uniform sampler3D sTexture;//3D纹理内容数据
//接收从顶点着色器过来的参数
in vec4 ambient;//接收从顶点着色器过来的环境光最终强度
in vec4 diffuse;//接收从顶点着色器过来的散射光最终强度
in vec4 specular;//接收从顶点着色器过来的镜面光最终强度
in vec3 vPosition;//接收从顶点着色器过来的位置坐标  
out vec4 fragColor;//最终输出的片元颜色
void main()                         
{  
   // 浅色木纹颜色
   const vec4 lightWood=vec4(0.6,0.3,0.1,1.0);
   // 深色木纹颜色
   const vec4 darkWood=vec4(0.4,0.2,0.07,1.0); // 越暗,rgb值越小

//#define SIMPLE_2 1
//#define SIMPLE_1 1

#ifdef SIMPLE_CENTER

//  float dis=distance(vPosition.xz+vec2(2.0,0.0) ,vec2(0,0));
    mat3 rotate30 = mat3(cos(3.14/6.),sin(3.14/6.),0,
                        -sin(3.14/6.),cos(3.14/6.),0,
                        0,0,1 );
    vec3 location  =  rotate30 * vec3(vPosition)  ;
    float dis=distance(  location.xz  ,vec2(0,0));
                        // +vec2(2.0,0.0)或者rotate30 因为模型在中间
                        // 如果按原来(x,z)~(0,0)计算的话茶壶的外侧都在差不多的同心圆上,不过茶壶底是清晰看到年轮的
    dis *=10.0;         // 因为模型只有-2~2的尺寸 要造成很多的0~1间隔,就要乘以10
                        // *10.0 这样0~0.5 0.5~1 就成了 0~1.0 和 1.0~2 分别是一个年轮
    float r=fract(dis); // 然后取 小数部分  左右 dark和light两种颜色之间插值的百分比
//    if( r == 0.5){    // 这种方法可以用来测试
//        discard;
//    }
    vec4 color = mix( lightWood, darkWood, r);
    fragColor =color*ambient+color*specular+color*diffuse;


#elif defined SIMPLE_1

    vec3 texCoor=vec3(((vPosition.x/0.52)+1.0)/2.0,vPosition.y/0.4,((vPosition.z/0.52)+1.0)/2.0);
    vec4 noiseVec=texture(sTexture,vec3(texCoor));
    vec3 location  =  vPosition  +  noiseVec.rgb*0.05; // 造成不完美同心圆的噪声(2-1)
    float dis=distance(location.xz,vec2(0,0)); // x0z平面的同心圆 导致了湖底看比较有年轮 但是茶壶侧面就看不出多大变化
    dis *= 10.0;                               // 可以把这个调大茶壶底部或者侧面年轮更明显
    //if(noiseVec.r+noiseVec.g*0.5+noiseVec.b*0.25+noiseVec.a*0.125 > 1.3f) discard; // 最大值 1+0.5+0.25+0.125=1.875
    float r=fract( dis + noiseVec.r+noiseVec.g*0.5+noiseVec.b*0.25+noiseVec.a*0.125)*2.0;// 造成不完美同心圆的噪声(2-2)
    //float r=fract( dis )*2.0; // 如果用这个噪声就少了很多,只有2-1起作用
    if( r > 1.0 ){
        r = 2.0-r; // fract(r)*2 然后在做 2.0-r  目的是做 浅--深--浅-(-深-) 这样的效果  也就是每隔开2个单位是一个年轮
    }
    vec4 color = mix( lightWood, darkWood, r);
    fragColor =color*ambient+color*specular+color*diffuse;

#elif defined SIMPLE_2

    vec3 texCoor=vec3(((vPosition.x/0.52)+1.0)/2.0,vPosition.y/0.4,((vPosition.z/0.52)+1.0)/2.0);
    vec4 noiseVec=texture(sTexture,vec3(texCoor));
    vec3 location  =  vPosition   +  noiseVec.rgb*0.05;         // 导致 x+y+z=a平面的点最后不在同一个平面
    float r =fract((location.y+location.x+location.z)*25.0+0.5);// 这里比SIMPLE_1缺了 浅--深--浅 效果
    noiseVec.a*=r;
    float ifelse = step(0.5,r );
    vec4 color = lightWood + ((1.0-ifelse)*(lightWood*1.0*noiseVec.a) - ifelse*lightWood*0.02*noiseVec.a);
    fragColor =color*ambient+color*specular+color*diffuse;

#else

    // 根据片元的位置折算出3D纹理坐标--3D噪声纹理的索引 ??? 不这样做会导致 x,y,z轴处的显示有断层??
    // 因为3D纹理是 64*64*64  都是从0~1的
    // 而顶点是 x=[-0.450000~0.515100],y=[0.000000~0.472500],z=[-0.240000~0.255000]
    vec3 texCoor=vec3(   ((vPosition.x/0.52)+1.0)/2.0, // vPosition.x/0.52 归一化 -1~1范围
                        vPosition.y/0.4,
                        ((vPosition.z/0.52)+1.0)/2.0);
    // 进行3D纹理采样
    vec4 noiseVec=texture(sTexture,vec3(texCoor));


    // 计算受3D柏林噪声纹理采样值影响的位置 , 因为r通道是一倍频 x坐标相互变化没有这么大, b是三倍频 相邻的z组坐标变化大
    vec3 location  =  vPosition   +  noiseVec.rgb*0.05;


    // 计算离平面中心点的距离
    float dis=distance(location.xz,vec2(0,0));

    // 整个模型的尺寸在-0.45~0.51  这里*2.0 大概在dis 0~1.0
    // 年轮的频率 控制圆环的多少
    dis *= 2.0;


    // 计算两种木纹颜色的混合因子 HHL因为3dNoise.bn3dtex是等幅度的0~255(glTexture3D时 归一化) 所以这里要做幅度衰减合并
    // dis 是主要的 noiseVec.r+noiseVec.g*0.5+noiseVec.b*0.25+noiseVec.a*0.125 是柏林噪声
    // noiseVec.r+noiseVec.g*0.5+noiseVec.b*0.25+noiseVec.a*0.125 最大值 1+0.5+0.25+0.125=1.875 测试大概最大在1.3
    float r=fract( dis + noiseVec.r+noiseVec.g*0.5+noiseVec.b*0.25+noiseVec.a*0.125)*2.0;

    if( r > 1.0)
    {
      r = 2.0 - r; // 浅--深--浅的效果  但实际经过后面的步骤后 还是变成了 深--浅--深--浅
    }



    // 进行两种木纹颜色的混合
    vec4 color = mix( lightWood, darkWood, r);
    //color = darkWood ; // mix之后偏向这个颜色

    // 再次计算调整因子  这里导致了倾斜
    // x+y+z=a 这是在一个平面上 与x+y+z=0平面平行 因为法向量都是(1,1,1) 朝向x,y,z轴正方向(通过法向量(1,1,1)知道)
    // 平面方程的一般形式是ax+by+cz+d=0,其中xyz前面的系数（a,b,c)就是法向量的坐标
    r =fract((location.y+location.x+location.z)*25.0+0.5);
    //r=fract(location.x*25.0); // 这样就变成与x轴方向间隔 *25.0加大间隔 +0.5没有多大影响,只是最内的同心圆会显示

    // 修改噪声值
    noiseVec.a*=r;
    // 优化 使用step代替if else
    // 注意区分step(r,0.5);  step(a,x) a<x then 0 else 1
//    float ifelse = step(0.5,r );
//    color += ((1.0-ifelse)*(lightWood*1.0*noiseVec.a) - ifelse*lightWood*0.02*noiseVec.a);

    // 根据调整因子调整颜色
    if(r<0.5){ // 使用step代替if else优化shader
        color += lightWood*1.0*noiseVec.a;
    }else{
        color -= lightWood*0.01*noiseVec.a; // 变黑
    }

   //给此片元颜色值
   //color = darkWood;
   fragColor = color*ambient+color*specular+color*diffuse;

#endif
}   
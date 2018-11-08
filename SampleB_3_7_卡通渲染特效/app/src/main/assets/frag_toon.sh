#version 300 es
precision mediump float;						//给出默认的浮点精度
uniform sampler2D uImageUnit;				//纹理内容数据
uniform float uMagTol;						//设定的阈值，判定当前点是否为边缘点
uniform float uQuantize;						//阈值量化因子
in vec2 vST;								//接收从顶点着色器过来的参数
out vec4 fFragColor;							//输出到的片元颜色
void main()
{
	ivec2 ires=textureSize(uImageUnit,0);		// 获得纹理图的宽高度
	float uResS = float(ires.s);				// 获得纹理图的S值
	float uResT = float(ires.t);				// 获得纹理图的T值

    const vec3 W=vec3(0.2125,0.7154,0.0721);	// BT709

	vec2 stp0=vec2(1.0/uResS, 0.0);			    // 与左右相邻像素间的距离向量
	vec2 st0p=vec2(0.0,       1.0/uResT);	    // 与上下相邻像素间的距离向量
	vec2 stpp=vec2(1.0/uResS, 1.0/uResT);		// 与左下、右上相邻像素间的距离向量
	vec2 stpm=vec2(1.0/uResS, -1.0/uResT);		// 与左上、右下相邻像素间的距离向量

	
	// dot 点乘 从rgb得到灰度值
	float m00 =dot( texture( uImageUnit,vST-stpp).rgb,W );
	float m01 =dot( texture( uImageUnit,vST-st0p).rgb,W );
    float m02 =dot( texture( uImageUnit,vST+stpm).rgb,W );

    float m10 =dot( texture( uImageUnit,vST-stp0).rgb,W );
    float m12 =dot( texture( uImageUnit,vST+stp0).rgb,W );

    float m20 =dot( texture( uImageUnit,vST-stpm).rgb,W );
    float m21 =dot( texture( uImageUnit,vST+st0p).rgb,W );
	float m22 =dot( texture( uImageUnit,vST+stpp).rgb,W );



	// 与图像作平面卷积计算，分别得出横向及纵向的亮度差分近似值，即sobel算子的横纵灰度值(下面对方向没要求)
	float h = -1.0 * m20 - 2.0 * m21 - 1.0 * m22 + 1.0 * m00 + 2.0 * m01 + 1.0 * m02;
	float v = -1.0 * m00 - 2.0 * m10 - 1.0 * m20 + 1.0 * m02 + 2.0 * m12 + 1.0 * m22;


	// 当前像素点的梯度值
	float mag=length( vec2(h, v));


	// soble求边界 需要 给定阈值
	// 梯度值大于阈值 才认为是边缘，阈值越小，更多元素变成边界，轮廓就越粗
	if(mag>uMagTol)
	{
		fFragColor=vec4(0.0,0.0,0.0,1.0);       // 如果梯度mag大于阈值，则认为该点为边缘点，黑色
	}else
	{   // 不在物体边缘,量化物体的颜色值
	    vec3 rgb = texture(uImageUnit,vST).rgb;   // 获得纹理采样的rgb值
		rgb.rgb *= uQuantize;                   // 将当前片元的颜色值乘以量化值 卡通成都 越大色彩越饱和
		rgb.rgb += vec3(0.5,0.5,0.5);           // hhl 这个可以不用加 只是为了向上取整
		ivec3 intrgb = ivec3(rgb.rgb);          // 转换成整数类型的向量  实际这里才是量化!! 离散化成uQuantize个值
		rgb.rgb = vec3(intrgb) / uQuantize;     // 将整数类型的片元颜色值除以量化值
		fFragColor=vec4(rgb,1.0);               // 获得重新计算的最终颜色值
	}
}

	

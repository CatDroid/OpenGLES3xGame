#version 300 es
precision mediump float;
uniform sampler2D sTexture;//纹理内容数据 hhl 没有用外部传入的纹理图
//接收从顶点着色器过来的参数
in vec4 ambient;
in vec4 diffuse;
in vec4 specular;
in vec2 vTexPosition;
const float maxIterations = 99.0;//最大迭代次数
const float zoom =0.5;      // 缩放系数
const float xCenter = 0.0;  // 中心坐标位置  hhl 也就是在复平面(0,0)附近的0.5*2.5=1.25 -1.25~1.25范围的茱莉亚集
const float yCenter = 0.0;
const vec3 innerColor = vec3(0.0, 0.0, 1.0);//内部颜色
const vec3 outerColor1 = vec3(1.0, 0.0, 0.0);//外部颜色1
const vec3 outerColor2 = vec3(0.0, 1.0, 0.0);//外部颜色2
out vec4 fFragColor;//输出的片元颜色
void main()                         
{    
   float real = vTexPosition.x * zoom + xCenter;//变换当前位置
	float imag = vTexPosition.y * zoom + yCenter;	
	float cReal = 0.32; // c的实部
	float cImag =0.043; // c的虚部
	float r2 = 0.0;     // 半径的平方
	float i;            // 迭代次数
	for(i=0.0; i<maxIterations && r2<4.0; i++){// 循环迭代
		float tmpReal = real;                               // 保存当前实部值
		real = (tmpReal * tmpReal) - (imag * imag) +cReal;  // 计算下一次迭代后实部的值
		imag = 2.0 *tmpReal * imag +cImag;                  // 计算下一次迭代后虚部的值
		r2 = (real * real) + (imag * imag);                 // 计算半径的平方
	}
    vec3 color;    
    if(r2 < 4.0){               // 如果r2未达到4就退出了循环，表明迭代次数已达到最大值
    	color = innerColor;     // 为内部颜色赋值
    }else{                      // 如果因r2大于4.0而退出循环，表明此位置在外部
    	color = mix(outerColor1, outerColor2, fract(i * 0.07));// 按迭代次数为外部颜色赋不同的值
    }    
    vec4 finalColor=vec4(color, 1.0);// 最终的颜色还考虑关照
    fFragColor = finalColor*ambient+finalColor*specular+finalColor*diffuse;
}   
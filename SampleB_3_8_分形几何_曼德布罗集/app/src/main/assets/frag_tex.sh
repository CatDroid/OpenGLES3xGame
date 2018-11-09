#version 300 es
precision mediump float;    // 给出默认的浮点精度
in vec2 vTexPosition;       // 从顶点着色器传递过来的纹理坐标
const float maxIterations =999.0;   // 最大迭代次数
const float zoom = 1.0;           // 缩放系数 -- 曼德布罗集
//const float zoom = 0.6;             // 缩放系数 -- 茱莉亚集
const float xCenter = 0.0;          // 中心x坐标
const float yCenter = 0.0;          // 中心y坐标
const vec3 innerColor = vec3(0.0, 0.0, 1.0);    // 内部颜色
const vec3 outerColor1 = vec3(1.0, 0.0, 0.0);   // 外部颜色1 Red 最外部的 代表趋于无穷越快
const vec3 outerColor2 = vec3(0.0, 1.0, 0.0);   // 外部颜色2
out vec4 fFragColor;                            // 输出的片元颜色
void main() {
	float real = vTexPosition.x * zoom + xCenter;// 变换当前位置
	float imag = vTexPosition.y * zoom + yCenter;	
	float cReal = real;   // c的实部 曼德布罗的c与位置有关 a+bi (a,b)就是当前纹素坐标
	float cImag = imag;   // c的虚部
//	float cReal = 0.32;     // c的实部  曼德布罗的c改成常量,与位置无关
//    float cImag =0.043;     // c的虚部
	float r2 = 0.0;         // 半径的平方
	float i;                // 迭代次数
	for(i=0.0; i < maxIterations && r2 < 4.0; i++){// 循环迭代
		float tmpReal = real;// 保存当前实部值
		real = (tmpReal * tmpReal) - (imag * imag) +cReal;// 计算下一次迭代后实部的值
		imag = 2.0 *tmpReal * imag +cImag;  // 计算下一次迭代后虚部的值
		r2 = (real * real) + (imag * imag); // 计算半径的平方
	}
    vec3 color;             // 最终颜色
    if(r2 < 4.0){           // 如果r2未达到4就退出了循环，表明迭代次数已达到最大值
    	color = innerColor; // 此时采用内部颜色对此片元着色
    }else{                  // 如果因r2大于4.0而退出循环，表明此位置在外部
    	color = mix(outerColor1, outerColor2, fract(i * 0.07));// 按迭代次数采用不同的颜色着色
    }    
   	fFragColor = vec4(color, 1.0);// 将最终颜色传递给渲染管线
}              
#version 300 es
precision mediump float;//给出默认的浮点精度
in vec2 vTextureCoord;//从顶点着色器传递过来的纹理坐标
uniform sampler2D sTexture;//纹理内容数据
out vec4 fFragColor;//输出的片元颜色

void main() {           
	// 1. 给出卷积内核中各个元素对应像素相对于待处理像素的纹理坐标偏移量  左上 正上 右上  正左 本身 正右边
	vec2 offset0=vec2(-1.0,-1.0); vec2 offset1=vec2(0.0,-1.0); vec2 offset2=vec2(1.0,-1.0);
	vec2 offset3=vec2(-1.0,0.0); vec2 offset4=vec2(0.0,0.0); vec2 offset5=vec2(1.0,0.0);
	vec2 offset6=vec2(-1.0,1.0); vec2 offset7=vec2(0.0,1.0); vec2 offset8=vec2(1.0,1.0); 

	// 2. 卷积内核中各个位置的值
	float kernelValue0 = 1.0; float kernelValue1 = 1.0; float kernelValue2 = 1.0;
	float kernelValue3 = 1.0; float kernelValue4 = 1.0; float kernelValue5 = 1.0;
	float kernelValue6 = 1.0; float kernelValue7 = 1.0; float kernelValue8 = 1.0;


	// 3. 获取卷积内核中各个元素对应像素的颜色值
	vec4 cTemp0,cTemp1,cTemp2,cTemp3,cTemp4,cTemp5,cTemp6,cTemp7,cTemp8;	
	cTemp0=texture(sTexture, vTextureCoord.st + offset0.xy/512.0); // hhl offset0.xy 代表偏移为1,然后纹理图的尺寸是512,归一化就是1.0/512
	cTemp1=texture(sTexture, vTextureCoord.st + offset1.xy/512.0);
	cTemp2=texture(sTexture, vTextureCoord.st + offset2.xy/512.0);
	cTemp3=texture(sTexture, vTextureCoord.st + offset3.xy/512.0);
	cTemp4=texture(sTexture, vTextureCoord.st + offset4.xy/512.0);
	cTemp5=texture(sTexture, vTextureCoord.st + offset5.xy/512.0);
	cTemp6=texture(sTexture, vTextureCoord.st + offset6.xy/512.0);
	cTemp7=texture(sTexture, vTextureCoord.st + offset7.xy/512.0);
	cTemp8=texture(sTexture, vTextureCoord.st + offset8.xy/512.0);

	// 4. 颜色求和
	vec4 sum; //最终的颜色和
	sum =kernelValue0*cTemp0+kernelValue1*cTemp1+kernelValue2*cTemp2+
		 kernelValue3*cTemp3+kernelValue4*cTemp4+kernelValue5*cTemp5+
	     kernelValue6*cTemp6+kernelValue7*cTemp7+kernelValue8*cTemp8;

    // 5. 进行亮度加权后将最终颜色传递给渲染管线
    const float scaleFactor = 1.0/9.0; // 给出最终求和时的加权因子(为调整亮度) hhl 就是平均
  	fFragColor = sum * scaleFactor;
}         
#version 300 es
uniform mat4 uMVPMatrix; 		//总变换矩阵

uniform sampler2D sTextureLand;//纹理内容数据（灰度图）
uniform float landHighAdjust;//陆地的高度调整值
uniform float landHighest;//陆地最大高差 
in vec2 aTexLandCoor;		//灰度图顶点纹理坐标
in vec3 aPosition;  		//顶点位置
in vec2 aTexCoor;    		//顶点纹理坐标
out vec2 vTextureCoord;  		//用于传递给片元着色器的纹理坐标
out float currY;				//用于传递给片元着色器的Y坐标

void main(){  
   vTextureCoord = aTexCoor;						//将接收的纹理坐标传递给片元着色器
   vec4 gColor=texture(sTextureLand, aTexLandCoor);	//从灰度图纹理中采样出颜色
   float tempy=(((gColor.r+gColor.g+gColor.b)/3.0)*landHighest)+landHighAdjust;//计算顶点的y值
   currY=tempy;		//将顶点的Y坐标传递给片元着色器
   gl_Position = uMVPMatrix * vec4(aPosition.x,tempy,aPosition.z,1); 	//根据总变换矩阵计算此次绘制此顶点的位置			
}    
                
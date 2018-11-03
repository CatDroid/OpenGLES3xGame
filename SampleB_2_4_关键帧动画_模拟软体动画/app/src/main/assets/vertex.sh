#version 300 es
uniform mat4 uMVPMatrix; //总变换矩阵
in vec3 aPosition;  //顶点位置(来自1号关键帧)
in vec3 bPosition;  //顶点位置(来自2号关键帧)
in vec3 cPosition;  //顶点位置(来自3号关键帧)
in vec2 aTexCoor;    //顶点纹理坐标
uniform float uBfb;//融合比例
out vec2 vTextureCoord; //用于传递给片元着色器的纹理坐标 

void main()     
{ 
	vec3 tv;   //融合后的结果顶点      		
   	if(uBfb<=1.0)//若融合比例小于等于1，则需要执行的是1、2号关键帧的融合
   	{
   		tv=mix(aPosition,bPosition,uBfb);
   	}
   	else//若融合比例大于1，则需要执行的是2、3号关键帧的融合
   	{
   		tv=mix(bPosition,cPosition,uBfb-1.0);
   	}
   	gl_Position = uMVPMatrix * vec4(tv,1);;	//根据总变换矩阵计算此次绘制此顶点的位置
   	vTextureCoord = aTexCoor;//将接收的纹理坐标传递给片元着色器
}                      
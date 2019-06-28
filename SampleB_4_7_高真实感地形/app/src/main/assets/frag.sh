#version 300 es
precision mediump float;					//给出默认的浮点精度
in vec2 vTextureCoord;					//接收从顶点着色器传过来的纹理坐标
in vec3 vPosition;						//接收从顶点着色器传过来的顶点位置
in vec4 vAmbient;						//接收从顶点着色器传过来的环境光分量
in vec4 vDiffuse;						//接收从顶点着色器传过来的散射光分量
in vec4 vSpecular;						//接收从顶点着色器传过来的镜面反射光分量
uniform sampler2D ssTexture;			//纹理内容数据(砂石)
uniform sampler2D lcpTexture;				//纹理内容数据(绿草皮)
uniform sampler2D dlTexture;				//纹理内容数据(道路)
uniform sampler2D hcpTexture;				//纹理内容数据(黄草皮)
uniform sampler2D rgbTexture;			//纹理内容数据(地形内容RGB贴图)
uniform float repeatVaule;						//纹理坐标最大repeat值
out vec4 fragColor;							//输出到片元的颜色
void main(){
   vec4 vColor=texture(rgbTexture,		//从地形内容RGB贴图中采样出颜色
		vec2(vTextureCoord.x/repeatVaule,vTextureCoord.y/repeatVaule));
   float rFactor=vColor.r;								//草皮的加权因子
   float gFactor=vColor.g;								//山石的加权因子
   float bFactor=vColor.b;								//道路的加权因子
   float aFactor=max(0.0,1.0-(rFactor+gFactor+bFactor));		//泥土的加权因子
   vec4 rColor=texture(ssTexture,vTextureCoord);		//从草皮纹理中采样出颜色
   vec4 gColor=texture(lcpTexture,vTextureCoord);	//从山石纹理中采样出颜色
   vec4 bColor=texture(dlTexture,vTextureCoord);		//从道路纹理中采样出颜色
   vec4 aColor=texture(hcpTexture,vTextureCoord);		//从泥土纹理中采样出颜色
   //计算片元加权颜色
   vec4 finalColor=rColor*rFactor+gColor*gFactor+bColor*bFactor+aColor*aFactor;
   //根据各个光照通道的强度计算最终片元颜色
   fragColor = finalColor*vAmbient + finalColor*vDiffuse + finalColor*vSpecular;
}

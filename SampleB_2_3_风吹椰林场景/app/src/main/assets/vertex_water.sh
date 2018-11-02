#version 300 es
uniform mat4 uMVPMatrix; //总变换矩阵
uniform float uStartAngle;//本帧起始角度
uniform float uWidthSpan;//横向长度总跨度
in vec3 aPosition;  //顶点位置
in vec2 aTexCoor;    //顶点纹理坐标
out vec2 vTextureCoord;  //用于传递给片元着色器的变量
void main()     
{                  
   
   //计算X向角度          		
   float angleSpanH=30.0*3.14159265;//横向角度总跨度 hhl 浪波不算抖,整个海域才有30度
   float startX=0.0;//起始X坐标
   //根据横向角度总跨度、横向长度总跨度及当前点X坐标折算出当前点X坐标对应的角度
   float currAngleH=uStartAngle+((aPosition.x-startX)/uWidthSpan)*angleSpanH;
   
   //计算出随z向发展起始角度的扰动值
   float startZ=0.0;//起始z坐标
   //根据纵向角度总跨度、纵向长度总跨度及当前点Y坐标折算出当前点Y坐标对应的角度
   float currAngleZ=((aPosition.z-startZ)/uWidthSpan)*angleSpanH;
      
   //计算斜向波浪
   float tzH=sin(currAngleH+currAngleZ)*0.8;   // hhl 海浪比较小，最大才有0.8 这里是+还是-没有影响 因为只影响相位
   //根据总变换矩阵计算此次绘制此顶点位置
   gl_Position = uMVPMatrix * vec4(aPosition.x,tzH,aPosition.z,1); 
   
  // gl_Position = uMVPMatrix * vec4(aPosition,1); 
   vTextureCoord = aTexCoor;//将接收的纹理坐标传递给片元着色器
}                      
#version 300 es
uniform mat4 uMVPMatrix; //总变换矩阵
in vec3 aPosition;  //顶点位置
in vec2 aTexCoor;    //顶点纹理坐标
out vec2 vTextureCoord;  //用于传递给片元着色器的纹理坐标
uniform float angleSpan;//本帧扭曲总角度
uniform float yStart;//Y坐标起始点
uniform float ySpan;//Y坐标总跨度
void main()     
{
   float tempAS= angleSpan*(aPosition.y-yStart)/ySpan;//计算当前顶点扭动(绕中心点选择)的角度
   vec3 tPosition=aPosition;
  
   //if(aPosition.y>yStart) // hhl 可以不用考虑 同一个用公式  根据绕y轴旋转的变换矩阵
   //{//若不是最下面一层的顶点则计算扭动后的X、Z坐标
     tPosition.x=(cos(tempAS)*aPosition.x-sin(tempAS)*aPosition.z);
     tPosition.z=(sin(tempAS)*aPosition.x+cos(tempAS)*aPosition.z);
   //}
   gl_Position = uMVPMatrix * vec4(tPosition,1); //根据总变换矩阵计算此次绘制此顶点的位置
   
   vTextureCoord = aTexCoor;//将接收的纹理坐标传递给片元着色器
}                      
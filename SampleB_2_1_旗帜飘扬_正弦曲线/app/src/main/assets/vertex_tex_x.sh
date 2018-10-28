#version 300 es
uniform mat4 uMVPMatrix; //总变换矩阵
uniform float uStartAngle;//本帧起始角度(即最左侧顶点的对应角度)
uniform float uWidthSpan;//横向长度总跨度
in vec3 aPosition;  //顶点位置
in vec2 aTexCoor;    //顶点纹理坐标
out vec2 vTextureCoord;  //用于传递给片元着色器的纹理坐标
void main()     
{            
   //计算X向波浪                		
   float angleSpanH = 4.0*3.14159265; // 横向角度总跨度，用于进行X距离与角度的换算 相当于4π  相当于整个旗帜在同一时刻有两个完整的正弦波
   float startX=-uWidthSpan/2.0;      // 起始X坐标(即最左侧顶点的X坐标)
   // 根据横向角度总跨度、横向长度总跨度及当前点X坐标折算出当前顶点X坐标对应的角度，相当于这一点的初相位 + uStartAngle
   float currAngle=uStartAngle+((aPosition.x-startX)/uWidthSpan)*angleSpanH;
   float tz = sin(currAngle)*0.1;  // 通过正弦函数求出当前点的Z坐标 幅度是0.1
   
   // 根据总变换矩阵计算此次绘制此顶点的位置
   gl_Position = uMVPMatrix * vec4(aPosition.x,aPosition.y,tz,1); 
   vTextureCoord = aTexCoor;// 将接收的纹理坐标传递给片元着色器
}                      
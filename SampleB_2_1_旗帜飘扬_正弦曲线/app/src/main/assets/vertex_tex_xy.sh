#version 300 es
uniform mat4 uMVPMatrix; //总变换矩阵
uniform float uStartAngle;//本帧起始角度(X、Y两个方向都是其值)
uniform float uWidthSpan;//横向长度总跨度
in vec3 aPosition;  //顶点位置
in vec2 aTexCoor;    //顶点纹理坐标
out vec2 vTextureCoord;  //用于传递给片元着色器的纹理坐标
void main()     
{                  
   //首先计算当前顶点X方向波浪对应的Z坐标		
   float angleSpanH=4.0*3.14159265;//横向角度总跨度，用于进行X距离与角度的换算
   float startX=-uWidthSpan/2.0;//起始X坐标(即最左侧顶点的X坐标)
   //根据横向角度总跨度、横向长度总跨度及当前点X坐标折算出当前顶点X坐标对应的角度
   float currAngleH=uStartAngle+((aPosition.x-startX)/uWidthSpan)*angleSpanH;
   float tzH=sin(currAngleH)*0.1;   //X方向波浪对应的Z坐标
   
   //接着计算当前顶点Y方向波浪对应的Z坐标
   float angleSpanZ=4.0*3.14159265;//纵向角度总跨度，用于进行Y距离与角度的换算
   float uHeightSpan=0.75*uWidthSpan;//纵向长度总跨度
   float startY=-uHeightSpan/2.0;//起始Y坐标(即最上侧顶点的Y坐标)
   //根据纵向角度总跨度、纵向长度总跨度及当前点Y坐标折算出当前顶点Y坐标对应的角度
   float currAngleZ=uStartAngle+
                     3.14159265/3.0+ // 这里还是相差π/3的角度
                    ((aPosition.y-startY)/uHeightSpan)*angleSpanZ;
   float tzZ=sin(currAngleZ)*0.1; //Y方向波浪对应的Z坐标
   
   //根据总变换矩阵计算此次绘制此顶点的位置  hhl 最后还是影响z坐标
   gl_Position = uMVPMatrix * vec4(aPosition.x,aPosition.y,
                (tzH+tzZ)/2.0  // hhl 自己加上2.0 避免幅度超过了0.1
                ,1);
   vTextureCoord = aTexCoor;//将接收的纹理坐标传递给片元着色器
}                      
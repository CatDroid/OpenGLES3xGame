#version 300 es
precision mediump float;//给出浮点默认精度
uniform sampler2D sTexture;//纹理内容数据
out vec4 fragColor; 	//输出片元的颜色
void main() 
{
   vec2 texCoor=gl_PointCoord; 	//从内建变量获取纹理坐标
   vec4 color = texture(sTexture,texCoor) ;

/*
   if( color.r < 0.1 && color.g < 0.1 && color.b < 0.1  ) {
   //if( gl_FragCoord.y < 960.0 ){
        //color.r = 1.0 ;color.g = 1.0 ;color.b = 1.0 ;
        color.a = 0.2 ;
   }else{
        color.a = 1.0 ;
   }
*/
   fragColor = color ;//进行纹理采样
}
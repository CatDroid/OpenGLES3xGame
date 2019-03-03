
attribute float aX;
attribute vec2 a1 ;
attribute vec2 a2 ;
attribute vec2 a3 ;

uniform float halt_width ;
uniform float halt_height ;

void main()     
{

    float y = a1.y * (aX - a2.x) *(aX - a3.x) / (a1.x - a2.x) / (a1.x - a3.x)
       + a2.y * (aX - a1.x) *(aX - a3.x) / (a2.x - a1.x) / (a2.x - a3.x)
       + a3.y * (aX - a1.x) *(aX - a2.x) / (a3.x - a1.x) / (a3.x - a2.x);

    float x = (aX - halt_width)/(halt_width) ;
    y = (y - halt_height)/(halt_height) ;
    gl_Position = vec4(x, y , 0.0, 1.0 );

//    gl_Position = vec4((a1.x - halt_width)/(halt_width), (a1.y - halt_height)/(halt_height) , 0.0, 1.0 );
}                      